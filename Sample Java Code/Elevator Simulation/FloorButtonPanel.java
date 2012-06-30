// Robert Cheng
// email:  robertmc@gmail.com

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class FloorButtonPanel extends JPanel
{
	private JPanel panel;
	private JButton upButton, downButton;
	private boolean upRequested, downRequested;		// to disable another up/down request once pressed
	private JLabel label;
	private Insets insetsButtons = new Insets(0,0,0,0);
	private int floorNumber;


	public FloorButtonPanel(int floorID)
	{
		floorNumber = floorID;

		panel = new JPanel();
		panel.setLayout(new GridLayout(1,3));

		label = new JLabel("Floor " + floorID + " ", SwingConstants.LEFT);
		panel.add(label);

		Icon up = new ImageIcon("images/up.jpg");
		Icon down = new ImageIcon("images/down.jpg");

		upButton = new JButton(up);
		upButton.setMargin(insetsButtons);
		panel.add(upButton);

		if(floorID != 9)
			upButton.addActionListener(new FloorButtonHandler(floorID, true, this));

		downButton = new JButton(down);
		downButton.setMargin(insetsButtons);
		panel.add(downButton);

		if(floorID != 0)
			downButton.addActionListener(new FloorButtonHandler(floorID, false, this));

		add(panel, BorderLayout.EAST);
	}

	private class FloorButtonHandler implements ActionListener
	{
		private int ID;
		private boolean dir;  // true = going up,  false = going down
		private FloorButtonPanel floor;

		public FloorButtonHandler(int id, boolean direction, FloorButtonPanel floor1)
		{
			ID = id;
			dir = direction;
			floor = floor1;
		}

		public void actionPerformed(ActionEvent event)
		{
			if(dir && !upRequested)
			{
				setUpFloorIcon();
				upRequested = true;
				Controller.decide(floorNumber,'u');
			}
			else if(!dir && !downRequested)
			{
				setDownFloorIcon();
				downRequested = true;
				Controller.decide(floorNumber,'d');
			}
		}
	}

	public void setUpFloorIcon()
	{
		upButton.setIcon(new ImageIcon("images/up_pressed.jpg"));
	}

	public void resetUpFloorIcon()
	{
		upButton.setIcon(new ImageIcon("images/up.jpg"));
		upRequested = false;
	}

	public void setDownFloorIcon()
	{
		downButton.setIcon(new ImageIcon("images/down_pressed.jpg"));
	}

	public void resetDownFloorIcon()
	{
		downButton.setIcon(new ImageIcon("images/down.jpg"));
		downRequested = false;
	}

	public void reset()
	{
		resetUpFloorIcon();
		resetDownFloorIcon();
	}
}