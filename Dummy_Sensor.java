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
    
    public String GetName()
    {
        return "MAC";
    }
    
    /**
     * RefreshSensorData()
     * @return time in miliseconds till next safe refresh
     */
    public int RefreshSensorData()
    {
        return 0; // you can refresh a fake FAST :)
    }

    /**
     * HasTemperature()
     */
    public boolean HasTemperature()
    {
        return true;
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
        return true;
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
        return true;
    }
 
    
    /**
     * GetBarometricPressure()
     */
    public double GetBarometrucPressure()
    {
        return 29.92;
    }
 
    
}
