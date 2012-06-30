import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class RightPanel extends JPanel
{
	private JPanel panel; // this Panel

	public RightPanel(Elevator elevators[])
	{
		panel = new JPanel();
		panel.setLayout(new GridLayout(1,elevators.length));	// (rows, columns) pair

		// adds the elevators to the panels
		for(int i=0; i<elevators.length; i++)
			panel.add(elevators[i]);

		add(panel, BorderLayout.WEST);
	}
}
