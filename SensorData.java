import java.net.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

/**
 * Data from a HW sensor or Web source
 *
 * @author Charles Thaeler
 * @version 0.1
 */
public class SensorData
{
    // Temperature
    private double mTemp = 0.0;
    private double mHumidity = 0.0;
    private double mWindSpeed = 0.0;
    private double mWindDirection = 0.0;
    private double mBarometer = 0.0;
    
    private boolean mTempValid = false;
    private boolean mHumidityValid = false;
    private boolean mWindSpeedValid = false;
    private boolean mWindDirectionValid = false;
    private boolean mBarometerValid = false;
    
    /* for web "sensor" only */
    private String mCurrConditionIconURL = "";
    private String mCurrObsTime = "";

    /**
     * Constructor for objects of class SensorData
     */
    public SensorData()
    {
        // initialise instance variables
        mTemp = 0.0;
        mHumidity = 0.0;
        mWindSpeed = 0.0;
        mWindDirection = 0.0;
        mBarometer = 0.0;
        
        mTempValid = false;
        mHumidityValid = false;
        mWindSpeedValid = false;
        mWindDirectionValid = false;
        mBarometerValid = false;
    }
 
    
    public boolean IsTempValid() { return mTempValid; }
    public double GetTemp()      { return mTemp; }
    
    
    public boolean IsHumidityValid() { return mHumidityValid; }
    public double GetHumidity()      { return mHumidity; }
    
    
    public boolean IsWindSpeedValid() { return mWindSpeedValid; }
    public double GetWindSpeed()      { return mWindSpeed; }
    
    
    public boolean IsWindDirectionValid() { return mWindDirectionValid; }
    public double GetWindDirection()      { return mWindDirection; }
    
    
    public boolean IsBarometerValid() { return mBarometerValid; }
    public double GetBarometer()      { return mBarometer; }
    
    public String GetCurrConditionIconURL() { return mCurrConditionIconURL; }
    public String GetObsTime() { return mCurrObsTime; }
    
    
    public void Set(double t, double h, double ws, double wd, double b)
    {
        mTemp = t;
        mHumidity = h;
        mWindSpeed = ws;
        mWindDirection = wd;
        mBarometer = b;
        
        mTempValid = true;
        mHumidityValid = true;
        mWindSpeedValid = true;
        mWindDirectionValid = true;
        mBarometerValid = true;
    }
    
     
    public boolean UpdateFromSensor(WxSensor sensor)
    {
        if (sensor == null) {
            mTemp = 0.0;
            mHumidity = 0.0;
            mWindSpeed = 0.0;
            mWindDirection = 0.0;
            mBarometer = 0.0;
            
            mTempValid = false;
            mHumidityValid = false;
            mWindSpeedValid = false;
            mWindDirectionValid = false;
            mBarometerValid = false;
            PiWeather.DumpError("No Sensor attached", null);
            return false;
        }
        
        sensor.RefreshSensorData();
        
        if (sensor.HasTemperature()) {
            mTempValid = true;
            mTemp = sensor.GetTemperature();
        } else {
            mTempValid = false;
        }

        
        if (sensor.HasHumidity()) {
            mHumidityValid = true;
            mHumidity = sensor.GetHumidity();
        } else {

            mHumidityValid = false;
        }
        
        if (sensor.HasWindSpeed()) {
            mWindSpeedValid = true;
            mWindSpeed = sensor.GetWindSpeed();
        } else {
            mWindSpeedValid = false;
        }
 
        
        if (sensor.HasWindDirection()) {
            mWindDirectionValid = true;
            mWindDirection = sensor.GetWindDirection();
        } else {
            mWindDirectionValid = false;
        }


        
        if (sensor.HasBarometricPressure()) {
            mBarometerValid = true;
            mBarometer = sensor.GetBarometricPressure();
        } else {
            mBarometerValid = false;
        }
        
        return true;
    }
    
    
    public boolean UpdateFromWeb(String urlStr)
    {
        /* Read from the web doc the weather information */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
                
            URL url = new URL(urlStr);
            InputStream stream = url.openStream();
            
            Document doc = factory.newDocumentBuilder().parse(stream);
            
            try {
                mCurrObsTime = WxWebDocUtils.ReadStringFromDoc(doc, "start-valid-time");          
                mTemp = WxWebDocUtils.ReadValueFromDoc(doc, "temperature");
                mHumidity = WxWebDocUtils.ReadValueFromDoc(doc, "humidity");           
                mWindDirection = WxWebDocUtils.ReadValueFromDoc(doc, "direction");
                mWindSpeed = WxWebDocUtils.ReadValueFromDoc(doc, "wind-speed");
                mBarometer = WxWebDocUtils.ReadValueFromDoc(doc, "pressure");
                mCurrConditionIconURL = WxWebDocUtils.ReadCurrentConditionsIconFromDocument(doc);
            } catch (Exception e) {
                PiWeather.DumpError("SensorData.UpdateFromWeb:", e);
                return false;
            }
        } catch (Exception e) {
            PiWeather.DumpError("SensorData.UpdateFromWeb:", e);
            return false;
        }
        return true;
    }
    
        
    @Override
    public String toString()
    {
        String result = String.format("SensorData\n\tTemp = %f (%s)\n\tHumi = %f (%s)\n\tWind = %f (%s)\n\tWDir = %f (%s)\n\tBaro = %f (%s)\n\tURL  = %s\n",
                    mTemp, mTempValid,
                    mHumidity, mHumidityValid,
                    mWindSpeed, mWindSpeedValid,
                    mWindDirection, mWindDirectionValid,
                    mBarometer, mBarometerValid,
                    mCurrConditionIconURL);
        
        return result;
    }

}
