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
    /** the value from the web to display */
    private double mValue;
    /** the sendor value to display */
    private double mInsideValue;
    /** the format string */
    private String mFormatStr;
    /** the legend text */
    private String mLegend;
    /** the JLabel for the data */
    private JLabel mDataLabel;
    /** the JLable for the legend */
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

    
    
    /**
    * Constructor for DataValue 
    * <p>
    * Constructor taking the value and legend
    *
    * @param  value  Data Value that will be displayed
    * @param  legend The Legend displayed under the data value
    */
    public DataValue(double value, String legend)
    {
        // initialise instance variables
        mValue = value;
        mInsideValue = -999;
        mLegend = legend;
        mFormatStr = "%.0f";
        CreateLabels();
    }
    
    
    
    /**
    * Constructor for DataValue 
    * <p>
    * Constructor taking the value, legend and format string
    *
    * @param  value  Data Value that will be displayed
    * @param  legend The Legend displayed under the data value
    * @param  fmt Format for the value
    */
    public DataValue(double value, String legend, String fmt)
    {
        // initialise instance variables
        mValue = value;
        mInsideValue = -999;
        mLegend = legend;
        mFormatStr = fmt;
        CreateLabels();
    }
    
    
    
    /**
    * Constructor for DataValue 
    * <p>
    * Constructor taking the value, inside value and legend
    *
    * @param  value  Data Value that will be displayed
    * @param  insideValue  Value read from a sensor (or other source)
    * @param  legend The Legend displayed under the data value
    */
    public DataValue(double value, double insideValue, String legend)
    {
        // initialise instance variables
        mValue = value;
        mInsideValue = insideValue;
        mLegend = legend;
        mFormatStr = "<html>%.0f (out)<br>%.0f (in)";
        CreateLabels();
    }
    
    
    
    /**
    * Constructor for DataValue 
    * <p>
    * Constructor taking the value, inside value and legend
    *
    * @param  value  Data Value that will be displayed
    * @param  insideValue  Value read from a sensor (or other source)
    * @param  legend The Legend displayed under the data value
    * @param  fmt Format for the value
    */
    public DataValue(double value, double insideValue, String legend, String fmt)
    {
        // initialise instance variables
        mValue = value;
        mInsideValue = insideValue;
        mLegend = legend;
        mFormatStr = fmt;
        CreateLabels();
    }
    
    
    
    /**
     * CreateLabels() Create the labels setting font, size and alignment info
     */
    private void CreateLabels()
    {
        mDataLabel = new JLabel(Double.toString(mValue), JLabel.LEFT);
        if (mFormatStr.contains("<br>")) {
            mDataLabel.setFont(new Font("Monospaced", Font.PLAIN, 40));
            mDataLabel.setMaximumSize(new Dimension(120, 100));
        } else {
            mDataLabel.setFont(new Font("Monospaced", Font.PLAIN, 48));
        }
        mDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mDataLabel.setForeground(Color.white);
        
        
        mLegendLabel = new JLabel(mLegend, JLabel.LEFT);
        mLegendLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        mLegendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mLegendLabel.setForeground(Color.white);
    }
    
    
    
    /**
     * setValue() set the value
     * 
     * @param webVal the value to display
     */
    public void setValue(double webVal)
    {
        mValue = webVal;
        String str = String.format(mFormatStr, mValue);
        mDataLabel.setText(str);
    }
    
    
    
    /**
     * setValue() set the web and sensor data
     * @param webVal web value
     * @param sensorVal sensor value
     */
    public void setValue(double webVal, double sensorVal)
    {
        mValue = webVal;
        mInsideValue = sensorVal;
        String str = String.format(mFormatStr, mValue, mInsideValue);
        mDataLabel.setText(str);
    }
    
    
    
    /**
     * setFormat()  set the format string
     * 
     * @param format the format of the string two show
     * 
     */
    public void setFormat(String format)
    {
        mFormatStr = format;
        if (mFormatStr.contains("<br>")) {
            mDataLabel.setFont(new Font("Monospaced", Font.PLAIN, 30));
        }
        String str = String.format(mFormatStr, mValue, mInsideValue);
        mDataLabel.setText(str);
    }
    
    
    /**
     * setLegend() set the legent string
     * 
     * @param s the string
     */
    public void setLegend(String s)
    {
        mLegend = s;
        mLegendLabel.setText(mLegend);
    }
 
    
    
    /**
     * getValueLabel() get the value label
     * 
     * @return the data JLabel
     */
    public JLabel getValueLabel()
    {
        return mDataLabel;
    }
    
    
    
    /**
     * getLegentLabel() get the legend label
     * 
     * @return the legend JLabel
     */
    public JLabel getLegendLabel()
    {
        return mLegendLabel;
    }
}
