import java.net.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

/**
 * HTML_Sensor generates fake data for all types
 * 
 * @author Charles Thaeler
 * @version 25 Jun 2019
 */
public class HTML_Sensor extends WxSensor
{

    /** string containing the URL of the webpage to query for wx data */
    private String mURL = "";
    
    private boolean mHasTemp = false;
    private boolean mHasHumidity = false;
    private boolean mHasPressure = false;
    
    /** the temperature cached at the last refresh */
    private double mTemp = 0.0;
    
    /** the humidity cached at the last refresh */
    private double mHumidity = 0.0;
    
    /** the pressure cached at the last refresh */
    private double mPressure = 0.0;

    /**
     * Constructor for objects of class HTML Sensor
     */
    public HTML_Sensor()
    {

    }
    

    /**
     * SetURL(String url) sets the URL
     * 
     * @return true
     */
    public boolean SetURL(String url)
    {
        mURL = url;
        
        /** we should test this... */
        return true;
    }
    
    /**
     * GetName() returns dummy
     * 
     * @return just "HTML" for now.
     */
    public String GetName()
    {
        return "HTML";
    }

    /**
     * RefreshSensorData() refreshes the sensor and returns the minimum time before the sensor can be called again
     * 
     * @return time in miliseconds till next safe refresh
     */
    public int RefreshSensorData()
    {
      
          /** supported sensors */
    
        try {
            URL url = new URL(mURL);
            InputStream is = url.openStream();
            int ptr = 0;
            StringBuffer buffer = new StringBuffer();
            while ((ptr = is.read()) != -1) {
                buffer.append((char)ptr);
            }
            String lines[] = buffer.toString().split("\\r?\\n");
            for (String line : lines) {
                int colon = line.indexOf(':');
                if (colon > 0) {
                    int end = line.indexOf('<');
                    String quantity = line.substring(0, colon);
                    String valstr = line.substring(colon+2, end);
 
                    switch (quantity) {
                        case "Sensor Abilities":
                            if (valstr.indexOf('T') != -1) mHasTemp = true; else mHasTemp = false;
                            if (valstr.indexOf('H') != -1) mHasHumidity = true; else mHasHumidity = false; 
                            if (valstr.indexOf('P') != -1) mHasPressure = true; else mHasPressure = false;  
                            break;
                        
                        case "Temp":
                            mTemp = Double.parseDouble(valstr);
                            break;
                        
                        case "Humidity":
                            mHumidity = Double.parseDouble(valstr);
                            break;
                        
                        case "Press":
                            mPressure = Double.parseDouble(valstr);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return 1000; // we'll just say 1000
    }

    /**
     * HasTemperature()
     * 
     * @return true
     */
    public boolean HasTemperature()
    {
        return mHasTemp;
    }

    
    /**
     * GetTemperature() Get the temerature value
     * 
     * @return temperature
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
        return mHasHumidity;
    }

    
    /**
     * GetHumidity()
     * 
     * @return humidity
     */
    public double GetHumidity()
    {
        return mHumidity;
    }

    
    /**
     * HasBarometricPressure()
     * 
     * @return true
     */
    public boolean HasBarometricPressure()
    {
        return mHasPressure;
    }
 
    
    /**
     * GetBarometricPressure()
     * 
     * @return pressure
     */
    public double GetBarometrucPressure()
    {
        return mPressure;
    }
 
    
}
