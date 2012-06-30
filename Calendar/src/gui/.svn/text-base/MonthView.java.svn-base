package gui;


import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import observer.Observer;
import subsystem.CakeCal;
import subsystem.Cakeday;
import subsystem.Event;
import subsystem.SimpleDate;

/**
 *  Views months in the calendar.
 *  
 */

public class MonthView implements ActionListener, MouseListener, Observer {
	final static boolean shouldFill = true;					//default gridbaglayout constraints
	final static boolean shouldWeightX = true;
	final static boolean RIGHT_TO_LEFT = false;

	float[] selectColor = new float[3];

	final static String[] MONTHS = Cakeday.MONTHS;			//contains the name of months

	private JPanel monthPanel;											//panel to be populated with number of days
	private CakeGUI parent;					//labels which day it is within the month
	private JLabel yTitle;				//Year to be displayed on the Title
	private JLabel mTitle;				//Month to be displayed on the Title

	private JTextArea[] days = new JTextArea[42];			//values to be stored in the MonthView

	private int DayToErase = -1;
	private int curMonths[];             // create the days in the particular month
	
	private JButton buttons[];

	/**
	 * default constructor
	 * 
	 * @param		 parent			the instance of CakeGUI object to be copied.
	 */

	public MonthView( CakeGUI parent ) {
		this.parent = parent;
		Color.RGBtoHSB(103, 137, 163, selectColor);
		yTitle = new JLabel( parent.getCurrentYear() + "" );
		mTitle = new JLabel( MONTHS[parent.getCurrentMonth() - 1] );
		curMonths = CakeCal.getMonths(parent.getCurrentYear()); 
		monthPanel = parent.getMonthPanel();
	}

