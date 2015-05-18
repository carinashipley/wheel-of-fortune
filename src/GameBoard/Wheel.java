package GameBoard;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

public class Wheel extends JComponent implements MouseInputListener {
  private static final long serialVersionUID = 1L;

  private BufferedImage OriginalImage;
  private BufferedImage WheelImage;
  
  private int WheelPosition;
  private int RandomSpinCount;
  
  private AffineTransform identity = new AffineTransform();
  private AffineTransform trans = new AffineTransform();
  
  private Map<Integer, Integer> WheelMap = new HashMap<Integer, Integer>();
  private Set<GameListener> GameListeners = new HashSet<GameListener>();

  public Wheel() {
    setWheelValues();

    if (OriginalImage == null) {
      OriginalImage = loadImage();
    }

    if (WheelImage == null) {
      WheelImage = loadImage();
    }
  }

  private BufferedImage loadImage() {
    BufferedImage img = null;

    try {
      img = ImageIO.read(new File("img/Wheel2.png"));
    } catch (IOException ex) {
      System.out.println(ex);
    }

    return img;
  }

  public void paint(Graphics g) {
    trans.setTransform(identity);

    Ellipse2D ellipse = new Ellipse2D.Float();
    ellipse.setFrame(90, 0, 220, 220);
    
    Graphics2D g2d = (Graphics2D) g;
    //g2d.setClip(ellipse);
    g2d.drawImage(WheelImage, trans, this);
    g2d.dispose();
  }

  public void spin() throws InterruptedException {
    setRandomSpinCount();

    timer.start();
  }

  private void setWheelValues() {
    WheelMap.put(0, 2000); // purple
    WheelMap.put(1, 1000); // blue
    WheelMap.put(2, 2750); // yellow
    WheelMap.put(3, -2); // Lose Turn
    WheelMap.put(4, 4000); // gold
    WheelMap.put(5, 0); // bankrupt
    WheelMap.put(6, 1000); // green
    WheelMap.put(7, 2500); // pink
    WheelMap.put(8, 1400); // blue
    WheelMap.put(9, 1000); // aqua
    WheelMap.put(10, 2250); // yellow
    WheelMap.put(11, 1000); // red
    WheelMap.put(12, 3250); // purple
    WheelMap.put(13, 1800); // blue
    WheelMap.put(14, 1000); // golden rod
    WheelMap.put(15, 1200); // green
    WheelMap.put(16, 3750); // yellow
    WheelMap.put(17, 0); // bankrupt
    WheelMap.put(18, 1000); // green
    WheelMap.put(19, 3000); // pink
    WheelMap.put(20, 1600); // blue
    WheelMap.put(21, 1000); // aqua
    WheelMap.put(22, 3500); // yellow
    WheelMap.put(23, 1000); // red
  }

  public int getWheelValue() {
    // 24 pie slices in the wheel
    // 360 / 24 = 15
    // each slice is equal to 15 degrees

    int mapKey = WheelPosition;

    if (!WheelMap.containsKey(mapKey))
      return -1;

    return WheelMap.get(mapKey);
  }

  private void rotate(double theta) {
    //System.out.println(WheelImage.getHeight());
    AffineTransform trans = new AffineTransform();
    trans.rotate(theta, 200, 200);

    AffineTransformOp op = new AffineTransformOp(trans, AffineTransformOp.TYPE_BICUBIC);
    WheelImage = op.filter(WheelImage, null);

    repaint();
  }

  int i = 0;
  private Timer timer = new Timer(100, new ActionListener() {
    public void actionPerformed(ActionEvent event) {
      // System.out.println(RandomSpinCount);
      // System.out.println("i: " + i);

      if (i <= RandomSpinCount) {
        rotate(Math.PI / 12);
        i++;
      } else {
        i = 0;
        timer.stop();
        System.out.println("timer stopped");
        WheelImage = OriginalImage;
        double rotation = (Math.PI/12)*WheelPosition;
        rotate(rotation);
        // RaiseStatusEvent();
        // RaiseEvent
      }
    }
  });

  private void setRandomSpinCount() {
    Random random = new Random();
    RandomSpinCount = (int) (24 * random.nextDouble());
    
    int start = 10;
    
    long range = 20 - start + 1;
    long fraction = (long)(range * random.nextDouble());
    int randomNumber =  (int)(fraction + start);    
    RandomSpinCount = randomNumber;
    
    System.out.println(randomNumber);

    if ((WheelPosition + RandomSpinCount) > 23) {
      WheelPosition = RandomSpinCount - (23 - WheelPosition);
    } else {
      WheelPosition += RandomSpinCount;
    }

    System.out.println("WheelPosition: " + WheelPosition);
    System.out.println("RandomSpinCount: " + RandomSpinCount);
  }

  // Event handling methods
  public void AddStatusListener(GameListener listener) {
    GameListeners.add(listener);
  }

  public void RemoveStatusListener(GameListener listener) {
    GameListeners.remove(listener);
  }

  public void RaiseStatusEvent(GameListener listener) {
    // for (GameListener listener : GameListeners) {
    // listener.BoardStatusChanged(new GameUpdateEvent());
    // }
  }

  public void RaiseStatusEvent() {

    // for (BoardstatusListener Listener : p_Status_Listeners) {
    // Listener.BoardStatusChanged(new BoardStatusEvent(Square,
    // p_OccupiedSquares.size(), pb_status, message));
    // }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mousePressed(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseMoved(MouseEvent e) {
    // TODO Auto-generated method stub

  }
}
