package GameBoard;

import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerQueue {
  public String PlayerName;
  public int Score;
  public JLabel ScoreLabel;
  public JPanel ScorePanel;

  public PlayerQueue(String name, JLabel lbl, JPanel pnl) {
    this.PlayerName = name;
    this.ScoreLabel = lbl;
    this.ScorePanel = pnl;
  }
  
  public void updateScore(String strScore)
  {
   int iScore = Integer.parseInt(strScore);
   updateScore(iScore);
  }
  
  public void updateScore(int score)
  {
    if(score == 0)
      Score = 0; // Bankrupt
    else
      Score += score;
    
    DecimalFormat myFormatter = new DecimalFormat("$###,###");
    ScoreLabel.setText(myFormatter.format(Score));
  }
  
  @Override
  public String toString()
  {
    return PlayerName + ": " + Score;
  }
}
