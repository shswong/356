// Robert Cheng
// email:  robertmc@gmail.com

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Toolkit;


public class LiftSim
{
	public static void main(String args[])
	{
		// defualt to 5 elevators
		int num = 5;
		if(args.length > 0) {
			Integer num_elevators = new Integer(args[0]);
			num = num_elevators.intValue();
		}

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		JFrame.setDefaultLookAndFeelDecorated(true);
		LiftSimFrame frame = new LiftSimFrame(num);
		frame.setLocation(screenWidth/3, screenHeight/4);
		frame.setDefaultLookAndFeelDecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.show();
	}
}


class LiftSimFrame extends JFrame
{
	private Elevator elevators[];
	private FloorButtonPanel floorButtons[];
	private Controller control;
	private CheckQueue check;

	public LiftSimFrame(int num_elevators)
	{
		setTitle("Elevator Simulation");
		setResizable(false);

		JMenu fileMenu = new JMenu("File");

		fileMenu.add(new AbstractAction("New Simulation")
		{
			public void actionPerformed(ActionEvent event)
			{
				// reset the elevators
				for(int i = 0; i < elevators.length; i++) {
					if(elevators[i] != null) {
						elevators[i].reset();
					}
				}
				// reset the floors
				for(int i = 0; i < floorButtons.length; i++) {
					if(floorButtons[i] != null) {
						floorButtons[i].reset();
					}
				}

				// reset the controller
				control.reset();
			}
		});

		fileMenu.add(new AbstractAction("Exit")
		{
			public void actionPerformed(ActionEvent event)
			{
				System.exit(0);
			}
		});

		JMenu aboutMenu = new JMenu("Help");
		aboutMenu.add(new AbstractAction("About")
		{
			public void actionPerformed(ActionEvent event)
			{
				JOptionPane.showMessageDialog(null, "LiftSim Version 1.0\nGroup 5.0 Productions", "LiftSim Version 1.0", JOptionPane.PLAIN_MESSAGE);
			}
		});

		aboutMenu.add(new AbstractAction("Legend")
		{
			public void actionPerformed(ActionEvent event)
			{
				String legend = "The U button on each floor in yellow signifies that an up request has been signalled\n"
							  + "The U button on each floor in white signifies that NO up request has been signalled\n"
							  + "The D button on each floor in yellow signifies that a down request has been signalled\n"
							  + "The D button on each floor in white signifies that NO down request has been signalled\n"
							  + "Stars next to each elevator signify that an internal stop request was made.\n";

				JOptionPane.showMessageDialog(null, legend,
											"Elevator Simulation Version 1.0 Legend",
											JOptionPane.PLAIN_MESSAGE);
			}
		});


		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		menuBar.add(aboutMenu);

		// create the floor buttons
		floorButtons = new FloorButtonPanel[10];
		for(int i=0; i<floorButtons.length; i++)
			floorButtons[i] = new FloorButtonPanel(i);

		// create the elevators each with their own reference to the floor buttons
		elevators = new Elevator[num_elevators];
		for(int i=0; i<elevators.length; i++)
			elevators[i] = new Elevator(i+1, floorButtons);

		//final Controller control = new Controller(elevators); //Antony
		control = new Controller(elevators); //Antony

		Container contentPane = getContentPane();
		MasterPanel panel = new MasterPanel(floorButtons,elevators);		// top panel passing both pre-created floors and elevators
		contentPane.add(panel);
		pack();

		check = new CheckQueue();
		check.start();

	}
}

