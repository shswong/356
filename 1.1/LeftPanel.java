import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class LeftPanel extends JPanel
{
	private JPanel panel; // the LeftPanel

	public LeftPanel(FloorButtonPanel floors[])
	{
		panel = new JPanel();
		panel.setLayout(new GridLayout(floors.length,1));	// (rows, columns) pair

		// adds the floor buttons to the left panel
		for(int i=9; i>=0; i--)
			panel.add(floors[i]);

		add(panel, BorderLayout.CENTER);
	}
}