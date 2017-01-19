/**
 * Write a description of class DataValue here.
 * 
 * @author Charles Thaeler <cst@soar-high.com>
 * @version 0.1
 */
import javax.swing.*;
import java.awt.*;

public class DataValue
{
    // instance variables
    private double mValue;
    private double mInsideValue;
    private String mFormatStr;
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
        mInsideValue = -999;
        mLegend = "";
        mFormatStr = "%.0f";
        CreateLabels();
    }

    
    public DataValue(double value, String legend)
    {
        // initialise instance variables
        mValue = value;
        mInsideValue = -999;
        mLegend = legend;
        mFormatStr = "%.0f";
        CreateLabels();
    }
    
    public DataValue(double value, String legend, String fmt)
    {
        // initialise instance variables
        mValue = value;
        mInsideValue = -999;
        mLegend = legend;
        mFormatStr = fmt;
        CreateLabels();
    }
    
    public DataValue(double value, double insideValue, String legend)
    {
        // initialise instance variables
        mValue = value;
        mInsideValue = insideValue;
        mLegend = legend;
        mFormatStr = "%.0f | %.0f";
        CreateLabels();
    }
    
    public DataValue(double value, double insideValue, String legend, String fmt)
    {
        // initialise instance variables
        mValue = value;
        mInsideValue = insideValue;
        mLegend = legend;
        mFormatStr = fmt;
        CreateLabels();
    }
    
    private void CreateLabels()
    {
        mDataLabel = new JLabel(Double.toString(mValue));
        mDataLabel.setFont(new Font("Monospaced", Font.PLAIN, 48));
        mDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mDataLabel.setForeground(Color.white);
        
        mLegendLabel = new JLabel(mLegend);
        mLegendLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        mLegendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mLegendLabel.setForeground(Color.white);
    }
    
    public void setValue(double x)
    {
        mValue = x;
        String str = String.format(mFormatStr, mValue);
        mDataLabel.setText(str);
    }
    
    public void setValue(double x, double i)
    {
        mValue = x;
        mInsideValue = i;
        String str = String.format(mFormatStr, mValue, mInsideValue);
        mDataLabel.setText(str);
    }
    
    public void setLegend(String s)
    {
        mLegend = s;
        mLegendLabel.setText(mLegend);
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
