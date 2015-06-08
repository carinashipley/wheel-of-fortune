package main;

import javax.swing.JFrame;

import gui.*;

public class frm_main {
  public static void main(String[] arg) {

    GameView view = new GameView();

    view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    view.setVisible(true);
  }

}
