import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Toolkit;

public class Simulator
{
    public static void main(String args[])
    {
        UIManager manager = new UIManager();
 
        manager.put("OptionPane.background", Color.white);
        manager.put("Panel.background", Color.white);
        manager.put("Button.background", Color.white);
        manager.put("Button.border", BorderFactory.createLineBorder(Color.white));

        manager.put("OptionPane.okButtonText", "");
        manager.put("OptionPane.okIcon", new ImageIcon("images/ok.jpg"));
        manager.put("OptionPane.cancelButtonText", "");
        manager.put("OptionPane.cancelIcon", new ImageIcon("images/cancel.jpg"));

        String input = JOptionPane.showInputDialog(null, new JLabel(new ImageIcon("images/title.jpg"),JLabel.CENTER), "", JOptionPane.PLAIN_MESSAGE);
        
        int elevator_count = Integer.parseInt(input);
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen_size = toolkit.getScreenSize();

        JFrame.setDefaultLookAndFeelDecorated(true);
        SimulatorFrame simulator = new SimulatorFrame(elevator_count);
        
        simulator.setLocation(screen_size.width / 4, screen_size.height / 4);
        simulator.setDefaultLookAndFeelDecorated(true);
        simulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simulator.show();
    }
}

