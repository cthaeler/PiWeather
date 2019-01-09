/**
 * Main for PiWeather
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.3
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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
        {"Novato",    "38.11",   "-122.57"},
        {"Petaluma",  "38.2324", "-122.6366"},
        {"Reno",      "39.5296", "-119.8138"},
        {"Escondido", "33.1192", "-117.0864"},
        {"Chicago",   "41.85",   "-87.65"},
        {"New_York",  "40.7142", "-74.0059"},
        {"Santa_Rosa", "38.5",   "-122.8"},
    };


    /** possible raw data for airports */
    private static final String[][] msAirports = {
        {"KDVO", "https://w1.weather.gov/data/METAR/KDVO.1.txt"},
        {"KO69", "https://w1.weather.gov/data/METAR/KO69.1.txt"},
    };
    
    /** map URLs for the center column of the UI */
    private static final String[][] msMapURLs = {      
        {"http://weather.rap.ucar.edu/model/ruc12hr_sfc_prcp.gif" ,"RUC12 - sfc - prcp"},  // Precipitation
        {"http://weather.rap.ucar.edu/model/ruc12hr_sfc_ptyp.gif" ,"RUC12 - sfc - prcp type"},  // Precipitation Type
        {"http://weather.rap.ucar.edu/model/ruc12hr_sfc_wind.gif" ,"RUC12 - sfc - Winds"},  // Surface Winds
        {"http://weather.rap.ucar.edu/model/ruc12hr_sfc_temp.gif" ,"RUC12 - sfc - Temp"},  // Temperature
        {"http://weather.rap.ucar.edu/model/ruc12hr_sfc_ptnd.gif" ,"RUC12 - sfc - radar"},  // Radar Reflectivity
        {"http://weather.rap.ucar.edu/model/ruc12hr_0_clouds.gif" ,"RUC12 - clouds"},  // Clouds
    };
    
    /** satalite URLs for the center column of the UI */
    private static final String[][] msSatURLs = {
        // Sat images - US
        {"https://www.aviationweather.gov/data/obs/sat/us/sat_vis_us.jpg", "US - Vis"},  // Sat image
        {"https://www.aviationweather.gov/data/obs/sat/us/sat_ircol_us.jpg", "US - IR"},  // Sat IR
        {"https://www.aviationweather.gov/data/obs/sat/us/sat_irbw_us.jpg", "US - IRBW"},  // Sat IRBW
        {"https://www.aviationweather.gov/data/obs/sat/us/sat_wv_us.jpg", "US - Wx"},  // Sat Weather

        // Sat Images - WMC (Winamuca, western US)
        {"https://www.aviationweather.gov/data/obs/sat/us/sat_vis_wmc.jpg", "WMC - Vis"},  // Sat image
        {"https://www.aviationweather.gov/data/obs/sat/us/sat_ircol_wmc.jpg", "WMC - IR"},  // Sat IR
        {"https://www.aviationweather.gov/data/obs/sat/us/sat_irbw_wmc.jpg", "WMC - IRBW"},  // Sat IRBW
        {"https://www.aviationweather.gov/data/obs/sat/us/sat_wv_wmc.jpg", "WMC - Wx"},  // Sat Weather
        
        // GOES
        {"https://www.ssd.noaa.gov/goes/west/weus/avn-l.jpg", "GOES-West IR"},    // GOES-West IR
        {"https://www.ssd.noaa.gov/goes/west/weus/vis-l.jpg", "GOES-West Visual"},    // GOES-West Visual
        {"https://www.ssd.noaa.gov/goes/west/weus/wv-l.jpg",  "GOES-West Water Vapor"},     // GOES-West Water Vapor
  
    };



    /** supported sensors */
    private static final String[] msSupportedSensors = {"DHT11", "DHT22", "BME280", "BMP280", "DUMMY"};
    
    /** trend data file */
    private String mTrendDataFilename;
    
    /** the name of the location */
    private String mLocationName;

    /** the location URL index we're getting */
    private String mLocationURL;
    

    /** Data from a data sensor */
    private SensorData mSensorData;
    /** Data from a weather.gov site */
    private SensorData mWebData;
    /** Forecast Data from weather.gov site */
    private ForecastData mForecastData;
    
    /** Wx Trend data */
    private TrendData mTrendData;


  
    /** cached observation time */
    private String mCurrObsTime = "";

    
    
    /** currentl map display index */
    private int mCurMap = 0;
    
    /** current satalite image */
    private int mCurSat = 0;

    // Data and UI state
    /** the data values UI objects */
    private ArrayList<DataValueUI> mValues;
    /** the forcast UI objects */
    private ArrayList<ForecastDataValueUI> mForecastValues;



    
    // UI components that need periodic update based on timer events
    /** the center panel map image */
    private JLabel mWxImageBitmap;
    private JLabel mWxImageLabel;
    /** the center panel sat image */
    private JLabel mSatImageBitmap;
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
    
    /** global variables for debugging */
    private static Verbosity msDebugLevel = Verbosity.ShowSilent;

    

    
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
        
        
        /* create data set objects and do an intial update */
        if (HaveSensor()) {
            mSensorData = new SensorData();
            mSensorData.UpdateFromSensor(mWxSensor);
        }
        mWebData = new SensorData();
        mForecastData = new ForecastData(15);
        mWebData.UpdateFromWeb(mLocationURL);
        mCurrObsTime = mWebData.GetObsTime();
        mForecastData.UpdateFromWeb(mLocationURL, mWebData);
        
        mTrendData = new TrendData();

        
        if (mGenFakeTrendData) {
            mTrendData.GenFakeTrendData();
        } else {
            mTrendData.ReadFromFile(mTrendDataFilename);
            mTrendData.CleanTrendData();
        }

        //if (DebugLevel().ShowDebugging())
        //    mTrendData.DumpTrendData("---- Initial Read ----", "---- END of Trend Data Dump ----");

        
        if (System.getProperty("os.name").equals("Mac OS X")) {  // should do better here, what about windows
            mIsPi = false;
        } else {
            mIsPi = true;
        }
        
        SetupUI();
    }
    
    /**
     * DebugLevel() get the debug level
     */
     public static Verbosity DebugLevel()
     {
        return msDebugLevel;
     }
     
     /**
      * DumpException(String errorString, Exception e)
      */
      public static void DumpError(String errorString, Exception exception)
      {
        if (DebugLevel().ShowErrors()) {
            System.err.println();
            //System.err.println((char)27 + "[31m");
            System.err.println("ERROR: " + errorString);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd MMM, yyyy");
            LocalDateTime dateTime = LocalDateTime.now();

            System.err.print(dateTime.format(formatter));
            //System.err.println((char)27 + "[39;49m");
        }
        if (exception != null && DebugLevel().ShowStackTrace()) {
             exception.printStackTrace();
        }
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
                        if (msDebugLevel.ShowInformation())
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
                DumpError("ExtractJarData", e);
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
     * SetupLocationFromLatLong()
     *
     */
    private void SetupLocationFromLatLong(double lat, double lng)
    {
         mLocationURL = "https://forecast.weather.gov/MapClick.php?lat=" +
                            String.format("%.4f", lat) + "&lon=" +
                            String.format("%.4f", lng) + "&unit=0&lg=english&FcstType=dwml";
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
                        if (msDebugLevel.ShowInformation() && mWxSensor != null)
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
                            double lat = Double.parseDouble(msLocations[l][1]);
                            double lng = Double.parseDouble(msLocations[l][2]);
                            SetupLocationFromLatLong(lat, lng);
                            mTrendDataFilename = "data/" + mLocationName + "_trend_data.txt";
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Location not found");
                        System.exit(1);
                    } else {
                        System.out.println(mLocationName + " URL is " + mLocationURL);
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
                        mTrendDataFilename = "data/" + mLocationName + "_trend_data.txt";
                        SetupLocationFromLatLong(lat, lng);
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
                if (i+2 >= args.length) {
                    PrintUsage();
                    System.exit(1);
                } else {
                    String latStr = args[i+1];
                    String longStr = args[i+2];
                    try {
                        mLocationName = "LL_"+latStr+"_"+longStr;
                        mTrendDataFilename = "data/" + mLocationName + "_trend_data.txt";
                        double lat = Double.parseDouble(latStr);
                        double lng = Double.parseDouble(longStr);
                        SetupLocationFromLatLong(lat, lng);
                    } catch (Exception e) {
                        System.out.println("Bad lat long");
                        PrintUsage();
                        System.exit(1);
                    }
                }
                i+=2;
                break;
                
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
                        int level = Integer.parseInt(args[i+1]);
                        try {
                            msDebugLevel = Verbosity.values()[ level ];
                        } catch (Exception e) {
                            System.err.println("Invalid debug level: " + level);
                            PrintUsage();
                            System.exit(1);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid debug level");
                        PrintUsage();
                        System.exit(1);
                    }
                    if (msDebugLevel.ShowInformation()) {
                        System.out.println("Debug Level set to: " + msDebugLevel);
                    }
                    i++;
                }
                break;
                
            case "-wx": // save wx files
                mSaveWxFiles = true;
                break;
                
            default: // nothing else matches
                DumpError("Unknown switch", null);
                PrintUsage();
                System.exit(1);
            }
        }
        
        // if no location was specified setup Novato
        if (mLocationName == null) {
            mLocationName = msLocations[0][0];
            double lat = Double.parseDouble(msLocations[0][1]);
            double lng = Double.parseDouble(msLocations[0][2]);
            SetupLocationFromLatLong(lat, lng);
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
        System.out.println("  -debug <level>   Debug Level (0 is the default)");

        int i = 0;
        for (Verbosity v : Verbosity.values()) {
            System.out.println("            " + i++ + "      " + v);
        }

    }
    
    
    
    /**
     * SetupUI()  Setup the User Interface
     * 
     */
    private void SetupUI()
    {
        // Create a new JFrame container.
        String frameLabel = "Pi Wx Display";
        if (!DebugLevel().ShowSilent()) frameLabel += " - " + DebugLevel();
        JFrame jfrm = new JFrame(frameLabel);



        // Terminate the program when the user closes the application
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (mFullFrame) {
            // set properties
            jfrm.setSize(Toolkit.getDefaultToolkit().getScreenSize());
           // jfrm.setUndecorated(true);
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
        

        try {            
            jfrm.setIconImage(ImageIO.read(new File("./icon.gif")));
        } catch (Exception e) {
            DumpError("SetupUI: Icon Not found", e);
        }

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
        mValues = new ArrayList<DataValueUI>();
        if (HaveSensor() && mWxSensor.HasTemperature())
            mValues.add(new DataValueUI(0, 0, "Temperature (out|in)", "%2.0f|%.0f"));
        else
            mValues.add(new DataValueUI(0, "Temperature"));

        
        if (HaveSensor() && mWxSensor.HasHumidity()) 
            mValues.add(new DataValueUI(0, 0, "Humidity    (out|in)", "%2.0f|%.0f"));
        else 
            mValues.add(new DataValueUI(0, "Humidity"));

        // Someday wind will come :)
        mValues.add(new DataValueUI(0, 0, "Wind", "%.0f@%.0f"));
        
        if (HaveSensor() && mWxSensor.HasBarometricPressure())
            mValues.add(new DataValueUI(0, "Barometer (out/in)", "<html>%.2f<br>%.2f</html>"));
        else
            mValues.add(new DataValueUI(0, "Barometer", "%.2f"));

            
        for (int i = 0; i < mValues.size(); i++) {
            DataValueUI dv = mValues.get(i);
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
        } else if (mTrendDataDays > 10 && mTrendDataDays < 20) {
            chooser.setSelectedItem("15");
        } if (mTrendDataDays >= 20 && mTrendDataDays < 30) {
            chooser.setSelectedItem("20");
        } if (mTrendDataDays >= 30) {
            chooser.setSelectedItem("30");
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
        
        // Wx Images
        mWxImageBitmap = new JLabel("");
        centerPanel.add(mWxImageBitmap);
        mWxImageLabel = new JLabel("WxImg");
        mWxImageLabel.setForeground(Color.white);
        centerPanel.add(mWxImageLabel);
        
        centerPanel.add(Box.createVerticalGlue());
        
        // Satelite Images
        mSatImageBitmap = new JLabel("");
        centerPanel.add(mSatImageBitmap);
        
        mSatImageLabel = new JLabel("Sat Img");
        mSatImageLabel.setForeground(Color.white);
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
        mForecastValues = new ArrayList<ForecastDataValueUI>();
        for (int fc = 0; fc < 10; fc++) {
            ForecastDataValueUI fv = new ForecastDataValueUI();
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
        
        // only show 10 for now
        for (int i = 0; i < 10; i+=2) {
            // left one
            ForecastDataValueUI lfv = mForecastValues.get(i);
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

            
            
            ForecastDataValueUI rfv = mForecastValues.get(i+1);
            
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
        
        mTrendDisplayPanel = new TrendDisplayPanel(mTrendDataDays, mWxSensor, false);
        
        rightMainPanel.add(mTrendDisplayPanel);
 
        return rightMainPanel;
    }
  
    
    
    /**
     *  UpdateWxMapImage()
     */
     
    private void UpdateWxMapImage(String urlStr, String text, JLabel bitmapLabel, JLabel textLabel)
    {
        int imageSize = 275;
        try {
          URL url = new URL(urlStr);
          URLConnection con = url.openConnection();
          con.setConnectTimeout(5000);
          con.setReadTimeout(5000);
          InputStream in = con.getInputStream();
        
          Image image = ImageIO.read(in);
          if (image.getHeight(null) > imageSize)
            bitmapLabel.setIcon(new ImageIcon(image.getScaledInstance(imageSize, -1, Image.SCALE_AREA_AVERAGING)));
           else
            bitmapLabel.setIcon(new ImageIcon(image));
          textLabel.setText(text);
          textLabel.setForeground(Color.white);
        } catch (Exception e) {
          if (DebugLevel().ShowStackTrace()) e.printStackTrace();
          // Don't do anything
          DumpError("UpdateWxMapImage: Failed to get image " + msMapURLs[mCurMap][0], e);
          
          try {
              // Load a dummy image
              Image image = ImageIO.read(new File("usa-physical.jpg"));
              if (image.getHeight(null) > imageSize)
                bitmapLabel.setIcon(new ImageIcon(image.getScaledInstance(imageSize, -1, Image.SCALE_AREA_AVERAGING)));
               else
                bitmapLabel.setIcon(new ImageIcon(image));
              textLabel.setText("Bad Image");
              textLabel.setForeground(Color.red);
            } catch (Exception ioe) {
                DumpError("UpdateWxMapImage: Failed to get Map image us-physical.jpg", ioe);
            }
        }
    }
      
    /**
     * UpdateWxMap()  Update the weather maps
     * 
     */
    public void UpdateWxMap()
    {
 
        /* first the Map */
        mCurMap++;
        if (mCurMap >= msMapURLs.length) mCurMap=0;
        UpdateWxMapImage(msMapURLs[mCurMap][0], msMapURLs[mCurMap][1], mWxImageBitmap, mWxImageLabel);
        
        /* then the Sat map */
        mCurSat++;
        if (mCurSat >= msSatURLs.length) mCurSat=0;
        UpdateWxMapImage(msSatURLs[mCurSat][0], msSatURLs[mCurSat][1], mSatImageBitmap, mSatImageLabel);
    }
    
    
    
    
    /**
     * UpdateClock() Update the clock UI
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
     * UpdateFromSensor() Sensor UI
     * 
     */
    public void UpdateFromSensor()
    {
        if (HaveSensor()) {
            mSensorData.UpdateFromSensor(mWxSensor);
            UpdateDataValuesUI();
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
     * UpdateFromWeb()  Get an update from the web.  Called by the UI update timer
     * 
     */
    public void UpdateFromWeb()
    {
        SetLastUpdateTime();
        
        //if (HaveSensor())
        //   mSensorData.UpdateFromSensor(mWxSensor);
            
        mWebData.UpdateFromWeb(mLocationURL);
        mCurrObsTime = mWebData.GetObsTime();

        mForecastData.UpdateFromWeb(mLocationURL, mWebData);


        if (UpdateDataValuesUI()) {
            
            if (UpdateForecastValuesUI()) {
                mLastObsLabel.setForeground(Color.GREEN);
            } else {
                mLastObsLabel.setForeground(Color.YELLOW);
            }
        } else {
            mLastObsLabel.setForeground(Color.RED);
        }
        
        
        return; 
    }


    /**
     * UpdateDataValuesUI()  Get the current observation data from the SensorData objects
     * 
     * @return true on sucess
     */
    private boolean UpdateDataValuesUI()
    {
        try {
            mLastObsLabel.setText(mCurrObsTime);
            /* Temperature */
            if (HaveSensor() && mWxSensor.HasTemperature()) {
                double webTemp = mWebData.GetTemp();
                mValues.get(0).setValue(webTemp, mSensorData.GetTemp());
                // match the width
                if (webTemp >= 100) {
                    mValues.get(0).setFormat("%3.0f|%.0f");
                } else {
                    mValues.get(0).setFormat("%2.0f|%.0f");
                }
                
            } else {
                mValues.get(0).setValue(mWebData.GetTemp());
            }
            
            /* Humidity */
            if (HaveSensor() && mWxSensor.HasHumidity()) {
                double webHumidity = mWebData.GetHumidity();
                mValues.get(1).setValue(webHumidity, mSensorData.GetHumidity());
                // match the width
                if (webHumidity >= 100) {
                    mValues.get(1).setFormat("%3.0f|%.0f");
                } else {
                    mValues.get(1).setFormat("%2.0f|%.0f");
                }
                
            } else {
                mValues.get(1).setValue(mWebData.GetHumidity());
            }
            
            /* wind speed and direction */
            mValues.get(2).setValue(mWebData.GetWindDirection(), mWebData.GetWindSpeed());
            
            /* Barometric Preassure */
            if (HaveSensor() && mWxSensor.HasBarometricPressure()) {
                mValues.get(3).setValue(mWebData.GetBarometer(), mSensorData.GetBarometer());
            } else {
                mValues.get(3).setValue(mWebData.GetBarometer());
            }
            
            
            
            boolean addOrRemoved = false;
            // sparce after we have 10
            if (mTrendData.NumValues() < 10 || !mCurrObsTime.equals(mTrendData.GetObsTime(mTrendData.NumValues()-1))) {
                if (HaveSensor()) {
                    mTrendData.AddValueSet(LocalDateTime.now(), mCurrObsTime,
                                    mWebData.GetTemp(), mWebData.GetHumidity(), mWebData.GetBarometer(),
                                    mSensorData.GetTemp(), mSensorData.GetHumidity(), mSensorData.GetBarometer());
                } else {
                    mTrendData.AddValueSet(LocalDateTime.now(), mCurrObsTime,
                                    mWebData.GetTemp(), mWebData.GetHumidity(), mWebData.GetBarometer(),
                                    0, 0, 0);
                }
                addOrRemoved = true;
            }
            // cull out "old" data, save 30 days worth, we can display less
            while (mTrendData.NumValues() > 2 &&
                    Duration.between(mTrendData.GetDateTime(0),
                                     mTrendData.GetDateTime(mTrendData.NumValues()-1)).getSeconds() > 60*60*24*30) { 
                mTrendData.DeleteByIndex(0);
                addOrRemoved = true;
            }
            
            if (addOrRemoved) {
                mTrendData.CleanTrendData();
                mTrendData.WriteToFile(mTrendDataFilename);
                if (DebugLevel().ShowDebugging())
                    mTrendData.DumpTrendData("---- Post Save Dump at at " + mCurrObsTime + " ----", "---- END of Dump ----");
            }
            
            mTrendDisplayPanel.UpdateData(mTrendData);

        } catch (Exception e) {
            DumpError("UpdateDataValueUIs:", e);
            mLastUpdateLabel.setForeground(Color.red);
            return false;
        }
        return true;
    }
    

   
    
    /**
     * UpdateForecastValuesUI()  Update the forecast data from the web
     * 
     * 
     * @return true if we successfully handled the update
     */
    private boolean UpdateForecastValuesUI()
    {
        try {
            for (int i = 0; i < mForecastValues.size(); i++) {
                double temp = mForecastData.GetTemp(i);
                String url = mForecastData.GetIconURL(i);
                String info = mForecastData.GetInfo(i);
                if (url == null) break;

                mForecastValues.get(i).setTemp(temp);
                mForecastValues.get(i).setIconURL(url);
                mForecastValues.get(i).setInfo(info, false);
            }
        } catch (Exception e) {
            DumpError("UpdateForecastValues", e);
            return false;
        }
        return true;
    }
    
    
    




    
    public static PiWeather piWXMain;        

  
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
                piWXMain = new PiWeather(args);
                
                piWXMain.UpdateFromWeb();
                
                new UIUpdateTimer(5 * 60, piWXMain); // update every 5 minutes
                
                new MapUpdateTimer(10, piWXMain);
                
                if (piWXMain.HaveSensor()) new SensorUpdateTimer(5, piWXMain);
                
                new TimeUpdateTimer(1, piWXMain);
            }
        });
    }

}
