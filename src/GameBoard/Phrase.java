package GameBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Phrase {
  public List<Word> WordList = new ArrayList<Word>();
  private List<Letter> LetterList = new ArrayList<Letter>();
  public String Value;
  public int WordCount;
  public int RowCount;
  public String Category;
  
  private String PuzzleValue;
  
  private Map<Integer, Integer> WordsPerRow = new HashMap<Integer,Integer>();

  public Phrase(String category, String phrase) {
    this.Category = category;
    this.Value = phrase;

    int chars = phrase.length();

    //System.out.println("chars: " + chars);

    String[] words = phrase.split(" ");
    this.WordCount = words.length;

    int spacesCount = WordCount-1;
    int rowSize = (chars - spacesCount ) / 2;

    //System.out.println("rowSize: " + rowSize);

    int charCount = 0;
    int wordCountPerRow = 0;
    for (String s : words) {
      WordList.add(new Word(s));

      if (charCount < rowSize && charCount + s.length() <= rowSize) {
        charCount += s.length();
        wordCountPerRow++;
        //System.out.println("charCount: " + charCount);
        //System.out.println("wordCountPerRow: " + wordCountPerRow);
      }
    }
    
    int charsPerLine = 0;
    
    for(int i = 0; i< wordCountPerRow; i++)
    {
      charsPerLine += words[i].length();
    }
    
    charsPerLine += wordCountPerRow -1;
    
    this.RowCount = 2;
  }
  
  public int getWordCountByRow(int rowNumber)
  {
   if (rowNumber > RowCount)
     throw new NoSuchElementException("Phrase does not have rowNumber: " + rowNumber);
   
   return WordsPerRow.get(rowNumber);
  }
  
  public int getCharCountByRow(int rowNumber)
  {
   if (rowNumber > RowCount)
     throw new NoSuchElementException("Phrase does not have rowNumber: " + rowNumber);
   
   return WordsPerRow.get(rowNumber);
  }
  
  public List<Letter> getLetters(){
    List<Letter> list = new ArrayList<Letter>();
    
    for(Word word : WordList)
    {
      list.addAll(word.LetterList);
    }
    
    return list;
  }
  
  public int turnLetter(String letter)
  {
    char c = letter.charAt(0);
    int count = 0;
    for(Word w : WordList)
    {
      for(Letter l : w.LetterList)
      {
        if(l.Value == c){
          l.IsVisible=true;
          count++;
        }
      }
    }
    
    return count;
  }
  
//  public String getPuzzleValue()
//  {
//    System.out.println("Phrase.getPuzzleValue()");
//    StringBuilder str = new StringBuilder();
//    
//    for(Word w : WordList)
//    {
//      for(Letter l: w.LetterList)
//      {
//        if(l.IsVisible)
//          str.append(l.Value);
//        else
//          str.append("_");
//      }
//      str.append(" ");
//    }
//    
//    return str.toString();
//  }

  @Override
  public String toString() {
    return Category + " " + Value;
  }
}
