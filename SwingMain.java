// Main for Swing Mandelbrot
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


class SwingMain
{
 
    private ArrayList<DataValue> mValues;
    
    /**
     * Constructor for objects of class SwingMain
     */
    SwingMain()
    {
        // Create a new JFrame container.
        JFrame jfrm = new JFrame("A Simple Swing Application");
        //frm.setLayout(new FlowLayout());
        JPanel mainPanel = new JPanel();
        BoxLayout frameBox = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
        mainPanel.setLayout(frameBox);
        
        JPanel leftPanel = new JPanel();
        BoxLayout leftBox = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        leftPanel.setLayout(leftBox);               
        // set properties
        //jfrm.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        //jfrm.setUndecorated(true);
        //jfrm.setVisible(true);
        
        jfrm.setSize(1024, 600);

        
        // Terminate the program when the user closes the application
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        mValues = new ArrayList<DataValue>();
        LoadValues("http://forecast.weather.gov/MapClick.php?lat=38.11&lon=-122.57&unit=0&lg=english&FcstType=dwml");
        for (int i=0; i < mValues.size(); i++) {
            DataValue dv = mValues.get(i);
            leftPanel.add(dv.getValueLabel());
            leftPanel.add(dv.getLegendLabel());
            
            leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }
        mainPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        mainPanel.add(leftPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(20, 0)));
         
        
        try {
          URL url = new URL("http://weather.rap.ucar.edu/model/ruc12hr_sfc_prcp.gif");
          Image image = ImageIO.read(url);
          JLabel imgLabel = new JLabel(new ImageIcon(image));
          mainPanel.add(imgLabel);
        }catch (IOException e) {
          JLabel errorLabel = new JLabel("Wx Not Loaded");
          mainPanel.add(errorLabel);
        }
        
        
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
    
    private void LoadValues(String url)
    {
        Document doc;
        try {
            doc = loadTestDocument(url);
        } catch (Exception e) {
            return;
        }
        
        double d;
        d = ReadValueFromDoc(doc, "temperature");
        mValues.add(new DataValue(d, "Temperature"));
        d = ReadValueFromDoc(doc, "humidity");
        mValues.add(new DataValue(d, "Humidity"));
        d = ReadValueFromDoc(doc, "wind-speed");
        mValues.add(new DataValue(d, "Wind Speed"));
        d = ReadValueFromDoc(doc, "direction");
        mValues.add(new DataValue(d, "Wind Direction"));
        d = ReadValueFromDoc(doc, "pressure");
        mValues.add(new DataValue(d, "Baraometer"));
        //d = ReadValueFromDoc(doc, "visibility");
        //mValues.add(new DataValue(d, "Visibility"));
    }
    
    public static void main(String args[])
    {
        // Create the frame on the event dispatching thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SwingMain();
            }
        });
    }

}
