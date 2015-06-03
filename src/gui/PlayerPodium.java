package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerPodium extends JPanel {
  private static final long serialVersionUID = 1L;

  private JLabel _nameLabel;
  private JLabel _scoreLabel;

  public PlayerPodium(String name) {
    this._nameLabel = getNameLabel(name);
    this._scoreLabel = new JLabel();

    _scoreLabel.setHorizontalAlignment(JLabel.CENTER);

    this.setLayout(new GridLayout(2, 1));
    this.setPreferredSize(new Dimension(90, 80));
    this.setBorder(BorderFactory.createBevelBorder(1));
    this.setBackground(Color.WHITE);

    this.add(_nameLabel);
    this.add(_scoreLabel);

    update("$0");
  }

  public void update(String message) {
    StringBuilder str = new StringBuilder();
    str.append("<html><h2>");
    str.append(message);
    str.append("</h2></html>");

    _scoreLabel.setText(str.toString());
  }

  private JLabel getNameLabel(String name) {
    JLabel lbl = new JLabel("<html><h2>" + name + "</h2></html>");
    lbl.setHorizontalAlignment(JLabel.CENTER);
    lbl.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
    return lbl;
  }
}
