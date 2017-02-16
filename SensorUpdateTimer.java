/**
 * Update the Sensor infomation
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 16 Feb 2017
 */
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDateTime;

/**
 * Update the Sensor infomation
 */

public class SensorUpdateTimer {
  Timer mTimer;
  PiWeather mMain;

  public SensorUpdateTimer(int seconds, PiWeather main) {
    mMain = main;
    mTimer = new Timer();
    if (seconds < 2) seconds = 2; // the sensor can't be sampled more frequently
    mTimer.schedule(new RemindTask(), 0, seconds * 1000);
  }

  class RemindTask extends TimerTask {
    public void run() {
      mMain.UpdateFromSensor();
    }
  }

}
