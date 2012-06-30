package calendar;

import gui.CakeFrame;
import gui.CakeGUI;
import gui.SuggestionFrame;
import gui.Tray;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import observer.Observer;
import subsystem.CakeCal;
import subsystem.DatabaseConnection;
import subsystem.DatabaseEvent;
import subsystem.Event;
import subsystem.Period;
import subsystem.ProgramSettings;
import subsystem.SimpleDate;
import subsystem.SimpleDateTime;
import subsystem.SimpleTime;

/**
 * <h1>Cake Calendar</h1>
 * 
 * The main class of the Cake Calendar System.  This handles all of the GUI elements.  When you start the Cake Calendar, this makes a new
 * instance of itself, CakeCal, and CakeGUI.  When adding a new calendar, a new CakeCal and CakeGUI are associated with each other.
 * Note that the GUI part of the application exists <i>both</i> here in Cake <i>and</i> in 
 * CakeGUI.  This is because different aspects of the GUI have to be associated with different
 * calendars, due to the multiple calendars feature.
 * 
 */

public class Cake implements WindowListener,ActionListener,Observer,ComponentListener
{

	static DatabaseConnection connection;
	private  CakeFrame mainWindow;
	private  CardLayout bigViewChanger;
	private  JPanel main;
	private  CakeGUI currentView;
	//private  JMenu viewCal;
	private  JMenu calendar;
	private  ArrayList<CakeGUI> calendars;
	private  ArrayList<JRadioButtonMenuItem> radioButtons;
	private  final String GUITitle = "Cake Calendar GUI - "; 
	private  String oldName;
	private boolean dispose = false;
	private ArrayList<String> invalidNames;

	private final int TODAYYEAR = CakeCal.getDate().year;
	private final int TODAYMONTH = CakeCal.getDate().month;
	private final int TODAYDAY = CakeCal.getDate().day;

	private final Period forTray = Period.parse( TODAYYEAR + "." + TODAYMONTH + "." + TODAYDAY + ":" + "00.00" + "-"
			+ TODAYYEAR + "." + TODAYMONTH + "." + TODAYDAY + ":" + "23.59");
	public Tray todaysEvents;

	private Rectangle bounds; //the bounds of the JFrame.  Used when the window is iconified/deiconified

	//Strings to be used for various menus
	public static final String CLOSE = "Close Calendar";
	public static final String EXIT = "Exit";
	public static final String ABOUT = "About";
	public static final String EXPORT_PDF = "Export calendar as PDF";

	/**This is the main method that should be used to run the Cake Calendar.
	 * 
	 * @param args  System arguments.  If provided, the calendar will start with the 
	 * specified calendar open when it starts
	 * @throws Exception 
	 * 
	 */
	public static void main(String args[]) throws Exception 
	{		
		connection = new DatabaseConnection();
		CakeCal cal = new CakeCal();
		CakeGUI gui = new CakeGUI(cal);
		Cake c = new Cake( gui );
		gui.setParent(c);
		
		ArrayList<DatabaseEvent> list = new ArrayList<DatabaseEvent>();
		
		list = connection.getAllEvents(list);
		
		for(int i = 0; i < list.size(); i++)
		{
			DatabaseEvent d = list.get(i);
			
			Period period = new Period(
					new SimpleDateTime(
							new SimpleDate(d.getStartMonth(), d.getStartDay(), d.getStartYear()), 
							new SimpleTime(d.getStartHour(), d.getStartMin())), 
					new SimpleDateTime(
							new SimpleDate(d.getEndMonth(), d.getEndDay(),d.getEndYear()),
							new SimpleTime(d.getEndHour(), d.getEndMin())));
			
			gui.addEvent(new Event(period, d.getEventName(), "", "0", ""));
		}
		
		ProgramSettings.loadSettings();
	}

