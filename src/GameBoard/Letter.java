package GameBoard;

public class Letter {
  public char Value; // letter | symbol | blank space
  public boolean IsVisible;
  
  public Letter(char c)
  {
    this.Value = c;
    this.IsVisible=false;
  }
}
