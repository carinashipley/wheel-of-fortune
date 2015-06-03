package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;

import GameBoard.Game;
import GameBoard.GameListener;
import GameBoard.GameSounds;
import GameBoard.GameUpdateEvent;
import GameBoard.Letter;
import GameBoard.Phrase;
import GameBoard.Player;
import GameBoard.Wheel;
import GameBoard.Word;

public class GameView extends JFrame implements GameListener {
  private static final long serialVersionUID = 1L;
  private static BufferedImage wheelImg;
  private final char[] nonLetters = { '’', '\'', '&', '-', '.' };

  private Game MyGame;

  private Set<Character> nonLettersSet = new HashSet<Character>();

  private Map<String, Boolean> LettersMap = new HashMap<String, Boolean>();
  private Map<Integer, PlayerPodium> Podiums;
  private Map<String, LetterButton> ConsonantButtons = new HashMap<String, LetterButton>();
  private Map<String, LetterButton> VowelButtons = new HashMap<String, LetterButton>();

  private JPanel pnlPuzzleRow1 = new JPanel();
  private JPanel pnlPuzzleRow2 = new JPanel();
  private JPanel pnlPuzzleRow3 = new JPanel();
  private JPanel pnlPuzzleRow4 = new JPanel();
  private JPanel pnlLetters = new JPanel();

  private JLabel lblWheelValue = new JLabel();
  private JLabel lblGameStatus = new JLabel();
  private JLabel lblCategory = new JLabel();

  private JButton btnSpin = new JButton("Spin");
  private JButton btnBuyVowel = new JButton("Buy a Vowel");
  private JButton btnSolve = new JButton("Solve the Puzzle");

  private Wheel wheel = new Wheel();
  private int WheelValue = 0;
  private int LetterCount = 0;
  private String CurrentPlayerName;
  private boolean IsPuzzleSolved = false;
  private boolean IsLetterClickable = false;

