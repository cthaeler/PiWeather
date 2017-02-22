import java.io.*;

/**
 * BMP280_Sensor access to a BME280 sensor
 * 
 * @author Charles Thaeler
 * @version 21 Feb 2017
 */
public class BMP280_Sensor extends WxSensor
{
    static private String msCmdStr = "python ./sensors/dmp280.py";
    private double mTemp = 0.0;
    private double mPressure = 0.0;

    /**
     * Constructor for objects of class DummySensor
     */
    public BMP280_Sensor()
    {

    }
    
    public String GetName()
    {
        return "DMP280";
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
                    mPressure = Double.parseDouble(data[1]);
                } else {
                    mTemp = 0.0;
                    mPressure = 0.0;
                }
            }
          
            bri.close();
            proc.waitFor();
        } catch  (Exception e) {
            mTemp = 0.0;
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
     * HasBarometerPressure()
     */
    public boolean HasBarometerPressure()
    {
        return true;
    }
 
    
    /**
     * GetBarometerPressure()
     */
    public double GetBarometerPressure()
    {
        return mPressure;
    }
 
    
}
