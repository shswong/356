package gui;


import widgets.FormatBox;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import observer.Observer;
import observer.Subject;
import subsystem.CakeCal;
import subsystem.CalendarSettings;
import subsystem.Event;
import subsystem.Period;
import subsystem.SimpleDate;
import calendar.Cake;

import opensource.PageSize;

/**
 * Creates a GUI for the Cake Calendar program.  Each instance is made by Cake.  
 * 
 */
public class CakeGUI implements ActionListener,MouseListener,Observer,Subject{

	private Cake parent;

	private ArrayList<Observer> observers = new ArrayList<Observer>();

	//JPanels which each contain a separate panel of information 
	//these panels switch out of the main window when the appropriate
	//button(day,week,month) is pressed
	private JPanel day;
	private JPanel week; 
	private JPanel month;

	public JPanel center; //the JPanel which changes on the button pressed

	//String representations of the different views
	final static String WEEK = "Week";
	final static String DAY = "Day";
	final static String MONTH = "Month";
	final int TODAYYEAR = CakeCal.getDate().year;
	final int TODAYMONTH = CakeCal.getDate().month;
	final int TODAYDAY = CakeCal.getDate().day;

	int currentMonthOffset; 
	int pastMonthOffset;

	private MonthView mview ;
	WeekView weekView;
	private DayView dayView;

	private JLabel yTitle;
	private JToolBar buttonToolBar = new JToolBar();

	int DayToErase = -1;
	int curMonths[];

	private CakeCal calendar; 							// the calendar used to handle all of the events

	public CardLayout viewChanger; 									//the card layout used to switch out of the center

	private EventDialog addEvent; // the instance of the dialog box in the side panel
	private SettingsDialog set; 									// the instance of the settings dialog box which pops up
	public EventDialog updateEv;

	private JPanel p = new JPanel();

	private CardLayout topRight; // the card layout for the top right part of the GUI

	private JPanel topRightPanel;

	private boolean openState; //true if the calendar has events

	/**Constructor for the CakeGUI
	 * 
	 * @param c The CakeCal to use for this GUI.
	 */
	public CakeGUI(CakeCal c){

		topRight = new CardLayout();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

		openState = false;

		calendar = c;
		calendar.attachObserver(this);
		calendar.setCurrentYear(TODAYYEAR);
		calendar.setCurrentMonth(TODAYMONTH);
		calendar.setCurrentDay(TODAYDAY);

		currentMonthOffset = CakeCal.getDayOfWeek(
				SimpleDate.parse(getCurrentYear() + "." + getCurrentMonth() + "." + 1));
		yTitle = new JLabel( getCurrentYear() + "" );
		curMonths = CakeCal.getMonths(getCurrentYear());	// create the days in the particular month

		//make the two context panes
		addEvent = new AddEventDialog(this);
		updateEv = new UpdateEventDialog(this);

		//the panelEvent context pane should update when we click a new day, so it
		//will also observer CakeGUI for changes
		this.attachObserver(addEvent);

		//make a new settings dialog
		set = new SettingsDialog(this);		

		//initialize the classes for the different views
		mview = new MonthView(this);
		weekView = new WeekView(this);
		dayView = new DayView(true, this);

		//add the different views as our observers, they will
		//update when we change
		this.attachObserver(mview);
		this.attachObserver(weekView);
		this.attachObserver(dayView);

		//initialize the three JPanels which will swap out of the center
		initializeCenter();

		//Initialize the buttons to switch out the views. added at the top
		JButton today = new JButton("Today");
		JButton day = new JButton("Day");
		JButton week = new JButton("Week");
		JButton month = new JButton("Month");

		//add action listeners to the buttons
		today.addActionListener(this);
		day.addActionListener(this);
		week.addActionListener(this);
		month.addActionListener(this);

		//add the cards to the top right panel
		topRightPanel = new JPanel( this.topRight );
		topRightPanel.add( addEvent.thePane, "Add Event" );
		topRightPanel.add( updateEv.thePane, "Update Event" );
		topRight.show(topRightPanel,DAY );

		FlowLayout f = new FlowLayout();
		f.setAlignment(FlowLayout.LEFT);
		JPanel top = new JPanel(f);
		buttonToolBar.add(month);
		buttonToolBar.add(week);
		buttonToolBar.add(day);
		buttonToolBar.add(today);
		buttonToolBar.setLayout( new GridLayout() );
		top.add(buttonToolBar);
		buttonToolBar.setPreferredSize(new Dimension(900,32));

		JPanel right = new JPanel();
		right.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.weighty = 1;
		g.weightx = 1;
		g.fill = GridBagConstraints.BOTH;
		right.add(topRightPanel, g);
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 1;
		g.weightx = 1;
		g.weighty = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		
		p.setLayout(new BorderLayout());
		p.add(top, BorderLayout.NORTH);
		p.add( BorderLayout.EAST, right );
		p.add( BorderLayout.CENTER, this.center);
		buttonToolBar.setFloatable(false);

		viewChanger.show(center, MONTH);
		this.updateData();

	}

