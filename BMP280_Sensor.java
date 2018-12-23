import java.io.*;

/**
 * BMP280_Sensor access to a BME280 sensor
 * 
 * @author Charles Thaeler
 * @version 21 Feb 2017
 */
public class BMP280_Sensor extends WxSensor
{
    /** the python command to run to query the bmp280 sensor */
    static private String msCmdStr = "python ./sensors_py/bmp280.py";
    
    /** the temperature cached at the last refresh */
    private double mTemp = 0.0;
    
    /** the pressure cached at the last refresh */
    private double mPressure = 0.0;

    /**
     * Constructor for objects of class DummySensor
     */
    public BMP280_Sensor()
    {

    }
    
    /**
     * GetName() name of the sensor - BMP280
     * 
     * @return the sensor name
     */
    public String GetName()
    {
        return "BMP280";
    }
    
    /**
     * RefreshSensorData()
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
                    mPressure = Double.parseDouble(data[1]);
                } else {
                    mTemp = 0.0;
                    mPressure = 0.0;
                }
            }
          
            bri.close();
            proc.waitFor();
        } catch  (Exception e) {
            if (PiWeather.DebugLevel().ShowStackTrace()) e.printStackTrace();
            mTemp = 0.0;
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
