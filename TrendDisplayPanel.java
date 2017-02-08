import javax.swing.*;
//import javax.swing.JPanel;
import java.awt.*;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Dimension;
//import java.awt.Stroke;
//import java.awt.BasicStroke;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Write a description of class TrendDisplayPanel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TrendDisplayPanel extends JPanel
{
    private int[][] mData;
    private int mDisplayDays=3;
    private boolean mVerbose = false;
    private boolean mHasSensor = false;
    
    private int GetTempY(double temp)
    {
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 10; // allow a bourder
        
        return(height - (int)temp);
    }
    
    private int GetHumidityY(double humidity)
    {
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 10; // allow a bourder
        
        return(height - (int)humidity);
    }
    
    
    private int GetBarometerY(double press)
    {
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 10; // allow a bourder
        
        return(height - (int)((press - 27.5) * 25.0));
    }
    
    private void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2)
    {

        //creates a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        //set the stroke of the copy, not the original 
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
        g2d.setStroke(dashed);
        g2d.drawLine(x1, y1, x2, y2);

        //gets rid of the copy
        g2d.dispose();
    }
    
    public void UpdateData(ArrayList<TrendData> list, int displayDays, boolean hasSensor, boolean verbose)
    {
        if (list.size() < 2) return;
        
        mVerbose = verbose;
        mHasSensor = hasSensor;
        mDisplayDays = displayDays;
        
        Dimension dim = getSize();
        int height = dim.height - 10; // allow a bourder
        int width = dim.width - 40;
        
        
        long totalTime = 24*60*60*displayDays;
        
        // we may have saved more than are going to be visible
        int firstVisible = 0;
        for (int i=0; i < list.size(); i++) {
            long dt = Duration.between(list.get(0).GetDateTime(), list.get(i).GetDateTime()).getSeconds();
            if (dt <= totalTime) {
                firstVisible = i;
                break;
            }
        }
        
        mData = new int[6][list.size()-firstVisible];
        
        for (int i=firstVisible; i < list.size(); i++)
        {
            long dt = Duration.between(list.get(0).GetDateTime(), list.get(i).GetDateTime()).getSeconds();
            mData[0][i] = 10 + (int)(width * dt / totalTime);
            mData[1][i] = GetTempY(list.get(i).GetTemp()); // 1 is the y temperature
            mData[2][i] = GetHumidityY(list.get(i).GetHumidity()); // 2 is the humidity
            mData[3][i] = GetBarometerY(list.get(i).GetBarometer()); // 3 is the barometer
            mData[4][i] = GetTempY(list.get(i).GetSensorTemp()); // 4 is the y sensor temperature
            mData[5][i] = GetHumidityY(list.get(i).GetSensorHumidity()); // 5 is the sensor humidity
        }
        repaint();
    }
    
    
    private void doDrawing(Graphics graphics)
    {
        Graphics2D g2d = (Graphics2D) graphics;
        setBackground(Color.BLACK);
        
        Dimension dim = getSize();
        int y;
        
        g2d.setColor(Color.red);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.drawString("Temp", 10, dim.height-5);
        g2d.setColor(new Color(128, 0, 0));
        for (double t = 0; t < 120; t+=20) {
            y = GetTempY(t); // freezing level
            drawDashedLine(g2d, 15, y, dim.width-30, y);
            g2d.drawString(String.format("%.0f", t), dim.width-30, y);
        }
        
        g2d.setColor(new Color(128, 128, 255));
        g2d.drawString("Humidity", 50, dim.height-5);
        /* Humidity share the same scale as temperature so don't do this */
        if (false)
        for (double h = 10; h < 100; h+=20) {
            y = GetHumidityY(h);
            drawDashedLine(g2d, 15, y, dim.width-20, y);
            g2d.drawString(String.format("%.0f", h), dim.width-15, y);
        }
        
        g2d.setColor(Color.green);
        g2d.drawString("Barometer", 120, dim.height-5);
        g2d.setColor(new Color(0, 128, 0));
        for (double b = 28; b < 32; b+=1) {
            y = GetBarometerY(b);
            drawDashedLine(g2d, 15, y, dim.width-30, y);
            g2d.drawString(String.format("%.0f", b), 0, y);
        }
        
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        if (mHasSensor) {
            g2d.setColor(Color.magenta);
            g2d.drawString("s-Temp", 200, dim.height-5);
            
            g2d.setColor(Color.cyan);
            g2d.drawString("s-Humidity", 260, dim.height-5);
        }
        
        
        if (mData == null) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("no Data", (int)getSize().getWidth() - 100, dim.height-5);
            return;
        }

        g2d.setColor(Color.red);
        if (true) {
            g2d.drawPolyline(mData[0], mData[1], mData[0].length);
        } else {
            for (int i = 0; i < mData[0].length-2; i++) {
                int r = 255;
                if (i%2 == 0) r = 128;
                g2d.setColor(new Color(r , 0, 0));
                g2d.drawLine(mData[0][i], mData[1][i], mData[0][i+1], mData[1][i+1]);
            }
        }

        g2d.setColor(new Color(128, 128, 255));
        g2d.drawPolyline(mData[0], mData[2], mData[0].length);

        
        g2d.setColor(Color.green);
        g2d.drawPolyline(mData[0], mData[3], mData[0].length);

        
        if (mHasSensor) {
            g2d.setColor(Color.magenta);
            g2d.drawPolyline(mData[0], mData[4], mData[0].length);
    
            g2d.setColor(Color.cyan);
            g2d.drawPolyline(mData[0], mData[5], mData[0].length);
        }
        
        if (mVerbose) {
            g2d.setColor(Color.WHITE);
            g2d.drawString(Integer.toString(mData[0].length), dim.width - 50, dim.height-5);
    
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        doDrawing(g);
    }
}
