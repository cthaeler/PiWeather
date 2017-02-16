/**
 * Write a description of class TimeUpdateTimer here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 26 Feb 2017
 */
import java.util.Timer;
import java.util.TimerTask;

public class TimeUpdateTimer
{
  Timer mTimer;
  PiWeather mMain;

  public TimeUpdateTimer(int seconds, PiWeather main) {
    mMain = main;
    mTimer = new Timer();
    mTimer.schedule(new RemindTask(), 0, seconds * 1000);
  }

  class RemindTask extends TimerTask {
    public void run() {
      mMain.UpdateClock();
    }
  }

}