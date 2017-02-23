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
public class TrendData
{
    // instance variables - replace the example below with your own
    /** time of the data was captures */
    private LocalDateTime mDateTime;
    /** observation time from the web */
    private String mObsTime;
    /** temperature from the web */
    private double mTemp;
    /** humidity from the web */
    private double mHumidity;
    /** barometric pressure from the web */
    private double mBarometer;
    /** temperature from the sensor */
    private double mSensorTemp;
    /** humidity from the sensor */
    private double mSensorHumidity;
    /** barometric pressure from the sensor */
    private double mSensorBarometer;
    

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
        mSensorBarometer = 0.0;
    }
    
    /**
     * Constructor that takes data
     * 
     * @param time              time we collected the data
     * @param obstime           time of the observation from the web
     * @param temp              temperature of the observation from the web
     * @param humidity          humidity of the observation from the web
     * @param barometer         barometer of the observation from the web
     * @param sensorTemp        temperature from the sensor
     * @param sensorHumidity    humidity from the sensor
     * @param sensorBarometer   barometer from the sensor
     */
    public TrendData(LocalDateTime time, String obstime, double temp, double humidity, double barometer,
                    double sensorTemp, double sensorHumidity, double sensorBarometer)
    {
        mDateTime = time;
        mTemp = temp;
        mHumidity = humidity;
        mBarometer = barometer;
        mObsTime = obstime;
        mSensorTemp = sensorTemp;
        mSensorHumidity = sensorHumidity;
        mSensorBarometer = sensorBarometer;
    }
    
    /**
     * GetDatTime()  get the time of the data entry
     * 
     * @return date time info
     */
    public LocalDateTime GetDateTime()
    {
        return mDateTime;
    }
    
    /**
     * SetDateTime()  Set the time the data was collected
     * 
     * @param time to save
     *  
     */
    public void SetDateTime(LocalDateTime time)
    {
        mDateTime = time;
    }
    
    /**
     * GetTemp()  Get the temperature
     * 
     * @return temperature
     */
    public double GetTemp()
    {
        return mTemp;
    }
    
    /**
     * SetTemp()  Set the temperature
     * 
     * @param temp the temperature
     * 
     */
    public void SetTemp(double temp)
    {
        mTemp = temp;
    }
    
    /**
     * GetHumidity() Get the humdity
     * 
     * @return humidity
     */
    public double GetHumidity()
    {
        return mHumidity;
    }
    
    /**
     * SetHumidity()  Set the humidity
     * 
     * @param humidity the humidity
     * 
     */
    public void SetHumidity(double humidity)
    {
        mHumidity = humidity;
    }
    
    /**
     * GetBarameter() get the barometric pressure
     * 
     * @return barometer
     */
    public double GetBarometer()
    {
        return mBarometer;
    }
    
    /**
     * SetBarometer() set the barometric presssure
     * 
     * @param barometer  the barometric pressure
     * 
     */
    public void SetBarometer(double barometer)
    {
        mBarometer = barometer;
    }

        
    /**
     * GetObsTime()  Get the time of the observation from the web
     * 
     * @return obs time as a string
     */
    public String GetObsTime()
    {
        return mObsTime;
    }
    
    /**
     * SetObsTime()  Set the observation time
     * 
     * @param obstime  the observation time
     * 
     */
    public void SetObsTime(String obstime)
    {
        mObsTime = obstime;
    }
    
    
    /**
     * GetSensorTemp()  Get the sensor temperature
     * 
     * @return sensor temperature
     */
    public double GetSensorTemp()
    {
        return mSensorTemp;
    }
    
    /**
     * SetSensorTemp()  set the sensor temperature
     * 
     * @param temp  the sensor temperature
     * 
     */
    public void SetSensorTemp(double temp)
    {
        mSensorTemp = temp;
    }
    
    /**
     * GetSensorHumidity() return the sensor humidity
     * 
     * @return sensor humidity
     * 
     */
    public double GetSensorHumidity()
    {
        return mSensorHumidity;
    }
    
    /**
     * SetSensorHumidity() set the sensor humidity value
     * 
     * @param humidity  the sensor humidity
     * 
     */
    public void SetSensorHumidity(double humidity)
    {
        mSensorHumidity = humidity;
    }
    
    /**
     * GetSensorBarometer()  Get the sensor barometric pressure
     * 
     * @return sensor barometer
     *
     */
    public double GetSensorBarometer()
    {
        return mSensorBarometer;
    }

    
    /**
     * SetSensorBarometer() set the sensor barometer
     *
     * @param barometer sensor barometer value
     *
     */
    public void SetSensorBarometer(double barometer)
    {
        mSensorBarometer = barometer;
    }

    
    /**
     * toString() conver the data to a string
     * 
     * @return the data string
     */
    public String toString()
    {
        return "TrendData[dt=" + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(mDateTime) +
                          ", obs=" + mObsTime +
                          ", temp=" + String.format("%3.0f", mTemp) + "/" + String.format("%3.0f", mSensorTemp) +
                          ", hum=" + String.format("%3.0f", mHumidity) + "/" + String.format("%3.0f", mSensorHumidity) +
                          ", pres=" + String.format("%5.2f", mBarometer) + "/" + String.format("%5.2f", mSensorBarometer) +
                          "]";
            
    }

}
