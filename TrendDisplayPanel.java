import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * TrendDisplayPanel is a JPanel used to display a line graph of historical observation data.
 * 
 * @author Charles Thaeler
 * @version Feb 15, 2017
 */
public class TrendDisplayPanel extends JPanel
{
    /** cached data so we don't need to recovert on redraws */
    private int[][] mData;
    /** vertical grid data, needs to update ever time the number of days to display is updated */
    private int[][] mVertGrid;
    /** number of days to display */
    private int mDisplayDays=3;
    /** cycle through number of days from 1 to 10 */
    private boolean mCycleDisplayDays = false;
    /** show verbose debugging data */
    private boolean mVerbose = false;
    /** the sensor to read data from */
    private WxSensor mSensor = null;
    /** start the graph here */
    private static int mGraphStartX = 25;
    /** end of the graph here */
    private static int mGraphEndX = 25;
    /** bottom of the graph */
    private static int mGraphStartY = 25;
    
    /**
     * TrendDisplayPanel()  Constructor
     * 
     * @param displayDays  days to display 0 says cycle through days
     * @param sensor       weather sensor to use
     * @param verbose      display debugging data
     */
    public TrendDisplayPanel(int displayDays, WxSensor sensor, boolean verbose)
    {
        super();
        if (displayDays == 0) {
            mCycleDisplayDays = true;
            mDisplayDays = 1;
        } else {
            mCycleDisplayDays = false;
            mDisplayDays = displayDays;
        }

        mSensor = sensor;
        mVerbose = verbose;
    }
    
    /**
     * GetTempY() return the Y value for a temperature value
     * 
     * @param temp the temp to display
     * 
     * @return the Y value to display
     * 
     */
    private int GetTempY(double temp)
    {
        // todo make this scale better for taller height
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 20; // allow a bourder
        
        return(height - (int)temp);
    }
    
    
    
    /**
     * GetHumidityY()  return the Y value for a humidity value
     * 
     * @param humidity the humidity to display (on the same scale as temperature (perhaps I should just call GetTempY()?
     * 
     * @return the Y value to display
     * 
     */
    private int GetHumidityY(double humidity)
    {
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 20; // allow a bourder
        
        return(height - (int)humidity);
    }
    
    
    
    /**
     * GetBarometerY()  returns the Y value of the barometric pressure (scales 29.0 up)
     * 
     * @param press the pressure to display
     * 
     * @return the Y value to display
     */
    private int GetBarometerY(double press)
    {
        // TODO make theis more adaptive based on height
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 20; // allow a bourder
        
        return(height - (int)((press - 29.00) * 70.0));
    }
    
    
    
    /**
     * DrawDashedLine() draw a dashed line between two points
     * 
     * @param g Graphics object
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @param dash SIMPLE dash partern
     * 
     */
    private void DrawDashedLine(Graphics g, int x1, int y1, int x2, int y2, int dash)
    {
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        if (dash != 0) {
            //set the stroke of the copy, not the original 
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{dash}, 0);
            g2d.setStroke(dashed);
        }
        g2d.drawLine(x1, y1, x2, y2);

