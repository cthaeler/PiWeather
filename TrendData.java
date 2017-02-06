import java.io.Serializable;
import java.time.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Write a description of class TrendData here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TrendData implements Serializable
{
    // instance variables - replace the example below with your own
    private LocalDateTime mDateTime;
    private double mTemp;
    private double mHumidity;

    /**
     * Constructor for objects of class TrendData
     */
    public TrendData()
    {
        // initialise instance variables
        mDateTime = LocalDateTime.now();
        mTemp = 0.0;
        mHumidity = 0.0;
    }
    
    public TrendData(LocalDateTime time, double temp, double humidity)
    {
        mDateTime = time;
        mTemp = temp;
        mHumidity = humidity;
    }
    
    /*
     * @return date time info
     */
    public LocalDateTime GetDateTime()
    {
        return mDateTime;
    }
    
    /*
     * @param time
     *      set the time
     */
    public void SetDateTime(LocalDateTime time)
    {
        mDateTime = time;
    }
    
    /*
     * @return temperature
     */
    public double GetTemp()
    {
        return mTemp;
    }
    
    /*
     * @param temp
     *      set the temp
     */
    public void SetTemp(double temp)
    {
        mTemp = temp;
    }
    
    /*
     * @return humidity
     */
    public double GetHumidity()
    {
        return mHumidity;
    }
    
    /*
     * @param humidity
     *      set the humidity
     */
    public void SetHumidity(double humidity)
    {
        mHumidity = humidity;
    }

    public String toString()
    {
        return "TrendData [mDateTime=" + DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss").format(mDateTime) +
                          ", mTemp=" + mTemp + ", mHumidity=" + mHumidity +"]";
            
    }
}