	/**Initialize the three panels which will switch out in the center
	 * 
	 */
	private void initializeCenter() {
		viewChanger = new CardLayout();

		//initialize the 3 different JPanels which go in the center
		center = new JPanel( viewChanger );
		month = new JPanel();
		mview.addComponentsToPane(month);

		week = weekView.getPanel();

		day = dayView.getPane();
		day.setFocusable(true);

		center.add(week, WEEK);
		center.add(day, DAY);
		center.add(month, MONTH);
	}

	/**Listens for an action.  Takes the appropriate action depending on what was generated.
	 * 
	 * @param e The action event that is generated
	 */
	public void actionPerformed( ActionEvent e ){
		if( e.getActionCommand() == ">>" ) {
			setCurrentYear(getCurrentYear() + 1);
		} else if( e.getActionCommand() == "<<" ){
			if( getCurrentYear() <= 1 ){
				JOptionPane.showMessageDialog(null, "Sorry, you can't go back that far!");
				return;
			}
			setCurrentYear(getCurrentYear() - 1);
		} else if( e.getActionCommand() == ">" ) {
			if( getCurrentMonth() == 12 ){
				setCurrentMonth(1);
				setCurrentYear(getCurrentYear() + 1);
			}else{
				int i = calendar.getCurrentMonth();
				i++;
				setCurrentMonth(i);
			}			
		} else if( e.getActionCommand() == "<") {
			if( getCurrentMonth() == 1 ){
				setCurrentMonth(12);
				setCurrentYear(getCurrentYear() - 1);
			}else
				setCurrentMonth(getCurrentMonth() - 1);
		}  else if( e.getActionCommand() == "Today" ) {
			this.setCurrentMonth(TODAYMONTH);
			this.setCurrentYear(TODAYYEAR);
			this.setCurrentDay(TODAYDAY);
			resetEventPanel();
			updateData();
		}else if (e.getActionCommand() == "YYYY") {

			int year = 0;

			try {
				//Getting the text field input and checking if it's integer!
			} catch (NumberFormatException ex) {
				System.err.println(ex);
			}

			//If the user's input is not
			if( year == 0 ){
				JOptionPane.showMessageDialog(null, "Your input value is incorrect! Please, enter an integer value between 1 and 9999!");
				return;
			} else {
				setCurrentYear(year);
				//dayView.updateDay();
			}

		} else 
			switchTopRightCard("Add Event");

		viewChanger.show(center, (String)e.getActionCommand());
		this.notifyObservers();

	}

	/**Switch out the top right card to be either an AddEvent or an UpdateEvent.
	 * 
	 * @param s The string associated with the card that you want to switch to.
	 */
	public void switchTopRightCard(String s) {
		topRight.show(topRightPanel, s);
	}

	/**Called when there is a mouse event.  Updates the mini-calendar.
	 * 
	 * @param e The mouse event generated.
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && !e.getComponent().getName().trim().equals("")) {
			viewChanger.show(center, DAY);
		}
		int s = Integer.parseInt(e.getComponent().getName());
		if( s < 1 || s > curMonths[calendar.getCurrentMonth()-1]){
			s = calendar.getCurrentDay();
		}
		setCurrentDay(s);
	}

	/**Method to handle when the mouse enters a particular area.  Does not do anything.
	 * 
	 * @param e The mouse event generated.
	 */
	public void mouseEntered(MouseEvent e) {}

