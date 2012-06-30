// Robert Cheng
// email:  robertmc@gmail.com

import java.awt.*;
import java.util.Queue;
import java.util.LinkedList; 
import java.awt.event.*;
import javax.swing.*;

class Elevator extends JPanel implements ActionListener
{
	private JPanel panel;
	public int elevatorID;	// shows which elevator this is
	private boolean isIdle;
    private int currentFloor;
    private char direction; //should only be 'u' for "up," 'd' for "down"
    private Queue q;    //queue of floor this elevator is going to

	// For drawing the elevator/star shaft, elevators and stars
	private Timer timer;										// a thread that animates the elevator
	private JLabel elevatorShaftLabel, starShaftLabel;			// a place holder for the elevator and stars
	private JLabel elevatorLabel, starLabels[];					// the elevator and stars
	private JLayeredPane elevatorShaftPane, starShaftPane;		// the layer underneath the elevator and stars
	private int elevatorShaftWidth, elevatorShaftHeight;		// the elevator shaft width and height
	private int starShaftWidth, starShaftHeight;				// the star width and height
	private int elevatorWidth, elevatorHeight;					// the elevator width and height
	private int starWidth, starHeight;							// the star shaft width and height
	private ElevatorButtonPanel floorRequests;					// used for creating a displayable button panel for this elevator
	private FloorButtonPanel floorButtons[];					// used for resetting the buttons to "not pressed"

	//For the actually logic of the simulation
	private int current_floor;					// the current level of the elevator (starts off at the bottom)
	private int elevator_location;				// the elevator starts at the bottom (in PIXELS)
	private int next_stop_location[];			// actual pixel locations for each stop
	private boolean elevator_pick_up_requests[];// true means there is a request to pick up on this floor
	private boolean elevator_stop_requests[]; 	// true means that there is request to stop at this floor made from inside the elevator
	private char elevator_moving_direction; 	// the moving direction of the elevator
	private int next_stop; 						// the next stop of the elevator (in PIXELS)
	private boolean available;		 			// used to determine if the door is opened (for controller purposes), does not allow this in calculation
	private char inside_moving_direction;		// for the ElevatorButtonPanel so that it knows which direction to allow buttons for

	public Elevator () {
        isIdle = true;
        currentFloor = 1;
        q = new LinkedList();
        direction = 'u'; 
    } 
	public Elevator(int elevatorNumber, FloorButtonPanel floors[])
	{
		isIdle = true;

        currentFloor = 1;

        q = new LinkedList();

        direction = 'u'; //elevator originally starts at ground floor, therefore should be up 
		available = true;
		timer = new Timer(100,this);
		next_stop_location = new int[10];
		elevator_pick_up_requests = new boolean[10];
		elevator_stop_requests = new boolean[10];
		current_floor = 0;				 // botton floor
		elevator_location = 423;		 // bottom floor (pixels)
		floorButtons = floors;			 // initialize with the original FloorButtonPanel objects
		starLabels = new JLabel[10];
		elevator_moving_direction = 'n'; // initialize to neutral state

		for(int i=0; i<10; i++)
			next_stop_location[i] = (9-i) * 47;	// translates all the "stops" to pixels


		/////////////// creates the elevator shaft and elevator ///////////////
		elevatorID = elevatorNumber;
		panel = new JPanel();
		panel.setLayout(new GridLayout(1,2));	// (rows, columns) pair

		Icon elevator_shaft_image = new ImageIcon("images/elevator_shaft.jpg");
		elevatorShaftWidth = elevator_shaft_image.getIconWidth();
		elevatorShaftHeight = elevator_shaft_image.getIconHeight();

		elevatorShaftLabel = new JLabel();
		elevatorShaftLabel.setIcon(elevator_shaft_image);
		elevatorShaftLabel.setOpaque(true);
		elevatorShaftLabel.setBounds(0,0,elevatorShaftWidth,elevatorShaftHeight);

		Icon elevator_image = new ImageIcon("images/elevator.jpg");
		elevatorWidth = elevator_image.getIconWidth();
		elevatorHeight = elevator_image.getIconHeight();

		elevatorLabel = new JLabel();
		elevatorLabel.setIcon(elevator_image);
		elevatorLabel.setBounds(0,elevator_location,elevatorWidth,elevatorHeight);

		elevatorShaftPane = new JLayeredPane();
		elevatorShaftPane.setPreferredSize(new Dimension(elevatorShaftWidth,elevatorShaftHeight));

		elevatorShaftPane.add(elevatorShaftLabel, new Integer(0));
		elevatorShaftPane.add(elevatorLabel, new Integer(1));

		/////////////// creates the star shaft and stars ///////////////
		Icon star_shaft_image = new ImageIcon("images/star_shaft.jpg");
		starShaftWidth = star_shaft_image.getIconWidth();
		starShaftHeight = star_shaft_image.getIconHeight();

		starShaftLabel = new JLabel();
		starShaftLabel.setIcon(star_shaft_image);
		starShaftLabel.setOpaque(true);
		starShaftLabel.setBounds(0,0,starShaftWidth,starShaftHeight);

		Icon star_image = new ImageIcon("images/blank.jpg");
		starWidth = star_image.getIconWidth();
		starHeight = star_image.getIconHeight();

		starShaftPane = new JLayeredPane();
		starShaftPane.setPreferredSize(new Dimension(starShaftWidth,starShaftHeight));
		starShaftPane.add(starShaftLabel, new Integer(0));

		for(int i=0; i<10; i++)
		{
			starLabels[i] = new JLabel();
			starLabels[i].setIcon(star_image);
			starLabels[i].setBounds(0,i*47,starWidth,starHeight);
			starShaftPane.add(starLabels[i], new Integer(1));
		}

		panel.add(elevatorShaftPane);
		panel.add(starShaftPane);
		add(panel);

		/////////////// end of creation of elevator and stars ///////////////
		floorRequests = new ElevatorButtonPanel(this, elevatorNumber); // builds an ElevatorButtonPanel
	}


