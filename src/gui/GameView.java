package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
import sun.io.Converters;

public class GameView extends JFrame implements GameListener {
  private static final long serialVersionUID = 1L;

  private Game MyGame;

  private boolean IsLetterClickable = false;

  private Map<String, Boolean> LettersMap = new HashMap<String, Boolean>();
  private Map<Integer, PlayerPodium> Podiums = new HashMap<Integer, PlayerPodium>();

  private static BufferedImage wheelImg;

  private JPanel pnlWheel = new JPanel();
  private JPanel pnlPuzzle = new JPanel();
  private JPanel pnlPuzzleRow1 = new JPanel();
  private JPanel pnlPuzzleRow2 = new JPanel();
  private JPanel pnlPuzzleRow3 = new JPanel();
  private JPanel pnlPlayer1 = new JPanel();
  private JPanel pnlPlayer2 = new JPanel();

  private JLabel lblWheelValue = new JLabel();
  private JLabel lblPlayer1Score = new JLabel("$0");
  private JLabel lblPlayer2Score = new JLabel("$0");
  private JLabel lblGameStatus = new JLabel("Welcome to Wheel of Fortune. Spin to begin.");
  private JLabel lblPhrase = new JLabel();
  private JLabel lblCategory = new JLabel();

  private JButton btnSpin = new JButton("Spin");
  private JButton btnBuyVowel = new JButton("Buy a Vowel");
  private JButton btnSolve = new JButton("Solve the Puzzle");

  private Wheel wheel = new Wheel();
  private int WheelValue = 0;
  private int LetterCount = 0;
  private String CurrentPlayerName;

