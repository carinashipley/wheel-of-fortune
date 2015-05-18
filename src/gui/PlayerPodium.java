package gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerPodium {
public JLabel ScoreLabel;
public JPanel PodiumPanel;

public PlayerPodium(JLabel lbl, JPanel pnl)
{
  this.ScoreLabel = lbl;
  this.PodiumPanel = pnl;
}
}
