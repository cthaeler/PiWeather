import java.net.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

import java.time.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * Utilities to pull data from the Web docs
 *
 * @author Charles Thaeler
 * @version 0.1
 */
public class WxWebDocUtils
{
    /**
     * Constructor for objects of class WxWebDocUtils
     */
    public WxWebDocUtils()
    {

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
    public static String GetCharacterDataFromElement(Element e) {
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
    public static double ReadValueFromDoc(Document doc, String elem)
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
                        PiWeather.DumpError("ReadValueFromDoc: Bad Double Val " + elem + " = " + strval , e);
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
    public static String ReadStringFromDoc(Document doc, String e)
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
    public static String ReadCurrentConditionsIconFromDocument(Document doc)
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
     * SaveWxXMLFile()  Save weather file data.  This is mostly for troubleshooting
     *
     *
     *
     */
    public static void SaveWxXMLFile(String locationURL, String locationName)
    {
        LocalDateTime dt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HH_mm");
        String tstr = dt.format(formatter);
        // save the xml
        Path p = Paths.get("./wxfiles/wxfile_" + locationName + "_" + tstr + ".xml");
        
        try {
            URL url = new URL(locationURL);
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
            PiWeather.DumpError("SaveWxXMLFile", e);
        }
    }
}