        //gets rid of the copy
        g2d.dispose();
    }
    
    
    
    /**
     * DrawDashedPolyline() Draw dashed polyline
     * 
     * @param g Graphics object
     * @param x x coordinate array
     * @param y y coordinate array
     * @param dash dash pattern array
     * 
     */
    private void DrawDashedPolyline(Graphics g, int[] x, int[] y, float[] dash)
    {
        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        if (dash.length != 0) {
            //set the stroke of the copy, not the original 
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0);
            g2d.setStroke(dashed);
        }
        g2d.drawPolyline(x, y, x.length);

        //gets rid of the copy
        g2d.dispose();
    }
    

    
    /**
     * GetX() Get the x location from a time
     * 
     * @param toShow  the time coordinate to show
     * 
     * @return x value of the time
     * 
     */
    private int GetX(LocalDateTime toShow)
    {
        LocalDateTime now = LocalDateTime.now();
        double totalTime = Duration.between(now.minusDays(mDisplayDays), now).getSeconds();
        double dispTime = Duration.between(toShow, now).getSeconds();
        
        Dimension dim = getSize();
        double width = dim.width - (mGraphStartX + mGraphEndX);

        return dim.width - mGraphEndX - (int)(width * (dispTime/totalTime));
    }
    
    
    /**
     * FindNext12() find the next noon or midnight from the specified time
     * 
     * @param t time
     * 
     * @return LocalDateTime of tne next noon or midnight
     * 
     */
    private LocalDateTime FindNext12(LocalDateTime t)
    {
        if (mVerbose) {
            System.out.println("----");
            System.out.println("now       = " + LocalDateTime.now().toString());
            System.out.println("t hour    = " + t.getHour());
        }
        LocalDateTime f12;
        if (t.getHour() > 12) {
            // midnight before then add a day
            f12 = LocalDateTime.of(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), 0, 0);
            f12 = f12.plusDays(1);
        } else {
            // use the next noon
            f12 = LocalDateTime.of(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), 12, 0);
        }
        if (mVerbose) {
            System.out.println("12 after  = " + f12.toString());
            System.out.println("----");
        }
        return f12;
    }
    
    /**
     * FindNextHout() find the next hour (no minutes or seconds)
     * 
     * @param t LocalDateTime time to check from
     * 
     * @return the next hour
     * 
     */
    private LocalDateTime FindNextHour(LocalDateTime t)
    {
        if (mVerbose) {
            System.out.println("----");
            System.out.println("now       = " + LocalDateTime.now().toString());
            System.out.println("t minute    = " + t.getMinute());
        }
        LocalDateTime fHour;
        fHour = LocalDateTime.of(t.getYear(), t.getMonthValue(), t.getDayOfMonth(), t.getHour(), 0).plusHours(1);

        if (mVerbose) {
            System.out.println("hour after  = " + fHour.toString());
            System.out.println("----");
        }
        return fHour;
    }
    
    
    /**
     * UpdateData()  Update the internal data arrays
     * 
     * @param list  the list of TrendData items.  Return doing nothing if there are less than 2 values
     * 
     */
    public void UpdateData(ArrayList<TrendData> list)
    {
        if (list == null || list.size() < 2) return;
        
        if (mCycleDisplayDays) {
            mDisplayDays++;
            if (mDisplayDays > 10) mDisplayDays = 1;
        }
        
        Dimension dim = getSize();
        int width = dim.width - (mGraphStartX + mGraphEndX);
        
        
        long totalTime = 24*60*60*mDisplayDays;
        
        // we may have saved more than are going to be visible so find the first visible one
        int firstVisible = 0;
        for (int i=0; i < list.size(); i++) {
            long dt = Duration.between(list.get(i).GetDateTime(), list.get(list.size()-1).GetDateTime()).getSeconds();
            if (dt <= totalTime) {
                firstVisible = i;
                break;
            }
        }
        

        if (mDisplayDays > 2) {
            // the first bar is mDisplay days before the next 12 hour mark
            LocalDateTime first12 = FindNext12(LocalDateTime.now()).minusDays(mDisplayDays);
            if (mVerbose) System.out.println("---- first bar " + first12.toString() + " ----");
            mVertGrid = new int[mDisplayDays*2][2];
            for (int i = 0; i < mDisplayDays*2; i++) {
                LocalDateTime disp = first12.plusHours(i*12);
                mVertGrid[i][0] = GetX(disp);
                mVertGrid[i][1] = (disp.getHour() >= 12) ? 3 : 0;
            }
        } else {
            LocalDateTime firstHour = FindNextHour(LocalDateTime.now()).minusHours(mDisplayDays*24);
            mVertGrid = new int[mDisplayDays*24][2];
            for (int i = 0; i < mDisplayDays*24; i++) {
                LocalDateTime disp = firstHour.plusHours(i);
                mVertGrid[i][0] = GetX(disp);
                switch(disp.getHour()) {
                case 0:
                    mVertGrid[i][1] = 0;
                    break;
                case 12:
                    mVertGrid[i][1] = 3;
                    break;
                default:
                    mVertGrid[i][1] = 1;
                    break;
                }
            }
        }
        mData = new int[7][list.size()-firstVisible];

        for (int i=firstVisible, di=0; i < list.size(); i++, di++)
        {
            mData[0][di] = GetX(list.get(i).GetDateTime());
            mData[1][di] = GetTempY(list.get(i).GetTemp()); // 1 is the y temperature
            mData[2][di] = GetHumidityY(list.get(i).GetHumidity()); // 2 is the humidity
            mData[3][di] = GetBarometerY(list.get(i).GetBarometer()); // 3 is the barometer
            mData[4][di] = GetTempY(list.get(i).GetSensorTemp()); // 4 is the y sensor temperature
            mData[5][di] = GetHumidityY(list.get(i).GetSensorHumidity()); // 5 is the sensor humidity
            mData[6][di] = GetBarometerY(list.get(i).GetSensorBarometer()); // 6 is the sensor barometer
        }
        repaint();
    }
    
    
    
    /**
     * UpdateNumDays() update the number of days to display.  This requires the local data arrays be updated.
     * 
     * @param list TrendData list
     * @param displayDays number of days to display (0 == cycling)
     * 
     */
    public void UpdateNumDays(ArrayList<TrendData> list, int displayDays)
    {
        if (displayDays == 0) {
            mCycleDisplayDays = true;
        } else {
            mCycleDisplayDays = false;
            mDisplayDays = displayDays;
        }

        UpdateData(list);
    }
    
    /**
     * doDrawing()  do the drawing
     * 
     * @param graphics Graphics object
     * 
     */
    private void doDrawing(Graphics graphics)
    {
        setBackground(Color.BLACK);
    
        if (mVertGrid == null || mData == null) return;
        
        Graphics2D g2d = (Graphics2D) graphics;
        
        Dimension dim = getSize();
        
        // draw the vertical grid lines
        g2d.setColor(new Color(100, 100, 100));
        for (int vl = 0; vl < mVertGrid.length; vl++) {
            DrawDashedLine(g2d, mVertGrid[vl][0], 0, mVertGrid[vl][0], dim.height-20, 1);
        }
        
        
        Color tempColor = Color.red;
        Color tempGridColor = new Color(165, 0, 0);
        Color humidityColor = new Color(128, 128, 255);
        Color pressureColor = Color.green;
        Color pressureGridColor = new Color(0, 128, 0);
        
        // Draw legend and grid lines for Temperature (and Humidity)
        g2d.setColor(tempColor);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.drawString("Temp", 0, dim.height-5);
        
        // draw the Temperature grid lines
        g2d.setColor(tempGridColor);
        for (double t = 0; t < ((dim.height>135)?130:120); t+=20) {
            int y = GetTempY(t);
            DrawDashedLine(g2d, mGraphStartX, y, dim.width-mGraphEndX, y, 1);
            g2d.drawString(String.format("%.0f", t), dim.width-mGraphEndX, y+5);
        }
        
        
        // Draw legend for Humidity
        g2d.setColor(humidityColor);
        g2d.drawString("Humidity", 40, dim.height-5);
        
        
        // Draw legend for Barometric Pressure
        g2d.setColor(pressureColor);
        g2d.drawString("Barometer", 110, dim.height-5);
        
        // Draw the Barometric Pressure Grid Lines
        g2d.setColor(pressureGridColor);
        for (double b = 29.0; b < 31; b+=0.5) {
            int y = GetBarometerY(b);
            DrawDashedLine(g2d, mGraphStartX, y, dim.width-mGraphEndX, y, 1);
            g2d.drawString(String.format("%.1f", b), 0, y+5);
        }
        
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        if (mSensor != null) {
            if (mSensor.HasTemperature()) {
                g2d.setColor(tempColor);
                g2d.drawString("s-Temp", 180, dim.height-5);
            }
            
            if (mSensor.HasHumidity()) {
                g2d.setColor(humidityColor);
                g2d.drawString("s-Humidity", 230, dim.height-5);
            }
            
            if (mSensor.HasBarometricPressure()) {
                g2d.setColor(pressureColor);
                g2d.drawString("s-Barometer", 310, dim.height-5);
            }
        }
        
        
        if (mData == null) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("no Data", (int)getSize().getWidth() - 100, dim.height-5);
            return;
        }

        // Temperature
        g2d.setColor(tempColor);
        g2d.drawPolyline(mData[0], mData[1], mData[0].length);

        // Humidity
        g2d.setColor(humidityColor);
        g2d.drawPolyline(mData[0], mData[2], mData[0].length);
        
        // Barometric Pressure
        g2d.setColor(pressureColor);
        g2d.drawPolyline(mData[0], mData[3], mData[0].length);

        
        if (mSensor != null) {
            if (mSensor.HasTemperature()) {
                // Temperature from a sensor
                g2d.setColor(tempColor);
                DrawDashedPolyline(g2d, mData[0], mData[4], new float[] {4, 2});
            }
    
            if (mSensor.HasHumidity()) {
                // Humidity from a sensor
                g2d.setColor(humidityColor);
                DrawDashedPolyline(g2d, mData[0], mData[5], new float[] {4, 2});
            }
            
            if (mSensor.HasBarometricPressure()) {
                // Barometric Pressure from a sensor
                g2d.setColor(pressureColor);
                DrawDashedPolyline(g2d, mData[0], mData[6], new float[] {4, 2});
            }
        }
        
        g2d.setColor(Color.WHITE);
        if (mCycleDisplayDays) {
            g2d.drawString(String.format("Cyc %2d %4d", mDisplayDays, mData[0].length), dim.width - 90, dim.height-5);
        } else {
            g2d.drawString(String.format("%4d", mData[0].length), dim.width - 35, dim.height-5);
        }
    }


    /**
     * paintComponent() paint the component
     * 
     * @param g Graphics object
     * 
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        doDrawing(g);
    }
}
