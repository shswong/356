package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import observer.Observer;
import subsystem.CakeCal;
import subsystem.Cakeday;
import subsystem.SimpleDate;

@SuppressWarnings("serial")
public class MiniMonth extends JPanel implements Observer,MouseListener{

	private CakeGUI parent;
	private JLabel miniDays[];
	int DayToErase = -1;
	int curMonths[]; 
	private JTextField yearField;

	/**Construct a new MiniMonth.  This is the month that is in the bottom right corner of
	 * the program
	 * 
	 * @param c The CakeGUI to use as this MiniMonth's parent
	 */
	public MiniMonth(CakeGUI c){

		parent = c;
		curMonths = CakeCal.getMonths(parent.getCurrentYear());
		miniDays = new JLabel[42];

		JPanel days = new JPanel();
		days.setLayout(new GridLayout(7,7));

		String weekDay[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};		
		JLabel label = new JLabel();		
		for (int i=0; i<7; i++) {
			//add the labels to the days
			label = new JLabel(weekDay[i]);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(new Font("Verdana", Font.BOLD, 12));
			days.add(label);
		}

		for (int j=0; j<42; j++) {
			//initialize the JLabels
			miniDays[j] = new JLabel(j-parent.currentMonthOffset + 1 + "");
			//make the border
			Border line = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder title = BorderFactory.createTitledBorder(line);
			miniDays[j].setBorder(title);
			miniDays[j].addMouseListener(this);
			days.add(miniDays[j]);
		}
		setLayout(new BorderLayout());

		//initialize the top part of the mini month(which contains the year box 
		//and the buttons for switching the year/month)
		JPanel bottomRightTop = new JPanel();
		bottomRightTop.setLayout( new BorderLayout());

		JButton monthPrevious = new JButton( "<" );
		monthPrevious.addActionListener(c);
		JButton monthNext = new JButton( ">" );
		monthNext.addActionListener(c);
		JButton yearPrevious = new JButton( "<<" );
		yearPrevious.addActionListener(c);
		JButton yearNext = new JButton( ">>" );
		yearNext.addActionListener(c);

		JPanel leftButtons = new JPanel();
		leftButtons.setLayout(new GridLayout(1,2));
		leftButtons.add(yearPrevious);
		leftButtons.add(monthPrevious);
		bottomRightTop.add(leftButtons, BorderLayout.WEST);

		JPanel rightButtons = new JPanel();
		rightButtons.setLayout(new GridLayout(1,2)); 
		rightButtons.add(monthNext);
		rightButtons.add(yearNext);
		bottomRightTop.add(rightButtons, BorderLayout.EAST);

		yearField = new JTextField();
		yearField.setFont(new Font("Verdana", Font.BOLD, 12));
		yearField.setHorizontalAlignment(JTextField.CENTER);
		yearField.setDocument(new JTextFieldLimit(4));
		yearField.setText(c.getCurrentYear() + "");
		yearField.setActionCommand("YYYY");
		yearField.addActionListener(c);	

		bottomRightTop.add(yearField, BorderLayout.CENTER);

		//------------TITLED BORDER-------------
		String shortDate = " " + curMonths[c.getCurrentMonth() - 1] + ", " + c.getCurrentYear() + " ";

		Border line = BorderFactory.createLineBorder(Color.BLACK);
		TitledBorder title = BorderFactory.createTitledBorder(line, shortDate);
		title.setTitleJustification(TitledBorder.RIGHT);
		title.setTitleFont(new Font("Verdana", Font.BOLD, 12));

		setBorder(title);
		//------------END OF---TITLED BORDER-------------

		add(bottomRightTop, BorderLayout.NORTH);
		add(days, BorderLayout.CENTER);

	}
	/**
	 *	This updates the mini month with the current data.  Will also highlight the current day
	 * in blue(if applicable)
	 * 
	 * @param s The day that is selected.
	 */
	private void highlightToday(int s) {

		int currentMonthOffset = parent.getCurrentMonthOffset();
		int currentDay = parent.getCurrentDay();
		int currentMonth = parent.getCurrentMonth();
		int currentYear = parent.getCurrentYear();

		// Creates border on the selected day
		for (int i=0; i<42; i++) {
			float[] n = new float[3];
			Color.RGBtoHSB(212, 208, 200, n);
			miniDays[i].setOpaque(true);
			miniDays[i].setForeground(Color.BLACK);
			miniDays[i].setBackground(Color.WHITE);
			miniDays[i].setFont(null);

			Border line = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder title = BorderFactory.createTitledBorder(line);
			miniDays[i].setBorder(title);
			miniDays[i].setName(i + "" );//just set the name to a number.  See MouseClicked.
			miniDays[i].setText(i - parent.currentMonthOffset + 1 + "");
			if( parent.getMonthView().hasEvents( new SimpleDate(parent.getCurrentMonth(), i,parent.getCurrentYear() )) == true) {
				miniDays[i].setToolTipText( "You have events scheduled here.");
			} else {
				miniDays[i].setToolTipText( "No Events have been scheduled.");
			}
		}

		//Creates the font for the selected day
		miniDays[(currentDay-1) + currentMonthOffset].setForeground(Color.black);
		miniDays[(s-1) + currentMonthOffset].setForeground(Color.BLACK);
		miniDays[(s-1) + currentMonthOffset].setFont(new Font("Verdana", Font.BOLD, 12));
		miniDays[(s-1) + currentMonthOffset].setOpaque(true);

		//Set the border of the selected date in the mini calendar
		Border line = BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK);
		TitledBorder title = BorderFactory.createTitledBorder(line);
		miniDays[(s-1) + currentMonthOffset].setBorder(title);

