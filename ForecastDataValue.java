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
    private JLabel mLabelIcon;
    private JLabel mLegendLabel;

    /**
     * Constructor for objects of class DataValue
     */
    public ForecastDataValue()
    {
        // initialise instance variables
        mTemp = 99.0;
        mIconURL = "";
        CreateUIComponents();
    }

    
    public ForecastDataValue(String iconURL, double temp)
    {
        // initialise instance variables
        mTemp = temp;
        mIconURL = iconURL;
        CreateUIComponents();
        SetIconImage();
    }

    private void CreateUIComponents()
    {
        mLabelIcon = new JLabel("none");
        
        mLegendLabel = new JLabel("Temp");
        mLegendLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        mLegendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mLegendLabel.setForeground(Color.white);
    }
    
        
    private void SetIconImage()
    {
        try {
          URL imgURL = new URL(mIconURL);
          Image image = ImageIO.read(imgURL);
          if (image.getHeight(null) > 60)
            mLabelIcon.setIcon(new ImageIcon(image.getScaledInstance(60, -1, Image.SCALE_AREA_AVERAGING)));
          else
            mLabelIcon.setIcon(new ImageIcon(image));
        } catch (IOException e) {
          mLabelIcon.setText("none");
        }
    }
    
    public void setTemp(double temp)
    {
        mTemp = temp;
        mLegendLabel.setText(Double.toString(mTemp));
    }

    
    public void setIconURL(String url)
    {
        mIconURL = url;
        SetIconImage();
    }
    
    
    public JLabel getValueIcon()
    {
        return mLabelIcon;
    }
    
    public JLabel getLegendLabel()
    {
        return mLegendLabel;
    }
}

