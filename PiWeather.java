/**
 * Main for PiWeather
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.1
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import java.awt.Image;
import java.net.*;

import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import javax.imageio.ImageIO;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import java.time.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class PiWeather
{
 
    private ArrayList<DataValue> mValues;
    private ArrayList<ForecastDataValue> mForecastValues;
    private ArrayList<String> mMapURLs;
    private int mCurMap;
    private JLabel mWxImageLabel;
    private JLabel mTimeLabel;
    private JButton mQuitButton;
    private boolean mIsPi;
    private static double mInsideTemp;
    private static double mInsideHumidity;
    
    private Toolkit mToolkit;
    private Timer mTimer;
    
    /**
     * Constructor for objects of class PiWeather
     */
    PiWeather()
    {
        // Create a new JFrame container.
        JFrame jfrm = new JFrame("Pi Wx Display");

        // Terminate the program when the user closes the application
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (System.getProperty("os.name").equals("Mac OS X")) {
            mIsPi = false;
            jfrm.setSize(1024, 600);
        } else {
            mIsPi = true;
            // set properties
            jfrm.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            jfrm.setUndecorated(true);
        }
        
        SetupMapList();
        GetInsideInfo();

        // Main panel to add the sub panels too
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLACK);
        BoxLayout frameBox = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
        mainPanel.setLayout(frameBox);
        
        /* Left Panel for the local current observations */
        JPanel leftPanel = new JPanel();
        BoxLayout leftBox = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        leftPanel.setLayout(leftBox);
        leftPanel.setBackground(Color.BLACK);
        
        
       
        mTimeLabel = new JLabel(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()));
        mTimeLabel.setForeground(Color.white);
        mTimeLabel.setFont(new Font("Serif", Font.PLAIN, 36));
        
        leftPanel.add(mTimeLabel);
        
        leftPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        SetupValuesUI();
        
        for (int i=0; i < mValues.size(); i++) {
            DataValue dv = mValues.get(i);
            leftPanel.add(dv.getValueLabel());
            leftPanel.add(dv.getLegendLabel());
            
            leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }
        mQuitButton = new JButton("Quit");
        mQuitButton.setActionCommand("quit");
        
        mQuitButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                    System.exit(0);
             }          
          });
        leftPanel.add(mQuitButton);
        
        mainPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        mainPanel.add(leftPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        // Center Panel for the weather map images
        mWxImageLabel = new JLabel("");
        mainPanel.add(mWxImageLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        
        // right panel for the forcast info
        JPanel rightPanel = new JPanel();
        BoxLayout rightBox = new BoxLayout(rightPanel, BoxLayout.Y_AXIS);
        rightPanel.setLayout(rightBox);
        rightPanel.setBackground(Color.BLACK);
        
        mForecastValues = new ArrayList<ForecastDataValue>();
        for (int fc = 0; fc < 14; fc++) {
            ForecastDataValue fv = new ForecastDataValue();
            mForecastValues.add(fv);
        }
        
       
        for (int i=-1; i < mForecastValues.size()-1; i+=2) {
            JPanel pairPanel = new JPanel();
            pairPanel.setBackground(Color.BLACK);
            BoxLayout pairBox = new BoxLayout(pairPanel, BoxLayout.X_AXIS);
            pairPanel.setLayout(pairBox);
            // left one
            if (i == -1) {
                // Just a Layout label 
                JLabel dummy = new JLabel("Forecast");
                dummy.setFont(new Font("Serif", Font.PLAIN, 32));
                dummy.setAlignmentX(Component.LEFT_ALIGNMENT);
                dummy.setForeground(Color.white);
                pairPanel.add(dummy);
                pairPanel.add(Box.createRigidArea(new Dimension(35, 0)));
            } else {
                JPanel forecastPanel = new JPanel();
                forecastPanel.setBackground(Color.BLACK);
                BoxLayout forecastBox = new BoxLayout(forecastPanel, BoxLayout.X_AXIS);
                forecastPanel.setLayout(forecastBox);
     
                ForecastDataValue lfv = mForecastValues.get(i);
                forecastPanel.add(lfv.getLegendLabel());
                forecastPanel.add(lfv.getValueIcon());
                pairPanel.add(forecastPanel);
                pairPanel.add(Box.createRigidArea(new Dimension(25, 0)));
            }
            JPanel forecastPanel = new JPanel();
            forecastPanel.setBackground(Color.BLACK);
            BoxLayout forecastBox = new BoxLayout(forecastPanel, BoxLayout.X_AXIS);
            forecastPanel.setLayout(forecastBox);
            ForecastDataValue rfv = mForecastValues.get(i+1);
            forecastPanel.add(rfv.getValueIcon());
            forecastPanel.add(rfv.getLegendLabel());
            pairPanel.add(forecastPanel);

            rightPanel.add(pairPanel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        mainPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        mainPanel.add(rightPanel);
        
        // Add the panel to the frame
        jfrm.add(mainPanel);
        
        // Display the frame.
        jfrm.setVisible(true);
    }
        
    
    private void GetInsideInfo()
    {
        try {
            Runtime rt = Runtime.getRuntime();
            String line;
            String[] data;
            String cmdStr;
            if (mIsPi) {
                cmdStr = "python /home/pi/bin/dht.py";
            } else {
                cmdStr = "python /Users/cst/bin/dht.py";
            }
            Process proc = rt.exec(cmdStr);
            BufferedReader bri = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            if((line = bri.readLine()) != null){
                if(!line.contains("ERRROR")) {
                    data=line.split("\\|");
                    mInsideTemp = Double.parseDouble(data[0]);
                    mInsideHumidity = Double.parseDouble(data[1]);
                } else {
                    mInsideTemp = 0.0;
                    mInsideHumidity = 0.0;
                }
            }
          
            bri.close();
            proc.waitFor();
        } catch  (Exception e) {
            mInsideTemp = 0.0;
            mInsideHumidity = 0.0;
        }
    }
    
    private static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }

    private static double ReadValueFromDoc(Document doc, String e)
    {
        NodeList dataNodes = doc.getElementsByTagName("data");
        for (int n = 0; n < dataNodes.getLength(); n++) {
            Element element = (Element) dataNodes.item(n);
            String typeString = element.getAttribute("type");
            if (typeString.equals("current observations")) {
                NodeList nodes = element.getElementsByTagName(e);
                // iterate the pressures
                for (int i = 0; i < nodes.getLength(); i++) {
                   Element childElement = (Element) nodes.item(i);
                   NodeList value = childElement.getElementsByTagName("value");
                   Element line = (Element) value.item(0);
                   String strval = getCharacterDataFromElement(line);   
                   if (strval.equals("NA"))
                       continue;
                   double d = Double.parseDouble(strval);
                   return d;
                }
            }
        }
        
        return 0;
    }
    
    private void SetupMapList()
    {
        mMapURLs = new ArrayList<String>();
        mCurMap = 0;
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_prcp.gif"));  // Precipitation
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_ptyp.gif"));  // Precipitation Type
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_wind.gif"));  // Surface Winds
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_temp.gif"));  // Temperature
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_ptnd.gif"));  // Radar Reflectivity
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_0_clouds.gif"));  // Clouds
        mMapURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_WMC_vis.jpg"));  // Sat image
        mMapURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_WMC_irbw.jpg")); // IR
        mMapURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_WMC_wv.jpg")); // Weather
    }
    
    private void SetupValuesUI()
    {
        mValues = new ArrayList<DataValue>();
        mValues.add(new DataValue(0, 0, "Temperature"));
        mValues.add(new DataValue(0, 0, "Humidity"));
        mValues.add(new DataValue(0, "Wind Speed"));
        mValues.add(new DataValue(0, "Wind Direction"));
        mValues.add(new DataValue(0, "Baraometer", "%.2f"));
    }
    
    public void UpdateWxMap()
    {
        try {
          mCurMap++;
          if (mCurMap >= mMapURLs.size()) mCurMap=0;
          URL imgURL = new URL(mMapURLs.get(mCurMap));
          Image image = ImageIO.read(imgURL);
          if (image.getHeight(null) > 500)
            mWxImageLabel.setIcon(new ImageIcon(image.getScaledInstance(500, -1, Image.SCALE_AREA_AVERAGING)));
           else
            mWxImageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
          // Don't do anything
        }
    }
    
    public void UpdateClock()
    {
        String tstr = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now());
        mTimeLabel.setText(tstr);
    }
    
    public void UpdateDataValues()
    {
        GetInsideInfo();
        try {
            String urlstr = "http://forecast.weather.gov/MapClick.php?lat=38.11&lon=-122.57&unit=0&lg=english&FcstType=dwml";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            URL url = new URL(urlstr);
            
            Document doc = factory.newDocumentBuilder().parse(url.openStream());
            
            double d;
            d = ReadValueFromDoc(doc, "temperature");
            mValues.get(0).setValue(d, mInsideTemp);
            d = ReadValueFromDoc(doc, "humidity");
            mValues.get(1).setValue(d, mInsideHumidity);
            d = ReadValueFromDoc(doc, "wind-speed");
            mValues.get(2).setValue(d);
            d = ReadValueFromDoc(doc, "direction");
            mValues.get(3).setValue(d);
            d = ReadValueFromDoc(doc, "pressure");
            mValues.get(4).setValue(d);
        } catch (Exception e) {
            for (int i=0; i < mValues.size(); i++) {
                mValues.get(i).setValue(99.99);
            }
        }
    }
    
    
    
    public void UpdateForecastValues()
    {
        try {
            String urlstr = "http://forecast.weather.gov/MapClick.php?lat=38.11&lon=-122.57&unit=0&lg=english&FcstType=dwml";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            URL url = new URL(urlstr);
            
            Document doc = factory.newDocumentBuilder().parse(url.openStream());
            NodeList dataNodes = doc.getElementsByTagName("data");
            int numDataNodes = dataNodes.getLength();
            for (int n = 0; n < dataNodes.getLength(); n++) {
                // first let's find the icon data
                Element dataElement = (Element) dataNodes.item(n);
                String typeString = dataElement.getAttribute("type");
                if (typeString.equals("forecast")) {
                    // found the forecasts, now find the conditions icons
                    NodeList conditionNodes = dataElement.getElementsByTagName("conditions-icon");
                    int numConditionNodes = conditionNodes.getLength();
                    if (conditionNodes.getLength() > 0) {
                        Element childElement = (Element) conditionNodes.item(0);
                        NodeList iconNodes = childElement.getElementsByTagName("icon-link");
                        int numIconNodes = iconNodes.getLength();
                        
                        // iterate the icons
                        for (int i = 0; i < iconNodes.getLength(); i++) {
                           Element iconElement = (Element) iconNodes.item(i);
                           String iconURL = getCharacterDataFromElement(iconElement);
                           mForecastValues.get(i).setIconURL(iconURL);
                        }
                    }
                    
                    // Now let's find the min and max temperature data
                    NodeList tempNodes = dataElement.getElementsByTagName("temperature");
                    int numTempNodes = tempNodes.getLength();
                    for (int t = 0; t < tempNodes.getLength(); t++) {
                        Element tempElement = (Element) tempNodes.item(t);
                        String tempType = tempElement.getAttribute("type"); // this should be "minimum" or "maximum"
                        NodeList tempValueNodes = tempElement.getElementsByTagName("value");
                        for (int v = 0; v < tempValueNodes.getLength(); v++) {
                            Element value = (Element) tempValueNodes.item(v);
                            String strval = getCharacterDataFromElement(value);   
                            double d = Double.parseDouble(strval);
                            int valueIndex = 0;
                            LocalTime lt = LocalTime.now();
                            int hour = lt.getHour();
                            if (tempType.equals("minimum")) {
                                if (hour > 11)
                                    valueIndex = v*2;
                                else
                                    // set a minimum value
                                    valueIndex = v*2 + 1;
                            } else {
                                if (hour > 11)
                                    // set a maximum value
                                    valueIndex = v*2 + 1;
                                else
                                    valueIndex = v*2;
                            }
                            if (valueIndex < mForecastValues.size())
                                mForecastValues.get(valueIndex).setTemp(d);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // do nothing :(
        }
    }
    
    
    
    public static void main(String args[])
    {
        // Create the frame on the event dispatching thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiWeather piWXMain = new PiWeather();
                new UpdateUITimer(60, piWXMain);
                new MapUpdateTimer(10, piWXMain);
                new TimeUpdateTimer(1, piWXMain);
            }
        });
    }

}
