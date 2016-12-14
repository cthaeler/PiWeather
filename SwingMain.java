// Main for Swing Mandelbrot
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import java.awt.Image;
import java.net.*;

import javax.imageio.ImageIO;


class SwingMain
{
 
    private ArrayList<DataValue> mValues;
    
    /**
     * Constructor for objects of class SwingMain
     */
    SwingMain()
    {
        // Create a new JFrame container.
        JFrame jfrm = new JFrame("A Simple Swing Application");
        //frm.setLayout(new FlowLayout());
        JPanel mainPanel = new JPanel();
        BoxLayout frameBox = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
        mainPanel.setLayout(frameBox);
        
        JPanel leftPanel = new JPanel();
        BoxLayout leftBox = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        leftPanel.setLayout(leftBox);               
        // set properties
        //jfrm.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        //jfrm.setUndecorated(true);
        //jfrm.setVisible(true);
        
        jfrm.setSize(1024, 600);

        
        // Terminate the program when the user closes the application
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        mValues = new ArrayList<DataValue>();
        LoadValues("foo.xml");
        for (int i=0; i < mValues.size(); i++) {
            DataValue dv = mValues.get(i);

            JLabel v = new JLabel(Double.toString(dv.getValue()));
            v.setFont(new Font("Serif", Font.PLAIN, 64));
            v.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(v);
            
            JLabel l = new JLabel(dv.getLabel());
            l.setFont(new Font("Serif", Font.PLAIN, 10));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(l);
            
            leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }
        mainPanel.add(leftPanel);
        
        
        JEditorPane jep = new JEditorPane();
        jep.setEditable(false);   
        
        try {
          jep.setPage("http://www.soar-high.com/charlie/wx.html");
          //jep.setPage("http://weather.rap.ucar.edu/model/ruc12hr_sfc_prcp.gif");
        }catch (IOException e) {
          jep.setContentType("text/html");
          jep.setText("<html>Could not load</html>");
        }
        
        JScrollPane scrollPane = new JScrollPane(jep);
        //jfrm.getContentPane().add(scrollPane);
        
        //URL url = new URL("http://weather.rap.ucar.edu/model/ruc12hr_sfc_prcp.gif");
        //URL url = new URL("http", "weather.rap.ucar.edu", 80, "/model/ruc12hr_sfc_prcp.gif");
        //Image image = ImageIO.read(url);

        //JLabel imgLabel = new JLabel(new ImageIcon(image));
        mainPanel.add(jep);
        
        jfrm.add(mainPanel);
        // Display the frame.
        jfrm.setVisible(true);
    }
    
    public void LoadValues(String url)
    {
        mValues.add(new DataValue(57.0, "Temperature"));
        mValues.add(new DataValue(10.0, "Humidity"));
        mValues.add(new DataValue(5.0, "Wind Speed"));
        mValues.add(new DataValue(30.0, "Baraometer"));
        mValues.add(new DataValue(60.0, "Dewpoint"));
        mValues.add(new DataValue(10.0, "Visibility"));
    }
    
    public static void main(String args[])
    {
        // Create the frame on the event dispatching thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SwingMain();
            }
        });
    }

}
