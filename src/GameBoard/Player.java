package GameBoard;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {
  static AtomicInteger nextId = new AtomicInteger();

  private int _playerId;
  private String _name;
  private int _score;

  public Player(String name) {
    _playerId = nextId.incrementAndGet();
    _name = name;
    _score = 0;
  }

  public int getPlayerId() {
    return _playerId;
  }

  public String getName() {
    return _name;
  }

  public String getFormattedScore() {
    DecimalFormat myFormatter = new DecimalFormat("$###,###");
    return myFormatter.format(_score);
  }

  public int getScore() {
    return _score;
  }

  public void updateScore(String strScore) {
    int iScore = Integer.parseInt(strScore);
    updateScore(iScore);
  }

  public void updateScore(int score) {
    if (score == 0)
      _score = 0; // Bankrupt
    else
      _score += score;
  }

  public boolean buyVowel() {
    boolean hasEnoughMoney = false;
    if (_score >= 250) {
      _score -= 250;
      hasEnoughMoney = true;
    }
    return hasEnoughMoney;
  }

  @Override
  public String toString() {
    return _name + ": " + getScore();
  }
}
