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

public class ForecastDataValue
{
    // instance variables - replace the example below with your own
    private double mTemp;
    private String mIconURL;
    private String mInfo;
    private JLabel mImageLabel;
    private JLabel mInfoLabel;
    private JLabel mTempLabel;

    /**
     * Constructor for objects of class DataValue
     */
    public ForecastDataValue()
    {
        // initialise instance variables
        mTemp = 999;
        mIconURL = "";
        mInfo = "";
        CreateUIComponents();
    }

    
    public ForecastDataValue(String iconURL, double temp, String info)
    {
        // initialise instance variables
        mTemp = temp;
        mIconURL = iconURL;
        mInfo = info;
        CreateUIComponents();
        SetIconImage();
    }

    private void CreateUIComponents()
    {
        mTempLabel = new JLabel("");
        mTempLabel.setFont(new Font("Monospaced", Font.PLAIN, 48));
        mTempLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mTempLabel.setForeground(Color.white);
        
        mInfoLabel = new JLabel("");
        mInfoLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        mInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mInfoLabel.setForeground(Color.white);
        
        mImageLabel = new JLabel("");
    }
    
    private boolean isImageCached(String url)
    {
        // make sure the cache directory exists
        File cacheDir = new File("cache");

        // if the directory does not exist, create it
        if (!cacheDir.exists()) {
            try {
                cacheDir.mkdir();
            } catch (Exception e) {
              return false;
            }
        }
    
        File cacheFile = new File(cachedImageFilename(url));
        return cacheFile.exists();
    }
    
    private String cachedImageFilename(String url)
    {
        String cacheFile = "badURL.png";
        
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
        
    private void SetIconImage()
    {

        try {
          if (isImageCached(mIconURL)) {
            Image image = ImageIO.read(new File(cachedImageFilename(mIconURL)));
            mImageLabel.setIcon(new ImageIcon(image));
          } else {
            URL imgURL = new URL(mIconURL);
            BufferedImage image = ImageIO.read(imgURL);
            
            File cacheFile = new File(cachedImageFilename(mIconURL));
            ImageIO.write(image, "PNG", cacheFile);
            //if (image.getHeight(null) > 80)
            //  mImageLabel.setIcon(new ImageIcon(image.getScaledInstance(80, -1, Image.SCALE_AREA_AVERAGING)));
            //else
            mImageLabel.setIcon(new ImageIcon(image));
          }
        } catch (IOException e) {
          mImageLabel.setIcon(new ImageIcon("badURL.png"));
        }
    }
    
    public void setTemp(double temp)
    {
        mTemp = temp;
        String str = String.format("%.0f", mTemp);
        mTempLabel.setText(str);
    }

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
    
    public void setIconURL(String url)
    {
        mIconURL = url;
        SetIconImage();
    }
    
    
    public JLabel getImageLabel()
    {
        return mImageLabel;
    }
    
    public JLabel getInfoLabel()
    {
        return mInfoLabel;
    }
    
        public JLabel getTempLabel()
    {
        return mTempLabel;
    }
}