	/**Method to handle when the mouse exits a particular area.  Does not do anything.
	 * 
	 * @param e The mouse event generated.
	 */
	public void mouseExited(MouseEvent e) {}

	/**Method to handle when the mouse is clicked.  Does not do anything.
	 * 
	 * @param e The mouse event generated.
	 */
	public void mousePressed(MouseEvent e) {}

	/**Method to handle when the mouse is released.  Does not do anything.
	 * 
	 * @param e The mouse event generated.
	 */
	public void mouseReleased(MouseEvent e) {

	}

	/**Get the events for a specific period.
	 * 
	 * @param p The period to get events for
	 * @return The events for the specific period
	 */
	public List<Event> getEvents( Period p)  {
		return calendar.getEvents(p);
	}

	/**Adds and event to the event database
	 * 
	 * @param e The event to add to the event database
	 */
	public void addEvent( Event e)
	{
		calendar.addEvent(e);
	}

	/**Updates an event.  Simply calls CakeCal.updateEvent()
	 * 
	 * @param e The event to update.
	 */
	public void updateEvent(Event e) 
	{
		calendar.updateEvent(e);
	}

	/**Delete an event.  Simply calls CakeCal.deleteEvent()
	 * 
	 * @param e The event to delete.
	 */
	public void deleteEvent(Event e) {
		calendar.deleteEvent(e);
	}

	/**Return the settings used by this calendar.
	 * 
	 * @return The settings used by this calendar.
	 */
	public CalendarSettings getSettings(){
		return calendar.getSettings();
	}

	/**Set the settings used by this calendar.
	 * 
	 * @param c The CalendarSettings that this calendar should used.
	 */
	public void setSettings( CalendarSettings c){
		calendar.setSettings(c);
	}

	/**Show the settings dialog.
	 * 
	 */
	public void showSettings(){
		set.showDialogBox();
	}

	/**Return the month panel used by this CakeGUI for the MonthView
	 * 
	 * @return The month panel used.
	 */
	public JPanel getMonthPanel(){
		return month;
	}

	/**Set the month panel to a new panel.
	 * 
	 * @param m The new Month panel to use.
	 */
	public void setMonthPanel( JPanel m ){
		month = m;
	}

	/**Calls CakeCal modified.
	 * 
	 * @return True if the calendar has been modified, else false
	 */
	public boolean isModified(){
		return calendar.modified;
	}

	/** Return an array list of events that the calendar is currently keeping track of
	 * 
	 * @return The array list of events that the calendar is keeping track of
	 */
	public ArrayList<Event> getEvents(){
		return calendar.getEvents(Period.parse(getCurrentYear() + "." + getCurrentMonth() + ".1:00.00-" + getCurrentYear() +
				"." + getCurrentMonth() + "." + curMonths[getCurrentMonth()-1] + ":24.00"));
	}

	/**Return the instance of JPanel that this calendar uses
	 * 
	 * @return The instance of JPanel that all the elements are in for this calendar.
	 */
	public JPanel getPanel(){
		return p;
	}

	/**Gets the current date that is displayed by this CakeGUI.
	 * 
	 * @return A new SimpleDate which represents the current day.
	 */
	public SimpleDate getCurrentDate() {
		return new SimpleDate(this.getCurrentMonth(), this.getCurrentDay(), this.getCurrentYear());
	}

	/**Set the parent of this CakeGUI.  The parent is defined as the instance of Cake which created this CakeGUI.
	 * 
	 * @param c The instance of Cake to set the parent of this CakeGUI to.
	 */
	public void setParent(Cake c){
		parent = c;
	}

	/**Get the CakeCal that this calendar uses.
	 * 
	 * @return The CakeCal that the calendar is using.
	 */
	public CakeCal getCakeCal() {
		return calendar;
	}

	/**Is this calendar open?
	 * 
	 * @return 
	 */
	public boolean isOpenState(){
		return openState;
	}