	// checks if there are anymore stop requests
	public boolean moreStops()
	{
		for(int i=0; i<elevator_stop_requests.length; i++)
			if(elevator_stop_requests[i])
				return true;

		return false;
	}

	// used to set the next stop location goin UP.  Set in pixels because the
	// elevator only knows when to stop by looking at actual pixels
	// need to differential between up and down because finding the next closests stop
	public int getNextStopGoingUp()
	{
		for(int i=0; i<10; i++)
			if(elevator_stop_requests[i] || elevator_pick_up_requests[i])
			{
				next_stop = next_stop_location[i];		// in pixels
				break;
			}

		return next_stop;
	}

	// used to set the next stop location goin UP.  Set in pixels because the
	// elevator only knows when to stop by looking at actual pixels
	public int getNextStopGoingDown()
	{
		for(int i=9; i>=0; i--)
			if(elevator_stop_requests[i] || elevator_pick_up_requests[i])
			{
				next_stop = next_stop_location[i];		// in pixels
				break;
			}

		return next_stop;
	}

	// reset the requested floors on the elevator
	void resetStars(int level)
	{
		starLabels[level].setIcon(new ImageIcon("images/blank.jpg"));
	}

	// for internal, Controller and ElevatorButtonPanel use, converts the pixel location into actual level location
	public int getCurrentLevelInt()
	{
		return Math.abs(elevator_location/47 - 9);
	}

	// to get the precise location
	public double getCurrentLevel()
	{
		return Math.abs(elevator_location/47.0 - 9.0);
	}
	//*******************************Added by Antony (Need this for controller.)*******************************
	public boolean[] getStopRequests() {return elevator_stop_requests;}

	public boolean[] getPickUpRequests() {return elevator_pick_up_requests;}
	//*******************************Added by Antony (Need this for controller.)*******************************

	// a list of stops that have been requested by ElevatorButtonPanel
	public void setRequestedStops(int level)
	{
		elevator_stop_requests[level] = true;
	}

	// set the requested floors on the elevator from ElevatorButtonPanel
	public void setStars(int level)
	{
		starLabels[level].setIcon(new ImageIcon("images/star.jpg"));
	}

	// checks for floor requests if one was made (can be used anytime, even while animation)
	boolean pickUpRequests()
	{
		for(int i=0; i<elevator_pick_up_requests.length; i++)
			if(elevator_pick_up_requests[i])
				return true;

		return false;
	}

	// sets for a pick up request
	public void setPickUpRequests(int level)
	{
		elevator_pick_up_requests[level] = true;
	}


	// for the users inside the elevator so that they know which direction that this elevator is going in
	public void setElevatorButtonMovingDirection(char a)
	{
		inside_moving_direction = a;
	}

	// to display in the elevator button panel
	public char getElevatorButtonMovingDirection()
	{
		return inside_moving_direction;
	}

	// for the controller to set the moving direction of this elevator (for pick up only)
	public void setMovingDirection(char a)
	{
		elevator_moving_direction = a;
	}

	// used by Controller, ElevatorButtonPanel  // u, d, n
	public char getMovingDirection()
	{
		return elevator_moving_direction;
	}


	// used by Controller to see if this elevator is available to include in decision
	public boolean getAvailable()
	{
		return available;
	}

	// used by ElevatorButtonPanel to give ok to include this in Controller's decision
	public void setAvailable()
	{
		available = true;
	}

	// animates the elevator to the next stop based on commands issued from ElevatorButtonPanel
	public void animateElevator()
	{
		timer.setInitialDelay(0);
		timer.setCoalesce(true);
		timer.start();
	}

