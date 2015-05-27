package GameBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class Game {
  private ArrayList<Phrase> Phrases = new ArrayList<Phrase>();
  private Phrase CurrentPhrase;
  private Queue<Player> Players = new LinkedList<Player>();
  private List<Player> PlayerList = new LinkedList<Player>();

  public void start() {
    createPlayers();

    loadPuzzlePhrases();

    for (Phrase p : Phrases) {
      // System.out.println("Value: " + p.Value);
      // System.out.println("WordCount: " + p.WordCount);
      // System.out.println("RowCount: " + p.RowCount);
      // System.out.println("WordList: " + p.WordList);
    }

  }

  public List<Player> getPlayers() {
    return PlayerList;
  }

  public Phrase getRandomPhrase() {
    int min = 1;
    int max = Phrases.size();

    Random rand = new Random();
    int randomNum = rand.nextInt((max - min) + 1);

    CurrentPhrase = Phrases.get(randomNum);

    System.out.println("CurrentPhrase: " + CurrentPhrase);

    return CurrentPhrase;
  }

  public Phrase getCurrentPhrase() {
    return CurrentPhrase;
  }

  public int updatePhrase(String letter) {
    return CurrentPhrase.turnLetter(letter);
  }

  public Player getCurrentPlayer() {
    return Players.peek();
  }

  public Player moveToNextPlayer() {
    Player currentPlayer = Players.remove();
    Players.add(currentPlayer);
    return Players.peek();
  }

  public boolean solvePuzzle(String answer) {
    return answer.toLowerCase().equals(CurrentPhrase.Value.toLowerCase());
  }

  private void loadPuzzlePhrases() {
    Scanner scanner = readFile("files/game_phrases.txt");

    while (scanner.hasNext()) {
      String line = scanner.nextLine();
      String[] array = line.split("\\|");

      Phrase phrase = new Phrase(array[0].trim(), array[1].trim());
      Phrases.add(phrase);
    }
  }

  private void createPlayers() {
    Players.add(new Player("Elly"));
    Players.add(new Player("Michael"));

    PlayerList = (LinkedList<Player>) Players;
  }

//  public void updatePlayer(int wheelValue, int letterCount) {
//    Player currentPlayer = Players.peek();
//    currentPlayer.updateScore(wheelValue * letterCount);
//  }

//  public boolean buyVowel() {
//    Player currentPlayer = Players.peek();
//    return currentPlayer.buyVowel();
//  }

  private Scanner readFile(String filename) {
    Scanner in = null;

    File file = new File(filename);

    try {
      in = new Scanner(file);
    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    }

    return in;
  }
}