	/**
	 * Creates a Month view panel
	 * 
	 * @param 		pane				The JPanel for which the Month View will be created in.
	 * 
	 * @return A new JPanel filled with all the elements of the Month View.
	 */
	public JPanel addComponentsToPane(JPanel pane) {
		buttons = new JButton[4];

		pane.removeAll();

		if (RIGHT_TO_LEFT) {														//set default gridbaglayout constraint values
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		if (shouldFill) {
			//natural height, maximum width
			c.fill = GridBagConstraints.HORIZONTAL;
		}

		buttons[0] = new JButton("<<"); 									//creates previous buttons for month and year
		buttons[0].addActionListener(this);
		buttons[1] = new JButton( "<" );
		buttons[1].addActionListener(this);
		JPanel b1 = new JPanel( new GridLayout(2,1) );
		b1.add( buttons[1] );
		b1.add(buttons[0]);
		c.weightx = 0.5;
		c.ipadx = 81;
		c.ipady = 7;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		pane.add(b1, c);

		JPanel Title = new JPanel( new GridLayout( 2, 1));				//creates titles for month and year
		mTitle.setFont( new Font( "Verdana", Font.PLAIN, 66));
		mTitle.setHorizontalAlignment(JLabel.CENTER);
		Title.add( mTitle );
		yTitle.setHorizontalAlignment(JLabel.CENTER);
		Title.add( yTitle );
		yTitle.setFont( new Font( "Verdana", Font.PLAIN, 66));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		pane.add(Title, c);

		buttons[2] = new JButton(">>");							//creates next buttons for month and year
		buttons[2].addActionListener(this);
		buttons[3] = new JButton(">");
		buttons[3].addActionListener(this);
		JPanel b2 = new JPanel(new GridLayout(2,1) );
		b2.add(buttons[3]);
		b2.add(buttons[2]);
		c.fill = GridBagConstraints.VERTICAL;
		c.ipadx = 81;
		c.ipady = 7;
		c.weightx = 0.5;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		pane.add(b2, c);


		c.gridy = 1;
		c.gridx = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		JPanel week = new JPanel( new GridLayout(1,7) );

		JLabel label = new JLabel();

		for (int i=0; i<7; i++) {
			label = new JLabel(Cakeday.DAYS[i]);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(new Font("Verdana", Font.BOLD, 16));
			week.add(label);
		}

		pane.add(week, c);

		monthPanel = new JPanel( new GridLayout( 6, 7, 3, 3 ));
		for( int i = 0; i < 42; i++ ) {
			if(i >= parent.currentMonthOffset && i < curMonths[parent.getCurrentMonth() - 1] + parent.currentMonthOffset) {
				days[i] = new JTextArea(i + 1 - parent.currentMonthOffset + "" );
				days[i].setName(i + 1 - parent.currentMonthOffset + "");
				days[i].addMouseListener(this);


				Border line = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
				TitledBorder title = BorderFactory.createTitledBorder(line);
				days[i].setBorder(title);



			} else {
				days[i] = new JTextArea( " " );
				days[i].setName(" ");
				days[i].addMouseListener(this);

				Border line = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
				TitledBorder title = BorderFactory.createTitledBorder(line);
				days[i].setBorder(title);
			}

			days[i].setEditable(false);
			monthPanel.add( days[i] );
		}
		//updateSelectedDay(parent.getCurrentDay());

		//fills the rest of the gridbaglayout

		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;   
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER; 

		c.gridx = 0;
		c.gridwidth =0; 
		c.gridy =2;      
		pane.add(monthPanel, c);
		return pane;
	}

	/**
	 * create a Month view given a specific month and year.
	 * 
	 * @param 		month		The JPanel to be populated.	
	 * @param 		yr			the year to be displayed in the JPanel.
	 * @param		mon			The month to be displayed in the JPanel.		
	 */

	public void addComponentsToPane( JPanel month, int yr, int mon){
		parent.setCurrentYear(yr);
		parent.setCurrentMonth(mon);
		addComponentsToPane(month);
	}

	/**
	 * Changes the year in the title, and tells the month to update
	 * 
	 */
	private void updateYear() {
		yTitle.setText( parent.getCurrentYear() + "" );
		updateMonth();
	}

	/**
	 * Changes the month in the title, and tells the days to update.
	 * 
	 */
	private void updateMonth() {
		mTitle.setText( MONTHS[parent.getCurrentMonth()-1] );
		this.updateDays();
	}

	/**
	 * Changes the arrangement of days in the Month view.
	 * 
	 */
	private void updateDays() {
		curMonths = CakeCal.getMonths(parent.getCurrentYear());             // create the days in the particular month
		ArrayList<Event> curEvents = parent.getEvents();
		float[] n = new float[3];



		for( int i = 0; i < 42; i++ ) {	
			days[i].setBackground(Color.WHITE);
			days[i].setForeground(Color.BLACK);
			Color.RGBtoHSB(212, 208, 200, n);
			days[i].setOpaque(true);
			if(i >= parent.currentMonthOffset && i < curMonths[parent.getCurrentMonth()-1] + parent.currentMonthOffset) {
				//if this is a valid day, set the name and the text
				days[i].setText(i + 1 - parent.currentMonthOffset + "" );
				days[i].setName(i + 1 - parent.currentMonthOffset + "" );
				for( int j = 0; j < curEvents.size(); j++){
					//fill in the current day with the events for that day
					if( curEvents.get(j).getPeriod().start.date.day == i+1-parent.currentMonthOffset ){
						days[i].setText(days[i].getText() + "\n   " + curEvents.get(j).getTitle());
					}
				}


			} else {
				days[i].setText(" ");
				days[i].setName(" ");
				//set the name to a blank string so nothing will happen when we click it
			}

			days[i].setEditable(false); //all days are uneditable
			if (i == parent.getCurrentDay() + parent.currentMonthOffset - 1) {
				//i'm not sure what this statement does, i tried changing the color to no effect
				days[i].setBackground(Color.getHSBColor(selectColor[0], selectColor[1], selectColor[2]));
				days[i].setForeground(Color.WHITE);
			}



		}

		if(DayToErase > 0){
			//not sure what this if statement does either
			days[DayToErase].setForeground(Color.BLACK);
			DayToErase = -1;
		}

		//grays out days of the previous/next months
		int previousMonthDates = curMonths[ ((parent.getCurrentMonth() - 2)+12) % 12];

		for (int k=parent.currentMonthOffset - 1; k >= 0; k--) {
			days[k].setText(previousMonthDates + "");
			days[k].setForeground(Color.GRAY);
			days[k].setBackground( Color.LIGHT_GRAY);
			previousMonthDates--;
		}	


		int count = 1;
		for (int k=(curMonths[parent.getCurrentMonth() - 1] + parent.currentMonthOffset); k < 42; k++) {
			days[k].setText(count + "");
			days[k].setForeground(Color.GRAY);
			days[k].setBackground(Color.LIGHT_GRAY);
			count++;
		}	

		if( parent.getCurrentYear() == parent.TODAYYEAR && parent.getCurrentMonth() == parent.TODAYMONTH){
			//set today's day to be blue
			days[(parent.TODAYDAY-1) + parent.currentMonthOffset].setForeground(Color.blue);
		} 

		parent.setMonthPanel(monthPanel);
	}

	/**
	 * Forwards this request to CakeGUI; since we do nothing special here we can just forward the request.
	 * 
	 * @param e The button that is pressed.
	 */

	public void actionPerformed( ActionEvent e ){
		parent.actionPerformed(e);
	}

	/**
	 * changes directly from month view to day view.
	 * 
	 * @param e The MouseEvent that detects either a single click or
	 * 			a Double Click.
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && !e.getComponent().getName().trim().equals("")) {
			int s = Integer.parseInt(e.getComponent().getName());
			parent.setCurrentDay(s);
			parent.viewChanger.show(parent.center, CakeGUI.DAY);
		}
		else if (!e.getComponent().getName().trim().equals("")) {
			int s = Integer.parseInt(e.getComponent().getName());
			parent.setCurrentDay(s);
		}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	/**
	 * Detects whether there is an event on the Month View.
	 * 
	 * @param date The date to be checked.
	 * @return i true if the day has events, false otherwise.
	 */
	public boolean hasEvents( SimpleDate date ) {
		boolean i = false;
		if( days[date.day].getText().length() > 2 ){
			//obviously, if the text has more than two characters(I.E. more data in the field
			//than just the date), there has to be an event.  
			i = true;
		}else{
			i = false;
		}

		return i;
	}

	/**Update the data for the month view.
	 * 
	 */
	public void updateData() {	
		this.updateYear();
	}
	
	/**Update the size of the font in the MonthView.  Also changes the sizes of the "<" and "<<"
	 * buttons
	 * 
	 * @param windowHeight The height of the window.
	 */
	public void updateFontSize(Dimension windowHeight){
		int buttonWidth = windowHeight.width / 16;
		int fontHeight = windowHeight.height / 16;
		mTitle.setFont( new Font( "Verdana", Font.PLAIN, fontHeight));
		yTitle.setFont( new Font( "Verdana", Font.PLAIN, fontHeight));
		
		//update the size of the buttons to also be 1/16 of the maximum height
		//so that they are not ridiculously large and take up a lot of screen real estate
		for( int x = 0; x < buttons.length; x++){
			buttons[x].setSize(buttonWidth, fontHeight);
		}
	}

}
