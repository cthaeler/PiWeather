/**
 * Main for PiWeather
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.1
 */

import java.awt.event.*;
import javax.swing.*;
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
import java.time.format.DateTimeFormatter;

class PiWeather
{
 
    private ArrayList<DataValue> mValues;
    private ArrayList<String> mMapURLs;
    private int mCurMap;
    private JLabel mWxImageLabel;
    private JLabel mUpdateTimeLabel;
    private JButton mQuitButton;
    
    private Toolkit mToolkit;
    private Timer mTimer;
    
    /**
     * Constructor for objects of class PiWeather
     */
    PiWeather()
    {
        SetupMapList();
        
        // Create a new JFrame container.
        JFrame jfrm = new JFrame("A Simple Swing Application");

        // Terminate the program when the user closes the application
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.BLACK);
        BoxLayout frameBox = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
        mainPanel.setLayout(frameBox);
        
        JPanel leftPanel = new JPanel();
        BoxLayout leftBox = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        leftPanel.setLayout(leftBox);
        leftPanel.setBackground(Color.BLACK);
        
        if (System.getProperty("os.name").equals("Mac OS X")) {
            jfrm.setSize(1024, 600);
        } else {
            // set properties
            jfrm.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            jfrm.setUndecorated(true);
        }
        
       
        mUpdateTimeLabel = new JLabel(DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.now()));
        mUpdateTimeLabel.setForeground(Color.white);
        mUpdateTimeLabel.setFont(new Font("Serif", Font.PLAIN, 48));
        
        leftPanel.add(mUpdateTimeLabel);
        
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

        mWxImageLabel = new JLabel("Wx Map");
        mainPanel.add(mWxImageLabel);
        
        jfrm.add(mainPanel);
        
        // Display the frame.
        jfrm.setVisible(true);
    }
        
    private static Document loadTestDocument(String url) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new URL(url).openStream());
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
        NodeList nodes = doc.getElementsByTagName(e);
        // iterate the pressures
        for (int i = 0; i < nodes.getLength(); i++) {
           Element element = (Element) nodes.item(i);

           NodeList value = element.getElementsByTagName("value");
           Element line = (Element) value.item(0);
           String strval = getCharacterDataFromElement(line);
           if (strval.equals("NA"))
            continue;
           double d = Double.parseDouble(strval);
           return d;
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
    }
    
    private void SetupValuesUI()
    {
        mValues = new ArrayList<DataValue>();
        mValues.add(new DataValue(0, "Temperature"));
        mValues.add(new DataValue(0, "Humidity"));
        mValues.add(new DataValue(0, "Wind Speed"));
        mValues.add(new DataValue(0, "Wind Direction"));
        mValues.add(new DataValue(0, "Baraometer"));
    }
    
    private void UpdateMap()
    {
        try {
          mCurMap++;
          if (mCurMap >= mMapURLs.size()) mCurMap=0;
          URL imgURL = new URL(mMapURLs.get(mCurMap));
          Image image = ImageIO.read(imgURL);
          mWxImageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
          mWxImageLabel.setText("Wx Not Loaded");
        }
    }
    
    private void UpdateValues()
    {
        String tstr = DateTimeFormatter.ofPattern("HH:mm").format(LocalTime.now());
        mUpdateTimeLabel.setText(tstr);
        
        try {
            String urlstr = "http://forecast.weather.gov/MapClick.php?lat=38.11&lon=-122.57&unit=0&lg=english&FcstType=dwml";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            URL url = new URL(urlstr);
            //InputStream iStream = url.openStream();
            
            Document doc = factory.newDocumentBuilder().parse(url.openStream());
            
            double d;
            d = ReadValueFromDoc(doc, "temperature");
            mValues.get(0).setValue(d);
            d = ReadValueFromDoc(doc, "humidity");
            mValues.get(1).setValue(d);
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
    
    public void UpdateUI()
    {
        UpdateValues();
    }
    
    public void UpdateWxMap()
    {
        UpdateMap();
    }
    
    public static void main(String args[])
    {
        // Create the frame on the event dispatching thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PiWeather m = new PiWeather();
                new UpdateUITimer(60, m);
                new MapUpdateTimer(10, m);
            }
        });
    }

}
