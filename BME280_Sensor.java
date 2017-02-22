import java.io.*;

/**
 * BME280_Sensor access to a BME280 sensor
 * 
 * @author Charles Thaeler
 * @version 21 Feb 2017
 */
public class BME280_Sensor extends WxSensor
{
    static private String msCmdStr = "python ./sensors/dme280.py";
    private double mTemp = 0.0;
    private double mHumidity = 0.0;
    private double mPressure = 0.0;
    
    /**
     * Constructor for objects of class DummySensor
     */
    public BME280_Sensor()
    {

    }
    
    public String GetName()
    {
        return "DME280";
    }
    
    /**
     * RefreshSensorData()
     * @return time in miliseconds till next safe refresh
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
        return mTemp;
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
        return mHumidity;
    }

    
    /**
     * HasBarometerPressure()
     */
    public boolean HasBarometricPressure()
    {
        return true;
    }
 
    
    /**
     * GetBarometerPressure()
     */
    public double GetBarometricPressure()
    {
        return mPressure;
    }
 
    
}
