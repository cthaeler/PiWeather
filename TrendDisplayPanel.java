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
    
    public void UpdateData(ArrayList<TrendData> list)
    {
        if (list.size() < 2) return;

        Dimension dim = getSize();
        int height = (int)dim.getHeight() - 5; // allow a bourder
        int width = (int)dim.getWidth() - 20;
        
        mData = new int[4][list.size()];
        long totalTime = Duration.between(list.get(0).GetDateTime(), LocalDateTime.now()).getSeconds();

        for (int i=0; i < list.size(); i++)
        {
            long dt = Duration.between(list.get(0).GetDateTime(), list.get(i).GetDateTime()).getSeconds();
            mData[0][i] = 10 + (int)(width * dt / totalTime);
            mData[1][i] = height - (int)list.get(i).GetTemp(); // 1 is the y temperature
            mData[2][i] = height - (int)list.get(i).GetHumidity(); // 2 is the humidity
            mData[3][i] = height - (int)list.get(i).GetBarometer(); // 3 is the barometer
        }
        repaint();
    }
    
    private void doDrawing(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        setBackground(Color.BLACK);
        
        g2d.setColor(Color.red);
        g2d.drawString("Temp", 10, (int)getSize().getHeight()-5);
        
        g2d.setColor(Color.blue);
        g2d.drawString("Humidity", 50, (int)getSize().getHeight()-5);
        
        g2d.setColor(Color.cyan);
        g2d.drawString("Barometer", 100, (int)getSize().getHeight()-5);
        
        if (mData == null) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("no Data", (int)getSize().getWidth() - 100, (int)getSize().getHeight()-5);
            return;
        }

        g2d.setColor(Color.red);
        g2d.drawString("Temp", 10, (int)getSize().getHeight()-5);
        g2d.drawPolyline(mData[0], mData[1], mData[0].length);
        //for (int i = 0; i < mData[0].length-2; i++) {
        //    g2d.drawLine(mData[0][i], mData[1][i], mData[0][i+1], mData[1][i+1]);
        //}

        g2d.setColor(Color.blue);
        g2d.drawPolyline(mData[0], mData[2], mData[0].length);
        
        g2d.setColor(Color.cyan);
        g2d.drawPolyline(mData[0], mData[3], mData[0].length);

        g2d.setColor(Color.WHITE);
        g2d.drawString(Integer.toString(mData[0].length), (int)getSize().getWidth() - 50, (int)getSize().getHeight()-5);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        doDrawing(g);
    }
}