  public GameView() {
    this.setLayout(new BorderLayout());
    this.setSize(775, 595);
    this.setTitle("Wheel of Fortune");
    this.setLocation(50, 50);

    wheel.AddStatusListener(this);

    BufferedImage img = null;
    try {
      File f = new File("img/wof_wallpaper_2_1024.jpg");
      img = ImageIO.read(f);
    } catch (Exception e) {
      System.out.println("Cannot read file: " + e);
    }

    BackgroundPanel background = new BackgroundPanel(img, BackgroundPanel.ACTUAL, 0.6f, 0.45f);
    this.setContentPane(background);
    this.setJMenuBar(getGameMenuBar());

    doNewGame();

    lblCategory.setHorizontalAlignment(JLabel.CENTER);
    lblCategory.setOpaque(false);
    lblCategory.setBorder(BorderFactory.createEmptyBorder(4, 0, 1, 0));

    JPanel pnlTop = new JPanel();
    pnlTop.setOpaque(false);
    pnlTop.setLayout(new BorderLayout());
    pnlTop.add(lblCategory, "North");
    pnlTop.add(getPuzzlePanel(), "Center");
    pnlTop.add(getLettersPanel(), "South");
    this.add(pnlTop, "North");

    lblGameStatus.setPreferredSize(new Dimension(200, 60));
    lblGameStatus.setHorizontalAlignment(JLabel.CENTER);

    JPanel pnlCenter = new JPanel();
    pnlCenter.setOpaque(false);
    pnlCenter.setLayout(new BorderLayout());
    pnlCenter.add(lblGameStatus, "North");

    pnlCenter.add(wheel, "Center");

    this.add(pnlCenter, "Center");

    btnSpin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          wheel.spin();
          WheelValue = wheel.getWheelValue();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    });

    btnBuyVowel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        buyVowel();
      }
    });

    btnSolve.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String answer = JOptionPane.showInputDialog(null, "Type your answer:", null);
        if (answer != null) {
          boolean isSolved = MyGame.solvePuzzle(answer);
          if (isSolved) {
            updatePuzzle();
          } else
            moveToNextPlayer("Incorrect guess. ", false);
        }
      }
    });

    this.add(getSouthPanel(), "South");
    this.add(getEastPanel(), "East");
  }

  private JPanel getSouthPanel() {
    JPanel pnl = new JPanel();
    pnl.setOpaque(false);
    pnl.setLayout(new FlowLayout());

    btnSpin.setPreferredSize(new Dimension(90, 40));
    btnSpin.setBackground(Color.GREEN);
    btnSpin.setFont(new Font("Tahoma", Font.BOLD, 20));
    btnSpin.setCursor(new Cursor(Cursor.HAND_CURSOR));

    btnBuyVowel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnSolve.setCursor(new Cursor(Cursor.HAND_CURSOR));

    pnl.add(btnSpin);
    pnl.add(btnBuyVowel);
    pnl.add(btnSolve);

    return pnl;
  }

  private JPanel getPuzzlePanel() {
    JPanel pnlPuzzle = new JPanel();

    pnlPuzzleRow1.setOpaque(false);
    pnlPuzzleRow2.setOpaque(false);
    pnlPuzzleRow3.setOpaque(false);
    pnlPuzzleRow4.setOpaque(false);

    pnlPuzzle.setLayout(new GridLayout(4, 1));
    pnlPuzzle.setOpaque(false);

    pnlPuzzle.add(pnlPuzzleRow1);
    pnlPuzzle.add(pnlPuzzleRow2);
    pnlPuzzle.add(pnlPuzzleRow3);
    pnlPuzzle.add(pnlPuzzleRow4);

    return pnlPuzzle;
  }

  private void updateGame() {
    if (WheelValue == -2) {
      // Lose a Turn
      moveToNextPlayer("Oh no! You lost your turn.", false);
      updateWheelValue("Lose a Turn");
    } else if (WheelValue == 0) {
      // Bankrupt
      moveToNextPlayer("Bankrupt! Sorry, " + CurrentPlayerName + ", you lose all your money.", true);
      updateWheelValue("Bankrupt");
    } else {
      Player currentPlayer = MyGame.getCurrentPlayer();
      updateWheelValue(wheel.getWheelValue());
      updateGameStatusText("<b>" + currentPlayer.getName() + "</b> needs to select a letter.");
      doAfterSpinState();
    }
  }

  private JMenuBar getGameMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Game");
    menuBar.add(menu);
    JMenuItem item = new JMenuItem("New Game");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        doNewGame();
      }
    });
    menu.add(item);
    item = new JMenuItem("Exit");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    menu.add(item);

    return menuBar;
  }

  private void doInitialGameState() {
    btnSpin.setEnabled(true);
    btnBuyVowel.setEnabled(false);
    btnSolve.setEnabled(true);
    IsLetterClickable = false;
    IsPuzzleSolved = false;
    updateGameStatusText("Welcome to Wheel of Fortune. Spin to begin.");
    updateWheelValue("");

    for (char c : nonLetters)
      nonLettersSet.add(c);
  }

  private void doNewGame() {
    if (MyGame == null)
      MyGame = new Game();
    MyGame.start();

    doInitialGameState();

    resetLetterButtons();

    Phrase phrase = MyGame.getRandomPhrase();
    lblCategory.setText("<html><div style='background-color:white;text-align:center;border:solid 2px black;padding:0 10px;'><h2>"
        + phrase.Category.toUpperCase() + "</h2></div></html>");

    CurrentPlayerName = MyGame.getCurrentPlayer().getName();

    if (Podiums != null) {
      for (Player p : MyGame.getPlayers()) {
        updatePodium(p);
        if (CurrentPlayerName == p.getName()) 
          Podiums.get(p.getPlayerId()).setBackground(Color.YELLOW);
        else
          Podiums.get(p.getPlayerId()).setBackground(Color.WHITE);
      }
    }

    updatePuzzle();
    repaint();
  }

  private void doNewTurnState() {
    btnSpin.setEnabled(true);
    IsLetterClickable = false;
    setBuyVowelBtn();
    setLetterBtns();
    setVowelBtns();
  }

  private void doSameTurnState() {
    btnSpin.setEnabled(true);
    IsLetterClickable = false;
    setBuyVowelBtn();
    setLetterBtns();
    setVowelBtns();
  }

  private void doAfterSpinState() {
    btnSpin.setEnabled(false);
    IsLetterClickable = true;
    btnBuyVowel.setEnabled(false);
    setLetterBtns();
  }

  private void doBuyVowelState() {
    btnSpin.setEnabled(false);
    IsLetterClickable = true;
    btnBuyVowel.setEnabled(false);
    setVowelBtns();
  }

  private void setBuyVowelBtn() {
    Player currentPlayer = MyGame.getCurrentPlayer();
    btnBuyVowel.setEnabled(currentPlayer.getScore() >= 250);
  }

  private void resetLetterButtons() {
    for (String vowel : VowelButtons.keySet())
      resetLetterButton(VowelButtons.get(vowel));

    for (String letter : ConsonantButtons.keySet())
      resetLetterButton(ConsonantButtons.get(letter));
  }

  private void resetLetterButton(LetterButton btn) {
    btn.IsUsed = false;
    btn.Button.setVisible(true);
  }

  private void setVowelBtns() {
    if (IsLetterClickable)
      pnlLetters.setBackground(Color.YELLOW);
    else
      pnlLetters.setBackground(Color.LIGHT_GRAY);

    for (String vowel : VowelButtons.keySet()) {
      LetterButton lb = VowelButtons.get(vowel);
      if (lb.IsUsed) {
        lb.Button.setEnabled(false);
        lb.Button.setVisible(false);
      } else
        lb.Button.setEnabled(IsLetterClickable);
    }
  }

  private void setLetterBtns() {
    if (IsLetterClickable)
      pnlLetters.setBackground(Color.YELLOW);
    else
      pnlLetters.setBackground(Color.LIGHT_GRAY);

    for (String letter : ConsonantButtons.keySet()) {
      LetterButton lb = ConsonantButtons.get(letter);
      if (lb.IsUsed) {
        lb.Button.setEnabled(false);
        lb.Button.setVisible(false);
      } else
        lb.Button.setEnabled(IsLetterClickable);
    }
  }

  private void updateGameStatusText(String message) {
    StringBuilder str = new StringBuilder();
    str.append("<html><p style='color:white;padding:0 10px;font-size:16px;'>");
    str.append(message);
    str.append("</p></html>");
    lblGameStatus.setText(str.toString());
  }

  private void moveToNextPlayer(String message, boolean updateScore) {
    Player currentPlayer = MyGame.getCurrentPlayer();

    if (updateScore) {
      updateScore();
    }

    // update current player's podium
    PlayerPodium currentPodium = Podiums.get(currentPlayer.getPlayerId());
    currentPodium.setBackground(Color.WHITE);

    // move to next player and make him/her the current player
    Player nextPlayer = MyGame.moveToNextPlayer();
    CurrentPlayerName = nextPlayer.getName();

    PlayerPodium nextPodium = Podiums.get(nextPlayer.getPlayerId());
    updateGameStatusText(message + " " + "It is <b>" + CurrentPlayerName + "</b>'s turn.");
    nextPodium.setBackground(Color.YELLOW);

    doNewTurnState();
  }

  private JPanel getEastPanel() {
    JPanel pnl = new JPanel();
    pnl.setOpaque(false);
    pnl.setLayout(new GridLayout(2, 1));
    pnl.add(getPlayersPanel());
    pnl.add(lblWheelValue);
    return pnl;
  }

  private void updateWheelValue(Integer value) {
    DecimalFormat myFormatter = new DecimalFormat("$###,###");
    updateWheelValue(myFormatter.format(value));
  }

  private void updateWheelValue(String value) {
    StringBuilder str = new StringBuilder();
    str.append("<html>");
    str.append("<h2 style='border: 2px solid #ffffff;padding: 5px 20px;background-color:#c0830b;background-image:(/img/score_bkg.jpg)'>");
    str.append(value);
    str.append("</h2>");
    str.append("</html>");

    lblWheelValue.setText(str.toString());
  }

  private JPanel getPlayersPanel() {
    JPanel pnl = new JPanel();

    pnl.setLayout(new FlowLayout());
    pnl.setOpaque(false);

    List<Player> players = MyGame.getPlayers();

    JPanel pnlPlayer;
    for (int i = 0; i < players.size(); i++) {
      Player p = players.get(i);
      pnlPlayer = getPlayerPanel(p);

      if (i == 0) {
        CurrentPlayerName = p.getName();
        pnlPlayer.setBackground(Color.YELLOW);
      } else {
        pnlPlayer.setBackground(Color.WHITE);
      }

      pnl.add(pnlPlayer);
    }

    return pnl;
  }

  private JPanel getPlayerPanel(Player player) {
    PlayerPodium pod = new PlayerPodium(player.getName());

    if (Podiums == null)
      Podiums = new HashMap<Integer, PlayerPodium>();

    Podiums.put(player.getPlayerId(), pod);

    return pod;
  }

  private JPanel getLettersPanel() {
    pnlLetters.setLayout(new GridLayout(1, 26));
    pnlLetters.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    pnlLetters.add(getLetterButton("A", true));
    pnlLetters.add(getLetterButton("B"));
    pnlLetters.add(getLetterButton("C"));
    pnlLetters.add(getLetterButton("D"));
    pnlLetters.add(getLetterButton("E", true));
    pnlLetters.add(getLetterButton("F"));
    pnlLetters.add(getLetterButton("G"));
    pnlLetters.add(getLetterButton("H"));
    pnlLetters.add(getLetterButton("I", true));
    pnlLetters.add(getLetterButton("J"));
    pnlLetters.add(getLetterButton("K"));
    pnlLetters.add(getLetterButton("L"));
    pnlLetters.add(getLetterButton("M"));
    pnlLetters.add(getLetterButton("N"));
    pnlLetters.add(getLetterButton("O", true));
    pnlLetters.add(getLetterButton("P"));
    pnlLetters.add(getLetterButton("Q"));
    pnlLetters.add(getLetterButton("R"));
    pnlLetters.add(getLetterButton("S"));
    pnlLetters.add(getLetterButton("T"));
    pnlLetters.add(getLetterButton("U", true));
    pnlLetters.add(getLetterButton("V"));
    pnlLetters.add(getLetterButton("W"));
    pnlLetters.add(getLetterButton("X"));
    pnlLetters.add(getLetterButton("Y"));
    pnlLetters.add(getLetterButton("Z"));

    return pnlLetters;
  }

  private JButton getLetterButton(String letter) {
    return getLetterButton(letter, false);
  }

  private JButton getLetterButton(String letter, boolean isVowel) {
    JButton btn = new JButton(letter);

    LettersMap.put(letter, true);

    btn.setMaximumSize(new Dimension(8, 10));
    btn.setFont(btn.getFont().deriveFont(19.0f));
    btn.setBorder(BorderFactory.createEmptyBorder());
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setEnabled(false);

    if (isVowel) {
      btn.setForeground(Color.BLUE);
      btn.addActionListener(getVowelButtonListener());
      VowelButtons.put(letter, new LetterButton(letter, btn));
    } else {
      btn.addActionListener(getConsonantButtonListener());
      ConsonantButtons.put(letter, new LetterButton(letter, btn));
    }

    return btn;
  }

  public void draw(Graphics g, int X, int Y, int width, int height) {
    g.drawImage(wheelImg, X, Y, width, height, null);
  }

  private ActionListener getVowelButtonListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (IsLetterClickable) {
          JButton btn = (JButton) e.getSource();
          String vowel = btn.getText();
          VowelButtons.get(vowel).IsUsed = true;

          LettersMap.put(vowel, false);

          LetterCount = MyGame.updatePhrase(vowel);
          updatePuzzle();

          if (!IsPuzzleSolved) {
            if (LetterCount == 0) {
              moveToNextPlayer("Sorry, no " + btn.getText() + ".", false);
              doNewTurnState();
            } else {
              GameSounds.playLetterTurn();
              updateGameStatusText(CurrentPlayerName + " needs to go again.");
              doSameTurnState();
            }
          }
        }
      }
    };
  }

  private ActionListener getConsonantButtonListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (IsLetterClickable) {
          JButton btn = (JButton) e.getSource();
          String letter = btn.getText();
          ConsonantButtons.get(letter).IsUsed = true;

          LettersMap.put(letter, false);

          LetterCount = MyGame.updatePhrase(letter);
          updatePuzzle();

          if (!IsPuzzleSolved) {
            if (LetterCount == 0) {
              moveToNextPlayer("No " + btn.getText() + ".", false);
              doNewTurnState();
            } else {
              updateScore();
              GameSounds.playLetterTurn();
              String isAre = LetterCount == 1 ? "is" : "are";
              String theS = LetterCount == 1 ? "" : "s";
              updateGameStatusText("There " + isAre + " " + getNumberWord(LetterCount) + " " + letter + theS + ". <b>"
                  + CurrentPlayerName + "</b> needs to spin again.");
              doSameTurnState();
            }
          }
        }
      }
    };
  }

  private String getNumberWord(Integer number) {
    switch (number) {
    case 1:
      return "one";
    case 2:
      return "two";
    case 3:
      return "three";
    default:
      return number.toString();
    }
  }

  private void buyVowel() {
    int answer = JOptionPane.showConfirmDialog(this, "Would you like to buy a vowel for $250?", "Buy a vowel?",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

    // 1 is no and 0 is yes - weird
    if (answer == 0) {
      boolean hasEnoughMoney = MyGame.getCurrentPlayer().buyVowel();
      updatePodium();

      if (hasEnoughMoney) {
        doBuyVowelState();
      } else {
        updateGameStatusText(CurrentPlayerName + ", you don't have enough money to buy a vowel.");
      }
    }
  }

  private void updateScore() {
    Player currentPlayer = MyGame.getCurrentPlayer();
    currentPlayer.updateScore(WheelValue * LetterCount);
    updatePodium(currentPlayer);
  }

  private void updatePodium() {
    Player currentPlayer = MyGame.getCurrentPlayer();
    updatePodium(currentPlayer);
  }

  private void updatePodium(Player currentPlayer) {
    Podiums.get(currentPlayer.getPlayerId()).update(currentPlayer.getFormattedScore());
  }

  private void updatePuzzle() {
    Phrase phrase = MyGame.getCurrentPhrase();

    if (phrase.isPuzzleSolved() || MyGame.isPuzzleSolved()) {
      showSolvedPuzzle();
    }

    pnlPuzzleRow1.removeAll();
    pnlPuzzleRow2.removeAll();
    pnlPuzzleRow3.removeAll();
    pnlPuzzleRow4.removeAll();

    int wordNum = 0;
    for (Word word : phrase.WordList) {
      wordNum++;
      if (phrase.WordCount <= 4)
        addWordToRow(word, wordNum, 1, phrase.WordCount);
      else if (phrase.WordCount <= 8)
        addWordToRow(word, wordNum, 2, phrase.WordCount);
      else
        addWordToRow(word, wordNum, 3, phrase.WordCount);
    }
  }

  private void addWordToRow(Word word, int wordNum, int wordsPerRow, int wordCount) {
    if (wordNum <= wordsPerRow) {
      putLettersInRow(pnlPuzzleRow1, word);

      if (wordNum < wordsPerRow)
        pnlPuzzleRow1.add(getSpaceBox());
    } else if (wordNum <= wordsPerRow * 2) {
      putLettersInRow(pnlPuzzleRow2, word);

      if (wordNum < wordsPerRow * 2)
        pnlPuzzleRow2.add(getSpaceBox());
    } else if (wordNum <= wordsPerRow * 3) {
      putLettersInRow(pnlPuzzleRow3, word);

      if (wordNum < wordsPerRow * 3)
        pnlPuzzleRow3.add(getSpaceBox());
    } else {
      putLettersInRow(pnlPuzzleRow4, word);

      if (wordNum < wordCount)
        pnlPuzzleRow4.add(getSpaceBox());
    }
  }

  private void showSolvedPuzzle() {
    GameSounds.playChant();
    updateGameStatusText(CurrentPlayerName + ", you win!");
    IsPuzzleSolved = true;
    btnSpin.setEnabled(false);
    btnBuyVowel.setEnabled(false);
    btnSolve.setEnabled(false);
  }

  private void putLettersInRow(JPanel pnl, Word word) {
    for (Letter letter : word.LetterList) {

      if (letter.IsVisible || IsPuzzleSolved || nonLettersSet.contains(letter.Value))
        pnl.add(getLetter(letter.Value));
      else
        pnl.add(getEmptyLetter());
    }
  }

  private JLabel getSpaceBox() {
    JLabel lbl = getLetterBox(Color.WHITE);
    return lbl;
  }

  private JLabel getEmptyLetter() {
    JLabel lbl = getLetterBox(Color.GREEN);
    return lbl;
  }

  private JLabel getLetter(Character letter) {
    JLabel lbl = getLetterBox(Color.CYAN);
    lbl.setText(letter.toString());
    return lbl;
  }

  private JLabel getLetterBox(Color color) {
    JLabel lbl = new JLabel();
    lbl.setOpaque(true);
    lbl.setBackground(color);
    lbl.setBorder(BorderFactory.createLineBorder(color));
    lbl.setPreferredSize(new Dimension(30, 35));
    lbl.setHorizontalAlignment(JLabel.CENTER);
    lbl.setFont(new Font("Verdana", Font.BOLD, 20));
    return lbl;
  }

  @Override
  public void GameChanged(GameUpdateEvent e) {
    updateGame();
  }
}
