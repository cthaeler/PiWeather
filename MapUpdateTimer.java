/**
 * Write a description of class MapUpdateTimer here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 16 Feb 2017
 */
import java.util.Timer;
import java.util.TimerTask;

/**
 * MapUpdateTimer used to schedule the update of the maps  Number of seconds is passed in the constructor
 */

public class MapUpdateTimer {
    /** the Timer object */
    Timer mTimer;
    /** the PiWeather that we need to callback to */
    PiWeather mMain;
    
    /**
    * MapUpdateTimer()
    * 
    * @param seconds number of seconds between calls to the handler
    * @param main PiWeather object to send the update to
    * 
    */
    public MapUpdateTimer(int seconds, PiWeather main) {
        mMain = main;
        mTimer = new Timer();
        mTimer.schedule(new RemindTask(), 0, seconds * 1000);
    }
    
    /**
    * RemindTask timer task used to call the required update function
    */
    class RemindTask extends TimerTask {
        /**
        * run() run this and call UpdateWxMap() in the PiWeather
        */
        public void run() {
            mMain.UpdateWxMap();
        }
    }

}