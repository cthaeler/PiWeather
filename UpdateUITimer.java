
/**
 * Write a description of class UpdateUITimer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple demo that uses java.util.Timer to schedule a task to execute once 5
 * seconds have passed.
 */

public class UpdateUITimer {
  //Toolkit toolkit;
  Timer timer;
  PiWeather mMain;

  public UpdateUITimer(int seconds, PiWeather main) {
    //toolkit = Toolkit.getDefaultToolkit();
    timer = new Timer();
    timer.schedule(new RemindTask(), 0, seconds * 1000);
    mMain = main;
  }

  class RemindTask extends TimerTask {
    public void run() {
      //toolkit.beep();
      mMain.UpdateUI();
    }
  }

}