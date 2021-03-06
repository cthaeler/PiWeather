import java.io.*;

/**
 * DHT11_Sensor access to a DHT11 sensor
 * 
 * @author Charles Thaeler
 * @version 21 Feb 2017
 */
public class DHT11_Sensor extends WxSensor
{
    /** the python command to run to query the dht11 sensor */
    static private String msCmdStr = "python ./sensors_py/dht11.py";
    
    /** the temperature cached at the last refresh */
    private double mTemp = 0.0;
    
    /** the humidity cached at the last refresh */
    private double mHumidity = 0.0;
    
    /**
     * Constructor for objects of class DummySensor
     */
    public DHT11_Sensor()
    {

    }
    
    /**
     * GetName() name of the sensor - DHT11
     * 
     * @return the sensor name
     */
    public String GetName()
    {
        return "DHT11";
    }
    
    /**
     * RefreshSensorData()
     * 
     * @return time in miliseconds till next safe refresh - 2 seconds
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
                } else {
                    mTemp = 0.0;
                    mHumidity = 0.0;
                }
            }
          
            bri.close();
            proc.waitFor();
        } catch  (Exception e) {
            if (PiWeather.DebugLevel().ShowStackTrace()) e.printStackTrace();
            mTemp = 0.0;
            mHumidity = 0.0;
        }
        return 2000;
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
     * GetTemperature() get the temperature
     * 
     * @return the cached sensor value
     */
    public double GetTemperature()
    {
        return mTemp;
    }

     /**
     * HasHumidity() we have a humidity sensor
     * 
     * @return true
     */
    public boolean HasHumidity()
    {
        return true;
    }

    
    /**
     * GetHumidity() get the humidity
     * 
     * @return the cached sensor value
     */
    public double GetHumidity()
    {
        return mHumidity;
    }

}
