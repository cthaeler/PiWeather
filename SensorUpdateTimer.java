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
    /** the Timer object */
    Timer mTimer;
    /** the PiWeather that we need to callback to */
    PiWeather mMain;
    
    /** 
     * SensorUpdateTimer()
     * 
     * @param seconds number of seconds per update.  If we have less than 5 seconds till the next minute
     * @param main PiWeather to callback
     */
    public SensorUpdateTimer(int seconds, PiWeather main) {
        mMain = main;
        
        mTimer = new Timer();
        
        if (seconds < 2) seconds = 2; // the sensor can't be sampled more frequently
        
        mTimer.schedule(new RemindTask(), 0, seconds * 1000);
    }
    
    /** 
     * The timer task it to use
     */
    class RemindTask extends TimerTask {
        /**
         * run() run this and call UpdateFromSensor() in the PiWeather
         */
        public void run() {
            mMain.UpdateFromSensor();
        }
    }

}
