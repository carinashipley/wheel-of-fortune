package gui;

import javax.swing.JButton;

public class LetterButton {
  public boolean IsVowel;
  public boolean IsUsed;
  public String Value;
  public JButton Button;

  public LetterButton(String value, JButton btn) {
    this(value, btn, false);
  }

  public LetterButton(String value, JButton btn, boolean isVowel) {
    IsVowel = isVowel;
    IsUsed = false;
    Value = value;
    Button = btn;
  }
}
