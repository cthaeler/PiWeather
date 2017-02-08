/**
 * Write a description of class SensorUpdateTimer here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version (a version number or a date)
 */
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDateTime;

/**
 * Update the Sensor infomation
 */

public class SensorUpdateTimer {
  //Toolkit toolkit;
  Timer timer;
  PiWeather mMain;

  public SensorUpdateTimer(int seconds, PiWeather main) {
    mMain = main;
    timer = new Timer();
    if (seconds < 2) seconds = 2; // the sensor can't be sampled more frequently
    timer.schedule(new RemindTask(), 0, seconds * 1000);
  }

  class RemindTask extends TimerTask {
    public void run() {
      mMain.UpdateFromSensor();
    }
  }

}
