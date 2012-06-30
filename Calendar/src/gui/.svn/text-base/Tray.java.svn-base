package gui;



import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import calendar.Cake;
import subsystem.Event;


/**
 * This class will create an icon for the system tray as well as handle
 * any notifications for the calendar.
 * 
 */

public class Tray implements ActionListener {

	private SystemTray tray;
	private ArrayList<Event> today;
	private TrayIcon trayIcon;
	private String name;
	private Cake parent;

	/**
	 * Constructor to make a new Tray object, which lies in the system tray
	 * 
	 * @param today Today's events
	 * @param name The name of the calendar which is open
	 * @param c The parent Cake object
	 */	
	public Tray( ArrayList<Event> today, String name,Cake c ) {
		this.name = name;
		this.today = today;
		this.parent = c;
		this.initialize();

	}

	/**
	 * intializes the System Tray.
	 */
	private void initialize() {
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		} else if( tray != SystemTray.getSystemTray() ) {


			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem(Cake.EXIT);
			defaultItem.addActionListener(this);
			MenuItem openCakeItem = new MenuItem("Open Cake");
			openCakeItem.addActionListener(this);
			popup.add(defaultItem);
			popup.add(openCakeItem);

			tray = SystemTray.getSystemTray();

			Image img = Toolkit.getDefaultToolkit().getImage("doc/images/cake.jpg");
			trayIcon = new TrayIcon(img, "Calendar", popup);
			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.out.println("TrayIcon could not be added.");
				return;
			}
			this.updateTodayEvent(today);
		}
	}

	/**
	 * updates today's events
	 * 
	 * @param today
	 */
	public void updateTodayEvent( ArrayList<Event> today ) {
		this.today = today;

		if( today == null || today.size() == 0 ) {
			trayIcon.setToolTip( "No events have been scheduled for today");
		} else {
			int cnt = today.size();
			String singleorplural;
			if( today.size() == 1) {
				singleorplural = "event";
			} else {
				singleorplural = "events";
			}
			trayIcon.displayMessage( "Events", "You have " + cnt + " " + singleorplural + " scheduled for today \n" 
					+ this.eventToString(), TrayIcon.MessageType.INFO);


			//Period p = today.get(0).getPeriod();
			//System.out.println( p.start.time.hour );

			trayIcon.setToolTip( "You have " + cnt + " " + singleorplural);
		}

	}

	/**
	 * creates the format of the notification window.
	 * 
	 * @return
	 */
	public String eventToString() {
		String altogether = "";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat( "HH");
		int systemHour = Integer.parseInt( sdf.format(cal.getTime()));
		boolean enterString = false;
		int eventNum = 0;
		Event temp = null;

		if( today != null ) {
			for( int i = 0; i < today.size(); i++ ) {
				if(today.get(i).getPeriod().start.time.hour > systemHour) {
					temp = today.get(i);
					eventNum = i+1;
					i = today.size();
					enterString = true;
				}
			}
		}

		if( enterString == true ) {
			altogether = "Your " + "event #" + eventNum + " in Calendar " + name + " is \n"+
			"Event: " + temp.getTitle() + "\n"+
			"Time: " + temp.getPeriod().start.time.toString() + "\n"+
			"Location: " + temp.getLocation() + "\n"+
			"Description " + temp.getDescription() + "\n";
		} else {
			altogether = "All your events are already done.";
		}
		return altogether;
	}

	/**
	 * handles options in the pop-up menu.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Open Cake") {
			try {
				parent.openWindow();
			} catch (Exception m) {
				System.out.println(m.getMessage());
			}
		}else if(e.getActionCommand() == Cake.EXIT){
			parent.actionPerformed(e);
		}
	}

	/**
	 * tells the notification of the window the name of the calendar.
	 * 
	 * @param newName
	 */

	public void setName( String newName ) {
		name = newName;
	}
}
