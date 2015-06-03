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
  private ArrayList<Phrase> Phrases;
  private Phrase CurrentPhrase;
  private Queue<Player> Players;
  private List<Player> PlayerList;
  private boolean IsPuzzleSolved;

  public Game() {
    createPlayers();
    loadPuzzlePhrases();
  }

  public void start() {
    createPlayerQueue();
    IsPuzzleSolved = false;
  }

  public List<Player> getPlayers() {
    return PlayerList;
  }

  public Phrase getRandomPhrase() {
    if (Phrases != null) {
      int min = 1;
      int max = Phrases.size();

      Random rand = new Random();
      int randomNum = rand.nextInt((max - min) + 1);

      CurrentPhrase = Phrases.get(randomNum);
      return CurrentPhrase;
    } else
      return null;
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
    if (answer.toLowerCase().trim().equals(CurrentPhrase.Value.toLowerCase().trim()))
      IsPuzzleSolved = true;
    
    return IsPuzzleSolved;
  }

  public boolean isPuzzleSolved()
  {
    return IsPuzzleSolved;
  }
  
  private void loadPuzzlePhrases() {
    if (Phrases == null) {
      Phrases = new ArrayList<Phrase>();

      Scanner scanner = readFile("files/game_phrases.txt");

      while (scanner.hasNext()) {
        String line = scanner.nextLine();
        String[] array = line.split("\\|");

        Phrase phrase = new Phrase(array[0].trim(), array[1].trim());
        Phrases.add(phrase);
      }
    }
  }

  private void createPlayerQueue() {
    Players = new LinkedList<Player>();
    for (Player p : PlayerList) {
      p.updateScore(0);
      Players.add(p);
    }
  }

  private void createPlayers() {
    if (PlayerList == null) {
      PlayerList = new LinkedList<Player>();
      PlayerList.add(new Player("Joel"));
      PlayerList.add(new Player("Erbes"));
      PlayerList.add(new Player("Bill"));
    }
  }

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
