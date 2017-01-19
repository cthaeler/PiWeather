/**
 * Write a description of class TimeUpdateTimer here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.1
 */
import java.util.Timer;
import java.util.TimerTask;
public class TimeUpdateTimer
{
    // instance variables - replace the example below with your own
  //Toolkit toolkit;
  Timer timer;
  PiWeather mMain;

  public TimeUpdateTimer(int seconds, PiWeather main) {
    mMain = main;
    timer = new Timer();
    timer.schedule(new RemindTask(), 0, seconds * 1000);
  }

  class RemindTask extends TimerTask {
    public void run() {
      mMain.UpdateClock();
    }
  }

}