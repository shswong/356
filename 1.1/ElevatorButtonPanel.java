// Robert Cheng
// email:  robertmc@gmail.com

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class ElevatorButtonPanel extends JPanel
{
	private JButton floor9,floor8,floor7,floor6,floor5,
					floor4,floor3,floor2,floor1,floor0, closeDoor;

	private JPanel panel;
	private JDialog dialog;

	private int currentFloor;
	private boolean floorRequest[];		// sets the requests made by the users of this elevator to where is must stop at
	private char elevatorDirection;		// the moving direction of this elevator
	private Elevator parentPanel;		// the parent of this panel
	private int elevatorID;				// the elevator that this button panel is associated with
	private char directionToElevator;
	private char insideMoving;


	public ElevatorButtonPanel(Elevator e, int elevatorNumber)
	{
		elevatorID = elevatorNumber;					// sets the id of the elevator
		parentPanel = e;								// gets a pointer to the current elevator
		elevatorDirection = e.getMovingDirection(); 	// sets the moving direction of the elevator

		Icon nine = new ImageIcon("images/9.jpg");
		Icon nine_pressed = new ImageIcon("images/9_pressed.jpg");

		Icon eight = new ImageIcon("images/8.jpg");
		Icon eight_pressed = new ImageIcon("images/8_pressed.jpg");

		Icon seven = new ImageIcon("images/7.jpg");
		Icon seven_pressed = new ImageIcon("images/7_pressed.jpg");

		Icon six = new ImageIcon("images/6.jpg");
		Icon six_pressed = new ImageIcon("images/6_pressed.jpg");

		Icon five = new ImageIcon("images/5.jpg");
		Icon five_pressed = new ImageIcon("images/5_pressed.jpg");

		Icon four = new ImageIcon("images/4.jpg");
		Icon four_pressed = new ImageIcon("images/4_pressed.jpg");

		Icon three = new ImageIcon("images/3.jpg");
		Icon three_pressed = new ImageIcon("images/3_pressed.jpg");

		Icon two = new ImageIcon("images/2.jpg");
		Icon two_pressed = new ImageIcon("images/2_pressed.jpg");

		Icon one = new ImageIcon("images/1.jpg");
		Icon one_pressed = new ImageIcon("images/1_pressed.jpg");

		Icon zero = new ImageIcon("images/0.jpg");
		Icon zero_pressed = new ImageIcon("images/0_pressed.jpg");

		floorRequest = new boolean[10];
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;	// the column
		c.gridy = 0;	// the row
		c.gridwidth = 3;
		floor9 = new JButton(nine);
		floor9.setPressedIcon(nine_pressed);
		floor9.addActionListener(new ElevatorButtonHandler(9, floor9, nine_pressed));
		panel.add(floor9,c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		floor6 = new JButton(six);
		floor6.addActionListener(new ElevatorButtonHandler(6, floor6, six_pressed));
		panel.add(floor6,c);

		c.gridx = 1;
		c.gridy = 1;
		floor7 = new JButton(seven);
		floor7.addActionListener(new ElevatorButtonHandler(7, floor7, seven_pressed));
		panel.add(floor7,c);

		c.gridx = 2;
		c.gridy = 1;
		floor8 = new JButton(eight);
		floor8.setSelected(false);
		floor8.addActionListener(new ElevatorButtonHandler(8, floor8, eight_pressed));
		panel.add(floor8,c);

		c.gridx = 0;
		c.gridy = 2;
		floor3 = new JButton(three);
		floor3.addActionListener(new ElevatorButtonHandler(3, floor3, three_pressed));
		panel.add(floor3,c);

		c.gridx = 1;
		c.gridy = 2;
		floor4 = new JButton(four);
		floor4.addActionListener(new ElevatorButtonHandler(4, floor4, four_pressed));
		panel.add(floor4,c);

		c.gridx = 2;
		c.gridy = 2;
		floor5 = new JButton(five);
		floor5.addActionListener(new ElevatorButtonHandler(5, floor5, five_pressed));
		panel.add(floor5,c);

		c.gridx = 0;
		c.gridy = 3;
		floor0 = new JButton(zero);
		floor0.addActionListener(new ElevatorButtonHandler(0, floor0, zero_pressed));
		panel.add(floor0,c);

		c.gridx = 1;
		c.gridy = 3;
		floor1 = new JButton(one);
		floor1.addActionListener(new ElevatorButtonHandler(1, floor1, one_pressed));
		panel.add(floor1,c);

		c.gridx = 2;
		c.gridy = 3;
		floor2 = new JButton(two);
		floor2.addActionListener(new ElevatorButtonHandler(2, floor2, two_pressed));
		panel.add(floor2,c);

		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 4;
		closeDoor = new JButton("CLOSE DOOR");
		closeDoor.addActionListener(new CloseDoorButtonHandler());
		panel.add(closeDoor,c);

		add(panel);
	}


	public void showDialog(Component parent)
	{

		Frame owner = null;
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;

		owner = (Frame)SwingUtilities.getAncestorOfClass(Frame.class, parent);
		dialog = new JDialog(owner, false);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setLocation(screenWidth/6, screenHeight/3);
		dialog.getContentPane().add(this);
		dialog.getRootPane().setDefaultButton(closeDoor);
		dialog.pack();

		String edirection = "UP";
		if(parentPanel.getElevatorButtonMovingDirection() == 'd')
			edirection = "DOWN";

		dialog.setTitle("Elevator" + elevatorID + " " + edirection);
		dialog.setVisible(true);
	}

	public void resetDialog()
	{
		if (dialog != null) {
			dialog.setVisible(false);
		}

		resetButtons();			// resets the buttons
		resetFloorRequest();	// resets the floors
	}

	private class ElevatorButtonHandler implements ActionListener
 	{
		private int floor;
		JButton button;
		Icon icon;

		ElevatorButtonHandler(int floorReq, JButton b, Icon i)
		{
			floor = floorReq;
			button = b;
			icon = i;
		}

		public void actionPerformed(ActionEvent event)
		{
			if(parentPanel.getElevatorButtonMovingDirection()== 'u' && floor > currentFloor)
			{
				floorRequest[floor] = true;
				button.setIcon(icon);
				directionToElevator = elevatorDirection;
			}
			else if(parentPanel.getElevatorButtonMovingDirection() == 'd' && floor < currentFloor)
			{
				floorRequest[floor] = true;
				button.setIcon(icon);
				directionToElevator = elevatorDirection;
			}
		}
	}

	// once the door has been closed
	private class CloseDoorButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			dialog.setVisible(false);					// close the window

			// send floor requests to the algorithm
			for(int i=0; i<floorRequest.length; i++)
				if(floorRequest[i])
				{
					parentPanel.setStars(9-i);			// displays the floors that have requests made
					parentPanel.setRequestedStops(i);	// sets a list of floors that the elevator must stop at
				}

			parentPanel.setAvailable();		// sets this elevator available for Controller calculation
			parentPanel.setMovingDirection(elevatorDirection);

			// make sure elevator gets set to neutral if it is done with it's stops
			if(!parentPanel.moreStops() && !parentPanel.pickUpRequests())	// if there are no more stops or pick up requests in down direction
			{
				parentPanel.stop();						// stops the animation
				parentPanel.setMovingDirection('n');	// make the state of the elevator in Neutral = not moving
				Controller.retryQueuedRequests();
			}

			if(getAnyFloorRequest() || parentPanel.pickUpRequests() || parentPanel.moreStops())
				parentPanel.animateElevator();	// if there are floor request then animate

			resetButtons();			// resets the buttons
			resetFloorRequest();	// resets the floors

			Controller.retryQueuedRequests();
		}
	}

	// resets the buttons, need this because we are using one reference
	public void resetButtons()
	{
		floor0.setIcon(new ImageIcon("images/0.jpg"));
		floor1.setIcon(new ImageIcon("images/1.jpg"));
		floor2.setIcon(new ImageIcon("images/2.jpg"));
		floor3.setIcon(new ImageIcon("images/3.jpg"));
		floor4.setIcon(new ImageIcon("images/4.jpg"));
		floor5.setIcon(new ImageIcon("images/5.jpg"));
		floor6.setIcon(new ImageIcon("images/6.jpg"));
		floor7.setIcon(new ImageIcon("images/7.jpg"));
		floor8.setIcon(new ImageIcon("images/8.jpg"));
		floor9.setIcon(new ImageIcon("images/9.jpg"));
	}


	public boolean getAnyFloorRequest()
	{
		for(int i=0; i<floorRequest.length; i++)
			if(floorRequest[i])
				return true;
		return false;
	}

	public void resetFloorRequest()
	{
		for(int i=0; i<floorRequest.length; i++)
			floorRequest[i] = false;
	}

	public void setElevatorFloor(int floor)
	{
		currentFloor = floor;
	}

	public void setInsideDirection(char d)
	{
		elevatorDirection = d;
	}
}


