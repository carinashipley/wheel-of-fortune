package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import GameBoard.GameListener;
import GameBoard.GameUpdateEvent;
import GameBoard.Wheel;
import sun.io.Converters;

public class GameView extends JFrame implements GameListener {
  private static final long serialVersionUID = 1L;

  private static BufferedImage wheelImg;
  private JPanel pnlWheel = new JPanel();
  private JPanel pnlPhraseSpace = new JPanel();
  private JLabel lblWheelValue = new JLabel();
  private JLabel lblPlayer1Score = new JLabel("0");
  
  private Wheel wheel = new Wheel();

  public GameView() {
    this.setLayout(new BorderLayout());
    this.setSize(500, 500);
    this.setTitle("Wheel of Fortune");
    this.add(wheel, "Center");

    JButton btnSpin = new JButton("Spin");
    btnSpin.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          wheel.spin();
          lblPlayer1Score.setText(Integer.toString(wheel.getWheelValue()));
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    });
    
    
    
    JPanel pnlSouth = new JPanel();
    pnlSouth.setLayout(new FlowLayout());
    
    pnlSouth.add(btnSpin);
    pnlSouth.add(lblPlayer1Score);
    pnlSouth.add(lblWheelValue);
    
    this.add(pnlSouth,"South");
    
    this.add(lblPlayer1Score,"East");
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
