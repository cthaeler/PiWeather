
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
    
    /**
     * GetName()  Name of the sensor.  This MUST be overridden
     * 
     * @return returns the name of the sensor 
     */
    abstract public String GetName();
    
    /**
     * RefreshSensorData()  Refresh the sensor data
     * 
     * @return time in miliseconds till next safe refresh
     */
    abstract public int RefreshSensorData();

    /**
     * HasTemperature()  by default say no.  Subclasses will say yes
     * 
     * @return returns false by default
     */
    public boolean HasTemperature()
    {
        return false;
    }

    
    /**
     * GetTemperature()  return the temperature.
     * 
     * @return return 65.0 by default
     */
    public double GetTemperature()
    {
        return 65.0;
    }

     /**
     * HasHumidity()
     * 
     * @return returns fauls by default
     */
    public boolean HasHumidity()
    {
        return false;
    }

    
    /**
     * GetHumidity()  return the humidity
     * 
     * @return humidity bewteen 0 and 100
     */
    public double GetHumidity()
    {
        return 50.0;
    }

    
    /**
     * HasBarometricPressure()
     * 
     * @return false by default
     */
    public boolean HasBarometricPressure()
    {
        return false;
    }
 
    
    /**
     * GetBarometricPressure() returns the barometric pressure
     * 
     * @return return standard atmospheric presssure by default
     */
    public double GetBarometricPressure()
    {
        return 29.92;
    }
 
    
}
