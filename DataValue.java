
/**
 * Write a description of class DataValue here.
 * 
 * @author Charles Thaeler 
 * @version 0.1
 */
public class DataValue
{
    // instance variables - replace the example below with your own
    private double mValue;
    private String mLabel;

    /**
     * Constructor for objects of class DataValue
     */
    public DataValue()
    {
        // initialise instance variables
        mValue = 0;
        mLabel = "dummy";
    }
    
    public DataValue(double value, String label)
    {
        // initialise instance variables
        mValue = value;
        mLabel = label;
    }

    public void setValue(double x)
    {
        mValue = x;
    }
    
    public void setLabel(String s)
    {
        mLabel = s;
    }
    
    public double getValue()
    {
        return mValue;
    }
    
    public String getLabel()
    {
        return mLabel;
    }
}
