
/**
 * WxSensor lists the capabilities for sensors and a method run the sensor 
 * 
 * @author Charles Thaeler
 * @version 21 Feb 2017
 */
public abstract class WxSensor
{
    /**
     * Constructor for objects of class WxSensor
     */
    public WxSensor()
    {

    }
    
    abstract public String GetName();
    
    /**
     * RefreshSensorData()
     * @return time in miliseconds till next safe refresh
     */
    abstract public int RefreshSensorData();

    /**
     * HasTemperature()
     */
    public boolean HasTemperature()
    {
        return false;
    }

    
    /**
     * GetTemperature()
     */
    public double GetTemperature()
    {
        return 65.0;
    }

     /**
     * HasHumidity()
     */
    public boolean HasHumidity()
    {
        return false;
    }

    
    /**
     * GetHumidity()
     */
    public double GetHumidity()
    {
        return 50.0;
    }

    
    /**
     * HasBarometricPressure()
     */
    public boolean HasBarometricPressure()
    {
        return false;
    }
 
    
    /**
     * GetBarometricPressure()
     */
    public double GetBarometricPressure()
    {
        return 29.92;
    }
 
    
}
