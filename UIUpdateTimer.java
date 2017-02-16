/**
 * Timer for updating the data from the web
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 16 Feb 2017
 */
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDateTime;

/**
 * Timer for updating the data from the web
 */

public class UIUpdateTimer {
  Timer mTimer;
  PiWeather mMain;

  public UIUpdateTimer(int seconds, PiWeather main) {
    mMain = main;
    mTimer = new Timer();
    LocalDateTime dt = LocalDateTime.now();
    // line up on an even minute (or close)
    int firstwait = (60 - dt.getSecond())*1000;
    if (firstwait < 5000) firstwait += 60000; // wait for the next minute
    mTimer.schedule(new RemindTask(), firstwait, seconds * 1000);
  }

  class RemindTask extends TimerTask {
    public void run() {
      mMain.UpdateFromWeb();
    }
  }

}
