
/**
 * Write a description of class DataValue here.
 * 
 * @author Charles Thaeler 
 * @version 0.1
 */
import javax.swing.*;
import java.awt.*;

public class DataValue
{
    // instance variables - replace the example below with your own
    private double mValue;
    private String mLegend;
    private JLabel mDataLabel;
    private JLabel mLegendLabel;

    /**
     * Constructor for objects of class DataValue
     */
    public DataValue()
    {
        // initialise instance variables
        mValue = 0;
        mLegend = "dummy";
        CreateLabels();
    }

    
    public DataValue(double value, String legend)
    {
        // initialise instance variables
        mValue = value;
        mLegend = legend;
        CreateLabels();
    }

    private void CreateLabels()
    {
        mDataLabel = new JLabel(Double.toString(mValue));
        mDataLabel.setFont(new Font("Serif", Font.PLAIN, 48));
        mDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mDataLabel.setForeground(Color.white);
        
        mLegendLabel = new JLabel(mLegend);
        mLegendLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        mLegendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mLegendLabel.setForeground(Color.white);
    }
    
    public void setValue(double x)
    {
        mValue = x;
        mDataLabel.setText(Double.toString(mValue));
    }
    
    public void setLegend(String s)
    {
        mLegend = s;
        mLegendLabel.setText(mLegend);
    }
    
    public double getValue()
    {
        return mValue;
    }
    
    public String getLegend()
    {
        return mLegend;
    }
    
    public JLabel getValueLabel()
    {
        return mDataLabel;
    }
    
    public JLabel getLegendLabel()
    {
        return mLegendLabel;
    }
}
