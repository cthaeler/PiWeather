import java.net.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

import java.util.ArrayList;

/**
 * Forcast Data pulled from web data
 *   Highs Lows and Icons for 5 days
 *
 * @author Charles Thaeler
 * @version 0.1
 */
public class ForecastData
{

    private class FData
    {
        /** forecast temperature */
        private double mTemp;
        /** URL of icon to show */
        private String mIconURL;
        /** info about the day */
        private String mInfo;
        
        public void FData()
        {
            mTemp = 0.0;
            mIconURL = "not set";
            mInfo = "not Set";
        }
        
        public void SetData(double temp, String iconURL, String info)
        {
            mTemp = temp;
            mIconURL = iconURL;
            mInfo = info;
        }
        
        public double GetTemp() { return mTemp; }
        public void SetTemp(double temp)
        {
            mTemp = temp;
        }
        
        public String GetIconURL() { return mIconURL; }
        public void SetIconURL(String iconURL)
        {
            mIconURL = iconURL;
        }
        
        public String GetInfo() { return mInfo; }
        public void SetInfo(String info)
        {
            mInfo = info;
        }
        
        @Override
        public String toString()
        {
            return mTemp + " " + mIconURL + " " + mInfo;
        }
    }

    
    
    private ArrayList<FData> mValues;

    /**
     * Constructor for objects of class ForecastData
     */
    public ForecastData(int num)
    {
        mValues = new ArrayList<FData>();
        for (int fc = 0; fc < num; fc++) {
            FData fd = new FData();
            fd.SetTemp(fc); /* for debugging */
            mValues.add(fd);
        }
    }
    
    public double GetTemp(int idx)
    {
        try {
            return mValues.get(idx).GetTemp();
        } catch (Exception e) {
            PiWeather.DumpError("ForecastData.GetTemp:", e);
        }
        return 0.0;
    }
    
    public String GetIconURL(int idx)
    {
        try {
            return mValues.get(idx).GetIconURL();
        } catch (Exception e) {
            PiWeather.DumpError("ForecastData.GetIconURL:", e);
        }
        return "BAD";
    }
    
    public String GetInfo(int idx)
    {
        try {
            return mValues.get(idx).GetInfo();
        } catch (Exception e) {
            PiWeather.DumpError("ForecastData.GetInfo:", e);
        }
        return "BAD";
    }
    
    
    @Override
    public String toString()
    {
        String res = "ForcastData:\n";
        for(int i = 0; i < mValues.size(); i++) {
            res += "\t" + i + " " + mValues.get(i).toString() + "\n";
        }
        return res;
    }

    public boolean UpdateFromWeb(String urlStr, SensorData sData)
    {
        /* Read from the web doc the weather information */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
                
            URL url = new URL(urlStr);
            InputStream stream = url.openStream();
            
            if (PiWeather.DebugLevel().ShowDebugging()) System.out.println("Stream Open " + stream);
            Document doc = factory.newDocumentBuilder().parse(stream);
            if (PiWeather.DebugLevel().ShowDebugging()) System.out.println("done reading from web");
            
            try {
                NodeList dataNodes = doc.getElementsByTagName("data");
    
                // the 0'th element is the current condition
                mValues.get(0).SetTemp(sData.GetTemp());
                mValues.get(0).SetIconURL(sData.GetCurrConditionIconURL());
                mValues.get(0).SetInfo("Now");
                
                //System.out.println("UpdateFromWeb: dataNodes " + dataNodes.getLength() + " mValues " + mValues.size());
                for (int n = 0; n < dataNodes.getLength(); n++) {
                    // first let's find the icon data
                    Element dataElement = (Element) dataNodes.item(n);
                    String typeString = dataElement.getAttribute("type");
                    
                    // look for the forecast data
                    if (typeString.equals("forecast")) {
                        // found the forecasts now find the time coordinates
                        NodeList timeCoordsNodes = dataElement.getElementsByTagName("time-layout");
                        for (int tc = 0; tc < timeCoordsNodes.getLength(); tc++) {
                            Element childElement = (Element) timeCoordsNodes.item(tc);
                            NodeList svtNodes = childElement.getElementsByTagName("start-valid-time");
                            // find the full list not the short lists just so it's easier
                            if (svtNodes.getLength() > 7) {
                                // found the full list
                                for (int svt = 0; svt < svtNodes.getLength(); svt++) {
                                    Element svtElement = (Element) svtNodes.item(svt);
                                    Attr attr = svtElement.getAttributeNode("period-name");
                                    String info = attr.getValue();
                                    mValues.get(svt+1).SetInfo(info);
                                    
                                    if (svt+1 >= mValues.size()-1) break;
                                }
                            }
                        }
                        
                        // now find the conditions icons
                        NodeList conditionNodes = dataElement.getElementsByTagName("conditions-icon");
                        int numConditionNodes = conditionNodes.getLength();
                        if (conditionNodes.getLength() > 0) {
                            Element childElement = (Element) conditionNodes.item(0);
                            NodeList iconNodes = childElement.getElementsByTagName("icon-link");
                            int numIconNodes = iconNodes.getLength();
                            
                            // iterate the icons
                            for (int i = 0; i < iconNodes.getLength(); i++) {
                               Element iconElement = (Element) iconNodes.item(i);
                               String iconURL = WxWebDocUtils.GetCharacterDataFromElement(iconElement);
                               if (i < mValues.size()-1) {
                                   mValues.get(i+1).SetIconURL(iconURL);
                                } else {
                                    break;
                                }
                            }
                        }
                        
                        // Now let's find the min and max temperature data
                        NodeList tempNodes = dataElement.getElementsByTagName("temperature");
                        // there should be two sections with 6, 7 or 8 entries as either minimum or maximum temperatures
                        int numTempNodes = tempNodes.getLength();
                        for (int nodeIdx = 0; nodeIdx < numTempNodes; nodeIdx++) {
                            Element tempElement = (Element) tempNodes.item(nodeIdx);
                            String tempType = tempElement.getAttribute("type"); // this should be "minimum" or "maximum"
                            String tempTimeLayout = tempElement.getAttribute("time-layout");
                            // the time layout should look like this "k-p24h-n8-1":
                            int numTemps = Integer.parseInt(tempTimeLayout.substring(8, 9));
                            int numSeq = Integer.parseInt(tempTimeLayout.substring(10, 11));
                            NodeList tempValueNodes = tempElement.getElementsByTagName("value");
                            int numValueNodes = tempValueNodes.getLength();
                            for (int valueNodeIdx = 0; valueNodeIdx < numValueNodes; valueNodeIdx++) {
                                Element valueElement = (Element) tempValueNodes.item(valueNodeIdx);
                                String strval = WxWebDocUtils.GetCharacterDataFromElement(valueElement);   
                                double temp = Double.parseDouble(strval);
                                int valueIndex = valueNodeIdx*2 + numSeq;
                                
                                if (valueIndex < mValues.size()) {
                                    mValues.get(valueIndex).SetTemp(temp);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                PiWeather.DumpError("ForecastData.UpdateFromWeb: Read Elements", e);
                return false;
            }
        } catch (Exception e) {
            PiWeather.DumpError("ForecastData.UpdateFromWeb: Open and Parse Stream", e);
            return false;
        }
        return true;
    }
}
