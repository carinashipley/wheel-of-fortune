package GameBoard;

import java.util.ArrayList;
import java.util.List;

public class Word {
  public List<Letter> LetterList = new ArrayList<Letter>();
  public String Value;
  public int CharCount;

  public Word(String word) {
    this.Value = word;
    this.CharCount = word.length();

    char[] chars = new char[CharCount];
    word.getChars(0, CharCount, chars, 0);

    if (chars != null) {
      for (char c : chars) {
        LetterList.add(new Letter(c));
      }
    }
  }
  
  @Override
  public String toString(){
    return this.Value;
  }
}
