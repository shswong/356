import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class SimulatorFrame extends JFrame
{
    private Elevator elevators[];
    private FloorButtonPanel floorButtons[];
    private Controller control;
    private ControllerIterator iter;

    public SimulatorFrame(int num_elevators)
    {
        this.setTitle("Elevator Simulation");
        this.setResizable(false);

        JMenu file_menu = new JMenu("Options");
        JMenu about_menu = new JMenu("About");

        file_menu.add(new AbstractAction("Reset")
        {
            public void actionPerformed(ActionEvent event)
            {
                for(int i = 0; i < elevators.length; i++) 
                {
                    if(elevators[i] != null) 
                    {
                        elevators[i].reset();
                    }
                }
                
                for(int i = 0; i < floorButtons.length; i++) 
                {
                    if(floorButtons[i] != null)
                    {
                        floorButtons[i].reset();
                    }
                }

                // reset the controller
                control.reset();
            }
        });

        file_menu.add(new AbstractAction("Exit")
        {
            public void actionPerformed(ActionEvent event){ System.exit(0); }
        });
        
        about_menu.add(new AbstractAction("Authors")
        {
            public void actionPerformed(ActionEvent event)
            {
                JOptionPane.showMessageDialog(null, "", "", JOptionPane.PLAIN_MESSAGE);
            }
        });

        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);
        menu.add(file_menu);
        menu.add(about_menu);

        // create the floor buttons
        floorButtons = new FloorButtonPanel[10];
        for(int i=0; i<floorButtons.length; i++)
        {
            floorButtons[i] = new FloorButtonPanel(i);
        }

        // create the elevators each with their own reference to the floor buttons
        elevators = new Elevator[num_elevators];
        for(int i=0; i<elevators.length; i++)
        {
            elevators[i] = new Elevator(i+1, floorButtons);
        }

        control = new Controller(elevators);

        Container contentPane = getContentPane();
        MasterPanel panel = new MasterPanel(floorButtons,elevators);        // top panel passing both pre-created floors and elevators
        contentPane.add(panel);
        pack();

        iter = new ControllerIterator();
        iter.start();

    }
}