import java.io.Serializable;
import java.time.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Write a description of class TrendData here.
 * 
 * @author Charles Thaeler
 * @version 15 Feb 2017
 */
public class TrendData implements Serializable
{
    // instance variables - replace the example below with your own
    private LocalDateTime mDateTime;
    private double mTemp;
    private double mHumidity;
    private double mBarometer;
    private double mSensorTemp;
    private double mSensorHumidity;
    //private double mSensorBarometer;
    private String mObsTime;

    /**
     * Constructor for objects of class TrendData
     */
    public TrendData()
    {
        // initialise instance variables
        mDateTime = LocalDateTime.now();
        mTemp = 0.0;
        mHumidity = 0.0;
        mBarometer = 0.0;
        mObsTime = "";
        mSensorTemp = 0.0;
        mSensorHumidity = 0.0;
        // mSensorBarometer = 0.0;
    }
    
    /**
     * Constructor that takes data
     */
    public TrendData(LocalDateTime time, String obstime, double temp, double humidity, double barometer,
                    double sensorTemp, double sensorHumidity) //, double sensorBarometer
    {
        mDateTime = time;
        mTemp = temp;
        mHumidity = humidity;
        mBarometer = barometer;
        mObsTime = obstime;
        mSensorTemp = sensorTemp;
        mSensorHumidity = sensorHumidity;
        //mSensorBarometer = sensorBarometer;
    }
    
    /**
     * @return date time info
     */
    public LocalDateTime GetDateTime()
    {
        return mDateTime;
    }
    
    /**
     * @param time
     *      set the time
     */
    public void SetDateTime(LocalDateTime time)
    {
        mDateTime = time;
    }
    
    /**
     * @return temperature
     */
    public double GetTemp()
    {
        return mTemp;
    }
    
    /**
     * @param temp
     *      set the temp
     */
    public void SetTemp(double temp)
    {
        mTemp = temp;
    }
    
    /**
     * @return humidity
     */
    public double GetHumidity()
    {
        return mHumidity;
    }
    
    /**
     * @param humidity
     *      set the humidity
     */
    public void SetHumidity(double humidity)
    {
        mHumidity = humidity;
    }
    
    /**
     * @return barometer
     */
    public double GetBarometer()
    {
        return mBarometer;
    }
    
    /**
     * @param barometer
     *      set the barometer
     */
    public void SetBarometer(double barometer)
    {
        mBarometer = barometer;
    }

        
    /**
     * @return obs time
     */
    public String GetObsTime()
    {
        return mObsTime;
    }
    
    /**
     * @param obstime
     *      set the obs time
     */
    public void SetObsTime(String obstime)
    {
        mObsTime = obstime;
    }
    
    
    /**
     * @return sensor temperature
     */
    public double GetSensorTemp()
    {
        return mSensorTemp;
    }
    
    /**
     * @param temp
     *      set the sensor temp
     */
    public void SetSensorTemp(double temp)
    {
        mSensorTemp = temp;
    }
    
    /**
     * @return sensor humidity
     */
    public double GetSensorHumidity()
    {
        return mSensorHumidity;
    }
    
    /**
     * @param humidity
     *      set the sensor humidity
     */
    public void SetSensorHumidity(double humidity)
    {
        mSensorHumidity = humidity;
    }
    
    /**
     * @return sensor barometer
     *
    public double GetSensorBarometer()
    {
        return mSensorBarometer;
    }
    */
    
    /**
     * @param sensor barometer
     *      set the sensor barometer
     *
    public void SetSensorBarometer(double barometer)
    {
        mSensorBarometer = barometer;
    }
    */
    
    public String toString()
    {
        return "TrendData[dt=" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(mDateTime) +
                          ", temp=" + String.format("%3.0f", mTemp) + "/" + String.format("%3.0f", mSensorTemp) +
                          ", hum=" + String.format("%3.0f", mHumidity) + "/" + String.format("%3.0f", mSensorHumidity) +
                          ", pres=" + String.format("%5.2f", mBarometer) + //"/" + String.format("%5.2f", mSensorBarometer) +
                          ", obs=" + mObsTime +
                          "]";
            
    }

}
