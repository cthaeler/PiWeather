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

import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

import javax.imageio.ImageIO;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import java.time.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class PiWeather
{
    // Novato
    private int mLocationURL = 0;
    private String[][] mLocations = {
        {"Novato", "http://forecast.weather.gov/MapClick.php?lat=38.11&lon=-122.57&unit=0&lg=english&FcstType=dwml"},
        {"Reno", "http://forecast.weather.gov/MapClick.php?lat=39.5296&lon=-119.8138&unit=0&lg=english&FcstType=dwml"},
        {"Escondido", "http://forecast.weather.gov/MapClick.php?lat=33.1192&lon=-117.0864&unit=0&lg=english&FcstType=dwml"},
    };
    
    private boolean mSaveXMLFile = true;
    private double mCurrTemp = 0.0;
    private double mInsideTemp = 0.0;
    private double mCurrHumidity = 0.0;
    private double mInsideHumidity = 0.0;
    private double mCurrPres = 0.0;
    private double mCurrSpeed = 0.0;
    private double mCurrDir = 0.0;
    private String mCurrConditionIconURL;

    private ArrayList<DataValue> mValues;
    private ArrayList<ForecastDataValue> mForecastValues;
    private ArrayList<String> mMapURLs;
    private ArrayList<String> mSatURLs;
    private int mCurMap = 0;
    private int mCurSat = 0;

    private JLabel mWxImageLabel;
    private JLabel mSatImageLabel;
    
    private JLabel mTimeLabel;
    private JLabel mLocationLabel;
    private JLabel mLastUpdateLabel;
    private JButton mQuitButton;
    
    private boolean mIsPi = true;
    private boolean mHasSensor = false;
    private String mSensor = "";
    

    
    /**
     * Constructor for objects of class PiWeather
     */
    PiWeather(String args[])
    {
        if (args.length > 0)
            mSensor = args[0];
        
        if (System.getProperty("os.name").equals("Mac OS X")) {
            mIsPi = false;
            mHasSensor = false;
            
            // allow for the fake mac sensor
            if (mSensor.length() > 0) {
                mHasSensor = true;
                if (!mSensor.equals("mac")) {
                    System.err.println("Bad sensor type");
                    System.exit(1);
                }
            }
        } else {
            mIsPi = true;
            mHasSensor = false;
            if (mSensor.length() > 0) {
                mHasSensor = true;
                if (!(mSensor.equals("DHT11") || mSensor.equals("DHT22"))) {
                    System.err.println("Bad sensor type");
                    System.exit(1);
                }
            }
        }
        // Create a new JFrame container.
        JFrame jfrm = new JFrame("Pi Wx Display");

        // Terminate the program when the user closes the application
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (mIsPi) {
            // set properties
            jfrm.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            jfrm.setUndecorated(true);
        } else {
            jfrm.setSize(1024, 600);
        }
        
        if (mHasSensor) {
            ReadInsideSensor();
        } else {
            mInsideTemp = 0.0;
            mInsideHumidity = 0.0;
        }

        // Main panel to add the sub panels too
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLACK);
        BoxLayout frameBox = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
        mainPanel.setLayout(frameBox);
        
        mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        mainPanel.add(SetupLeftPanel());

        mainPanel.add(Box.createRigidArea(new Dimension(30, 0)));
  
        mainPanel.add(SetupCenterPanel());

        mainPanel.add(Box.createRigidArea(new Dimension(30, 0)));

        mainPanel.add(SetupRightPanel());
        
        // Add the panel to the frame
        jfrm.add(mainPanel);
        
        // Display the frame.
        jfrm.setVisible(true);
    }
    
    private JPanel SetupLeftPanel()
    {
        // Left Panel for the local current observations
        JPanel leftPanel = new JPanel();
        BoxLayout leftBox = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        leftPanel.setLayout(leftBox);
        leftPanel.setBackground(Color.BLACK);
       
        mTimeLabel = new JLabel(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()));
        mTimeLabel.setForeground(Color.white);
        mTimeLabel.setFont(new Font("Monospaced", Font.PLAIN, 36));
        
        
        leftPanel.add(mTimeLabel);
        
        mLocationLabel = new JLabel(mLocations[mLocationURL][0]);
        mLocationLabel.setForeground(Color.green);
        mLocationLabel.setFont(new Font("Monospaced", Font.PLAIN, 28));
        leftPanel.add(mLocationLabel);
        
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // create the Current Conditions values list
        mValues = new ArrayList<DataValue>();
        if (mHasSensor) {
            mValues.add(new DataValue(0, 0, "Temperature (out|in)", "%2.0f|%.0f"));
            mValues.add(new DataValue(0, 0, "Humidity    (out|in)", "%2.0f|%.0f"));
        } else {
            mValues.add(new DataValue(0, "Temperature"));
            mValues.add(new DataValue(0, "Humidity"));
        }
        mValues.add(new DataValue(0, 0, "Wind", "%.0f@%.0f"));
        mValues.add(new DataValue(0, "Barometer", "%.2f"));
        for (int i = 0; i < mValues.size(); i++) {
            DataValue dv = mValues.get(i);
            leftPanel.add(dv.getValueLabel());
            leftPanel.add(dv.getLegendLabel());
            
            leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JLabel lu = new JLabel("Last Update");
        lu.setForeground(Color.green);
        lu.setFont(new Font("Monospaced", Font.PLAIN, 12));
        leftPanel.add(lu);
        
        mLastUpdateLabel = new JLabel("Now");
        mLastUpdateLabel.setForeground(Color.green);
        mLastUpdateLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        SetLastUpdateTime();
        
        leftPanel.add(mLastUpdateLabel);
        
        mQuitButton = new JButton("Quit");
        mQuitButton.setActionCommand("quit");
        
        mQuitButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                    System.exit(0);
             }          
          });
          
        leftPanel.add(mQuitButton);
        
        return leftPanel;
    }
    
    private JPanel SetupCenterPanel()
    {
        mMapURLs = new ArrayList<String>();
        mCurMap = 0;
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_prcp.gif"));  // Precipitation
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_ptyp.gif"));  // Precipitation Type
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_wind.gif"));  // Surface Winds
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_temp.gif"));  // Temperature
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_sfc_ptnd.gif"));  // Radar Reflectivity
        mMapURLs.add(new String("http://weather.rap.ucar.edu/model/ruc12hr_0_clouds.gif"));  // Clouds
        

        mSatURLs = new ArrayList<String>();
        mCurSat = 0;
        // Sat images - US
        mSatURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_US_vis.jpg"));  // Sat image
        mSatURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_US_ir.jpg")); // IR
        mSatURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_US_irbw.jpg")); // IRBW
        mSatURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_US_wv.jpg")); // Weather
        // Sat Images - WMC (Winamuca, western US)
        mSatURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_WMC_vis.jpg"));  // Sat image
        mSatURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_WMC_ir.jpg")); // IR
        mSatURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_WMC_irbw.jpg")); // IRBW
        mSatURLs.add(new String("http://www.aviationweather.gov/adds/data/satellite/latest_sm_WMC_wv.jpg")); // Weather


        // Center Panel for the weather map images
        JPanel centerPanel = new JPanel();
        BoxLayout imageBox = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        centerPanel.setLayout(imageBox);
        centerPanel.setBackground(Color.BLACK);
        
        mWxImageLabel = new JLabel("");
        centerPanel.add(mWxImageLabel);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        mSatImageLabel = new JLabel("");
        centerPanel.add(mSatImageLabel);
        
        return centerPanel;
     }
    
    private JPanel SetupRightPanel()
    {
        // allow 18 slots
        mForecastValues = new ArrayList<ForecastDataValue>();
        for (int fc = 0; fc < 18; fc++) {
            ForecastDataValue fv = new ForecastDataValue();
            mForecastValues.add(fv);
        }
        
        // right panel for the forcast info
        JPanel rightMainPanel = new JPanel();
        BoxLayout rightMainBox = new BoxLayout(rightMainPanel, BoxLayout.X_AXIS);
        rightMainPanel.setLayout(rightMainBox);
        rightMainPanel.setBackground(Color.BLACK);
        
        JPanel leftListPanel = new JPanel();
        leftListPanel.setBackground(Color.BLACK);
        BoxLayout leftListPanelBox = new BoxLayout(leftListPanel, BoxLayout.Y_AXIS);
        leftListPanel.setLayout(leftListPanelBox);
        
        JPanel rightListPanel = new JPanel();
        rightListPanel.setBackground(Color.BLACK);
        BoxLayout rightListPanelBox = new BoxLayout(rightListPanel, BoxLayout.Y_AXIS);
        rightListPanel.setLayout(rightListPanelBox);
        
        // only show 12 for now
        for (int i = 0; i < 12; i+=2) {
            // left one
            ForecastDataValue lfv = mForecastValues.get(i);
            // a forecast panel contains an info panel with temp and info and the image
            JPanel leftFP = new JPanel();
            leftFP.setBackground(Color.BLACK);
            BoxLayout leftFB = new BoxLayout(leftFP, BoxLayout.X_AXIS);
            leftFP.setLayout(leftFB);
            
            // The info Panel contains temp and info strings
            JPanel leftIP = new JPanel();
            leftIP.setBackground(Color.BLACK);
            BoxLayout infoBox = new BoxLayout(leftIP, BoxLayout.Y_AXIS);
            leftIP.setLayout(infoBox);
            
            JLabel leftTL = lfv.getTempLabel();
            leftTL.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftIP.add(leftTL);
            JLabel leftIL = lfv.getInfoLabel();
            leftIL.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftIP.add(leftIL);
            
            leftIP.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftFP.add(leftIP);

            leftFP.add(Box.createRigidArea(new Dimension(10, 0)));
            
            JLabel leftImg = lfv.getImageLabel();
            leftImg.setAlignmentX(Component.RIGHT_ALIGNMENT);
            leftFP.add(leftImg);
            
            leftListPanel.add(leftFP);
            leftListPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            
            
            ForecastDataValue rfv = mForecastValues.get(i+1);
            
            JPanel rightFP = new JPanel();
            rightFP.setBackground(Color.BLACK);
            BoxLayout rightFB = new BoxLayout(rightFP, BoxLayout.X_AXIS);
            rightFP.setLayout(rightFB);
            
            JLabel rightImg = rfv.getImageLabel();
            rightImg.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightFP.add(rightImg);
            rightFP.add(Box.createRigidArea(new Dimension(10, 0)));
            
            JPanel rightIP = new JPanel();
            rightIP.setBackground(Color.BLACK);
            BoxLayout rightIB = new BoxLayout(rightIP, BoxLayout.Y_AXIS);
            rightIP.setLayout(rightIB);
            
            JLabel rightTL = rfv.getTempLabel();
            rightTL.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightIP.add(rightTL);
            JLabel rightIL = rfv.getInfoLabel();
            rightIL.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightIP.add(rightIL);
            
            rightIP.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightFP.add(rightIP);
            
            rightListPanel.add(rightFP);
            //rightListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        rightMainPanel.add(leftListPanel);
        rightMainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        rightMainPanel.add(rightListPanel);

        return rightMainPanel;
    }
        
    
    private void ReadInsideSensor()
    {
        try {
            Runtime rt = Runtime.getRuntime();
            String line;
            String[] data;
            String cmdStr;
            if (mIsPi) {
                if (mSensor.equals("DHT11"))
                    cmdStr = "python ./dht11.py";
                else
                    cmdStr = "python ./dht.py";
            } else {
                cmdStr = "python ./dht_mac.py";
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
    
    private String ReadCurrentConditionsIconFromDocument(Document doc)
    {
        NodeList dataNodes = doc.getElementsByTagName("data");
        for (int n = 0; n < dataNodes.getLength(); n++) {
            Element element = (Element) dataNodes.item(n);
            String typeString = element.getAttribute("type");
            if (typeString.equals("current observations")) {
                NodeList nodes = element.getElementsByTagName("conditions-icon");
                
                if (nodes.getLength() > 0) {
                    Element e = (Element)nodes.item(0);
                    NodeList iconNodes = e.getElementsByTagName("icon-link");
                    Element line = (Element) iconNodes.item(0);
                    String strval = getCharacterDataFromElement(line);
                    return strval;
                }

            }
        }
        return "";
    }
    
    
    
    public void UpdateWxMap()
    {
        int imageSize = 275;
        try {
          mCurMap++;
          if (mCurMap >= mMapURLs.size()) mCurMap=0;
          URL imgURL = new URL(mMapURLs.get(mCurMap));
          Image image = ImageIO.read(imgURL);
          if (image.getHeight(null) > imageSize)
            mWxImageLabel.setIcon(new ImageIcon(image.getScaledInstance(imageSize, -1, Image.SCALE_AREA_AVERAGING)));
           else
            mWxImageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
          // Don't do anything
        }
        
        try {
          mCurSat++;
          if (mCurSat >= mSatURLs.size()) mCurSat=0;
          URL imgURL = new URL(mSatURLs.get(mCurSat));
          Image image = ImageIO.read(imgURL);
          if (image.getHeight(null) > imageSize)
            mSatImageLabel.setIcon(new ImageIcon(image.getScaledInstance(imageSize, -1, Image.SCALE_AREA_AVERAGING)));
           else
            mSatImageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
          // Don't do anything
        }
    }
    
    public void UpdateClock()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String tstr = dateTime.format(formatter);
        mTimeLabel.setText(tstr);
    }
    
    public boolean HasSensor()
    {
        return mHasSensor;
    }
    
    public void UpdateFromSensor()
    {
        if (mHasSensor) {
            ReadInsideSensor();
            mValues.get(0).setValue(mCurrTemp, mInsideTemp);
        }
    }
    
    
    private void SetLastUpdateTime()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String tstr = dateTime.format(formatter);
        mLastUpdateLabel.setText(tstr);
    }
    
    
    private void SaveWxXMLFile()
    {
        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HH_mm");
        String tstr = dt.format(formatter);
        // save the xml
        Path p = Paths.get("./wxfiles/wxfile_"+mLocations[mLocationURL][0]+"_"+tstr+".xml");
        try {
            URL url = new URL(mLocations[mLocationURL][1]);
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
            OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(p, CREATE, APPEND));
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
            writer.flush();
        } catch (IOException x) {
            System.err.println(x);
        }
    }
    
    public void UpdateFromWeb()
    {
        if (mHasSensor) {
            ReadInsideSensor();
        }

        SetLastUpdateTime();
        
        mLocationURL++;
        if (mLocationURL >= mLocations.length) mLocationURL = 0;
        mLocationLabel.setText(mLocations[mLocationURL][0]);
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            
            if (false) {
                LocalDateTime dt = LocalDateTime.now();
                if (mSaveXMLFile && dt.getMinute()%20 == 0) {
                    SaveWxXMLFile();
                }
            }

            URL url = new URL(mLocations[mLocationURL][1]);
            InputStream stream = url.openStream();
            Document doc = factory.newDocumentBuilder().parse(stream);
            

            if (UpdateDataValues(doc)) {
                // don't bother trying to update the forcast if the data values update failed
                if (UpdateForecastValues(doc)) {
                    mLastUpdateLabel.setForeground(Color.green);
                } else {
                    mLastUpdateLabel.setForeground(Color.yellow);
                }
            } else {
                mLastUpdateLabel.setForeground(Color.red);
            }
        } catch (Exception e) {
            mLastUpdateLabel.setForeground(Color.red);
        }
    }
    
    private boolean UpdateDataValues(Document doc)
    {
        try {
            mCurrTemp = ReadValueFromDoc(doc, "temperature");
            if (mHasSensor) {
                mValues.get(0).setValue(mCurrTemp, mInsideTemp);
                // match the width
                if (mCurrTemp >= 100) {
                    mValues.get(0).setFormat("%3.0f|%.0f");
                    mValues.get(1).setFormat("%3.0f|%.0f");
                } else {
                    mValues.get(0).setFormat("%2.0f|%.0f");
                    mValues.get(1).setFormat("%2.0f|%.0f");
                }
            } else {
                mValues.get(0).setValue(mCurrTemp);
            }
            
            mCurrHumidity = ReadValueFromDoc(doc, "humidity");
            if (mHasSensor)
                mValues.get(1).setValue(mCurrHumidity, mInsideHumidity);
            else
                mValues.get(1).setValue(mCurrHumidity);
            
            mCurrDir = ReadValueFromDoc(doc, "direction");
            mCurrSpeed = ReadValueFromDoc(doc, "wind-speed");
            mValues.get(2).setValue(mCurrDir, mCurrSpeed);
            mCurrPres = ReadValueFromDoc(doc, "pressure");
            mValues.get(3).setValue(mCurrPres);
            
            mCurrConditionIconURL = ReadCurrentConditionsIconFromDocument(doc);
        } catch (Exception e) {
            mLastUpdateLabel.setForeground(Color.red);
            return false;
        }
        return true;
    }
    
    
   
    private boolean UpdateForecastValues(Document doc)
    {
        try {
            NodeList dataNodes = doc.getElementsByTagName("data");

            // the 0'th element is the current condition
            mForecastValues.get(0).setTemp(mCurrTemp);
            mForecastValues.get(0).setIconURL(mCurrConditionIconURL);
            mForecastValues.get(0).setInfo("Now", false);
            
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
                                mForecastValues.get(svt+1).setInfo(info, ((svt % 2) == 0));
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
                           String iconURL = getCharacterDataFromElement(iconElement);
                           mForecastValues.get(i+1).setIconURL(iconURL);
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
                            String strval = getCharacterDataFromElement(valueElement);   
                            double d = Double.parseDouble(strval);
                            int valueIndex = valueNodeIdx*2 + numSeq;
                            
                            if (valueIndex < mForecastValues.size())
                                mForecastValues.get(valueIndex).setTemp(d);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    
    
    public static void main(String args[])
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiWeather piWXMain = new PiWeather(args);
                piWXMain.UpdateFromWeb();
                new UpdateUITimer(30, piWXMain);
                new MapUpdateTimer(5, piWXMain);
                if (piWXMain.HasSensor()) new UpdateSensorTimer(5, piWXMain);
                new TimeUpdateTimer(1, piWXMain);
            }
        });
    }

}
