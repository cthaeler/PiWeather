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
    /** the Timer object */
    Timer mTimer;
    /** the PiWeather that we need to callback to */
    PiWeather mMain;
    
    /** 
     * UIUpdateTimer()
     * 
     * @param seconds number of seconds per update.  If we have less than 5 seconds till the next minute
     * @param main PiWeather to callback
     */
    public UIUpdateTimer(int seconds, PiWeather main) {
        mMain = main;
        mTimer = new Timer();
        LocalDateTime dt = LocalDateTime.now();
        
        // line up on an even minute (or close)
        int firstwait = (60 - dt.getSecond())*1000;
        
        if (firstwait < 5000)
            firstwait += 60000; // wait for the next minute
            
        mTimer.schedule(new RemindTask(), firstwait, seconds * 1000);
    }
    
    /** 
     * The timer task it to use
     */
    class RemindTask extends TimerTask {
        /**
         * run() run this and call UpdateFromWeb() in the PiWeather
         */
        public void run() {
            mMain.UpdateFromWeb();
        }
    }

}