  public GameView() {
    MyGame = new Game();
    MyGame.start();

    Phrase phrase = MyGame.getRandomPhrase();

    lblCategory.setHorizontalAlignment(JLabel.CENTER);
    lblCategory.setText("<html><h2>" + phrase.Category.toUpperCase() + "</h2></html>");

    pnlPuzzle.setOpaque(true);
    pnlPuzzle.setBackground(Color.BLACK);
    updatePuzzle();

    pnlPuzzle.setLayout(new GridLayout(3, 1));
    pnlPuzzle.setBorder(BorderFactory.createRaisedBevelBorder());
    pnlPuzzle.add(pnlPuzzleRow1);
    pnlPuzzle.add(pnlPuzzleRow2);
    pnlPuzzle.add(pnlPuzzleRow3);

    System.out.println("game started");

    this.setLayout(new BorderLayout());
    this.setSize(700, 500);
    this.setTitle("Wheel of Fortune");

    JPanel pnlTop = new JPanel();
    pnlTop.setLayout(new BorderLayout());
    pnlTop.add(lblCategory, "North");
    pnlTop.add(pnlPuzzle, "Center");
    pnlTop.add(getLettersPanel(), "South");

    this.add(pnlTop, "North");
    this.add(wheel, "Center");

    btnSpin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          JButton btn = (JButton) e.getSource();
          btn.setEnabled(false);

          wheel.spin();
          WheelValue = wheel.getWheelValue();
          updateGame();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    });

    JPanel pnlSouth = new JPanel();
    pnlSouth.setLayout(new FlowLayout());

    pnlSouth.add(btnSpin);
    pnlSouth.add(btnBuyVowel);
    pnlSouth.add(btnSolve);
    pnlSouth.add(lblWheelValue);
    pnlSouth.add(lblGameStatus);

    this.add(pnlSouth, "South");

    this.add(getPlayersPanel(), "East");
  }

  private void updateGame() {
    System.out.println("updateGame() wheelValue: " + WheelValue);
    if (WheelValue == -2) {
      // Lose a Turn
      moveToNextPlayer("Oh no! You lost your turn.", false);
    } else if (WheelValue == 0) {
      doBankrupt();
    } else {
      Player currentPlayer = MyGame.getCurrentPlayer();
      lblWheelValue.setText(Integer.toString(wheel.getWheelValue()));
      lblGameStatus.setText(currentPlayer.Name + " needs to select a letter.");
      IsLetterClickable = true;
    }
  }
  
  private void doBankrupt(){
 // Bankrupt
    System.out.println("Bankrupt - WheelValue: " + WheelValue + ", LetterCount: " + LetterCount);
    moveToNextPlayer("Bankrupt! Sorry, " + CurrentPlayerName + ", you lose all your money.", true);
  }

  private void moveToNextPlayer(String message, boolean updateScore) {
    Player currentPlayer = MyGame.getCurrentPlayer();

    // update current player's podium
    PlayerPodium currentPodium = Podiums.get(currentPlayer.getPlayerId());
    currentPodium.PodiumPanel.setBackground(Color.WHITE);

    // move to next player and make him/her the current player
    Player nextPlayer = MyGame.getNextPlayer();
    CurrentPlayerName = nextPlayer.Name;
    System.out.println("CurrentPlayerName: " + CurrentPlayerName);
    
    PlayerPodium nextPodium = Podiums.get(nextPlayer.getPlayerId());
    lblGameStatus.setText(message + " " + "It is " + CurrentPlayerName + "'s turn.");
    nextPodium.PodiumPanel.setBackground(Color.YELLOW);

    btnSpin.setEnabled(true);

    if (updateScore) {
      System.out.println("WheelValue: " + WheelValue + ", LetterCount: " + LetterCount);
      updateScore();
    }

    MyGame.moveToNextPlayer();
  }

  private JPanel getPlayersPanel() {
    JPanel pnl = new JPanel();

    pnl.setLayout(new FlowLayout());

    List<Player> players = MyGame.getPlayers();
    Player p1 = players.get(0);
    CurrentPlayerName = p1.Name;
    JPanel player1 = getPlayerPanel(p1, lblPlayer1Score, pnlPlayer1);
    JPanel player2 = getPlayerPanel(players.get(1), lblPlayer2Score, pnlPlayer2);

    player1.setBackground(Color.YELLOW);

    pnl.add(player1);
    pnl.add(player2);

    return pnl;
  }

  private JPanel getPlayerPanel(Player player, JLabel lbl, JPanel pnl) {
    pnl.add(getPlayerLabel(player.Name));
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

    if (isVowel)
      btn.setForeground(Color.BLUE);

    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (IsLetterClickable) {
          JButton btn = (JButton) e.getSource();
          btn.setEnabled(false);
          IsLetterClickable = false;
          btnSpin.setEnabled(true);
          LettersMap.put(btn.getText(), false);

          LetterCount = MyGame.updatePhrase(btn.getText());
          updatePuzzle();

          if (LetterCount == 0) {
            moveToNextPlayer("No " + btn.getText() + ".", false);
          } else {
            updateScore();
            lblGameStatus.setText(CurrentPlayerName + " needs to spin again.");
          }

          System.out.println(btn.getText() + " was clicked");
          // System.out.println(lettersMap.toString());
        } else {
          System.out.println("need to spin before picking a letter");
        }
      }
    });

    return btn;
  }

  public void draw(Graphics g, int X, int Y, int width, int height) {
    g.drawImage(wheelImg, X, Y, width, height, null);
  }

  private void updateScore() {
    System.out.println("update score!");
    MyGame.updatePlayer(WheelValue, LetterCount);
    Player currentPlayer = MyGame.getCurrentPlayer();
    Podiums.get(currentPlayer.getPlayerId()).ScoreLabel.setText(currentPlayer.getScore());
  }

  private void updatePuzzle() {
    pnlPuzzleRow1.removeAll();
    pnlPuzzleRow2.removeAll();
    pnlPuzzleRow3.removeAll();

    Phrase phrase = MyGame.getCurrentPhrase();

    int wordCount = 0;

    pnlPuzzleRow1.add(getSpaceBox());
    pnlPuzzleRow2.add(getSpaceBox());
    pnlPuzzleRow3.add(getSpaceBox());
    for (Word word : phrase.WordList) {
      wordCount++;
      if (wordCount <= 2 && wordCount <= phrase.WordCount)
        putLettersInRow(pnlPuzzleRow1, word);
      else if (wordCount <= 4 && wordCount <= phrase.WordCount)
        putLettersInRow(pnlPuzzleRow2, word);
      else
        putLettersInRow(pnlPuzzleRow3, word);
    }
    pnlPuzzleRow1.add(getSpaceBox());
    pnlPuzzleRow2.add(getSpaceBox());
    pnlPuzzleRow3.add(getSpaceBox());
  }

  private void putLettersInRow(JPanel pnl, Word word) {

    for (Letter letter : word.LetterList) {
      if (letter.IsVisible)
        pnl.add(getLetter(letter.Value));
      else
        pnl.add(getEmptyLetter());
    }
    pnl.add(getSpaceBox());
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

  private JPanel setupWheel() {
    JLabel lbl = new JLabel("Wheel of Fortune");
    pnlWheel.add(lbl);

    return pnlWheel;
  }

  @Override
  public void GameChanged(GameUpdateEvent e) {
    lblWheelValue.setText(Integer.toString(e.WheelValue));
  }

}
