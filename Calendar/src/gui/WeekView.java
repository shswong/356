package gui;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import observer.Observer;

import subsystem.CakeCal;
import subsystem.Cakeday;
import subsystem.Period;
import subsystem.SimpleDate;

/**View a week in the calendar.  Consists of 7 day views put together.
 * 		
 */
public class WeekView implements ActionListener, Observer {

	private CakeGUI parent;
	private JPanel panel;
	private JPanel bigPanel;
	private Period startDay;
	private ArrayList<DayCanvas> days;
	private JLabel dateText;

	/**Constructor
	 * 
	 * @param parent the CakeGUI which is this week view's parent.
	 */
	public WeekView(CakeGUI parent){
		panel = new JPanel( new BorderLayout());
		SimpleDate d = new SimpleDate(parent.getCurrentMonth(), parent.getCurrentDay(), parent.getCurrentYear());
		startDay = Period.parse(d.year + "." + d.month + "." +
				d.day + ":00.00-" + d.year + "." +
				d.month + "." + d.day + ":24.00");
		while(CakeCal.getDayOfWeek(startDay.start.date) != 0){	
			startDay.start.date.day--;
			startDay.end.date.day--;
		}

		this.dateText = new JLabel("" + parent.curMonths[parent.getCurrentMonth()-1] + " " +
				parent.getCurrentDay() + ", " + parent.getCurrentYear());

		days = new ArrayList<DayCanvas>();

		//the week panel which goes in the center
		JPanel weekPanel = new JPanel(new GridLayout(1,7));
		for( int x = 0; x < 7; x++){
			days.add(new DayCanvas(x == 0, parent,startDay));
			weekPanel.add(days.get(x));
			//startDay.start.date.day++;
			//startDay.end.date.day++;
		}



		//the panel which has the name of the days 
		JPanel dayPanel = new JPanel( new GridLayout(1,7));

		JLabel label = new JLabel();

		for (int i=0; i<7; i++) {
			label = new JLabel(Cakeday.DAYS[i]);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(new Font("Verdana", Font.PLAIN, 12));
			dayPanel.add(label);
		}

		panel.add(weekPanel,BorderLayout.CENTER);
		panel.add(dayPanel, BorderLayout.NORTH);

		bigPanel = new JPanel(new BorderLayout());

		JPanel top = new JPanel();
		JButton prevWeek = new JButton("<");
		JButton nextWeek = new JButton(">");
		prevWeek.addActionListener(this);
		nextWeek.addActionListener(this);
		top.add(prevWeek);
		top.add(dateText);
		top.add(nextWeek);

		bigPanel.add(panel, BorderLayout.CENTER);
		bigPanel.add(top, BorderLayout.NORTH);

		this.parent = parent;

		updateWeek();
	}

	/**Get the main panel
	 * 
	 * @return The main panel of the week view
	 */
	JPanel getPanel() {
		return bigPanel;
	}

