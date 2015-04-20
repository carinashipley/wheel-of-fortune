package gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GameView extends JFrame {
  private static final long serialVersionUID = 1L;
  
  static BufferedImage wheelImg;

  JPanel pnlWheel = new JPanel();
  
  public GameView()
  {
    this.setLayout(new BorderLayout());    
    this.setSize(500, 500);
    this.setTitle("Wheel of Fortune");
    this.add(setupWheel(), "Center");
    
    
    if (wheelImg == null) {
      try {
        wheelImg = ImageIO.read(new File("img/BlackKnight.png"));
      } catch (IOException e) {
        wheelImg = new BufferedImage(45, 45, BufferedImage.TYPE_INT_ARGB);
        wheelImg.getGraphics().drawString("wheel", 0, 0);
      }
    }
  }
  
  public void draw(Graphics g, int X, int Y, int width, int height) {
      g.drawImage(wheelImg, X, Y, width, height, null);
  }
  
  

  private void spinWheel() {
    
    
    // The required drawing location
    int drawLocationX = 300;
    int drawLocationY = 300;

    // Rotation information

    double rotationRequired = Math.toRadians(45);
    // double locationX = image.getWidth() / 2;
    // double locationY = image.getHeight() / 2;
    // AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired,
    // locationX, locationY);
    // AffineTransformOp op = new AffineTransformOp(tx,
    // AffineTransformOp.TYPE_BILINEAR);

    // Drawing the rotated image at the required drawing locations
    // g2d.drawImage(op.filter(image, null), drawLocationX, drawLocationY,
    // null);
  }

  private JPanel setupWheel() {
JLabel lbl = new JLabel("Wheel of Fortune");
pnlWheel.add(lbl);
    
    return pnlWheel;
  }

}
