/**
 * Main for PiWeather
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.2
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import java.util.ArrayList;

import java.net.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;
import java.util.jar.*;

import javax.imageio.ImageIO;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Enumeration;

import java.time.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class PiWeather
{

    /** locations we know about */
    private static final String[][] msLocations = {
        {"Novato", "https://forecast.weather.gov/MapClick.php?lat=38.11&lon=-122.57&unit=0&lg=english&FcstType=dwml"},
        {"Petaluma", "https://forecast.weather.gov/MapClick.php?lat=38.2324&lon=-122.6366&unit=0&lg=english&FcstType=dwml"},
        {"Reno", "https://forecast.weather.gov/MapClick.php?lat=39.5296&lon=-119.8138&unit=0&lg=english&FcstType=dwml"},
        {"Escondido", "https://forecast.weather.gov/MapClick.php?lat=33.1192&lon=-117.0864&unit=0&lg=english&FcstType=dwml"},
        {"Chicago", "https://forecast.weather.gov/MapClick.php?lat=41.85&lon=-87.65&unit=0&lg=english&FcstType=dwml"},
        {"New York", "https://forecast.weather.gov/MapClick.php?lat=40.7142&lon=-74.0059&unit=0&lg=english&FcstType=dwml"},
    };


    /** possible raw data for airports */
    private static final String[][] msAirports = {
        {"KDVO", "https://w1.weather.gov/data/METAR/KDVO.1.txt"},
        {"KO69", "https://w1.weather.gov/data/METAR/KO69.1.txt"},
    };
    
    /** map URLs for the center column of the UI */
    private static final String[] msMapURLs = {
        "http://weather.rap.ucar.edu/model/ruc12hr_sfc_prcp.gif",  // Precipitation
        "http://weather.rap.ucar.edu/model/ruc12hr_sfc_ptyp.gif",  // Precipitation Type
        "http://weather.rap.ucar.edu/model/ruc12hr_sfc_wind.gif",  // Surface Winds
        "http://weather.rap.ucar.edu/model/ruc12hr_sfc_temp.gif",  // Temperature
        "http://weather.rap.ucar.edu/model/ruc12hr_sfc_ptnd.gif",  // Radar Reflectivity
        "http://weather.rap.ucar.edu/model/ruc12hr_0_clouds.gif",  // Clouds
    };
    
    /** satalite URLs for the center column of the UI */
    private static final String[] msSatURLs = {
        // Sat images - US
        "https://www.aviationweather.gov/data/obs/sat/us/sat_vis_us.jpg",  // Sat image
        "https://www.aviationweather.gov/data/obs/sat/us/sat_ircol_us.jpg",  // Sat IR
        "https://www.aviationweather.gov/data/obs/sat/us/sat_irbw_us.jpg",  // Sat IRBW
        "https://www.aviationweather.gov/data/obs/sat/us/sat_wv_us.jpg",  // Sat Weather

        // Sat Images - WMC (Winamuca, western US)
        "https://www.aviationweather.gov/data/obs/sat/us/sat_vis_wmc.jpg",  // Sat image
        "https://www.aviationweather.gov/data/obs/sat/us/sat_ircol_wmc.jpg",  // Sat IR
        "https://www.aviationweather.gov/data/obs/sat/us/sat_irbw_wmc.jpg",  // Sat IRBW
        "https://www.aviationweather.gov/data/obs/sat/us/sat_wv_wmc.jpg",  // Sat Weather
    };


    /** global variables for debugging */
    public static int msDebugLevel = 0;

    /** supported sensors */
    private static final String[] msSupportedSensors = {"DHT11", "DHT22", "BME280", "BMP280", "DUMMY"};
    
    /** trend data file */
    private String mTrendDataFilename;
    
    /** the name of the location */
    private String mLocationName;

    /** the location URL index we're getting */
    private String mLocationURL;
    

    
    /** cached current temperature */
    private double mCurrTemp = 0.0;
    /** cached sensor temperature */
    private double mInsideTemp = 0.0;
    
    /** cached current humidity */
    private double mCurrHumidity = 0.0;
    /** cached sensor humidity */
    private double mInsideHumidity = 0.0;
    
    /** cached current barometric pressure */
    private double mCurrPres = 0.0;
    /** cached sensor barometric pressure */
    private double mInsidePres = 0.0;
    
    /** cached current wind speed */
    private double mCurrSpeed = 0.0;
    /** cached current wind direction */
    private double mCurrDir = 0.0;
    
    /** cached observation time */
    private String mCurrObsTime = "";
    
    /** cached current conditios URL */
    private String mCurrConditionIconURL;
    
    /** currentl map display index */
    private int mCurMap = 0;
    
    /** current satalite image */
    private int mCurSat = 0;

    // Data and UI state
    /** the data values UI objects */
    private ArrayList<DataValue> mValues;
    /** the forcast UI objects */
    private ArrayList<ForecastDataValue> mForecastValues;

    
    /** the trend data */
    private ArrayList<TrendData> mTrendData;

    
    // UI components that need periodic update based on timer events
    /** the center panel map image */
    private JLabel mWxImageLabel;
    /** the center panel sat image */
    private JLabel mSatImageLabel;
    
    /** the trend display panel */
    private TrendDisplayPanel mTrendDisplayPanel;
    
    /** clock time label */
    private JLabel mTimeLabel;
    /** location label */
    private JLabel mLocationLabel;
    /** last update time label */
    private JLabel mLastUpdateLabel;
    /** last web observation time label */
    private JLabel mLastObsLabel;
    
    
    // Variables from command line switches
    /** are we on a pi */
    private boolean mIsPi = true;
    /** show full frame */
    private boolean mFullFrame = false;
    /** generate fake data */
    private boolean mGenFakeTrendData = false;
    /** save wx xml files */
    private boolean mSaveWxFiles = false;
    /** number of trend days to display - defaults to 3 days unless -td is specified on the command line */
    private int mTrendDataDays = 3;
    
    /** the weather sensor */
    private WxSensor mWxSensor;
    

    
    /**
     * Constructor for objects of class PiWeather
     * 
     * @param args the command line arguments
     * 
     */
    PiWeather(String args[])
    {
        ExtractJarData();
        
        ProcessArgs(args);
        
        mTrendData = new ArrayList<TrendData>();
        if (mGenFakeTrendData) {
            GenFakeTrendData();
        } else {
            ReadTextTrendData();
            CleanTrendData();
        }
        if (msDebugLevel > 0)
            DumpTrendData("---- Initial Read ----");
            
        if (HaveSensor())
            ReadInsideSensor();

        
        if (System.getProperty("os.name").equals("Mac OS X")) {  // should do better here, what about windows
            mIsPi = false;
        } else {
            mIsPi = true;
        }
        
        SetupUI();
    }
    
    /**
     * GetJarFilename() get the jar file name if we're in a jar file
     * @return returns the jar filename if we're executing from a jar.  null if not
     */
    private String GetJarFilename()
    {
        String myClassName = this.getClass().getName() + ".class";
        URL urlJar = this.getClass().getClassLoader().getSystemResource(myClassName);
        if (urlJar == null)
            return null;
        String urlStr = urlJar.toString();
        int from = "jar:file:".length();
        int to = urlStr.indexOf("!/");
        if (from == -1 || to == -1) return null;
        return urlStr.substring(from, to);
    }
    
    /**
     * ExtractJarData() Extract the sensors and icons from the jar file if we are in one
     * 
     */
    private void ExtractJarData()
    {
        String jarfilename = GetJarFilename();
        if (jarfilename != null) {
            try {
                // extract the sensors from the jar file
                JarFile jarfile = new JarFile(new File(jarfilename));
                Enumeration<JarEntry> enu = jarfile.entries();
                while(enu.hasMoreElements())
                {
                    String destdir = ".";     // destination directory is the current directory
                    JarEntry je = enu.nextElement();
            
                    if (je.getName().contains("sensor") || je.getName().contains("icons") ) {
                        File fl = new File(destdir, je.getName());
                        if (!fl.exists()) {
                            fl.getParentFile().mkdirs();
                            fl = new File(destdir, je.getName());
                        }
                        
                        if (je.isDirectory()) 
                            continue;
                        if (msDebugLevel > 0)
                            System.out.println(je.getName());
                        InputStream is = jarfile.getInputStream(je);
                        FileOutputStream fo = new FileOutputStream(fl);
                        
                        while (is.available() > 0)
                            fo.write(is.read());

                        fo.close();
                        is.close();
                    }
                }
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * IsSupportedSensor() determine if the specified sensor is available
     * 
     * @param sensor name of the sensor to check
     * 
     * @return boolean true if the sensor is found
     * 
     */
    private boolean IsSupportedSensor(String sensor)
    {
        for (String s: msSupportedSensors) {
            if (s.equalsIgnoreCase(sensor)) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * 
     * HaveSensor() determine if a sensor has been specified.  We can't actually determine if a sensor exists.
     * 
     * @return true if a valid sensor was specified.
     * 
     */
    public boolean HaveSensor()
    {
        return (mWxSensor != null);
    }
    
    /**
     * GetWxSensor() get the sensor class for the named sensor
     * 
     * @param sensor Name of the sensor
     * 
     * @return returns a new WxSensor of the matching type or null if no matching class is found
     */
    private WxSensor GetWxSensor(String sensor)
    {
        switch (sensor) {
        case "DHT11":
            return new DHT11_Sensor();
        case "DHT22":
            return new DHT22_Sensor();
        case "BME280":
            return new BME280_Sensor();
        case "BMP280":
            return new BMP280_Sensor();
        case "DUMMY":
            return new Dummy_Sensor();
        }
        
        return null;
    }
    
    /**
     * ProcessArgs()  Parse the command line arguments.  Sets variable to match arguments
     * 
     * @param args the arguments array
     * 
     * 
     */
    private void ProcessArgs(String[] args)
    {
        for (int i = 0; i < args.length; i++) {
            switch(args[i]) {
            case "-s": // sensor
                if (i+1 >= args.length) {
                    PrintUsage();
                    System.exit(1);
                } else {
                    if (IsSupportedSensor(args[i+1])) {
                        String sensor = args[i+1].toUpperCase();
                        mWxSensor = GetWxSensor(sensor);
                        if (msDebugLevel > 0 && mWxSensor != null)
                            System.out.println("Sensor: " + mWxSensor.GetName());
                        i++;
                    } else {
                        System.err.println("Bad Sensor type");
                        PrintUsage();
                        System.exit(1);
                    }
                }
                break;
                
            case "-f": // full frame
                mFullFrame = true;
                break;
                
            case "-l": // specify location
                if (i+1 >= args.length) {
                    PrintUsage();
                    System.exit(1);
                } else {
                    boolean found = false;
                    for (int l = 0; l < msLocations.length; l++) {
                        String locUC = args[i+1].toUpperCase();
                        if (locUC.equals(msLocations[l][0].toUpperCase())) {
                            mLocationName = msLocations[l][0];
                            mLocationURL = msLocations[l][1];
                            mTrendDataFilename = "data/" + mLocationName + "_trend_data.txt";
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Location not found");
                        System.exit(1);
                    }
                    i++;
                }
                break;
                
            case "-airport": case "-a":
                if (i+1 >= args.length) {
                    PrintUsage();
                    System.exit(1);
                } else {
                    String airport = args[i+1].toUpperCase();
                    String data = AirportData.FindAirport(airport);
                    if (data == null) {
                        System.out.println("Airport not found");
                        System.exit(1);
                    }
                    String country = AirportData.GetAirportCountry(data);
                    if (country == null || !country.equals("United States")) {
                        System.out.println("Only us airports supported");
                        System.exit(1);
                    }
                    // found the airport and it's a US city
                    double lat = AirportData.GetAirportLatitude(data);
                    double lng = AirportData.GetAirportLongitude(data);
                    if (lat != -999 && lng != -999) { // got valid data
                        mLocationName = airport;
                        mLocationURL = "https://forecast.weather.gov/MapClick.php?lat=" +
                                        String.format("%.4f", lat) + "&lon=" +
                                        String.format("%.4f", lng) + "&unit=0&lg=english&FcstType=dwml";
                        mTrendDataFilename = "data/" + airport + "_trend_data.txt";
                    }
                    i++;
                }
                break;
                
            case "-list": // list locations and exit
                for (String[] loc : msLocations) {
                    System.out.println(loc[0]);
                }
                System.exit(1);
                
            case "-ll": // by Lat Long
                System.out.println("Not yet Implemented");
                System.exit(1);
                
            case "-ftd": // generate take Trend Data
                mGenFakeTrendData = true;
                mTrendDataFilename = "data/fake_trend_data.txt";
                break;
                
            case "-td": // set the number of trend data days to display
                if (i+1 >= args.length) {
                    PrintUsage();
                    System.exit(1);
                } else {
                    try {
                        mTrendDataDays = Integer.parseInt(args[i+1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number of days for trend data");
                        PrintUsage();
                        System.exit(1);
                    }
                    if (mTrendDataDays < 0 || mTrendDataDays > 30) {
                        System.err.println("Invalid number of days for trend data");
                        PrintUsage();
                        System.exit(1);
                    }
                    i++;
                }
                break;
                
            case "-h": // help
                PrintUsage();
                System.exit(1);
                
            case "-debug":
                if (i+1 >= args.length) {
                    PrintUsage();
                    System.exit(1);
                } else {
                    try {
                        msDebugLevel = Integer.parseInt(args[i+1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid debug level");
                        PrintUsage();
                        System.exit(1);
                    }
                    if (msDebugLevel < 0 || msDebugLevel > 5) {
                        System.err.println("Invalid debug level");
                        PrintUsage();
                        System.exit(1);
                    }
                    i++;
                }
                break;
                
            case "-wx": // save wx files
                mSaveWxFiles = true;
                break;
                
            default: // nothing else matches
                System.err.println("Unknown switch");
                PrintUsage();
                System.exit(1);
            }
        }
        
        // if no location was specified setup Novato
        if (mLocationName == null) {
            mLocationName = msLocations[0][0];
            mLocationURL = msLocations[0][1];
            mTrendDataFilename = "data/" + mLocationName + "_trend_data.txt";
        }
    }
    
    /**
     * PrintUsate() prints a useage messge on how to use the application
     */
    private void PrintUsage()
    {
        System.out.println("Usage: PiWeather -s [DHT11, DHT22, BME280, DUMMY] -td 4 -f");
        System.out.println("  -s              Sensor type, one of DHT11, DHT22, BME280 or DUMMY The DUMMY sensor is a simulatored sensor for testing");
        System.out.println("  -f               Full frame");
        System.out.println("  -td <num>        Show num (1-30) days of trend data.  0 == cycle through # of days");
        System.out.println("  -ftd             Generate Fake Trend Data and exit");
        System.out.println("  -wx              Save Wx files for later analysis");
        System.out.println("  -l               Specify a location");
        System.out.println("  -list            Show a list of known locations and exit");
        System.out.println("  -ll <lat> <long> Use Lat Long for location");
        System.out.println("  -airport         Use an airport (must be US) as a location");
        System.out.println("  -debug <level>   Debug Level (0-5)");
    }
    
    
    
    /**
     * SetupUI()  Setup the User Interface
     * 
     */
    private void SetupUI()
    {
        // Create a new JFrame container.
        JFrame jfrm = new JFrame("Pi Wx Display");

        // Terminate the program when the user closes the application
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (mFullFrame) {
            // set properties
            jfrm.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            jfrm.setUndecorated(true);
        } else {
            jfrm.setSize(1024, 600);
        }

        // Main panel to add the sub panels too
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLACK);
        BoxLayout frameBox = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
        mainPanel.setLayout(frameBox);
        
        
        mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        

        mainPanel.add(SetupLeftPanel());

        mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));
  
        mainPanel.add(SetupCenterPanel());

        mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        mainPanel.add(SetupRightPanel());
        
        // Add the panel to the frame
        jfrm.add(mainPanel);
        
        // Display the frame.
        jfrm.setVisible(true);
    }
    

    /**
     * SetupLeftPanel()  Sets up the UI on the left column of the application.
     * 
     * This shows the time, location, temps, humidity, wind, barometric pressure and update times
     * 
     * @return returns a JPanel for the left chunk of UI
     */
    private JPanel SetupLeftPanel()
    {
        // Left Panel for the local current observations
        JPanel leftPanel = new JPanel();
        BoxLayout leftBox = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        leftPanel.setLayout(leftBox);
        leftPanel.setBackground(Color.BLACK);
       
        mTimeLabel = new JLabel(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
        mTimeLabel.setForeground(Color.white);
        mTimeLabel.setFont(new Font("Monospaced", Font.PLAIN, 36));
        
        
        leftPanel.add(mTimeLabel);
        
        mLocationLabel = new JLabel(mLocationName);
        mLocationLabel.setForeground(Color.green);
        mLocationLabel.setFont(new Font("Monospaced", Font.PLAIN, 28));
        leftPanel.add(mLocationLabel);
        
        leftPanel.add(Box.createVerticalGlue());
        
        // create the Current Conditions values list
        mValues = new ArrayList<DataValue>();
        if (HaveSensor() && mWxSensor.HasTemperature())
            mValues.add(new DataValue(0, 0, "Temperature (out|in)", "%2.0f|%.0f"));
        else
            mValues.add(new DataValue(0, "Temperature"));

        
        if (HaveSensor() && mWxSensor.HasHumidity()) 
            mValues.add(new DataValue(0, 0, "Humidity    (out|in)", "%2.0f|%.0f"));
        else 
            mValues.add(new DataValue(0, "Humidity"));

        // Someday wind will come :)
        mValues.add(new DataValue(0, 0, "Wind", "%.0f@%.0f"));
        
        if (HaveSensor() && mWxSensor.HasBarometricPressure())
            mValues.add(new DataValue(0, "Barometer (out/in)", "<html>%.2f<br>%.2f</html>"));
        else
            mValues.add(new DataValue(0, "Barometer", "%.2f"));

            
        for (int i = 0; i < mValues.size(); i++) {
            DataValue dv = mValues.get(i);
            leftPanel.add(dv.getValueLabel());
            leftPanel.add(dv.getLegendLabel());
            leftPanel.add(Box.createVerticalGlue());
        }

        String lustr = "Last Update";
        if (HaveSensor()) lustr+= " - " + mWxSensor.GetName();
        JLabel lu = new JLabel(lustr);
        lu.setForeground(Color.green);
        lu.setFont(new Font("Monospaced", Font.PLAIN, 12));
        leftPanel.add(lu);
        
        mLastUpdateLabel = new JLabel("Now");
        mLastUpdateLabel.setForeground(Color.green);
        mLastUpdateLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        SetLastUpdateTime();
        
        leftPanel.add(mLastUpdateLabel);
        
        mLastObsLabel = new JLabel("Now");
        mLastObsLabel.setForeground(Color.white);
        mLastObsLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        leftPanel.add(mLastObsLabel);
        
        JButton quitButton = new JButton("Quit");
        quitButton.setActionCommand("quit");
        
        quitButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                    System.exit(0);
             }          
          });
          
        
        JComboBox<String> chooser = new JComboBox<String>(new String[]{"Cycle", "1", "2", "3", "4", "5", "6", "7", "10", "15", "20", "30"}) {
            /** 
             * @inherited <p>
             */
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.width = getPreferredSize().width;
                max.height = getPreferredSize().height;
                return max;
            }
        };
        if (mTrendDataDays >= 0 && mTrendDataDays <=10) {
            chooser.setSelectedItem(Integer.toString(mTrendDataDays));
        } else if (mTrendDataDays > 10 && mTrendDataDays <= 20) {
            chooser.setSelectedItem("15");
        } if (mTrendDataDays > 20) {
            chooser.setSelectedItem("20");
        }
        chooser.setPrototypeDisplayValue("XXXXX");
        chooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox jcmbType = (JComboBox) e.getSource();
                String selStr = jcmbType.getSelectedItem().toString();
                if (selStr.equals("Cycle")) {
                    mTrendDataDays = 0; // cycle
                } else {
                    mTrendDataDays = Integer.parseInt(selStr);
                }
                mTrendDisplayPanel.UpdateNumDays(mTrendData, mTrendDataDays);
            }
        });

        JPanel ctlPanel = new JPanel();
        BoxLayout ctlBox = new BoxLayout(ctlPanel, BoxLayout.X_AXIS);
        ctlPanel.setLayout(ctlBox);
        ctlPanel.setBackground(Color.BLACK);
        
        ctlPanel.add(quitButton);
        ctlPanel.add(chooser);
        
        ctlPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(ctlPanel);

        
        return leftPanel;
    }
    
    
    /**
     * SetupCenterPanel() contructs the center panel.
     * This shows maps and satalite imagery for the western US.
     * 
     * @return JPanel for the center of the UI
     */
    private JPanel SetupCenterPanel()
    {
        mCurMap = 0;
        mCurSat = 0;

        // Center Panel for the weather map images
        JPanel centerPanel = new JPanel();
        BoxLayout imageBox = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        centerPanel.setLayout(imageBox);
        centerPanel.setBackground(Color.BLACK);
        
        mWxImageLabel = new JLabel("");
        centerPanel.add(mWxImageLabel);
        
        centerPanel.add(Box.createVerticalGlue());
        
        mSatImageLabel = new JLabel("");
        centerPanel.add(mSatImageLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        return centerPanel;
     }
    
     
     /**
      * SetupRightPanel() setup the right section of the window showing forecasts info and trend data from past queries of data
      * 
      * @return JPanel of the right side of the UI
      * 
      */
    private JPanel SetupRightPanel()
    {
        // allow 18 slots
        mForecastValues = new ArrayList<ForecastDataValue>();
        for (int fc = 0; fc < 18; fc++) {
            ForecastDataValue fv = new ForecastDataValue();
            mForecastValues.add(fv);
        }
        
        JPanel rightMainPanel = new JPanel();
        BoxLayout rightMainBox = new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS);
        rightMainPanel.setLayout(rightMainBox);
        rightMainPanel.setBackground(Color.BLACK);
        rightMainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // right panel for the forcast info
        JPanel forcastPanel = new JPanel();
        BoxLayout forcastBox = new BoxLayout(forcastPanel, BoxLayout.X_AXIS);
        forcastPanel.setLayout(forcastBox);
        forcastPanel.setBackground(Color.BLACK);
        

        
        JPanel leftListPanel = new JPanel();
        leftListPanel.setBackground(Color.BLACK);
        BoxLayout leftListPanelBox = new BoxLayout(leftListPanel, BoxLayout.Y_AXIS);
        leftListPanel.setLayout(leftListPanelBox);
        
        JPanel rightListPanel = new JPanel();
        rightListPanel.setBackground(Color.BLACK);
        BoxLayout rightListPanelBox = new BoxLayout(rightListPanel, BoxLayout.Y_AXIS);
        rightListPanel.setLayout(rightListPanelBox);
        
        // only show 12 for now
        for (int i = 0; i < 10; i+=2) {
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
            leftListPanel.add(Box.createRigidArea(new Dimension(0, 2)));

            
            
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
            rightListPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        }
        forcastPanel.add(leftListPanel);

        forcastPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        forcastPanel.add(rightListPanel);
        
        rightMainPanel.add(forcastPanel);
        
        mTrendDisplayPanel = new TrendDisplayPanel(mTrendDataDays, mWxSensor, msDebugLevel > 0);
        
        rightMainPanel.add(mTrendDisplayPanel);
 
        return rightMainPanel;
    }
  
    
    /**
     * ReadInsideSensor()  Read the specified sensor if it is specified.
     * 
     */
    private void ReadInsideSensor()
    {
        if (HaveSensor()) {
            mWxSensor.RefreshSensorData();
            mInsideTemp = mWxSensor.GetTemperature();
            mInsideHumidity = mWxSensor.GetHumidity();
            mInsidePres = mWxSensor.GetBarometricPressure();
        } else {
            mInsideTemp = 0.0;
            mInsideHumidity = 0.0;
            mInsidePres = 0.0;
        }
    }
    
    
    /****************************************************************************************
     *  Info from the weather forcast document downloaded
     *
     *
     ****************************************************************************************/
    /**
     * GetCharacterDataFromElement()  Get data from the specified element of the Document
     * 
     * @param e docuement element  to query data from
     * 
     * @return returns the string for the element e
     */
    private static String GetCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }

    
    /**
     * ReadValueFromDoc()  Read a double value from the document
     * 
     * @param doc the Document
     * @param e the element to read
     * 
     * @return a double value for e or 0 if it fails
     */
    private static double ReadValueFromDoc(Document doc, String elem)
    {
        NodeList dataNodes = doc.getElementsByTagName("data");
        for (int n = 0; n < dataNodes.getLength(); n++) {
            Element element = (Element) dataNodes.item(n);
            String typeString = element.getAttribute("type");
            if (typeString.equals("current observations")) {
                NodeList nodes = element.getElementsByTagName(elem);
                // iterate the pressures
                for (int i = 0; i < nodes.getLength(); i++) {
                   Element childElement = (Element) nodes.item(i);
                   NodeList value = childElement.getElementsByTagName("value");
                   Element line = (Element) value.item(0);
                   String strval = GetCharacterDataFromElement(line);   
                   if (strval.equals("NA"))
                       continue;
                   try {    
                       double d = Double.parseDouble(strval);
                       return d;
                    } catch (Exception e) {
                        System.out.println("Bad Double Val " + elem + " = " + strval);
                        return 0.0;
                    }
                }
            }
        }
        
        return 0;
    }
    
    
    
    /**
     * ReadStringFromDoc()  Read a string value from the document
     * 
     * @param doc the Document
     * @param e the element to read
     * 
     * @return a string if found or the empty string (not null) if it isn't found
     */
    private static String ReadStringFromDoc(Document doc, String e)
    {
        NodeList dataNodes = doc.getElementsByTagName("data");
        for (int n = 0; n < dataNodes.getLength(); n++) {
            Element element = (Element) dataNodes.item(n);
            String typeString = element.getAttribute("type");
            if (typeString.equals("current observations")) {
                NodeList nodes = element.getElementsByTagName(e);
                
                for (int i = 0; i < nodes.getLength(); i++) {
                   // just the first
                   return GetCharacterDataFromElement((Element) nodes.item(i));
                }
            }
        }
        
        return "";
    }
    
    
    
    
    /**
     * ReadCurrentConditionsIconFromDocument() Get the URL for the current condtions icon
     * 
     * @param doc Document to read from
     * 
     * @return the URL or an empty string
     */
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
                    String strval = GetCharacterDataFromElement(line);
                    return strval;
                }

            }
        }
        return "";
    }
    
    
    /**
     * UpdateWxMap()  Update the weather maps
     * 
     */
    public void UpdateWxMap()
    {
        int imageSize = 275;
        try {
          mCurMap++;
          if (mCurMap >= msMapURLs.length) mCurMap=0;
          URL imgURL = new URL(msMapURLs[mCurMap]);
          Image image = ImageIO.read(imgURL);
          if (image.getHeight(null) > imageSize)
            mWxImageLabel.setIcon(new ImageIcon(image.getScaledInstance(imageSize, -1, Image.SCALE_AREA_AVERAGING)));
           else
            mWxImageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
          // Don't do anything
          System.out.println("Failed to get image " + msMapURLs[mCurMap]);
        }
        
        try {
          mCurSat++;
          if (mCurSat >= msSatURLs.length) mCurSat=0;
          URL imgURL = new URL(msSatURLs[mCurSat]);
          Image image = ImageIO.read(imgURL);
          if (image.getHeight(null) > imageSize)
            mSatImageLabel.setIcon(new ImageIcon(image.getScaledInstance(imageSize, -1, Image.SCALE_AREA_AVERAGING)));
           else
            mSatImageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
          // Don't do anything
          System.out.println("Failed to get image " + msSatURLs[mCurSat]);
        }
    }
    
    
    
    
    /**
     * UpdateClock() Update the cock UI
     * 
     */
    public void UpdateClock()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String tstr = dateTime.format(formatter);
        mTimeLabel.setText(tstr);
    }
    
    
    
    
    /**
     * UpdateFromSensor()  Update the UI with current sensor data, inside and out.
     * 
     */
    public void UpdateFromSensor()
    {
        if (HaveSensor()) {
            ReadInsideSensor();
            if (mWxSensor.HasTemperature())
                mValues.get(0).setValue(mCurrTemp, mInsideTemp);
            if (mWxSensor.HasHumidity())
                mValues.get(1).setValue(mCurrHumidity, mInsideHumidity);
            if (mWxSensor.HasBarometricPressure())
                mValues.get(3).setValue(mCurrPres, mInsidePres);
        }
    }
    
    
    
    
    /**
     * SetLastUpdateTime()  Show the last update time in the UI
     * 
     */
    private void SetLastUpdateTime()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String tstr = dateTime.format(formatter);
        mLastUpdateLabel.setText(tstr);
    }
    
    
    
    
    /**
     * SaveWxXMLFile()  Save weather file data.  This is mostly for troubleshooting
     */
    private void SaveWxXMLFile()
    {
        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HH_mm");
        String tstr = dt.format(formatter);
        // save the xml
        Path p = Paths.get("./wxfiles/wxfile_" + mLocationName + "_" + tstr + ".xml");
        
        try {
            URL url = new URL(mLocationURL);
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
            OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(p, CREATE, APPEND));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println("Got line:"+line);
                writer.write(line + "\n");
            }
            reader.close();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("SaveWxXMLFile: Catch exception");
            System.err.println(e);
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * UpdateFromWeb()  Get an update from the web.  Called by the UI update timer
     * 
     */
    public void UpdateFromWeb()
    {
        if (HaveSensor())
            ReadInsideSensor();

        SetLastUpdateTime();
        
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            if (mSaveWxFiles) {
                LocalDateTime dt = LocalDateTime.now();
                if (dt.getMinute()%20 == 0) { // only save every 20 minutes max
                    SaveWxXMLFile();
                }
            }
            
            //System.out.println("Dump wx file:"+ mLocationURL);
            //SaveWxXMLFile();
            //System.out.println("Done Dump");
            //System.out.println("Reading From Web");
            URL url = new URL(mLocationURL);
            InputStream stream = url.openStream();
            //System.out.println("Stream Open");
            Document doc = factory.newDocumentBuilder().parse(stream);
            //System.out.println("done reading from web");
           
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
            System.out.println("UpdateFromWeb: Catch exception");
            e.printStackTrace();
            mLastUpdateLabel.setForeground(Color.red);
        }
    }
    
    
    
    /**
     * DumpTrendData(String header)  Dump the trend data to standard out.
     * 
     * @param header a header to show first
     * 
     */
    private void DumpTrendData(String header)
    {
        System.out.println(header);
        int i = 0;
        for (TrendData td : mTrendData) {
            System.out.print(String.format("%4d ", i++));
            System.out.println(td.toString());
        }

        if (mTrendData.size() > 1) {
            System.out.println("First: " + mTrendData.get(0).GetDateTime());
            System.out.println("Last:  " + mTrendData.get(mTrendData.size()-1).GetDateTime());
        }
        System.out.println("----");
    }
    
    
    
    
    /**
     * CleanTrendData()  Clean up trend data.  If it's unreasonable grab the previous entry to "fix" it
     * 
     */
    private void CleanTrendData()
    {
        // look for humidity zeros and ...  First one better be good...
        for (int i = 1; i < mTrendData.size(); i++)
        {
            if (mTrendData.get(i).GetBarometer() < 25 || mTrendData.get(i).GetBarometer() > 32)
                mTrendData.get(i).SetBarometer(mTrendData.get(i-1).GetBarometer());
            if (mTrendData.get(i).GetHumidity() < 0 || mTrendData.get(i).GetHumidity() > 100)
                mTrendData.get(i).SetHumidity(mTrendData.get(i-1).GetHumidity());
            if (mTrendData.get(i).GetSensorHumidity() < 0 || mTrendData.get(i).GetSensorHumidity() > 100)
                mTrendData.get(i).SetSensorHumidity(mTrendData.get(i-1).GetSensorHumidity());
            if (mTrendData.get(i).GetSensorTemp() < -40 || mTrendData.get(i).GetSensorTemp() > 150)
                mTrendData.get(i).SetSensorTemp(mTrendData.get(i-1).GetSensorTemp());
        }
    }
    
    
    
    
    /**
     * GenFakeTrendData()  generate 3 days of fake data for trouble shooting
     * 
     */
    private void GenFakeTrendData()
    {
        // Generate 3 days worth
        for (int i = 24*6*3; i > 0; i--) {
            LocalDateTime n = LocalDateTime.now();
            LocalDateTime t = n.minusMinutes(i*10);
            // 2017-02-07T07:55:00
            String obstime = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(t) +"T" + DateTimeFormatter.ofPattern("HH:mm:ss").format(t);
            // -20 to 120
            double temp = -20 + i % 140;
            // 0 to 100
            double humidity = i % 100;
            // 27 to 32
            double press = 27 + (i%500)/100.0;
            mTrendData.add(new TrendData(t, obstime, temp, humidity, press, temp+3, humidity+3, press + 0.2));
        }
    }
    
    
    
    
    /**
     * UpdateDataValues(Document doc)  Get the current observation data from the doc
     * 
     * @param doc the Document
     * 
     * @return true on sucess
     */
    private boolean UpdateDataValues(Document doc)
    {
        try {
            mCurrObsTime = ReadStringFromDoc(doc, "start-valid-time");
            mLastObsLabel.setText(mCurrObsTime);
            
            mCurrTemp = ReadValueFromDoc(doc, "temperature");
            if (HaveSensor() && mWxSensor.HasTemperature()) {
                mValues.get(0).setValue(mCurrTemp, mInsideTemp);
                // match the width
                if (mCurrTemp >= 100) {
                    mValues.get(0).setFormat("%3.0f|%.0f");
                } else {
                    mValues.get(0).setFormat("%2.0f|%.0f");
                }
                
            } else {
                mValues.get(0).setValue(mCurrTemp);
            }
            
            mCurrHumidity = ReadValueFromDoc(doc, "humidity");
            if (HaveSensor() && mWxSensor.HasHumidity()) {
                mValues.get(1).setValue(mCurrHumidity, mInsideHumidity);
                // match the width
                if (mCurrHumidity >= 100) {
                    mValues.get(1).setFormat("%3.0f|%.0f");
                } else {
                    mValues.get(1).setFormat("%2.0f|%.0f");
                }
                
            } else {
                mValues.get(1).setValue(mCurrHumidity);
            }
            
            
            
            mCurrDir = ReadValueFromDoc(doc, "direction");
            mCurrSpeed = ReadValueFromDoc(doc, "wind-speed");
            mValues.get(2).setValue(mCurrDir, mCurrSpeed);
            
            mCurrPres = ReadValueFromDoc(doc, "pressure");
            if (HaveSensor() && mWxSensor.HasBarometricPressure()) {
                mValues.get(3).setValue(mCurrPres, mInsidePres);
            } else {
                mValues.get(3).setValue(mCurrPres);
            }
            
            boolean addOrRemoved = false;
            // sparce after we have 10
            if (mTrendData.size() < 10 || !mCurrObsTime.equals(mTrendData.get(mTrendData.size()-1).GetObsTime())) {
                if (HaveSensor()) {
                    mTrendData.add(new TrendData(LocalDateTime.now(),
                                    mCurrObsTime, mCurrTemp, mCurrHumidity, mCurrPres,
                                    mInsideTemp, mInsideHumidity, mInsidePres));
                } else {
                    mTrendData.add(new TrendData(LocalDateTime.now(),
                                    mCurrObsTime, mCurrTemp, mCurrHumidity, mCurrPres,
                                    0, 0, 0));
                }
                addOrRemoved = true;
            }
            
            // cull out "old" data, save 30 days worth, we can display less
            while (mTrendData.size() > 2 &&
                    Duration.between(mTrendData.get(0).GetDateTime(),
                                     mTrendData.get(mTrendData.size()-1).GetDateTime()).getSeconds() > 60*60*24*30) { 
                mTrendData.remove(0);
                addOrRemoved = true;
            }
            
            if (addOrRemoved) {
                CleanTrendData();
                SaveTextTrendData();
                if (msDebugLevel > 0)
                    DumpTrendData("---- Post Save Dump at at " + mCurrObsTime + " ----");
            }
            
            mTrendDisplayPanel.UpdateData(mTrendData);

            mCurrConditionIconURL = ReadCurrentConditionsIconFromDocument(doc);
        } catch (Exception e) {
            e.printStackTrace();
            mLastUpdateLabel.setForeground(Color.red);
            return false;
        }
        return true;
    }
    
    
    
    
    /**
     * SaveTextTrendData() Save Trend Data in text format
     * 
     */
    private void SaveTextTrendData()
    {
        try {
            File file = new File(mTrendDataFilename);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(file);
            writer.println("2"); //version number
            for (TrendData td : mTrendData) {
                String s =  td.GetDateTime().toString() + "|" +
                            td.GetObsTime() + "|" +
                            String.format("%f", td.GetTemp()) + "|" +
                            String.format("%f", td.GetHumidity()) + "|" +
                            String.format("%f", td.GetBarometer()) + "|" +
                            String.format("%f", td.GetSensorTemp()) + "|" +
                            String.format("%f", td.GetSensorHumidity()) + "|" +
                            String.format("%f", td.GetSensorBarometer());
                writer.println(s);
            }
            writer.close();
        } catch (IOException e) {
           System.err.println("error writing trend data file");
           e.printStackTrace();
        }
    }
     


    /**
     * SetDataFromString() set a TrendData from a saved string
     * 
     * @param version   the version of the data file
     * @param s         the string of data "|" deliniated
     * @param td        TendData to fill in
     * 
     */
    private void SetDataFromString(int version, String s, TrendData td)
    {
        String[] data = s.split("\\|");
        switch(version) {
        case 1:
            td.SetDateTime(LocalDateTime.parse(data[0]));
            td.SetObsTime(data[1]);
            td.SetTemp(Double.parseDouble(data[2]));
            td.SetHumidity(Double.parseDouble(data[3]));
            td.SetBarometer(Double.parseDouble(data[4]));
            td.SetSensorTemp(Double.parseDouble(data[5]));
            td.SetSensorHumidity(Double.parseDouble(data[6]));
            break;
        case 2:
            td.SetDateTime(LocalDateTime.parse(data[0]));
            td.SetObsTime(data[1]);
            td.SetTemp(Double.parseDouble(data[2]));
            td.SetHumidity(Double.parseDouble(data[3]));
            td.SetBarometer(Double.parseDouble(data[4]));
            td.SetSensorTemp(Double.parseDouble(data[5]));
            td.SetSensorHumidity(Double.parseDouble(data[6]));
            td.SetSensorBarometer(Double.parseDouble(data[7]));
            break;
        }
    }

    
    
    
    /**
     * ReadTextTrendData() Read Trend Data in text format
     * 
     */
    private void ReadTextTrendData()
    {
        File f = new File(mTrendDataFilename);
        if (f.exists() && f.canRead()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;
                int version = 1;
                if ((line = reader.readLine()) != null) {
                    // the first line is the version number
                    version = Integer.parseInt(line);
                }
    
                while ((line = reader.readLine()) != null)
                {
                  TrendData td = new TrendData();
                  SetDataFromString(version, line, td);
                  mTrendData.add(td);
                }
                reader.close();
            } catch (Exception e) {
                System.err.format("Exception occurred trying to read '%s'.", mTrendDataFilename);
                e.printStackTrace();
            }
        }
    }
    
    
    
    
    /**
     * UpdateForecastValues()  Update the forecast data from the web
     * 
     * @param doc the Document to read from
     * 
     * @return true if we successfully handled the update
     */
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
                           String iconURL = GetCharacterDataFromElement(iconElement);
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
                            String strval = GetCharacterDataFromElement(valueElement);   
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
    
    
    
    
    
        

  
    /**
     * main The main
     * 
     * @param args the command line arguments
     * 
     */
    public static void main(String args[])
    {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiWeather piWXMain = new PiWeather(args);
                piWXMain.UpdateFromWeb();
                new UIUpdateTimer(5 * 60, piWXMain); // update every 5 minutes
                new MapUpdateTimer(10, piWXMain);
                if (piWXMain.HaveSensor()) new SensorUpdateTimer(5, piWXMain);
                new TimeUpdateTimer(1, piWXMain);
            }
        });
    }

}
