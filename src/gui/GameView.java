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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;

import GameBoard.Game;
import GameBoard.GameListener;
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
  private Map<Integer, PlayerPodium> Podiums = new HashMap<Integer, PlayerPodium>();
  private Map<String, LetterButton> ConsonantButtons = new HashMap<String, LetterButton>();
  private Map<String, LetterButton> VowelButtons = new HashMap<String, LetterButton>();

  private JPanel pnlPuzzle = new JPanel();
  private JPanel pnlPuzzleRow1 = new JPanel();
  private JPanel pnlPuzzleRow2 = new JPanel();
  private JPanel pnlPuzzleRow3 = new JPanel();
  private JPanel pnlPlayer1 = new JPanel();
  private JPanel pnlPlayer2 = new JPanel();

  private JLabel lblWheelValue = new JLabel();
  private JLabel lblPlayer1Score = new JLabel("$0");
  private JLabel lblPlayer2Score = new JLabel("$0");
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
    this.setSize(700, 500);
    this.setTitle("Wheel of Fortune");
    this.setLocation(50, 50);

    BufferedImage img = null;
    try {
      File f = new File("img/background_1.jpg");
      img = ImageIO.read(f);
      System.out.println("File " + f.toString());
    } catch (Exception e) {
      System.out.println("Cannot read file: " + e);
    }

    BackgroundPanel background = new BackgroundPanel(img, BackgroundPanel.ACTUAL, 0.50f, 0.5f);
    this.setContentPane(background);

    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Menu");
    menuBar.add(menu);
    JMenuItem item = new JMenuItem("Exit");
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    menu.add(item);
    // this.setJMenuBar(menuBar);

    MyGame = new Game();
    MyGame.start();

    doInitialGameState();

    Phrase phrase = MyGame.getRandomPhrase();

    lblCategory.setHorizontalAlignment(JLabel.CENTER);
    // lblCategory.setBackground(Color.WHITE);
    lblCategory.setOpaque(false);
    // lblCategory.setPreferredSize(new Dimension(50,50));
    lblCategory.setText("<html><div style='background-color:white;text-align:center;border:solid 2px black;padding:0 10px;'><h2>"
        + phrase.Category.toUpperCase() + "</h2></div></html>");

    updatePuzzle();

    pnlPuzzleRow1.setOpaque(false);
    pnlPuzzleRow2.setOpaque(false);
    pnlPuzzleRow3.setOpaque(false);

    pnlPuzzle.setLayout(new GridLayout(3, 1));
    pnlPuzzle.setOpaque(false);
    pnlPuzzle.add(pnlPuzzleRow1);
    pnlPuzzle.add(pnlPuzzleRow2);
    pnlPuzzle.add(pnlPuzzleRow3);

    JPanel pnlTop = new JPanel();
    pnlTop.setOpaque(false);
    pnlTop.setLayout(new BorderLayout());
    pnlTop.add(lblCategory, "North");
    pnlTop.add(pnlPuzzle, "Center");
    pnlTop.add(getLettersPanel(), "South");
    this.add(pnlTop, "North");

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
          updateGame();
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
          if (isSolved)
            showSolvedPuzzle();
          else
            moveToNextPlayer("Incorrect guess. ", false);
        }
      }
    });

    JPanel pnlSouth = new JPanel();
    pnlSouth.setOpaque(false);
    pnlSouth.setLayout(new FlowLayout());

    pnlSouth.add(btnSpin);
    pnlSouth.add(btnBuyVowel);
    pnlSouth.add(btnSolve);
    pnlSouth.add(lblWheelValue);

    this.add(pnlSouth, "South");

    this.add(getPlayersPanel(), "East");
  }

  private void updateGame() {
    System.out.println("updateGame() wheelValue: " + WheelValue);
    if (WheelValue == -2) {
      // Lose a Turn
      moveToNextPlayer("Oh no! You lost your turn.", false);
    } else if (WheelValue == 0) {
      // Bankrupt
      System.out.println("Bankrupt - WheelValue: " + WheelValue + ", LetterCount: " + LetterCount);
      moveToNextPlayer("Bankrupt! Sorry, " + CurrentPlayerName + ", you lose all your money.", true);
    } else {
      Player currentPlayer = MyGame.getCurrentPlayer();
      lblWheelValue.setText(Integer.toString(wheel.getWheelValue()));
      updateGameStatusText(currentPlayer.getName() + " needs to select a letter.");
      doAfterSpinState();
    }
  }

  private void doInitialGameState() {
    btnSpin.setEnabled(true);
    btnBuyVowel.setEnabled(false);
    IsLetterClickable = false;
    updateGameStatusText("Welcome to Wheel of Fortune. Spin to begin.");

    for (char c : nonLetters)
      nonLettersSet.add(c);
  }

  private void doNewTurnState() {
    System.out.println("doNewTurnState()");
    btnSpin.setEnabled(true);
    IsLetterClickable = false;
    setBuyVowelBtn();
    setLetterBtns();
  }

  private void doSameTurnState() {
    btnSpin.setEnabled(true);
    IsLetterClickable = false;
    setBuyVowelBtn();
    setLetterBtns();
  }

  private void doAfterSpinState() {
    btnSpin.setEnabled(false);
    IsLetterClickable = true;
    btnBuyVowel.setEnabled(false);
    setLetterBtns();
  }

  private void doBuyVowelState() {
    for (String vowel : VowelButtons.keySet()) {
      LetterButton lb = VowelButtons.get(vowel);
      if (lb.IsUsed)
        lb.Button.setEnabled(false);
      else
        lb.Button.setEnabled(true);
    }
  }

  private void setBuyVowelBtn() {
    Player currentPlayer = MyGame.getCurrentPlayer();
    btnBuyVowel.setEnabled(currentPlayer.getScore() >= 250);
  }

  private void setLetterBtns() {
    for (String letter : ConsonantButtons.keySet()) {
      LetterButton lb = ConsonantButtons.get(letter);
      if (lb.IsUsed)
        lb.Button.setEnabled(false);
      else
        lb.Button.setEnabled(IsLetterClickable);
    }
  }

  private void updateVowelButtons(boolean isVowelClickable) {
    for (String vowel : VowelButtons.keySet()) {
      LetterButton lb = VowelButtons.get(vowel);
      if (lb.IsUsed)
        lb.Button.setEnabled(false);
      else
        lb.Button.setEnabled(isVowelClickable);
    }
  }

  private void updateGameStatusText(String message) {
    StringBuilder str = new StringBuilder();
    str.append("<html><h2 style='color:white'>");
    str.append(message);
    str.append("</h2></html>");
    lblGameStatus.setText(str.toString());
  }

  private void moveToNextPlayer(String message, boolean updateScore) {
    Player currentPlayer = MyGame.getCurrentPlayer();

    if (updateScore) {
      updateScore();
    }

    // update current player's podium
    PlayerPodium currentPodium = Podiums.get(currentPlayer.getPlayerId());
    currentPodium.PodiumPanel.setBackground(Color.WHITE);

    // move to next player and make him/her the current player
    Player nextPlayer = MyGame.moveToNextPlayer();
    CurrentPlayerName = nextPlayer.getName();

    PlayerPodium nextPodium = Podiums.get(nextPlayer.getPlayerId());
    updateGameStatusText(message + " " + "It is " + CurrentPlayerName + "'s turn.");
    nextPodium.PodiumPanel.setBackground(Color.YELLOW);

    doNewTurnState();
  }

  private JPanel getPlayersPanel() {
    JPanel pnl = new JPanel();

    pnl.setLayout(new FlowLayout());

    List<Player> players = MyGame.getPlayers();
    Player p1 = players.get(0);
    CurrentPlayerName = p1.getName();
    JPanel player1 = getPlayerPanel(p1, lblPlayer1Score, pnlPlayer1);
    JPanel player2 = getPlayerPanel(players.get(1), lblPlayer2Score, pnlPlayer2);

    player1.setBackground(Color.YELLOW);

    pnl.add(player1);
    pnl.add(player2);

    return pnl;
  }

  private JPanel getPlayerPanel(Player player, JLabel lbl, JPanel pnl) {
    pnl.add(getPlayerLabel(player.getName()));
    pnl.add(lbl);
    pnl.setBorder(BorderFactory.createBevelBorder(1));
    pnl.setBackground(Color.WHITE);

    Podiums.put(player.getPlayerId(), new PlayerPodium(lbl, pnl));

    return pnl;
  }

  private JLabel getPlayerLabel(String name) {
    JLabel lbl = new JLabel(name);
    return lbl;
  }

  private JPanel getLettersPanel() {
    JPanel pnlLetters = new JPanel();
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
    btn.setFont(btn.getFont().deriveFont(18.0f));
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
        JButton btn = (JButton) e.getSource();
        String vowel = btn.getText();
        VowelButtons.get(vowel).IsUsed = true;

        LettersMap.put(vowel, false);

        LetterCount = MyGame.updatePhrase(vowel);
        updatePuzzle();

        updateVowelButtons(false);

        if (LetterCount == 0) {
          moveToNextPlayer("Sorry, no " + btn.getText() + ".", false);
          doNewTurnState();
        } else {
          updateGameStatusText(CurrentPlayerName + " needs to go again.");
          doSameTurnState();
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

          if (LetterCount == 0) {
            moveToNextPlayer("No " + btn.getText() + ".", false);
            doNewTurnState();
          } else {
            updateScore();
            updateGameStatusText(CurrentPlayerName + " needs to spin again.");
            doSameTurnState();
          }

          System.out.println(btn.getText() + " was clicked");
          // System.out.println(lettersMap.toString());
        } else {
          System.out.println("need to spin before picking a letter");
        }
      }
    };
  }

  private void buyVowel() {
    System.out.println("buyVowel()");

    int answer = JOptionPane.showConfirmDialog(null, "Would you like to buy a vowel for $250?", "Buy a vowel?",
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
    System.out.println("updateScore()");
    Player currentPlayer = MyGame.getCurrentPlayer();
    currentPlayer.updateScore(WheelValue * LetterCount);
    Podiums.get(currentPlayer.getPlayerId()).ScoreLabel.setText(currentPlayer.getFormattedScore());
  }

  private void updatePodium() {
    Player currentPlayer = MyGame.getCurrentPlayer();
    Podiums.get(currentPlayer.getPlayerId()).ScoreLabel.setText(currentPlayer.getFormattedScore());
  }

  private void updatePuzzle() {
    pnlPuzzleRow1.removeAll();
    pnlPuzzleRow2.removeAll();
    pnlPuzzleRow3.removeAll();

    Phrase phrase = MyGame.getCurrentPhrase();

    pnlPuzzleRow1.add(getSpaceBox());
    pnlPuzzleRow2.add(getSpaceBox());

    if (phrase.WordCount >= 3)
      pnlPuzzleRow3.add(getSpaceBox());

    int wordNum = 0;
    for (Word word : phrase.WordList) {
      wordNum++;
      if (phrase.WordCount <= 3)
        addWordToRow(word, wordNum, 1, phrase.WordCount);
      else if (phrase.WordCount <= 6)
        addWordToRow(word, wordNum, 2, phrase.WordCount);
      else
        addWordToRow(word, wordNum, 3, phrase.WordCount);
    }

    pnlPuzzleRow1.add(getSpaceBox());

    if (phrase.WordCount >= 2)
      pnlPuzzleRow2.add(getSpaceBox());

    if (phrase.WordCount >= 3)
      pnlPuzzleRow3.add(getSpaceBox());
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
    } else {
      putLettersInRow(pnlPuzzleRow3, word);

      if (wordNum < wordCount)
        pnlPuzzleRow3.add(getSpaceBox());
    }
  }

  private void showSolvedPuzzle() {
    updateGameStatusText(CurrentPlayerName + ", you win!");
    IsPuzzleSolved = true;
    updatePuzzle();
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
    lbl.setFont(new Font("Tahoma", Font.BOLD, 20));
    return lbl;
  }

  @Override
  public void GameChanged(GameUpdateEvent e) {
    lblWheelValue.setText(Integer.toString(e.WheelValue));
  }

}
