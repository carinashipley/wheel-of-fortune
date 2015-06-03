package GameBoard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameSounds {

  public static void playWheel() {
    playSound("files/wheel_spin.wav");
  }

  public static void playLetterTurn() {
    playSound("files/puzzle_reveal.wav");
  }

  public static void playChant() {
    playSound("files/chant.wav");
  }

  private static void playSound(String filepath) {
    try {
      File audioFile = new File(filepath);
      AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
      AudioFormat format = stream.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, format);

      Clip audioClip = (Clip) AudioSystem.getLine(info);
      audioClip.open(stream);
      audioClip.start();

    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (UnsupportedAudioFileException ex) {
      ex.printStackTrace();
    } catch (LineUnavailableException ex) {
      ex.printStackTrace();
    }
  }
}
