// Robert Cheng
// email:  robertmc@gmail.com

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class MasterPanel extends JPanel
{
	private JPanel panel; // the LeftPanel

	public MasterPanel(FloorButtonPanel floors[], Elevator elevators[])
	{
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;

		c.gridx = 0;
		c.gridy = 0;

		panel.add(new LeftPanel(floors));

		c.gridx = 1;
		c.gridy = 0;

		panel.add(new RightPanel(elevators));
		add(panel);
	}

}
