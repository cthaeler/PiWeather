import java.io.*;

/**
 * BME280_Sensor access to a BME280 sensor
 * 
 * @author Charles Thaeler
 * @version 21 Feb 2017
 */
public class BME280_Sensor extends WxSensor
{
    /** the python command to run to query the bme280 sensor */
    static private String msCmdStr = "python ./sensors/bme280.py";
    
    /** the temperature cached at the last refresh */
    private double mTemp = 0.0;
    
    /** the humidity cached at the last refresh */
    private double mHumidity = 0.0;
    
    /** the pressure cached at the last refresh */
    private double mPressure = 0.0;
    
    /**
     * Constructor for objects of class DummySensor
     */
    public BME280_Sensor()
    {

    }
    
    /**
     * GetName() name of the sensor - BME280
     * 
     * @return the sensor name
     */
    public String GetName()
    {
        return "BME280";
    }
    
    /**
     * RefreshSensorData()
     * 
     * @return time in miliseconds till next safe refresh - 1 second
     */
    public int RefreshSensorData()
    {
        try {
            Runtime rt = Runtime.getRuntime();
            String line;
            String[] data;
            Process proc = rt.exec(msCmdStr);
            BufferedReader bri = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            if((line = bri.readLine()) != null){
                if(!line.contains("ERRROR")) {
                    data=line.split("\\|");
                    mTemp = Double.parseDouble(data[0]);
                    mHumidity = Double.parseDouble(data[1]);
                    mPressure = Double.parseDouble(data[2]);
                } else {
                    mTemp = 0.0;
                    mHumidity = 0.0;
                    mPressure = 0.0;
                }
            }
          
            bri.close();
            proc.waitFor();
        } catch  (Exception e) {
            mTemp = 0.0;
            mHumidity = 0.0;
            mPressure = 0.0;
        }
        return 1000;
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
     * GetTemperature()
     * 
     * @return the cached sensor value
     */
    public double GetTemperature()
    {
        return mTemp;
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
     * @return the cached sensor value
     */
    public double GetHumidity()
    {
        return mHumidity;
    }

    
    /**
     * HasBarometerPressure()
     * 
     * @return true
     */
    public boolean HasBarometricPressure()
    {
        return true;
    }
 
    
    /**
     * GetBarometerPressure()
     * 
     * @return the cached sensor value
     */
    public double GetBarometricPressure()
    {
        return mPressure;
    }
 
    
}
