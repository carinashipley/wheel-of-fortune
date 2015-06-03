package GameBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Phrase {
  public int RowCount;
  public int WordCount;

  public String Category;
  public String Value;

  public List<Word> WordList = new ArrayList<Word>();

  private Map<Integer, Integer> WordsPerRow = new HashMap<Integer, Integer>();

  public Phrase(String category, String phrase) {
    this.Category = category;
    this.Value = phrase;

    int chars = phrase.length();

    String[] words = phrase.split(" ");
    this.WordCount = words.length;

    int spacesCount = WordCount - 1;
    int rowSize = (chars - spacesCount) / 2;

    int charCount = 0;
    for (String s : words) {
      WordList.add(new Word(s));

      if (charCount < rowSize && charCount + s.length() <= rowSize) {
        charCount += s.length();
      }
    }
  }

  public int getWordCountByRow(int rowNumber) {
    if (rowNumber > RowCount)
      throw new NoSuchElementException("Phrase does not have rowNumber: " + rowNumber);

    return WordsPerRow.get(rowNumber);
  }

  public int getCharCountByRow(int rowNumber) {
    if (rowNumber > RowCount)
      throw new NoSuchElementException("Phrase does not have rowNumber: " + rowNumber);

    return WordsPerRow.get(rowNumber);
  }

  public List<Letter> getLetters() {
    List<Letter> list = new ArrayList<Letter>();

    for (Word word : WordList) {
      list.addAll(word.LetterList);
    }

    return list;
  }

  public int turnLetter(String letter) {
    char c = letter.charAt(0);
    
    int count = 0;
    for (Word w : WordList) {
      for (Letter l : w.LetterList) {
        if (l.Value == c) {
          l.IsVisible = true;
          count++;
        }
      }
    }

    return count;
  }

  public boolean isPuzzleSolved() {
    boolean hasHiddenLetters = false;
    for (Word w : WordList) {
      for (Letter l : w.LetterList) {
        if (!l.IsVisible) {
          // there's still at least one letter that isn't turned
          hasHiddenLetters = true;
          break;
        }
      }
    }

    return !hasHiddenLetters;
  }

  @Override
  public String toString() {
    return Category + " " + Value;
  }
}
