/**
 * Write a description of class MapUpdateTimer here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.1
 */
import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple demo that uses java.util.Timer to schedule a task to execute once 5
 * seconds have passed.
 */

public class MapUpdateTimer {
  //Toolkit toolkit;
  Timer timer;
  PiWeather mMain;

  public MapUpdateTimer(int seconds, PiWeather main) {
    timer = new Timer();
    timer.schedule(new RemindTask(), 0, seconds * 1000);
    mMain = main;
  }

  class RemindTask extends TimerTask {
    public void run() {
      mMain.UpdateWxMap();
    }
  }

}