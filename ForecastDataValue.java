/**
 * Write a description of class ForecastDataaValue here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.1
 */
import javax.swing.*;
import java.awt.*;
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
    
        
    private void SetIconImage()
    {
        try {
          URL imgURL = new URL(mIconURL);
          Image image = ImageIO.read(imgURL);
          //if (image.getHeight(null) > 80)
          //  mImageLabel.setIcon(new ImageIcon(image.getScaledInstance(80, -1, Image.SCALE_AREA_AVERAGING)));
          //else
            mImageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
          mImageLabel.setText("none");
        }
    }
    
    public void setTemp(double temp)
    {
        mTemp = temp;
        String str = String.format("%.0f", mTemp);
        mTempLabel.setText(str);
    }

    public void setInfo(String info)
    {
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