	public void reset()
	{
		timer.stop();

		int i;

		for(i = 0; i < elevator_pick_up_requests.length; i++) {
			elevator_pick_up_requests[i] = false;
			elevator_stop_requests[i] = false;
		}

		current_floor = 0;				 // botton floor
		elevator_location = 423;		 // bottom floor (pixels)

		elevator_moving_direction = 'n'; // initialize to neutral state

		// redraw elevator in new location
		elevatorLabel.setBounds(0,elevator_location,elevatorWidth,elevatorHeight);

		setAvailable();

		inside_moving_direction = 'n';		// for the ElevatorButtonPanel so that it knows which direction to allow buttons for

		// reset the stars
		for(i = 0; i < starLabels.length; i++)
			resetStars(i);

		floorRequests.resetDialog();

	}
	public void stop()
	{
		timer.stop();
	}
	public void start()
	{
		timer.start();
	}


	// this handles elevator stops requested from inside the elevator
	public void actionPerformed(ActionEvent e)
	{
		if(elevator_moving_direction == 'd')
		{
			if(elevator_location + 6 < getNextStopGoingDown())
			{
				elevator_location+=4;
				elevatorLabel.setBounds(0,elevator_location,elevatorWidth,elevatorHeight);
			}
			else if(elevator_location == getNextStopGoingDown())
			{
				// if this request was made from inside the elevator (ElevatorButtonPanel)
				if(elevator_stop_requests[getCurrentLevelInt()])
				{
					resetStars(9 - getCurrentLevelInt());
					elevator_stop_requests[getCurrentLevelInt()] = false;						// marks this stop request as visited
				}

				if(!moreStops() && !pickUpRequests())	// if there are no more stops or pick up requests in down direction
				{
					timer.stop();						// stops the animation
					elevator_moving_direction = 'n';	// make the state of the elevator in Neutral = not moving
					Controller.retryQueuedRequests();
				}

				// if there is a floor button lit up then need to pause and pick up
				else if(elevator_pick_up_requests[getCurrentLevelInt()])
				{
					timer.stop();
					available = false;		// so that the Controller will not include this elevator in it's decision process
					elevator_pick_up_requests[getCurrentLevelInt()] = false;	// mark this level as picked up
					floorButtons[getCurrentLevelInt()].resetDownFloorIcon();  	// resets the floor requests button for DOWN
					floorRequests.showDialog(this);							// displays the elevator button panels
					floorRequests.setInsideDirection(inside_moving_direction);

					if(inside_moving_direction == 'u')
						floorButtons[getCurrentLevelInt()].resetUpFloorIcon();

					floorRequests.setElevatorFloor(getCurrentLevelInt());
				}
			}
			else
			{
				elevator_location++;
				elevatorLabel.setBounds(0,elevator_location,elevatorWidth,elevatorHeight);
			}
		}

		else if(elevator_moving_direction == 'u')
		{
			if(elevator_location - 6 > getNextStopGoingUp())
			{
				elevator_location-=4;
				elevatorLabel.setBounds(0,elevator_location,elevatorWidth,elevatorHeight);
			}
			else if(elevator_location == getNextStopGoingUp())
			{
				// if this request was made from inside the elevator (ElevatorButtonPanel)
				if(elevator_stop_requests[getCurrentLevelInt()])
				{
					resetStars(9 - getCurrentLevelInt());
					elevator_stop_requests[getCurrentLevelInt()] = false;						// marks this stop request as visited
				}

				if(!moreStops() && !pickUpRequests())	// if there are no more stops or pick up requests in down direction
				{
					timer.stop();						// stops the animation
					elevator_moving_direction = 'n';	// make the state of the elevator in Neutral = not moving
					Controller.retryQueuedRequests();
				}

				// if there is a floor button lit up then need to pause and pick up
				else if(elevator_pick_up_requests[getCurrentLevelInt()])
				{
					timer.stop();
					available = false;		// so that the Controller will not include this elevator in it's decision process
					elevator_pick_up_requests[getCurrentLevelInt()] = false;	// mark this level as picked up
					floorButtons[getCurrentLevelInt()].resetUpFloorIcon();  	// resets the floor requests button for DOWN
					floorRequests.showDialog(this);								// displays the elevator button panels
					floorRequests.setInsideDirection(inside_moving_direction);

					if(inside_moving_direction == 'd')
						floorButtons[getCurrentLevelInt()].resetDownFloorIcon();

					floorRequests.setElevatorFloor(getCurrentLevelInt());
				}
			}
			else
			{
				elevator_location--;
				elevatorLabel.setBounds(0,elevator_location,elevatorWidth,elevatorHeight);
			}
		}
	}
}