	/**Construct
	 * 
	 * @param g - The CakeGUI to originally start the program with.
	 */
	public Cake(CakeGUI g)
	{
		currentView = g;
		bigViewChanger = new CardLayout();
		main = new JPanel(bigViewChanger);
		
		main.add(currentView.getPanel(),currentView.getSettings().getName());


		//Vlad's Menu
		JMenuBar menuBar = new JMenuBar();

		//Handling ---> FILE menu
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		//Export as PDF
		JMenuItem pdfExport = new JMenuItem(EXPORT_PDF);
		pdfExport.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		pdfExport.addActionListener(this);
		file.add(pdfExport);
		//-----------Separator-------------//
		file.addSeparator();
		//CLOSE CALENDAR
		JMenuItem closeCalendar = new JMenuItem(CLOSE);
		closeCalendar.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_C, ActionEvent.ALT_MASK));
		closeCalendar.addActionListener(this);
		file.add(closeCalendar);
		//EXIT
		JMenuItem exit = new JMenuItem(EXIT, KeyEvent.VK_X);
		exit.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_X, ActionEvent.ALT_MASK));
		exit.addActionListener(this);
		file.add(exit);

		//Handling ---> EDIT menu
		JMenu edit = new JMenu("Edit");
		edit.setMnemonic(KeyEvent.VK_E);

		//Handling ---> CALENDAR menu
		calendar = new JMenu("Calendar");
		calendar.setMnemonic(KeyEvent.VK_C);
		JRadioButtonMenuItem cal1 = new JRadioButtonMenuItem(currentView.getSettings().getName(), true);
		radioButtons = new ArrayList<JRadioButtonMenuItem>();
		radioButtons.add( cal1 );
		cal1.addActionListener(this);
		calendar.add(cal1);

		//Handling ---> HELP menu
		JMenu help = new JMenu("Help");
		help.setMnemonic(KeyEvent.VK_H);
		//HELP

		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(calendar);
		menuBar.add(help);

		todaysEvents = new Tray( g.getCakeCal().getEvents(forTray),g.getCakeCal().getSettings().getFilename(),this );

		mainWindow = new CakeFrame("CakeGUI - " + currentView.getSettings().getFilename(),this);
		mainWindow.add( main );
		mainWindow.setJMenuBar(menuBar);
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.addWindowListener(this);
		mainWindow.addComponentListener(this);
		mainWindow.pack();
		mainWindow.setExtendedState(mainWindow.getExtendedState() | Frame.MAXIMIZED_BOTH);
		mainWindow.setFocusable(true);
		mainWindow.setVisible(true);

		//switchComponent( currentView );
		bigViewChanger.show(main,currentView.getSettings().getName());
		mainWindow.setTitle(GUITitle + currentView.getSettings().getName());
		mainWindow.repaint();

		calendars = new ArrayList<CakeGUI>();
		calendars.add(currentView);

		invalidNames = new ArrayList<String>();
		invalidNames.add(CLOSE);
		invalidNames.add(EXIT);
		invalidNames.add(ABOUT);
		invalidNames.add(EXPORT_PDF);
		invalidNames.add(currentView.getSettings().getName());
	}

	/**Switch to the specified component.
	 * 
	 * @param card
	 */
	private void switchComponent( CakeGUI card ){
		//currentView = card;
		bigViewChanger.show(main,card.getSettings().getName());
		//makes none of the radio buttons selected.
		for( int x = 0; x < radioButtons.size(); x++){
			radioButtons.get(x).setSelected(false);
			if( radioButtons.get(x).getText().equals(card.getSettings().getName())){
				radioButtons.get(x).setSelected(true);
			}
		}
		mainWindow.setTitle(GUITitle + currentView.getSettings().getName());

	}


	/**Called when the window is activated.  Ignored.
	 * 
	 * @param e The window event to do something with.
	 */
	public void windowActivated(WindowEvent e) {
	}

	/**Called when the window is closed.
	 * 
	 * @param e The WindowEvent to do something with
	 */
	public void windowClosed(WindowEvent e) 
	{
		try {
			connection.closeConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**Called when the window is closing, and when the user goes to File -> Exit
	 * 
	 * @param e The WindowEvent to do something with.
	 */
	public void windowClosing(WindowEvent e) {
		for( int x = 0; x < calendars.size(); x++){
			currentView = calendars.get(x);
			int selection = 3; //non-existant selection by default
			if( currentView.isModified() ){
				selection = JOptionPane.showConfirmDialog(mainWindow,"Save calendar " + currentView.getSettings().getName() + " before Quitting?");
				//System.out.println(selection);
			}

			if( selection == 2 ){
				dispose = false;
				//return;
			}
			else{
				dispose = true;
			}
		}
		mainWindow.dispose();
		
		

		//System.exit(0);
	}

	/**Called when the window is deactivated.  Ignored.
	 * 
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	/**Called when the window is Deiconified.  
	 * 
	 */
	public void windowDeiconified(WindowEvent e) {
		if(ProgramSettings.getVars(ProgramSettings.minimizeToTray).getBoolean() == true){
			mainWindow.setVisible(true);
			mainWindow.setBounds(bounds);
			mainWindow.toFront();
		}		

	}

	/**Called when the window is iconified. Checks to see if we should minimize to tray,
	 * and if we should set the window to be invisible.
	 * 
	 */
	public void windowIconified(WindowEvent e) {
		if(ProgramSettings.getVars(ProgramSettings.minimizeToTray).getBoolean() == true){
			bounds = mainWindow.getBounds();
			mainWindow.setVisible(false);
		}
	}

	/**Called when the window is opened.  Ignored.
	 * 
	 */
	public void windowOpened(WindowEvent e) {

	}

	/**Does an action depending on the specified ActionEvent.  Handles switching calendars, and menu bar.  Other actions are performed
	 * by the CakeGUI.
	 * 
	 * @param e The ActionEvent to do something with.
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if( e.getActionCommand().equals(EXIT) ) {
			
			this.windowClosing(new WindowEvent(mainWindow,WindowEvent.WINDOW_CLOSING));
		}
		else if( e.getActionCommand().equals(CLOSE)){
			int selection = 3; //non-existant selection by default
			if( currentView.isModified() ){
				selection = JOptionPane.showConfirmDialog(mainWindow,"Close Calendar " + currentView.getSettings().getName() + "?");
				//System.out.println(selection);
			}			
			if( selection == JOptionPane.CANCEL_OPTION ){
				return;
			}
			bigViewChanger.removeLayoutComponent(currentView.getPanel());
			//remove the calendar from the array list of calendars
			for( int x = 0; x < calendars.size(); x++){
				if( calendars.get(x).equals(currentView)){
					calendars.remove(x);
				}
			}

			//remove the calendar from the list of invalid names
			for(int x = 0; x < invalidNames.size(); x++){
				if(invalidNames.get(x).equals(currentView.getSettings().getName())){
					invalidNames.remove(x);
				}
			}

			//remove the calendar from the array list of radio buttons
			for( int x = 0; x < radioButtons.size(); x ++){
				if( radioButtons.get(x).getText().equals(currentView.getSettings().getName())){
					calendar.remove(radioButtons.get(x));
					radioButtons.remove(x);
				}
			}

			//update the view(defaults to the first calendar)
			if( calendars.size() > 0){
				currentView = calendars.get(0);
				radioButtons.get(0).setSelected(true);
				switchComponent( currentView );
			}
			else{
				JOptionPane.showMessageDialog(mainWindow,"Sorry, but you can't close that","Close Error",0);

			}
		} 
		else if (e.getActionCommand().equals(ABOUT)) 
		{
			// JOptionPane here
		}
		else if( e.getActionCommand().equals(EXPORT_PDF))
		{
			currentView.exportAsPDF();
		}


		//search thru the array to find the right card to switch to
		//if there is no calendar with this name, it does not switch
		for( int x = 0; x < calendars.size(); x++ ){
			if( calendars.get(x).getSettings().getName().equals(e.getActionCommand())){
				currentView = calendars.get(x);
				switchComponent( currentView );
			}
		}

		if( currentView.isModified() == true ) {
			todaysEvents.updateTodayEvent( currentView.getCakeCal().getEvents(forTray));
		}

	}

	/**Update the GUI to reflect the new changes in the title of a calendar.
	 * 
	 */
	public void updateGUI(){
		bigViewChanger.removeLayoutComponent(currentView.getPanel());
		main.add(currentView.getPanel(), currentView.getSettings().getName());
		bigViewChanger.show(main, currentView.getSettings().getName());
		mainWindow.setTitle(GUITitle + currentView.getSettings().getName());
		for( int x = 0; x < radioButtons.size(); x++ ){
			if( radioButtons.get(x).getText().equals(oldName)){
				radioButtons.get(x).setText(currentView.getSettings().getName());
			}
		}
	}

	/**Called by CakeGUI with the new name of the calendar when saving a file.  Sets up a proper call to updateGUI()
	 * 
	 * @param name The old name of the calendar
	 */
	public void updateGUI( String name ){
		oldName = name;
		updateGUI();
	}

	/**Update the titles of the radio buttons
	 * 
	 */
	private void updateCals(){
		String formerName = currentView.getCakeCal().getSettings().getName();
		for( int x = 0; x < calendars.size(); x++ ){
			radioButtons.get(x).setText(calendars.get(x).getSettings().getName());
		}
		if( currentView.getCakeCal().getSettings().getFilename() != formerName ) {
			todaysEvents.setName(currentView.getCakeCal().getSettings().getFilename());
			todaysEvents.updateTodayEvent( currentView.getCakeCal().getEvents(forTray));
		}
	}

	/**Do we want to kill the process?
	 * 
	 * @return true if the user wants to exit the program, false otherwise
	 */
	public boolean dispose(){
		return dispose;
	}

	/**Get new data once a change has been made.
	 * 
	 */
	public void updateData() {
		//we want to update the current CakeGUI that we have open.
		currentView.updateData();

	}

	/**Opens the window after it has been minimized to the tray
	 * 
	 */
	public void openWindow(){
		mainWindow.toFront();
		this.windowDeiconified(new WindowEvent(mainWindow, 0));
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {		
	}

	public void componentResized(ComponentEvent arg0) {
		currentView.updateSize(mainWindow.getSize());		
	}

	public void componentShown(ComponentEvent arg0) {
		
	}


}