	/**Does something depending on the action.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("<")) {
			if (parent.getCurrentDay() <= 7 ) {
				if (parent.getCurrentMonth() > 1) {		
					//set the current day and month to be their new values
					parent.setCurrentDayMonth(parent.curMonths[parent.getCurrentMonth() - 2] - (7 - parent.getCurrentDay()),parent.getCurrentMonth() - 1);		
				} else {
					//if we are here, we may have to change the year
					parent.setCurrentDayMonth(parent.curMonths[11] - (7 - parent.getCurrentDay()),12);					
					if (parent.getCurrentDay() > 7) {
						parent.setCurrentYear(parent.getCurrentYear() - 1);
					}
				}
			} else {
				//nothing has to be changed, make the day 7 less than what we are currently at
				parent.setCurrentDay(parent.getCurrentDay() - 7);
			}
		} else if (e.getActionCommand().equals(">")) {
			int currentDay = parent.getCurrentDay() + 7;
			//try setting the current day to the next day that we have to go to.
			//then we perform checks on this day.
			if (currentDay > parent.curMonths[parent.getCurrentMonth() - 1]) {
				//if the day is greater then the max days in the month
				parent.setCurrentDay((currentDay - parent.curMonths[parent.getCurrentMonth() - 1]));					
				if (parent.getCurrentMonth() <= 11) {
					parent.setCurrentMonth(parent.getCurrentMonth() + 1);
				} else {
					parent.setCurrentMonth(1);
					parent.setCurrentYear(parent.getCurrentYear() + 1);
				}
			}else{
				//if we don't have to change anything, just set the current day to the
				//next day that we want to go to.
				parent.setCurrentDay(currentDay);
			}
		}
	}


	/**Update the week based on the current period.
	 * 
	 */
	private void updateWeek(){
		//first get a simple date of the current date.
		SimpleDate d = new SimpleDate(parent.getCurrentMonth(), parent.getCurrentDay(), parent.getCurrentYear());
		//convert it into a period
		startDay = Period.parse(d.year + "." + d.month + "." +
				d.day + ":00.00-" + d.year + "." +
				d.month + "." + d.day + ":24.00");

		//get the days of the months
		int[] months = CakeCal.getMonths(parent.getCurrentYear());

		while(CakeCal.getDayOfWeek(startDay.start.date) != 0){
			//we want the week to start on a sunday, so make the date lower until it is
			startDay.start.date.day--;
			startDay.end.date.day--;
			if( startDay.start.date.day == 0 ){
				//if we get to a 0 date before we get to sunday, this means that
				//the month must end in this week.  lower the month and set the 
				//current day to the last of the previous month.
				if( startDay.start.date.month != 1){
					startDay.start.date.month--;
					startDay.end.date.month--;
				}else{
					startDay.start.date.month = 12;
					startDay.end.date.month = 12;
					startDay.start.date.year = parent.getCurrentYear() - 1;
					startDay.end.date.year = parent.getCurrentYear() - 1;
					months = CakeCal.getMonths(parent.getCurrentYear() -1);
				}
				//set the day to the last of the month we just got to
				startDay.start.date.day = months[startDay.start.date.month - 1];
				startDay.end.date.day = months[startDay.start.date.month - 1];
			}
		}
		
		//alright so this line of code might be somewhat confusing.  basically,
		//without this when are on a week that has december and january from two
		//different years we will jump forwards/backwards in time when we click on 
		//a day in the week of the month that we are NOT in.(so if it is thurs jan 1
		//and we click on wednesday dec 31, this will ensure that the week stays the same
		//and that we don't get events from the next year/previous year that occur
		//within the time period of the current week(dec28-jan3) showing up)
		//Trust me, this is needed.  If you(I?) can read this later, good luck.
		int tempYear = parent.getCurrentYear();
		if(parent.getCurrentMonth() != 1){
			tempYear++;
		}
		
		
		//start out the string of the week
		int index = startDay.start.date.month - 1; 
		//for some reason java doesn't like the arithmetic
		//inside of the brackets.
		String weekDate = Cakeday.MONTHS[index] + " " +
			startDay.start.date.day + " - ";
		for( int x = 0; x < days.size(); x++){
			if( startDay.start.date.day > months[index]){
				//check to see if we go into the next month.  if we do, set the day to be
				//the first day of the next month and advance the month	
				if( startDay.start.date.month >= 12){
					startDay.start.date.month = 1;
					startDay.end.date.month = 1;
					//startDay.start.date.
					startDay.start.date.year = tempYear;
					startDay.end.date.year = tempYear;
				}else{
					startDay.start.date.month++;
					startDay.end.date.month++;
				}
				startDay.start.date.day = 1;
				startDay.end.date.day = 1;
			}

			//now initialize the day views to be their respective days
			DayCanvas day = days.get(x);
			day.setDay(new Period(startDay));
			day.render();

			startDay.start.date.day++;
			startDay.end.date.day++;
		}

		int endDay = startDay.end.date.day - 1;
		int endMonth = startDay.start.date.month - 1;
		weekDate = weekDate + Cakeday.MONTHS[endMonth] + " " + endDay;

		this.dateText.setText(weekDate);
	}

	/**Causes the day canvas that is associated with this week view to scroll, and all the other ones to scroll 
	 * at the same time.
	 * 
	 * @param e The MouseWheelEvent to parse
	 * @param dayOfSender The day which sent this command
	 */
	public void Scroll(MouseWheelEvent e, int dayOfSender) {
		for (int i = 0; i < 7; i++) {
			if (days.get(i).getDay().start.date.day != dayOfSender) days.get(i).zoomScroll(e);
		}
	}

	/**Update all the data that week view contains
	 * 
	 */
	public void updateData() {
		updateWeek();

	}

}
