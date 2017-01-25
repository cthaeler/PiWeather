/**
 * Write a description of class UpdateUITimer here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version (a version number or a date)
 */
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDateTime;

/**
 * Simple demo that uses java.util.Timer to schedule a task to execute once 5
 * seconds have passed.
 */

public class UpdateUITimer {
  //Toolkit toolkit;
  Timer timer;
  PiWeather mMain;

  public UpdateUITimer(int seconds, PiWeather main) {
    mMain = main;
    timer = new Timer();
    LocalDateTime dt = LocalDateTime.now();
    // line up on an even minute (or close)
    timer.schedule(new RemindTask(), (60 - dt.getSecond())*1000, seconds * 1000);
  }

  class RemindTask extends TimerTask {
    public void run() {
      mMain.UpdateFromWeb();
    }
  }

}