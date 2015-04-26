package GameBoard;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
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

  private BufferedImage WheelImage;
  private int WheelPosition;
  private int RandomSpinCount;
  private AffineTransform identity = new AffineTransform();
  private Map<Integer, Integer> WheelMap = new HashMap<Integer, Integer>();
  private Set<GameListener> GameListeners = new HashSet<GameListener>();

  public Wheel() {
    setWheelValues();

    try {
      WheelImage = ImageIO.read(new File("img/Wheel2.png"));
    } catch (IOException ex) {
      System.out.println(ex);
    }
  }

  public void paint(Graphics g) {
    AffineTransform trans = new AffineTransform();
    trans.setTransform(identity);
    trans.scale(.5, .5);

    Graphics2D g2d = (Graphics2D) g;
    g2d.drawImage(WheelImage, trans, this);
  }

  public void spin() throws InterruptedException {
    setRandomSpinCount();
    timer.start();
  }

  public BufferedImage getImage() {
    return WheelImage;
  }

  private void setWheelValues() {
    WheelMap.put(1, 1000); // red
    WheelMap.put(2, 3500); // yellow
    WheelMap.put(3, 1000); // aqua
    WheelMap.put(4, 1600); // blue
    WheelMap.put(5, 3000); // pink
    WheelMap.put(6, 1000); // green
    WheelMap.put(7, 0); // bankrupt
    WheelMap.put(8, 3750); // yellow
    WheelMap.put(9, 1200); // green
    WheelMap.put(10, 1000); // golden rod
    WheelMap.put(11, 1800); // blue
    WheelMap.put(12, 3250); // purple
    WheelMap.put(13, 1000); // red
    WheelMap.put(14, 2250); // yellow
    WheelMap.put(15, 1000); // aqua
    WheelMap.put(16, 1400); // blue
    WheelMap.put(17, 2500); // pink
    WheelMap.put(18, 1000); // green
    WheelMap.put(19, 0); // bankrupt
    WheelMap.put(20, 4000); // gold
    WheelMap.put(21, -1); // Lose Turn
    WheelMap.put(22, 2750); // yellow
    WheelMap.put(23, 1000); // blue
    WheelMap.put(24, 2000); // purple
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
    AffineTransform trans = new AffineTransform();
    trans.rotate(theta, 404, 400);

    AffineTransformOp op = new AffineTransformOp(trans, AffineTransformOp.TYPE_BICUBIC);
    WheelImage = op.filter(WheelImage, null);

    repaint();
  }

  int i = 0;
  private Timer timer = new Timer(100, new ActionListener() {
    public void actionPerformed(ActionEvent event) {
      // System.out.println(RandomSpinCount);
      // System.out.println("i: " + i);

      if (i < RandomSpinCount) {
        // if (i > RandomSpinCount - 4)
        // rotate(.1);
        // else if (i > RandomSpinCount - 6)
        // rotate(.2);
        // else if (i > RandomSpinCount - 8)
        // rotate(.3);
        // else
        rotate(1.8);

        i++;

        repaint();
      } else {
        i = 0;
        timer.stop();
        RaiseStatusEvent();
        // RaiseEvent
      }
    }
  });

  private void setRandomSpinCount() {
    Random random = new Random();
    long fraction = (long) (12 * random.nextDouble());
    RandomSpinCount = (int) fraction + 11;

    if ((WheelPosition + RandomSpinCount) > 24) {
      WheelPosition = RandomSpinCount - (24 - WheelPosition);
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
