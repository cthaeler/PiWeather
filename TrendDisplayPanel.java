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
    private int[][] mData;
    private int[][] mVertGrid;
    private int mDisplayDays=3;
    private boolean mCycleDisplayDays = false;
    private boolean mVerbose = false;
    private boolean mHasSensor = false;
    private String mSensor = "";
    private static int mGraphStartX = 25;
    private static int mGraphEndX = 25;
    private static int mGraphStartY = 25;
    
    
    public TrendDisplayPanel(int displayDays, boolean hasSensor, String sensor, boolean verbose)
    {
        super();
        if (displayDays == 0) {
            mCycleDisplayDays = true;
            mDisplayDays = 1;
        } else {
            mCycleDisplayDays = false;
            mDisplayDays = displayDays;
        }
        mHasSensor = hasSensor;
        mSensor = sensor;
        mVerbose = verbose;
    }
    
    /**
     * 
     * 
     */
    private int GetTempY(double temp)
    {
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 20; // allow a bourder
        
        return(height - (int)temp);
    }
    
    
    
    /**
     * 
     * 
     */
    private int GetHumidityY(double humidity)
    {
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 20; // allow a bourder
        
        return(height - (int)humidity);
    }
    
    
    
    /**
     * 
     * 
     */
    private int GetBarometerY(double press)
    {
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 20; // allow a bourder
        
        return(height - (int)((press - 29.00) * 70.0));
    }
    
    
    /**
     * 
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
     * Draw dashed polyline
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
     * 
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
     * 
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
     * 
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
     * 
     * 
     */
    public void UpdateData(ArrayList<TrendData> list)
    {
        if (list.size() < 2) return;
        
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
            mData[5][di] = GetHumidityY(list.get(i).GetBarometer()+0.1); // 6 is the sensor barometer - faked for now
        }
        repaint();
    }
    
    /**
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
     * 
     * 
     */
    private void doDrawing(Graphics graphics)
    {      
        Graphics2D g2d = (Graphics2D) graphics;
        setBackground(Color.BLACK);
        
        Dimension dim = getSize();
        
        // draw the vertical grid lines
        g2d.setColor(new Color(100, 100, 100));
        for (int vl = 0; vl < mVertGrid.length; vl++) {
            DrawDashedLine(g2d, mVertGrid[vl][0], 0, mVertGrid[vl][0], dim.height-20, 1);
        }
        
        // Draw legend and grid lines for Temperature (and Humidity)
        g2d.setColor(Color.red);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.drawString("Temp", 0, dim.height-5);
        g2d.setColor(new Color(165, 0, 0));

        // draw the Temperature grid lines
        for (double t = 0; t < ((dim.height>135)?130:120); t+=20) {
            int y = GetTempY(t);
            DrawDashedLine(g2d, mGraphStartX, y, dim.width-mGraphEndX, y, 1);
            g2d.drawString(String.format("%.0f", t), dim.width-mGraphEndX, y+5);
        }
        
        // Draw legend for Humidity
        g2d.setColor(new Color(128, 128, 255));
        g2d.drawString("Humidity", 40, dim.height-5);
        
        // Draw legend and grid lines for Barometer
        g2d.setColor(Color.green);
        g2d.drawString("Barometer", 110, dim.height-5);
        g2d.setColor(new Color(0, 128, 0));
        for (double b = 29.0; b < 31; b+=0.5) {
            int y = GetBarometerY(b);
            DrawDashedLine(g2d, mGraphStartX, y, dim.width-mGraphEndX, y, 1);
            g2d.drawString(String.format("%.1f", b), 0, y+5);
        }
        
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        if (mHasSensor || mVerbose) {
            g2d.setColor(Color.magenta);
            g2d.drawString("s-Temp", 180, dim.height-5);
            
            g2d.setColor(Color.cyan);
            g2d.drawString("s-Humidity", 230, dim.height-5);
            
            if (mSensor.equals("BME280") || mVerbose) {
                g2d.setColor(new Color(128, 255, 128));
                g2d.drawString("s-Barometer", 310, dim.height-5);
            }
        }
        
        
        if (mData == null) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("no Data", (int)getSize().getWidth() - 100, dim.height-5);
            return;
        }

        g2d.setColor(Color.red);
        g2d.drawPolyline(mData[0], mData[1], mData[0].length);

        g2d.setColor(new Color(128, 128, 255));
        g2d.drawPolyline(mData[0], mData[2], mData[0].length);
        
        g2d.setColor(Color.green);
        g2d.drawPolyline(mData[0], mData[3], mData[0].length);

        
        if (mHasSensor) {
            g2d.setColor(Color.magenta);
            DrawDashedPolyline(g2d, mData[0], mData[4], new float[] {4, 2});
    
            g2d.setColor(Color.cyan);
            DrawDashedPolyline(g2d, mData[0], mData[5], new float[] {4, 2});
            
            if (mSensor.equals("BME280")) {
                g2d.setColor(new Color(128, 128, 255));
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
     * 
     * 
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        doDrawing(g);
    }
}