	/**Get new data when something we are looking at changes.
	 * 
	 */
	public void updateData() {
		//notify our observers that something has changed

		//first we need to figure out what our offset is
		currentMonthOffset = CakeCal.getDayOfWeek(
				SimpleDate.parse(getCurrentYear() + "." + getCurrentMonth() + "." + 1));

		//then, we will notify our observers that something has changed
		this.notifyObservers();
	}

	/**Attach an observer to this object.
	 * 
	 */
	public void attachObserver(Observer o) {
		observers.add(o);
	}

	/**Notify our observers that a change has happened.
	 * 
	 */
	public void notifyObservers() {
		Iterator<Observer> i = observers.iterator();
		while( i.hasNext() ){
			Observer o = i.next();
			o.updateData();
		}

	}

	/**Get the current month offset.  The current month offset is how many days
	 * should be allocated for the previous month, depending on what day
	 * of the week the first of the month is.
	 * 
	 * @return The current month offset.
	 */
	public int getCurrentMonthOffset(){
		return currentMonthOffset;
	}

	/**Set the current month of the CakeGUI.
	 * 
	 * @param currentMonth The current month of the calendar.
	 */
	void setCurrentMonth(int currentMonth) {
		calendar.setCurrentMonth(currentMonth);
		updateData();
	}

	/**Get the current month that we are in.
	 * 
	 * @return The current month that we are in.
	 */
	int getCurrentMonth() {
		return calendar.getCurrentMonth();
	}

	/**Set the current year of the CakeGUI.
	 * 
	 * @param currentYear The new year.
	 */
	void setCurrentYear(int currentYear) {
		calendar.setCurrentYear(currentYear);
		yTitle.setText(currentYear + "");
		updateData();
	}

	/**Get the current year of the cakeGUI.
	 * 
	 * @return The current year of the CakeGUI.
	 */
	int getCurrentYear() {
		return calendar.getCurrentYear();
	}

	/**Set the current day of the CakeGUI.
	 * 
	 * @param currentDay The new day that we want.
	 */
	void setCurrentDay(int currentDay) {		
		calendar.setCurrentDay(currentDay);
		updateData();
	}

	/**Get the current day of the program.
	 * 
	 * @return "Today", as in what day is selected.  If you want "today" for what day your computer
	 * is currently on, use CakeGUI.TODAYDAY
	 */
	int getCurrentDay() {
		return calendar.getCurrentDay();
	}

	/**Get the month view used by this CakeGUI.
	 * 
	 * @return The month view used by this CakeGUI.
	 */
	MonthView getMonthView(){
		return mview;
	}

	/**Use this method when you want to set both the day and the month at the same time.
	 * This method is here so that setting the day and the month immediately after each
	 * other in the calling class will not cause odd things to happen, because the update
	 * will happen before the second method can be called.
	 * 
	 * @param day2 The new current day
	 * @param month2 The new current month
	 */
	public void setCurrentDayMonth(int day2, int month2) {
		calendar.setCurrentDay(day2);
		calendar.setCurrentMonth(month2);
		updateData();
	}
	
	/**Use this method when you want to set the day, month, and year at the same time.
	 * See setCurrentDayMonth for the reason why.
	 * 
	 * @param day2 The new current day
	 * @param month2 The new current month
	 * @param year2 The new current year
	 */
	public void setCurrentDayMonthYear(int day2, int month2, int year2){
		calendar.setCurrentYear(year2);
		setCurrentDayMonth(day2,month2);
	}

	public DayView getDayView(){
		return dayView;
	}

	/**Updates the sizes of relevant components.  Pass in a Dimension of the current size of 
	 * the cake window
	 * 
	 * @param d The dimensions of the main window
	 */
	public void updateSize(Dimension d){
		mview.updateFontSize(d);
	}

	/**Reset the event panel so that it is reset to the default, Add Event.
	 * 
	 */
	public void resetEventPanel(){
		switchTopRightCard("Add Event");
	}

