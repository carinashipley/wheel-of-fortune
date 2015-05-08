package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.util.Map;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.*;

import GameBoard.GameListener;
import GameBoard.GameUpdateEvent;
import GameBoard.PlayerQueue;
import GameBoard.Wheel;
import sun.io.Converters;

public class GameView extends JFrame implements GameListener {
  private static final long serialVersionUID = 1L;

  private boolean IsLetterClickable = false;

  private Map<String, Boolean> lettersMap = new HashMap<String, Boolean>();
  private Queue<PlayerQueue> playerQueue = new LinkedList<PlayerQueue>();

  private static BufferedImage wheelImg;

  private JPanel pnlWheel = new JPanel();
  private JPanel pnlPhraseSpace = new JPanel();
  private JPanel pnlPlayer1 = new JPanel();
  private JPanel pnlPlayer2 = new JPanel();

  private JLabel lblWheelValue = new JLabel();
  private JLabel lblPlayer1Score = new JLabel("$0");
  private JLabel lblPlayer2Score = new JLabel("$0");
  private JLabel lblGameStatus = new JLabel("Welcome to Wheel of Fortune. Spin to begin.");

  private JButton btnSpin = new JButton("Spin");

  private Wheel wheel = new Wheel();
  private int wheelValue = 0;

  public GameView() {
    this.setLayout(new BorderLayout());
    this.setSize(700, 500);
    this.setTitle("Wheel of Fortune");

    pnlPhraseSpace.setLayout(new BorderLayout());
    pnlPhraseSpace.add(new JLabel("phrase"), "Center");
    pnlPhraseSpace.add(getLettersPanel(), "South");

    this.add(pnlPhraseSpace, "North");
    this.add(wheel, "Center");

    btnSpin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          JButton btn = (JButton) e.getSource();
          btn.setEnabled(false);
          
          wheel.spin();
          wheelValue = wheel.getWheelValue();
          changePlayer();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    });

    JPanel pnlSouth = new JPanel();
    pnlSouth.setLayout(new FlowLayout());

    pnlSouth.add(btnSpin);
    pnlSouth.add(lblWheelValue);
    pnlSouth.add(lblGameStatus);

    this.add(pnlSouth, "South");

    this.add(getPlayersPanel(), "East");
  }
  
  private void changePlayer()
  {
    System.out.println("changePlayer() wheelValue: " + wheelValue);
   // complete current player's turn and move to the next player        
    if (wheelValue == -2) {
      // Lose a Turn
      nextPlayer("Oh no! You lost your turn.",false);
    } else if (wheelValue == 0) {
      // Bankrupt
      PlayerQueue player = nextPlayer("Bankrupt! You lose all your money.",true);
      player.updateScore(wheelValue);
    } else {
      lblWheelValue.setText(Integer.toString(wheel.getWheelValue()));
      lblGameStatus.setText("Select a letter.");
      IsLetterClickable = true;
    }
  }

  private PlayerQueue nextPlayer(String message, boolean updateScore) {
    PlayerQueue currentPlayer = playerQueue.remove();
    playerQueue.add(currentPlayer);
    currentPlayer.ScorePanel.setBackground(Color.WHITE);
    
    PlayerQueue nextPlayer = playerQueue.peek();
    lblGameStatus.setText(message + " " + "It is " + nextPlayer.PlayerName + "'s turn.");
    nextPlayer.ScorePanel.setBackground(Color.YELLOW);
        
    btnSpin.setEnabled(true);
    
    if(updateScore){
      currentPlayer.updateScore(wheelValue);
    }
    
    return currentPlayer;
  }

  private JPanel getPlayersPanel() {
    JPanel pnl = new JPanel();

    pnl.setLayout(new FlowLayout());

    JPanel player1 = getPlayerPanel("Bob", lblPlayer1Score, pnlPlayer1);
    JPanel player2 = getPlayerPanel("Buster", lblPlayer2Score, pnlPlayer2);
    
    player1.setBackground(Color.YELLOW);

    System.out.println(playerQueue.toString());

    pnl.add(player1);
    pnl.add(player2);

    return pnl;
  }

  private JPanel getPlayerPanel(String name, JLabel lbl, JPanel pnl) {
    pnl.add(getPlayerLabel(name));
    pnl.add(lbl);
    pnl.setBorder(BorderFactory.createBevelBorder(1));
    pnl.setBackground(Color.WHITE);

    playerQueue.add(new PlayerQueue(name, lbl, pnl));

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

    lettersMap.put(letter, true);

    btn.setMaximumSize(new Dimension(8, 10));
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
          lettersMap.put(btn.getText(), false);

          nextPlayer("",true);

          System.out.println(btn.getText() + " was clicked");
          System.out.println(lettersMap.toString());
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
