/**
 * Write a description of class TimeUpdateTimer here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 26 Feb 2017
 */
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer for updating the clock
 */
public class TimeUpdateTimer
{
    /** the Timer object */
    Timer mTimer;
    /** the PiWeather that we need to callback to */
    PiWeather mMain;
    
    /** 
     * TimeUpdateTimer()
     * 
     * @param seconds number of seconds per update
     * @param main PiWeather to callback
     */
    public TimeUpdateTimer(int seconds, PiWeather main) {
        mMain = main;
        mTimer = new Timer();
        mTimer.schedule(new RemindTask(), 0, seconds * 1000);
    }
    
    /** 
     * The timer task it to use
     */
    class RemindTask extends TimerTask {
        /**
         * run() run this and call UpdateClock() in the PiWeather
         */
        public void run() {
            mMain.UpdateClock();
        }
    }

}