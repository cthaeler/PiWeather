import javax.swing.*;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
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
    private boolean mVerbose = false;
    
    public void UpdateData(ArrayList<TrendData> list, boolean verbose)
    {
        if (list.size() < 2) return;
        
        mVerbose = verbose;
        
        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 10; // allow a bourder
        int width = (int)dim.getWidth() - 30;
        
        mData = new int[4][list.size()];
        long totalTime = 24*60*60*3; 

        for (int i=0; i < list.size(); i++)
        {
            long dt = Duration.between(list.get(0).GetDateTime(), list.get(i).GetDateTime()).getSeconds();
            mData[0][i] = 10 + (int)(width * dt / totalTime);
            mData[1][i] = height - (int)list.get(i).GetTemp(); // 1 is the y temperature
            mData[2][i] = height - (int)list.get(i).GetHumidity(); // 2 is the humidity
            // need to scale barometric preasure
            double baro = ((list.get(i).GetBarometer() - 27.0) * 25.0); // 27 == 0
            mData[3][i] = height - (int)baro; // 3 is the barometer
        }
        repaint();
    }
    
    
    private void doDrawing(Graphics graphics)
    {
        Graphics2D g2d = (Graphics2D) graphics;
        setBackground(Color.BLACK);
        
        g2d.setColor(Color.red);
        g2d.drawString("Temp", 10, (int)getSize().getHeight()-5);
        
        g2d.setColor(Color.blue);
        g2d.drawString("Humidity", 50, (int)getSize().getHeight()-5);
        
        g2d.setColor(Color.green);
        g2d.drawString("Barometer", 120, (int)getSize().getHeight()-5);
        
        if (mData == null) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("no Data", (int)getSize().getWidth() - 100, (int)getSize().getHeight()-5);
            return;
        }

        boolean usePolyline = !mVerbose;
        g2d.setColor(Color.red);
        g2d.drawString("Temp", 10, (int)getSize().getHeight()-5);
        if (usePolyline) {
            g2d.drawPolyline(mData[0], mData[1], mData[0].length);
        } else {
            for (int i = 0; i < mData[0].length-2; i++) {
                int r = 255;
                if (i%2 == 0) r = 128;
                g2d.setColor(new Color(r , 0, 0));
                g2d.drawLine(mData[0][i], mData[1][i], mData[0][i+1], mData[1][i+1]);
            }
        }

        g2d.setColor(Color.blue);
        if (usePolyline) {
            g2d.drawPolyline(mData[0], mData[2], mData[0].length);
        } else {
            for (int i = 0; i < mData[0].length-2; i++) {
                int b = 255;
                if (i%2 == 0) b = 128;
                g2d.setColor(new Color(0 , 0, b));
                g2d.drawLine(mData[0][i], mData[2][i], mData[0][i+1], mData[2][i+1]);
            }
        }
        
        g2d.setColor(Color.green);
        if (usePolyline) {
            g2d.drawPolyline(mData[0], mData[3], mData[0].length);
        } else {
            for (int i = 0; i < mData[0].length-2; i++) {
                int g = 255;
                if (i%2 == 0) g = 128;
                g2d.setColor(new Color(0 , g, 0));
                g2d.drawLine(mData[0][i], mData[3][i], mData[0][i+1], mData[3][i+1]);
            }
        }
        
        if (mVerbose) {
            g2d.setColor(Color.WHITE);
            g2d.drawString(Integer.toString(mData[0].length), (int)getSize().getWidth() - 50, (int)getSize().getHeight()-5);
    
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        doDrawing(g);
    }
}