	/**Export this calendar in a PDF format.  Does not make a dialog box; instead
	 * switches out of the center panel in place of the current view.
	 * 
	 */
	public void exportAsPDF()
	{
		JPanel PDFoptions = new JPanel(new GridLayout(4,1));

		JPanel titlePanel = new JPanel();
		titlePanel.add(new JLabel("PDF export options"));
		PDFoptions.add(titlePanel);

		//the time panel is where the user selects what time period they would like to 
		//generate a pdf for
		JPanel timePanel = new JPanel();
		timePanel.add(new JLabel("Time Period:"));
		final FormatBox startDate = new FormatBox("date");
		startDate.setColumns(10);
		SimpleDate d = new SimpleDate(getCurrentMonth(), getCurrentDay(), getCurrentYear());
		startDate.setData(d.format());
		timePanel.add(startDate);
		final FormatBox endDate = new FormatBox("date");
		endDate.setColumns(10);
		endDate.setData(d.format());
		timePanel.add(endDate);
		PDFoptions.add(timePanel);

		//the pageSize pane is in the middle bottom part
		JPanel pageSize = new JPanel();
		pageSize.add(new JLabel("Page Size:"));
		final JRadioButton letter = new JRadioButton("Letter");
		final JRadioButton legal = new JRadioButton("Legal");
		final JRadioButton a4 = new JRadioButton("A4");
		final ArrayList<JRadioButton> test = new ArrayList<JRadioButton>(3);
		test.add(letter);
		test.add(a4);
		test.add(legal);
		letter.setSelected(true);
		ActionListener buttonAL = new ActionListener(){
			//this action listener modifies the buttons that are selected.
			public void actionPerformed(ActionEvent arg0) {
				for( JRadioButton jr : test){
					if(jr.getActionCommand().equals(arg0.getActionCommand()))
						jr.setSelected(true);
					else
						jr.setSelected(false);
				}
			}
		};
		letter.addActionListener(buttonAL);
		a4.addActionListener(buttonAL);
		legal.addActionListener(buttonAL);
		pageSize.add(letter);
		pageSize.add(legal);
		pageSize.add(a4);
		PDFoptions.add(pageSize);

		//the buttons control the export of the PDF
		JPanel buttons = new JPanel();
		JButton ex = new JButton("Export");
		JButton cancel = new JButton("Cancel");
		buttons.add(ex);
		buttons.add(cancel);
		PDFoptions.add(buttons);

		ActionListener exportAL = new ActionListener(){
			//this action listener does the appropriate action when the user
			//clicks on 'export'
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				chooser.setFileFilter(new FileFilter(){
					@Override
					public boolean accept(File arg0) {
						//alright the level of indentation is getting somewhat ridiculous here
						if(arg0.isDirectory())
							return true;
						int indexOf = arg0.getName().lastIndexOf('.');
						String extension = arg0.getName().substring(indexOf, arg0.getName().length());
						if (extension.equalsIgnoreCase(".pdf"))
							return true;
						return false;
					}
					@Override
					public String getDescription() {
						return "Portable Document Format (*.pdf)";
					}

				});
				chooser.showSaveDialog(null);
				Period p = Period.parse(startDate.format() + ":00.00-" + 
						endDate.format() + ":24.00");
				String filename = chooser.getSelectedFile().getAbsolutePath();
				int lastIndex = filename.lastIndexOf( '.' );
				if( lastIndex == -1 ){
					filename = filename.concat(".pdf");
				}
				
				com.itextpdf.text.Rectangle pageSize = null;
				if(letter.isSelected())
				{
					pageSize = PageSize.LETTER;
				}
				if(legal.isSelected())
				{
					pageSize = PageSize.LEGAL;
				}
				if(a4.isSelected())
				{
					pageSize = PageSize.A4;
				}
				
				calendar.exportAsPDF(p, filename, pageSize);
				
				//now that we have exported the calendar, we can go back to month view
				viewChanger.show(center, MONTH);
			}			
		};
		ex.addActionListener(exportAL);

		//when the user clicks cancel, go to month view.
		cancel.setActionCommand(MONTH);
		cancel.addActionListener(this);

		center.add(PDFoptions, "PDF");
		viewChanger.show(center, "PDF");
	}

}
