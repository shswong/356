package gui;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import subsystem.Event;
import subsystem.Period;
import subsystem.SimpleTime;

/**
 * A DayCanvas is a canvas which shows you the events for the current day.  Originally extended 
 * java.awt.canvas; now uses JPanel for better compatibility.
 * 
 * 
 */
@SuppressWarnings("serial")
public class DayCanvas extends JPanel implements MouseListener, 
MouseMotionListener, MouseWheelListener, ComponentListener{
	
	private boolean showTimeField;
	private int offset = 50;
	private CakeGUI parent;
	private SimpleTime start, end;
	private boolean draggingAnEvent = false;
	private Point dragStart, dragEnd;
	private EventShape draggedEvent = null, selectedEvent = null;
	private boolean rightButtonPressed = false;

	private ArrayList<EventShape> events;
	
	private Period today;
	private boolean inWeek;

	/**
	 * Constructor.  Assumes that you want to show the time
	 * 
	 * @param parent The CakeGUI that is this DayCanvas' parent.
	 */
	public DayCanvas(CakeGUI parent) {
		setDay(Period.parse(parent.getCurrentYear() + "." + parent.getCurrentMonth() + "." +
				parent.getCurrentDay() + ":00.00-" + parent.getCurrentYear() + "." +
				parent.getCurrentMonth() + "." + parent.getCurrentDay() + ":24.00"));
		inWeek = false;
		initialize(true, parent);
	}

	/**
	 * Constructor
	 * 
	 * @param showTime true if you want to show the time, else false
	 * @param parent The CakeGUI which is this view's parent
	 */
	public DayCanvas(boolean showTime, CakeGUI parent) {
		setDay(Period.parse(parent.getCurrentYear() + "." + parent.getCurrentMonth() + "." +
				parent.getCurrentDay() + ":00.00-" + parent.getCurrentYear() + "." +
				parent.getCurrentMonth() + "." + parent.getCurrentDay() + ":24.00"));
		inWeek = false;
		initialize(showTime, parent);
	}
	
	/**Constructor for showing the time, as well as having a period to show.
	 * 
	 * @param showTime true if you want to show the time, false otherwise
	 * @param parent The CakeGUI to use as this view's parent
	 * @param p The period to have this daycanvas represent.
	 */
	public DayCanvas(boolean showTime, CakeGUI parent, Period p){
		setDay( new Period(p) );
		inWeek = true;
		initialize(showTime,parent);
	}

	/**
	 * Called by the constructors to initialize the dayCanvas
	 * 
	 * @param showTime true if you want to show the time, false otherwise.
	 * @param parent the CakeGUI which is this DayCanvas' parent
	 */
	private void initialize(boolean showTime, CakeGUI parent) {
		events = new ArrayList<EventShape>();
		this.start = new SimpleTime(6, 0);
		this.end = new SimpleTime(18, 0);
		this.showTimeField = showTime;
		this.parent = parent;
		this.updateDataSet();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.addComponentListener(this);
		this.setFocusable(true);
	}
	
	/**
	 * Paint this component.
	 * 
	 */
	public void paintComponent(Graphics g){
		//this.paint(g);
		super.paintComponent(g);
		Graphics2D g2;
		g2 = (Graphics2D) g;

		this.setBackground(Color.white);
		drawTimeSlots(g2);
		drawEvents(g2);
	}

	/**
	 * Render this component
	 * 
	 */
	public void render() {
		updateDataSet();
		updateBounds();
		if (selectedEvent != null) {
			int i;
			for (i = 0; i < events.size(); i++) {
				//go through all of the events and see if it is the selected event
				if (selectedEvent.event.getUID() == events.get(i).event.getUID()) {
					//if it is selected, this should set the color
					selectedEvent = events.get(i);
					selectedEvent.selected = true;
					selectedEvent.redrawImage();
					break;
				}
			}
			if (i == events.size()){
				selectedEvent = null;
			}
		}
		
		repaint();
	}

	/**
	 * Draws the time slots on the canvas
	 * 
	 * @param g2 the Graphics2D that should be used to draw.
	 */
	private void drawTimeSlots(Graphics2D g2) {
		int offset = 0;
		if (showTimeField) offset = this.offset;
		if (inWeek && (today.start.date.format().equals(parent.getCurrentDate().format()))) {
			//if we are not showing the time, uh do something?  
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRect(offset, 0, this.getWidth(), this.getHeight());
		}
		g2.setColor(Color.GRAY);
		int hours = Math.abs(start.hour - end.hour)+1; // how many hours are we showing?
		if (showTimeField) g2.drawLine(offset, 0, offset, this.getHeight());
		for (int i = 0; i < hours; i++) {
			//this loop calculates where to draw the lines
			int in = i*(this.getHeight()/hours);
			
			g2.drawLine(offset, in, this.getWidth(), in);
			if (showTimeField){
				if( start.hour + i > 12 ){
					g2.drawString(start.hour-12+i + ":00", offset/4, 
						in + (this.getHeight()/hours)/2);
				}else{				
				g2.drawString(start.hour+i + ":00", offset/4, 
						in + (this.getHeight()/hours)/2);
				}
			}
		}
		g2.drawLine(this.getWidth()-1, 0, this.getWidth()-1, this.getHeight());
	}

	/**
	 * Draw the events on the day view
	 * 
	 * @param g2 the Graphics2D to use to draw.
	 */
	private void drawEvents(Graphics2D g2) {
		for (int i = 0; i < events.size(); i++) {
			events.get(i).DrawOn(g2);
		}
	}

	/**
	 * Get the number of conflicts out of this event shape
	 * 
	 * @param e The EventShape to get the out conflicts of.
	 * @return an array list of EventShapes which conflict with this EventShape
	 */
	private int conflictsOut(EventShape e) {
		int c = 0;

		int etime = e.event.getPeriod().start.time.hour * 100 + 
		e.event.getPeriod().start.time.minutes; //put the time into HHMM format

		for (int i = 0; i < events.size(); i++) {
			//check all of the events to see how many conflicts there are
			int ftimea = events.get(i).event.getPeriod().start.time.hour * 100 +
			events.get(i).event.getPeriod().start.time.minutes;
			int ftimeb = events.get(i).event.getPeriod().end.time.hour * 100 +
			events.get(i).event.getPeriod().end.time.minutes;
			if (etime >= ftimea && etime < ftimeb && !e.event.getTitle().equals("")){
				//if the start time of the parameter lies in between the start
				//and end time of an event, they conflict.
				c++;
			}
		}

		return c;
	}

	/**
	 * Gets the total number of conflicts for the specified event shape
	 * 
	 * @param e The EventShape to get the total number of conflicts for.
	 * @return The total number of conflicts on this event in an array list.
	 */
	private ArrayList<EventShape> conflictsTotal(EventShape e) {
		ArrayList<EventShape> c = new ArrayList<EventShape>();

		int etimea = e.event.getPeriod().start.time.hour * 100 +
		e.event.getPeriod().start.time.minutes;
		int etimeb = e.event.getPeriod().end.time.hour * 100 +
		e.event.getPeriod().end.time.minutes;
		//convert to HHMM format

		for (int i = 0; i < events.size(); i++) {
			int ftimea = events.get(i).event.getPeriod().start.time.hour * 100 +
			events.get(i).event.getPeriod().start.time.minutes;
			int ftimeb = events.get(i).event.getPeriod().end.time.hour * 100 +
			events.get(i).event.getPeriod().end.time.minutes;
			if ((etimea >= ftimea && etimea < ftimeb) ||
					(ftimea >= etimea && ftimea < etimeb)){
				//either the paramater's start time is in the current event's time,
				//or the current events start time is in the parameter's time
				c.add(events.get(i));
			}
		}

		return c;
	}

	/**
	 * Gets a new data set to render.
	 * 
	 */
	private void updateDataSet() {
		events.clear();
		ArrayList<Event> temp = new ArrayList<Event>();
		try {
			if( !inWeek ){
			setDay(Period.parse(parent.getCurrentYear() + "." + parent.getCurrentMonth() + "." +
					parent.getCurrentDay() + ":00.00-" + parent.getCurrentYear() + "." +
					parent.getCurrentMonth() + "." + parent.getCurrentDay() + ":24.00"));
			}
			temp = (ArrayList<Event>) parent.getEvents(getDay());
						
			if( events.size() != 0){
				selectedEvent = events.get(0);
				selectedEvent.selected = true;
				selectedEvent.redrawImage();
			}
		} catch(Exception e) {
			System.err.println("Error Occured when attempting to get new events:\n" + e.getMessage() + "\nStack Trace:\n" );
			e.printStackTrace();
			System.err.println();
		}

		try {
			for (int i=0; i < temp.size(); i++) {
				events.add(new EventShape(temp.get(i)));
			}
		} catch (NullPointerException s) {
			System.out.println("In updateDataSet()");
			System.out.println(s.getMessage());
		}
	}

	/**
	 * Update the bounds of the events
	 * 
	 */
	private void updateBounds() {
		try {
			int offset = 0;
			if (showTimeField){
				offset = this.offset;
			}
			int hours = Math.abs(start.hour - end.hour)+1;

			for (int i = 0; i < events.size(); i++){ 
				events.get(i).setBounds(new Rectangle(0,0,0,0));
				//set all the bounds to default values.
			}

			//-- Pass 1
			//calculates the height & width.  i think.
			for (int i = 0; i < events.size(); i++) {
				Event e = events.get(i).event;
				int conflicts = this.conflictsOut(events.get(i));
				double height = this.getHeight()/hours;
				height *= ((e.getPeriod().end.time.hour - e.getPeriod().start.time.hour) + 
							(e.getPeriod().end.time.minutes - e.getPeriod().start.time.minutes)/60.0);
				
				events.get(i).setBounds(new Rectangle(0, 0, 
						(this.getWidth()-offset)/conflicts, (int)height ));
				events.get(i).weight = conflictsTotal(events.get(i)).size();
			}
			sortEventsByWeight(events);

			//-- Pass 2
			for (int i = 0; i < events.size(); i++) {
				EventShape e = events.get(i);
				ArrayList<EventShape> conflicts = conflictsTotal(e);
				int conflictsO = conflictsOut(e);
				int reAgain = 0;

				int tWidth = 0;
				int n = 0, m = 0, k = 0;
				for (int j = 0; j < conflicts.size(); j++) {
					if (sameSlot(e, conflicts.get(j)) != true) {
						if (e.weight <= conflicts.get(j).weight) {
							tWidth += conflicts.get(j).getBounds().width;	
							m++;
						} else {
							if ((conflictsO-1) < (conflicts.size()-conflictsO)) {
								reAgain = (this.getWidth()-offset+1)/(conflicts.size()-conflictsO+1);
							}
						}
					} else if (sameSlot(e, conflicts.get(j))) {
						n++;
						if (e.event.getUID() == conflicts.get(j).event.getUID()) {
							k = j;
						}
					}
				}
				int w = (this.getWidth()-(offset+1)-tWidth)/n;
				int x = 1+offset+tWidth+(w*(k-m));
				int y = (e.event.getPeriod().start.time.hour - start.hour)*(this.getHeight()/hours);
				y += ((e.event.getPeriod().start.time.minutes/60.0) * (this.getHeight()/hours));
				int h = e.getBounds().height;
				if (reAgain != 0){
					w = reAgain;
				}
				e.setBounds(new Rectangle(x, y, w, h));
			}
		} catch (Exception s) {
			System.err.println("Error in DayCanvas.java");
			System.err.println(s.getMessage());
			s.printStackTrace();
		}
	}

	/**
	 * Checks to see if the two parameters take up the same spot
	 * 
	 * @param a The first EventShape to check
	 * @param b The second EventShape to check
	 * @return true if they both conflict, false otherwise
	 */
	private boolean sameSlot(EventShape a, EventShape b) {
		return (a.event.getPeriod().start.time.hour == b.event.getPeriod().start.time.hour);
	}

	/**
	 * Sorts the events by their weight(the total number of conflicts)
	 * 
	 * @param a The array list of events to sort by weight.
	 * 
	 */
	private void sortEventsByWeight(ArrayList<EventShape> a) {
		for (int i = 0; i < a.size()-1; i++) {
			for (int j = 0; j < a.size()-1-i; j++) {
				if (a.get(j+1).weight > a.get(j).weight) {
					//bubble sort, swap the two locations
					EventShape e = a.get(j);
					a.set(j, a.get(j+1));
					a.set(j+1, e);
				} else if (a.get(j+1).weight == a.get(j).weight) {
					//if the weights are the same do some more calculations
					if (a.get(j+1).event.getPeriod().start.time.hour < a.get(j).event.getPeriod().start.time.hour) {
						//
						EventShape e = a.get(j);
						a.set(j, a.get(j+1));
						a.set(j+1, e);
					} else if (a.get(j+1).event.getPeriod().start.time.hour == a.get(j).event.getPeriod().start.time.hour){
						//first sort by minutes, then by UID
						if( a.get(j+1).event.getPeriod().start.time.minutes < a.get(j).event.getPeriod().start.time.minutes){
							EventShape e = a.get(j);
							a.set(j, a.get(j+1));
							a.set(j+1, e);	
						}
						else if (a.get(j+1).event.getUID() < a.get(j).event.getUID()) {
							EventShape e = a.get(j);
							a.set(j, a.get(j+1));
							a.set(j+1, e);
						} 
					}
						
				}
			}
		}
	}

	/**
	 * Called when the mouse is clicked
	 * 
	 */
	public void mouseClicked(MouseEvent e) {
		parent.setCurrentDayMonthYear(today.start.date.day,
				today.start.date.month,
				today.start.date.year);
		
		//once we do the update stuff, we can now select the event
		select(e);
		
		if (inWeek && e.getClickCount() == 2) {
			parent.viewChanger.show(parent.center, CakeGUI.DAY);
		}		
	}

	/**
	 * Called when the mouse is pressed
	 * 
	 */
	public void mousePressed(MouseEvent e) {
		dragStart = e.getPoint();
		if (e.getButton() == MouseEvent.BUTTON1) {
				draggingAnEvent = true;
				select(e);
				draggedEvent = selectedEvent;
		} else if (e.getButton() == MouseEvent.BUTTON3) rightButtonPressed = true;
		this.repaint();
	}

	/**
	 * Called when the mouse is released.
	 * 
	 */
	public void mouseReleased(MouseEvent e) {
		dragEnd = e.getPoint();
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (draggingAnEvent && draggedEvent != null) {
				int dy = dragEnd.y - dragStart.y;

				int hourSize = this.getHeight()/(Math.abs(start.hour - end.hour)+1);
				dy = (int) Math.floor(dy/hourSize);
				if (dy != 0) {
					try {
						draggedEvent.event.getPeriod().start.time.hour += dy;
						draggedEvent.event.getPeriod().end.time.hour += dy;
						parent.updateEvent(draggedEvent.event);
						updateDataSet();
						update();
					} catch (Exception x) {
						System.err.println(x.getMessage());
					}
				}
				this.updateBounds();
				select(e);
			}
			draggingAnEvent = false;
			draggedEvent = null;
		} else if (e.getButton() == MouseEvent.BUTTON3) rightButtonPressed = false;
	}

	/**Called when the mouse wheel moves
	 * 
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (inWeek) {
			parent.weekView.Scroll(e, getDay().start.date.day);
		}
		zoomScroll(e);
	}

	/**Called when the mouse is dragged.  ignored
	 * 
	 */
	public void mouseDragged(MouseEvent e) {
		/*Point p = e.getPoint();
		int hours = this.start.hour - this.end.hour;
		
		//int ticks = (int) Math.floor((int)(((double)(this.getHeight()/hours)) / Math.abs(p.y - this.dragStart.y)));
		int ticks = (int)Math.floor((this.getHeight()/hours) / (p.y - this.dragStart.y)) % hours;
		start.hour += ticks;
		end.hour += ticks;
		this.repaint();
		dragStart = p;*/
	}
	
	/**Called when the mouse is moved.
	 * 
	 */
	public void mouseMoved(MouseEvent e) {
		EventShape inEvent = null;
		
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).contains(e.getPoint())) {
				inEvent = events.get(i);
				break;
			}
		}
		
		if (inEvent != null) this.setToolTipText(inEvent.event.getDescription());
		else this.setToolTipText(null);
	}

	/**Called when the mouse entered an object.  ignored.
	 * 
	 */
	public void mouseEntered(MouseEvent arg0) {}
	
	/**Called when the mouse exits an object.  ignored.
	 * 
	 */
	public void mouseExited(MouseEvent arg0) {}

	/**Called when the component is resized.  Makes a call to render()
	 * 
	 */
	public void componentResized(ComponentEvent arg0) {
		render();
	}

	/**Called when the component is shown.  Makes a call to render()
	 * 
	 */
	public void componentShown(ComponentEvent arg0) {
		render();
	}

	/**Called when the component is hidden.  ignored.
	 * 
	 */
	public void componentHidden(ComponentEvent arg0) {}
	
	/**Called when a component is moved.  ignored.
	 * 
	 */
	public void componentMoved(ComponentEvent arg0) {}
	
	/**
	 * zooms in on DayCanvas
	 * 
	 * @param e the MouseWheelEvent 
	 */
	public void zoomScroll(MouseWheelEvent e) {
		int notches = e.getWheelRotation();

		if (notches < 0) {
			if (e.getModifiersEx() != (MouseWheelEvent.WHEEL_UNIT_SCROLL | 
					MouseWheelEvent.CTRL_DOWN_MASK) && start.hour > 0) {
				start.hour -= 1;
				end.hour -= 1;
			} else if (e.getModifiersEx() == (MouseWheelEvent.WHEEL_UNIT_SCROLL | 
					MouseWheelEvent.CTRL_DOWN_MASK)) {
				if (start.hour < 12) start.hour += 1;
				if (end.hour > 12) end.hour -= 1;
			}
		} else {
			if (e.getModifiersEx() != (MouseWheelEvent.WHEEL_UNIT_SCROLL | 
					MouseWheelEvent.CTRL_DOWN_MASK) && end.hour < 24) {
				end.hour += 1;
				start.hour += 1;
			} else if (e.getModifiersEx() == (MouseWheelEvent.WHEEL_UNIT_SCROLL | 
					MouseWheelEvent.CTRL_DOWN_MASK)) {
				if (start.hour > 0) start.hour -= 1;
				if (end.hour < 24) end.hour += 1;
			}
		}
		this.render();
	}
	
	/**
	 * selects an event on DayCanvas.
	 * 
	 * @param e The MouseEvent to be detected.
	 */
	public void select(MouseEvent e) {
		for(int i = 0; i < events.size(); i++) {
			EventShape ev = events.get(i);
			if (ev.contains(e.getPoint())) {
				ev.selected = true;
				ev.redrawImage();
				selectedEvent = ev;
				update();
			}else{
				deselect();
			}
		}

		if (selectedEvent == null) {
			parent.switchTopRightCard("Add Event");
		}
		this.repaint();
	}
	
	/**
	 * deselects an event in the DayCanvas.
	 */
	public void deselect() {
		if (selectedEvent != null) {
			selectedEvent.selected = false;
			selectedEvent.redrawImage();
			selectedEvent = null;
		}
		//parent.weekView.updateWeek();
		repaint();
	}

	/**
	 * Sets which day is to be displayed on the DayCanvas.
	 * 
	 * @param today The day to be displayed. 
	 */
	public void setDay(Period today) {
		this.today = today;
	}

	/**
	 * returns the day that is appeared on the DayCanvas.
	 * 
	 * @return the day that is currently displayed on the DayCanvas.
	 */
	public Period getDay() {
		return today;
	}
	
	/**
	 * updates the DayCanvas so events are displayed.
	 */
	public void update() {
		if (selectedEvent != null) {
			parent.updateEv.fillIn(selectedEvent.event);
			parent.switchTopRightCard("Update Event");
		} else {
			parent.switchTopRightCard("New Event");
		}
	}
}
