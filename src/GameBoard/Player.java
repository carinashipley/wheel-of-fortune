package GameBoard;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {
  static AtomicInteger nextId = new AtomicInteger();
  private int PlayerId;
  public String Name;
  public int Score;
  // public JLabel ScoreLabel;
  // public JPanel ScorePanel;

  private String ScoreDisplay;

  public Player(String name) {
    this.PlayerId = nextId.incrementAndGet();
    this.Name = name;
    this.Score = 0;
    this.ScoreDisplay = "$0";
  }

  public String getScore() {
    return ScoreDisplay;
  }
  
  public int getPlayerId()
  {
    return PlayerId;
  }

  public void updateScore(String strScore) {
    int iScore = Integer.parseInt(strScore);
    updateScore(iScore);
  }

  public void updateScore(int score) {
    if (score == 0)
      Score = 0; // Bankrupt
    else
      Score += score;

    DecimalFormat myFormatter = new DecimalFormat("$###,###");
    ScoreDisplay = myFormatter.format(Score);
    // ScoreLabel.setText(myFormatter.format(Score));
  }

  public void buyVowel() {
    if (Score >= 250) {
      Score -= 250;
    }
  }

  @Override
  public String toString() {
    return Name + ": " + ScoreDisplay;
  }
}