		//miniDays[(s-1) + currentMonthOffset].setBackground(Color.WHITE);

		currentDay = s;
		if( currentMonth == parent.TODAYMONTH && currentYear == parent.TODAYYEAR ) {
			// Highlights the current day in blue when the current month and year is selected
			miniDays[(parent.TODAYDAY-1) + currentMonthOffset].setForeground(Color.BLUE);
			miniDays[(parent.TODAYDAY-1) + currentMonthOffset].setFont(new Font("Verdana", Font.BOLD, 12));
			DayToErase = (parent.TODAYDAY-1) + currentMonthOffset;
		} else if(DayToErase > 0){
			// changes the current day back to black when the current month and year are not highlighted.
			miniDays[DayToErase].setForeground(Color.black);
			DayToErase = -1;
		}


		//This var holds the number of dates of the previous month
		int previousMonthDates = curMonths[ ((currentMonth - 2)+12) % 12];

		for (int k=currentMonthOffset - 1; k >= 0; k--) {
			//set dates from the last month to dark.
			miniDays[k].setText(previousMonthDates + "");
			miniDays[k].setForeground(Color.GRAY);
			miniDays[k].setBackground(Color.LIGHT_GRAY);
			previousMonthDates--;
		}	

		int count = 1;
		for (int k=(curMonths[currentMonth - 1] + currentMonthOffset); k < 42; k++) {
			//set the days after this month to be dark
			miniDays[k].setText(count + "");
			miniDays[k].setForeground(Color.GRAY);
			miniDays[k].setBackground(Color.LIGHT_GRAY);
			count++;
		}		

	}
	
	public void setYearField(String str){
		yearField.setText(str);
	}
	
	public String getYearField(){
		return yearField.getText();
	}

	public void updateData() {
		highlightToday(parent.getCurrentDay());
		
		//update the border around the mini month
		String shortDate = " " + Cakeday.MONTHS[parent.getCurrentMonth() - 1] + ", " + parent.getCurrentYear() + " ";

		Border line = BorderFactory.createLineBorder(Color.BLACK);
		TitledBorder title = BorderFactory.createTitledBorder(line, shortDate);
		title.setTitleJustification(TitledBorder.RIGHT);
		title.setTitleFont(new Font("Verdana", Font.BOLD, 12));

		setBorder(title);
	}

	public void mouseClicked(MouseEvent arg0) {
		int i = Integer.parseInt(arg0.getComponent().getName());
		i -= parent.currentMonthOffset;
		i++;
		//all of the days are numbered from 0 to 42.  we parse this to an int,
		//subtract the offset and add 1(since the values are from 0 to 42, months start at 1)		
		arg0.getComponent().setName(i+"");		
		parent.mouseClicked(arg0);

	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

}
