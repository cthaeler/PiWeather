/**
 * Dummy_Sensor generates fake data for all types
 * 
 * @author Charles Thaeler
 * @version 21 Feb 2017
 */
public class Dummy_Sensor extends WxSensor
{
    /**
     * Constructor for objects of class DummySensor
     */
    public Dummy_Sensor()
    {

    }
    
    /**
     * GetName() returns dummy
     * 
     * @return just "Dummy" for now.
     */
    public String GetName()
    {
        return "DUMMY";
    }
    
    /**
     * RefreshSensorData() refreshes the sensor and returns the minimum time before the sensor can be called again
     * 
     * @return time in miliseconds till next safe refresh
     */
    public int RefreshSensorData()
    {
        return 0; // you can refresh a fake FAST :)
    }

    /**
     * HasTemperature()
     * 
     * @return true
     */
    public boolean HasTemperature()
    {
        return true;
    }

    
    /**
     * GetTemperature() Get the temerature value
     * 
     * @return 65 degrees
     */
    public double GetTemperature()
    {
        return 65.0;
    }

     /**
     * HasHumidity()
     * 
     * @return true
     */
    public boolean HasHumidity()
    {
        return true;
    }

    
    /**
     * GetHumidity()
     * 
     * @return 50.0 degrees
     */
    public double GetHumidity()
    {
        return 50.0;
    }

    
    /**
     * HasBarometricPressure()
     * 
     * @return true
     */
    public boolean HasBarometricPressure()
    {
        return true;
    }
 
    
    /**
     * GetBarometricPressure()
     * 
     * @return 29.92
     */
    public double GetBarometricPressure()
    {
        return 29.92;
    }
 
    
}
