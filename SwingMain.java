// Main for Swing Mandelbrot
import javax.swing.*;
import java.awt.Toolkit;
import java.io.*;

class SwingMain
{
 
    /**
     * Constructor for objects of class SwingMain
     */
    SwingMain()
    {
        JEditorPane jep = new JEditorPane();
        jep.setEditable(false);   
        
        try {
          jep.setPage("http://www.soar-high.com");
        }catch (IOException e) {
          jep.setContentType("text/html");
          jep.setText("<html>Could not load</html>");
        } 
        JScrollPane scrollPane = new JScrollPane(jep);

        // Create a new JFrame container.
        JFrame jfrm = new JFrame("A Simple Swing Application");
        
        // set properties
        jfrm.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        jfrm.setUndecorated(true);
        jfrm.setVisible(true);

        
        // Terminate the program when the user closes the application
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create a text-based label
        JLabel jlab = new JLabel(" Swing defines the modern Java GUI.");
        
        // Add the label to the content pane
        jfrm.add(jlab);
        
        jfrm.getContentPane().add(scrollPane);
        
        // Display the frame.
        jfrm.setVisible(true);
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
