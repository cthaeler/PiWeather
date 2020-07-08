/**
 * Write a description of class ForecastDataaValue here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.1
 */
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;

public class ForecastDataValueUI
{
    /** forecast temperature */
    private double mTemp;
    /** URL of icon to show */
    private String mIconURL;
    /** info about the day */
    private String mInfo;
    
    /** the JLabel for the image */
    private JLabel mImageLabel;
    /** the JLabel for the info */
    private JLabel mInfoLabel;
    /** the JLabel for the temperature */
    private JLabel mTempLabel;

    /**
     * ForecastDataValueUI() Constructor for objects of class ForecastDataValueUI
     */
    public ForecastDataValueUI()
    {
        // initialise instance variables
        mTemp = 999;
        mIconURL = "";
        mInfo = "";
        CreateUIComponents();
    }

    /**
     * ForecastDataValueUI() constructor
     * 
     * @param iconURL the icon URL
     * @param temp the temperature
     * @param info info to display
     */
    public ForecastDataValueUI(String iconURL, double temp, String info)
    {
        // initialise instance variables
        mTemp = temp;
        
        // if it's an http make it https
        if (iconURL.substring(0,5).equalsIgnoreCase("http:")) {
            mIconURL = "https:" + iconURL.substring(5);
        } else {
            mIconURL = iconURL;
        }

        mInfo = info;
        CreateUIComponents();
        SetIconImage();
    }

    /**
     * CreateUIComponents() Create the components 
     */
    private void CreateUIComponents()
    {
        mTempLabel = new JLabel("");
        mTempLabel.setFont(new Font("Monospaced", Font.PLAIN, 48)); // UI SIZE DATA
        mTempLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mTempLabel.setForeground(Color.white);

        mInfoLabel = new JLabel("");
        mInfoLabel.setFont(new Font("Monospaced", Font.PLAIN, 14)); // UI SIZE DATA
        mInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mInfoLabel.setForeground(Color.white);

        mImageLabel = new JLabel("");
    }

    /**
     * isImageCached()  we cache icons.  Is this one cached?
     * 
     * @param url url to check the cache for
     * 
     * @return true if cached
     */
    private boolean isImageCached(String url)
    {
        // make sure the cache directory exists
        File cacheDir = new File("cache");

        // if the directory does not exist, create it
        if (!cacheDir.exists()) {
            try {
                cacheDir.mkdir();
            } catch (Exception e) {
                if (PiWeather.DebugLevel().ShowStackTrace()) e.printStackTrace();
                return false;
            }
        }

        String filename = cachedImageFilename(url);
        File cacheFile = new File(filename);
        return cacheFile.exists();
    }

    /**
     * cachedImageFileName() returns the badURL icon if there's a glitch
     * 
     * @param url url of the icon
     * 
     * @return the file name of the cached icon
     */
    private String cachedImageFilename(String url)
    {
        String cacheFile = "icons/badURL.png";

        // the URL should look like one of the following
        // http://forecast.weather.gov/newimages/medium/nshra60.png
        // http://forecast.weather.gov/DualImage.php?i=shra&amp;j=few&amp;ip=20

        if (url.contains("DualImage")) {
            int idxi = url.indexOf("i=");
            int idxamp = url.indexOf("&", idxi);
            if (idxamp == -1) idxamp = url.length();
            String fnamei = url.substring(idxi+2, idxamp);
            int idxj = url.indexOf("j=");
            idxamp = url.indexOf("&", idxj);
            if (idxamp == -1) idxamp = url.length();
            String fnamej = url.substring(idxj+2, idxamp);
            cacheFile = "cache/"+fnamei+"_"+fnamej+".png";
        } else {
            int idx = url.lastIndexOf("/");
            String fname = url.substring(idx+1, url.length());
            cacheFile = "cache/"+fname;
        }

        return cacheFile;
    }

    /**
     * SetIconImage()  Set the icon image from the url
     */
    private void SetIconImage()
    {

        try {
            String filename = cachedImageFilename(mIconURL);
            if (!isImageCached(mIconURL)) {
                URL url = new URL(mIconURL);
                
                BufferedImage image = ImageIO.read(url);
                if (image != null) {
                    ImageIO.write(image, "png", new File(filename));
                } else {
                    System.out.println("image is null");
                }
            }
            Image image = ImageIO.read(new File(filename));
            mImageLabel.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            if (PiWeather.DebugLevel().ShowStackTrace()) e.printStackTrace();
            System.out.println("Bad Icon Image " + mIconURL);
            mImageLabel.setIcon(new ImageIcon("badURL.png"));
        }
    }

    /**
     * setTemp() set the temperature
     * 
     * @param temp the temperature
     */
    public void setTemp(double temp)
    {
        mTemp = temp;
        String str = String.format("%.0f", mTemp);
        mTempLabel.setText(str);
    }

    /**
     * setInfo()  set the info string
     * 
     * @param info the info to display
     * @param isEven even or odd determines how we align
     * 
     */
    public void setInfo(String info, boolean isEven)
    {
        if (info.length() > 15) {
            info = info.substring(0, Math.min(info.length(), 15));
        } else {
            if(isEven) {
                // even
                info = String.format("%15s", info);
            } else {
                // odd
                info = String.format("%-15s", info);
            }
        }
        mInfo = info;
        mInfoLabel.setText(mInfo);
    }

    /**
     * setIconURL() set the icon URL
     * 
     * @param url the icon url
     */
    public void setIconURL(String iconURL)
    {
        //System.out.println("setIconURL: " + iconURL);
        if (iconURL.substring(0,5).equalsIgnoreCase("http:")) {
            mIconURL = "https:" + iconURL.substring(5);
        } else {
            mIconURL = iconURL;
        }

        SetIconImage();
    }

    /**
     * getImageLabel()
     * 
     * @return get the JLabel for the image
     */
    public JLabel getImageLabel()
    {
        return mImageLabel;
    }

    /**
     * getInfoLabel()
     * 
     * @return get the JLabel for the info label
     */
    public JLabel getInfoLabel()
    {
        return mInfoLabel;
    }

    /**
     * getTempLabel() get the JLabel
     * 
     * @return the JLabel for this object
     */
    public JLabel getTempLabel()
    {
        return mTempLabel;
    }
}

