package GameBoard;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class Wheel extends JComponent {
  private static final long serialVersionUID = 1L;

  private BufferedImage OriginalImage;
  private BufferedImage WheelImage;
  private BufferedImage PointerImage;

  private int WheelPosition;
  private int RandomSpinCount;

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

    if (PointerImage == null) {
      PointerImage = loadPointerImage();
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

  private BufferedImage loadPointerImage() {
    BufferedImage img = null;

    try {
      img = ImageIO.read(new File("img/pointer.png"));
    } catch (IOException ex) {
      System.out.println(ex);
    }

    return img;
  }

  public void paint(Graphics g) {
    AffineTransform trans = new AffineTransform();
    AffineTransform identity = new AffineTransform();
    trans.setTransform(identity);

    Graphics2D g2d = (Graphics2D) g;
    g2d.drawImage(WheelImage, trans, this);

    trans.rotate(Math.PI / 3, 320, 380);
    g2d.drawImage(PointerImage, trans, this);

    g2d.dispose();
  }

  public void spin() throws InterruptedException {
    GameSounds.playWheel();
    setRandomSpinCount();

    timer.start();
  }

  private void setWheelValues() {
    WheelMap.put(0, 3000); // pink
    WheelMap.put(1, 1600); // blue
    WheelMap.put(2, 1000); // aqua
    WheelMap.put(3, 3500); // yellow
    WheelMap.put(4, 1000); // red
    WheelMap.put(5, 2000); // purple
    WheelMap.put(6, 1000); // blue
    WheelMap.put(7, 2750); // yellow
    WheelMap.put(8, -2); // Lose Turn
    WheelMap.put(9, 4000); // gold
    WheelMap.put(10, 0); // bankrupt
    WheelMap.put(11, 1000); // green
    WheelMap.put(12, 2500); // pink
    WheelMap.put(13, 1400); // blue
    WheelMap.put(14, 1000); // aqua
    WheelMap.put(15, 2250); // yellow
    WheelMap.put(16, 1000); // red
    WheelMap.put(17, 3250); // purple
    WheelMap.put(18, 1800); // blue
    WheelMap.put(19, 1000); // golden rod
    WheelMap.put(20, 1200); // green
    WheelMap.put(21, 3750); // yellow
    WheelMap.put(22, 0); // bankrupt
    WheelMap.put(23, 1000); // green
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
    trans.rotate(theta, 200, 200);

    AffineTransformOp op = new AffineTransformOp(trans, AffineTransformOp.TYPE_BICUBIC);
    WheelImage = op.filter(WheelImage, null);

    repaint();
  }

  int i = 0;
  private Timer timer = new Timer(100, new ActionListener() {
    public void actionPerformed(ActionEvent event) {
      if (i <= RandomSpinCount) {
        rotate(Math.PI / 12);
        i++;
      } else {
        i = 0;
        timer.stop();
        WheelImage = OriginalImage;
        double rotation = (Math.PI / 12) * WheelPosition;
        rotate(rotation);
        RaiseStatusEvent(getWheelValue());
      }
    }
  });

  private void setRandomSpinCount() {
    Random random = new Random();
    RandomSpinCount = (int) (24 * random.nextDouble());

    int start = 10;
    long range = 20 - start + 1;
    long fraction = (long) (range * random.nextDouble());
    int randomNumber = (int) (fraction + start);
    RandomSpinCount = randomNumber;

    if ((WheelPosition + RandomSpinCount) > 23) {
      WheelPosition = RandomSpinCount - (23 - WheelPosition);
    } else {
      WheelPosition += RandomSpinCount;
    }
  }

  // Event handling methods
  public void AddStatusListener(GameListener listener) {
    GameListeners.add(listener);
  }

  public void RemoveStatusListener(GameListener listener) {
    GameListeners.remove(listener);
  }

  public void RaiseStatusEvent(int wheelValue) {
    for (GameListener gl : GameListeners) {
      gl.GameChanged(new GameUpdateEvent(wheelValue));
    }
  }
}
