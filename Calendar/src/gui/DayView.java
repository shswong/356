package gui;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import observer.Observer;

import subsystem.Cakeday;
import subsystem.Period;


/**View a day in the calendar.  Basically serves as a wrapper class for an instance of DayCanvas.
 * 
 *
 */
public class DayView implements ActionListener, Observer {
	
	private JPanel pane;
	private CakeGUI parent;
	private DayCanvas day;
	private JLabel dateText;

	
	/**Constructor for the day view
	 * 
	 */
	public DayView(boolean showTime, CakeGUI parent){
		this.parent = parent;
		this.dateText = new JLabel(Cakeday.MONTHS[parent.getCurrentMonth()-1] + " " +
				parent.getCurrentDay() + ", " + parent.getCurrentYear());

		pane = new JPanel(new BorderLayout());

		day = new DayCanvas(showTime, this.parent);
		
		JPanel top = new JPanel();
		JButton prevDay = new JButton("<");
		JLabel currentDay = dateText;
		JButton nextDay = new JButton(">");
		prevDay.addActionListener(this);
		nextDay.addActionListener(this);
		top.add(prevDay);
		top.add(currentDay);
		top.add(nextDay);
		
		pane.add(top, BorderLayout.NORTH);
		pane.add(day, BorderLayout.CENTER);
	}
	
	public DayView(boolean showTime, CakeGUI parent, Period today){
		this.parent = parent;
		pane = new JPanel(new GridLayout(1,1));
		
		day = new DayCanvas(showTime, this.parent, today);
		pane.add(day);
	}
	    
    /**Get the JPanel
     * 
     * @return The JPanel for this DayView
     */
    public JPanel getPane(){
    	return pane;
    }

    /**Do something depending on the ActionEvent.  Handles the buttons to go to the next/previous day.
     * 
     * @param e The ActionEvent to do something with.
     */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == ">") {
			if( parent.getCurrentDay() == parent.curMonths[parent.getCurrentMonth()-1] ){
				//if we are at the last day of the month, go to the next month
				parent.setCurrentMonth(parent.getCurrentMonth() + 1);
				parent.setCurrentDay(1);
			}else{
				//else let's just go forward one day
				parent.setCurrentDay(parent.getCurrentDay()+1);
			}
			
		} else if (e.getActionCommand() == "<") {
			if( parent.getCurrentDay() == 1 ){
				//if we are at the first day of the month, go to the last day of the prev. month
				parent.setCurrentMonth(parent.getCurrentMonth() - 1);
				parent.setCurrentDay(parent.curMonths[parent.getCurrentMonth()-1]);
			}else{
				//else let's just go backwards one day
				parent.setCurrentDay(parent.getCurrentDay()-1);
			}
		}
				
		this.dateText.setText(Cakeday.MONTHS[parent.getCurrentMonth() - 1] + " " +
				parent.getCurrentDay() + ", " + parent.getCurrentYear());
		parent.switchTopRightCard("Add Event");
	}

	public void updateData() {
		this.dateText.setText(Cakeday.MONTHS[parent.getCurrentMonth() - 1] + " " +
				parent.getCurrentDay() + ", " + parent.getCurrentYear());
		Period parsedPd = Period.parse(parent.getCurrentYear() + "." + parent.getCurrentMonth() + "." +
				parent.getCurrentDay() + ":00.00-" + parent.getCurrentYear() + "." +
				parent.getCurrentMonth() + "." + parent.getCurrentDay() + ":24.00");
		day.setDay(parsedPd);
		day.render();
	}
	
	/**Make sure that no events are selected.
	 * 
	 */
	public void clear(){
		day.deselect();
	}
}
