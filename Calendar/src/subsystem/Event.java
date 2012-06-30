package subsystem;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Describes an Event. Each event will have a unique ID, which will allow for fast searching
 * and deleting of events. UID's should never be modified by anything, and they should only
 * be created by the EventDatabase.
 *
 */

public class Event 
{
	private String title;
	private String description;
	private String location;
	private Period period;
	private int category = 0;
	private int UID;
	private ArrayList<Integer> UIDS = new ArrayList<Integer>();
	private ArrayList<SimpleDate> recurring = new ArrayList<SimpleDate>();
	private String recurringType;   //A string representing how the recurring type is stored, etc.  

	/**
	 * Private inner classed used to parse recurring expressions.
	 * 
	 */
	private class Operand 
	{
		public boolean reverse; //are we selecting reverse, for example the 2nd to last day
		public boolean groupBy;
		public int n; //how much to select
		public char modifier;  //'d' for day, 'm' for month, 'y' for year(not currently used)


		/**
		 * A-type constructor
		 */
		/*public Operand(int n) {
			this.n = n;
			this.modifier = '0';
			this.reverse = false;
		}*/

		/**
		 * B-type constructor
		 */
		public Operand(int n, char modifier, boolean reverse) {
			this.n = n;
			this.modifier = modifier;
			this.reverse = reverse;
		}
	}

	/**
	 * Defualt constructor.
	 *
	 */
	public Event() 
	{
		setUID(0);
		title = "";
		description = "";
		location = "";
		setPeriod(new Period());
		recurringType = "0";
	}

	/**
	 * Overloaded constructor; accepts data for the event
	 * 
	 * @param period time frame in which this event occurs
	 * @param title name of the event
	 * @param description a description of the event
	 */
	public Event(Period period, String title, String description, String newRecurringType) {
		this.period = period;
		this.title = title;
		this.description = description;
		this.location = "";
		recurringType = newRecurringType;
		parseRecurring(recurringType);

		setUID(0);
	}

	/**
	 * Overloaded constructor; accepts data for the event
	 * 
	 * @param period time frame in which this event occurs
	 * @param title name of the event
	 * @param description a description of the event
	 */
	public Event(Period period, String title, String description, String newRecurringType, String location) {
		this.period = period;
		this.title = title;
		this.description = description;
		this.location = location;
		recurringType = newRecurringType;
		//recurring.add(period.start.date);
		parseRecurring(recurringType);
		setUID(0);
	}

	public Event( Event event ) {

		this.setPeriod(new Period( event.getPeriod() ));
		title = new String( event.getTitle() );
		description = new String( event.getDescription());
		location = new String( event.getLocation() );
		recurringType = new String( event.getRecurringType() );
		recurring = new ArrayList<SimpleDate>(event.getRecurring());
		this.setUID(event.getUID());
	}

	public boolean isValid() {

		return getPeriod().isValid() && getTitle().length() >= 1;

	}

	/**
	 * returns an XML representation of the event;
	 * 
	 * @return xml xml representation of the event
	 */

	public String toXML() 
	{
		if(getTitle().equals("") || getTitle() == null){
			return "";
		}
		String xml =
			"<event>" + CakeCal.NL + CakeCal.TAB +
			"<title>" + getTitle() + "</title>" + CakeCal.NL + CakeCal.TAB +
			"<period>" + getPeriod().format() + "</period>" + CakeCal.NL + CakeCal.TAB +
			"<recu>" + getRecurringType() +"</recu>" + CakeCal.NL + CakeCal.TAB +
			"<desc>" + getDescription() + "</desc>" + CakeCal.NL + CakeCal.TAB +
			"<location>" + getLocation() + "</location>" + CakeCal.NL +
			"</event>" + CakeCal.NL;
		return xml;
	}

	/**
	 * returns the title of the event
	 * 
	 * @return title name of the event
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * returns the description of the event.
	 * 
	 * @return description the description of the event.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * return a String representation of the event.
	 * 
	 * @return temp String representation of the event.
	 */
	public String toString() {
		if(getTitle().equals("") || getTitle() == null){
			return "Blank Event";
		}
		String temp = "Title: " + getTitle() + "\nDescription: " + getDescription() + "\nPeriod: " + getPeriod().format();
		return temp;
	}

	/**
	 * Take the recurring type that you get from reading in a file, and parse this into the
	 * recurring ArrayList. Uses infix/postfix to do this.
	 * 
	 * @param recurType The recurring type string that you retrieve from the XML file.
	 */
	private void parseRecurring( String recurType ){
		ArrayList<Period> masterList = new ArrayList<Period>();
		ArrayList<Period> slaveList = new ArrayList<Period>();
		slaveList.add(this.period);
		recurringType = recurType;

		if( recurType.equals(null) || recurType.equals("")){
			recurringType = "0";
			//if the recurType is null, this string does not repeat
			//so you can go along and do whatever you were doing before
			recurring.add(period.start.date);
			return;

		} else if( recurType.charAt(0) == '0' ) {
			recurringType = "0";
			//if the first character is zero, this string does not repeat
			//so you can go along and do whatever you were doing before
			recurring.add(period.start.date);
			return;

		}

		Stack<Character> operators = new Stack<Character>(); //stack of operators (square and round brackets)
		Stack<Operand> operandsA = new Stack<Operand>();  //'A' operands are the things on the left within []
		Stack<Operand> operandsB = new Stack<Operand>(); //'B' operands are inside parentheses
		//thus, in the expression [-7(*1)[-30m]]: -30m is an 'A' operand, *1 is a 'B' operand, and -7 
		//is an 'A' operand
		Stack<Integer> cases = new Stack<Integer>();

		//convert this to a char array because it's a little easier to do things
		char[] theString = recurType.toCharArray();


		for( int x = 0; x < theString.length; x++) {
			if (theString[x] == '[') {
				operators.push('[');
				cases.push(1);
			} else if (theString[x] == '(') {
				operators.push('(');
				cases.push(cases.pop()+1);
			} else if (theString[x] == ')') {
				if (operators.peek() != '(') {
					throw new CREXLException("Malformed parenthesis!");
				}
				operators.pop();
			} else if (theString[x] == ']') {
				if (operators.peek() != '[') {
					throw new CREXLException("Malformed square braces!");
				}

				operators.pop();
				int c = cases.pop();

				if (c == 1) {
					if (operandsA.size() == 0) {
						throw new CREXLException("Missing operands!");
					}

					Operand a = operandsA.pop();

					if (a.n < 0) {
						slaveList = specialSlice(slaveList, a);
					} else if (a.n > 0) {
						slaveList = normalSlice(slaveList, a);
					} else if (a.n == 0) {
						throw new CREXLException("Zeros not allowed in recurring event expressions!");
					}
				} else if (c == 2) {
					if (operandsA.size() == 0 || operandsB.size() == 0) {
						throw new CREXLException("Missing operands!");
					}

					Operand a = operandsA.pop();
					Operand b = operandsB.pop();

					if((b.groupBy || b.reverse) && slaveList.size() > 1){
						//special case - we have already split up the period once.
						//example: [-7(*1)[-30m]]
						//this should select the last saturday in every month; if we are in
						//this case the [-30m] has already been processed and we have months.
						//now, we just want to select the last saturday in each of these periods.
						ArrayList<Period> slaveCopy = new ArrayList<Period>();
						slaveCopy.addAll(slaveList);
						slaveList.clear();
						ArrayList<Period> tempList = new ArrayList<Period>();
						for( Period aPeriod : slaveCopy ){
							ArrayList<Period> temp2 = new ArrayList<Period>();
							temp2.add(aPeriod);
							if (a.n < 0) {
								tempList = specialSlice(temp2, a);
							} else if (a.n > 0) {
								tempList = normalSlice(temp2, a);
							} else if (a.n == 0) {
								throw new CREXLException("Zeros not allowed in recurring event expressions!");
							}

							if (b.n < 0) {
								throw new CREXLException("Special selectors not allowed");
							} else if (b.n > 0) {
								tempList = normalSelect(tempList, b);
							} else if (b.n == 0) {
								throw new CREXLException("Zeroes not allowed in recurring event expressions!");
							}
							slaveList.addAll(tempList);
							tempList.clear();
						}
						
					}else{

						if (a.n < 0) {
							slaveList = specialSlice(slaveList, a);
						} else if (a.n > 0) {
							slaveList = normalSlice(slaveList, a);
						} else if (a.n == 0) {
							throw new CREXLException("Zeros not allowed in recurring event expressions!");
						}

						if (b.n < 0) {
							throw new CREXLException("Special selectors not allowed");
						} else if (b.n > 0) {
							slaveList = normalSelect(slaveList, b);
						} else if (b.n == 0) {
							throw new CREXLException("Zeroes not allowed in recurring event expressions!");
						}
					}
				} else {
					throw new CREXLException("Malformed selectors! (only one is allowed within each set of square braces)");
				}
			} else {
				int m = 1;
				char modifier = '0';
				boolean reverse = false;
				boolean groupby = false;

				if (theString[x] == '*') {
					reverse = true;
					x++;
				}

				if (theString[x] == '-') {
					x++;
					m = -1;
				}
				
				if (theString[x] == '#') {
					//System.out.println("Adding 'Group By'");
					groupby = true;
					x++;
				}

				String temp = "";
				int k = x;
				while(Character.isDigit(theString[k])) {
					temp += theString[k++];

				}
				x = k-1;



				if (theString[x+1] == 'd' || theString[x+1] == 'm' || theString[x+1] == 'y') {
					modifier = theString[(x+1)];
					x++;
				}

				Operand op;
				if (cases.peek() == 1) {
					op = new Operand(Integer.parseInt(temp)*m, modifier, reverse);
					op.groupBy = groupby;
					operandsA.push(op);
				}
				else if (cases.peek() == 2) {
					op = new Operand(Integer.parseInt(temp)*m, modifier, reverse);
					op.groupBy = groupby;
					operandsB.push(op);
				}
				else throw new CREXLException("Malformed selectors! (only one is allowed within each set of square braces)");

			}

			if (operators.size() == 0) {
				masterList.addAll(slaveList);
				slaveList.clear();
				slaveList.add(this.period);
			} else if (x == theString.length - 1) {
				throw new CREXLException("Unterminated square braces!");
			}
		}
		for (int i = 0; i < masterList.size(); i++) {
			if(period.lieInside(masterList.get(i))){
				//only add it if it falls in between the period.
				recurring.add(new SimpleDate(masterList.get(i).start.date));
			}
		}

	}

	/**
	 * Slices each period passed in into the periods of the days in them, and returns a selection 
	 * of every nth period.
	 * 
	 * @param d list of periods to break up
	 * @param a The 'A' operand to act with
	 * @return List of every nth day
	 */
	private ArrayList<Period> normalSlice(ArrayList<Period> d, Operand a) 
	{
		ArrayList<Period> p = new ArrayList<Period>();
		ArrayList<Period> temp = new ArrayList<Period>();

		for (int i = 0; i < d.size(); i++) {
			temp = Period.splitIntoDays(d.get(i));
			if (a.modifier == '0') {
				for (int j = 0; j < temp.size(); j++) {
					if ((j % a.n) == 0) {
						p.add(new Period(temp.get(j)));
					}
				}
			} else {
				switch (a.modifier) {
				case 'd':
					for (Period s : temp) 
						if (s.start.date.day == a.n) p.add(s);
					break;
				case 'm':
					for (Period s : temp) 
						if (s.start.date.month == a.n) p.add(s);
					break;
				case 'y':
					for (Period s : temp)
						if (s.start.date.year == a.n) p.add(s);
					break;
				}
			}
			temp.clear();
		}

		return p;
	}

	/**
	 * Selects every nth period from the list of periods passed in.
	 * 
	 * @param d list of periods to look at
	 * @param b The 'B' operand to act with
	 * @return list of nth periods
	 */
	private ArrayList<Period> normalSelect(ArrayList<Period> d, Operand b) 
	{
		ArrayList<Period> p = new ArrayList<Period>();

		if (b.modifier == '0') {
			if (b.reverse) {
				//select the nth from the end					
				p.add(d.get(d.size()-b.n));

			} else if(b.groupBy){
				//select the nth
				if( !((b.n -1 ) >= d.size())){
					//only add it if it exists.  we don't want to walk off of the end of our array
					p.add(d.get(b.n - 1));
				}
			} else {
			
				//select every nth
				for (int i = 1; i <= d.size(); i++) {
					if (0 == (i%b.n)) {
						p.add(new Period(d.get(i-1)));
					}
				}
			}
		} else {
			switch (b.modifier) {
			case 'd':
				for (Period s : d) 
					if (s.start.date.day == b.n) p.add(s);
				break;
			case 'm':
				for (Period s : d) 
					if (s.start.date.month == b.n) p.add(s);
				break;
			case 'y':
				for (Period s : d)
					if (s.start.date.year == b.n) p.add(s);
				break;
			}
		}

		return p;
	}

	/**
	 * Slices up  each period passed in based upon the special number passed in:
	 * 
	 * -1 - -7: Sunday - Saturday
	 * -30: monthly
	 * -8: weekly
	 * 
	 * @param d The list of periods to break up
	 * @param a The 'A' operand to work with
	 * @return The list of periods broken up according to the 'A' operand
	 */
	private ArrayList<Period> specialSlice(ArrayList<Period> d, Operand a) {
		ArrayList<Period> p = new ArrayList<Period>();
		int m = (a.n+1)*-1;//the day of the week to select, 0-6

		if (a.n <= -1 && a.n >= -7) {
			ArrayList<Period> temp = new ArrayList<Period>();
			for (int i = 0; i < d.size(); i++) {
				temp.addAll(Period.splitIntoDays(d.get(i)));
			}
			for (int i = 0; i < temp.size(); i++) {
				if (Cakeday.getDayOfWeek(temp.get(i).start.date) == m) 
					p.add(temp.get(i));
			}
		}else if (a.n == -30){
			//group by month
			Period overflow = new Period(d.get(0));
			overflow.start.date.day = 1;
			overflow.end.date.day = Cakeday.getMonths(overflow.end.date.year)[overflow.end.date.month-1];
			d.remove(0);
			d.add(0,overflow);
			for( Period d2 : d){
				p.addAll(Period.splitIntoMonths(d2));
			}
		}else if(a.n == -365){
			//group by year
			for( Period d2 : d){
				p.addAll(Period.splitIntoYears(d2));
			}
		}

		return p;
	}

	/**Checks to see if two events have equal periods.
	 * 
	 * @param e The event to compare this event to.
	 * @return true if the periods are equal, false otherwise
	 */
	public boolean equals(Event e){
		//YES THIS CODE IS UGLY BUT IT WORKS
		if( this.UID == e.getUID() ){
			if( e.period.start.date.year != this.period.start.date.year ){
				return false;
			}
			if( e.period.start.date.month != this.period.start.date.month ){
				return false;
			}
			if( e.period.start.date.day != this.period.start.date.day ){
				return false;
			}


			if( e.period.end.date.year != this.period.end.date.year ){
				return false;
			}
			if( e.period.end.date.month != this.period.end.date.month ){
				return false;
			}
			if( e.period.end.date.day != this.period.end.date.day ){
				return false;
			}


			return true;
		}
		else{
			return false;
		}
	}

	//-- Accessors
	public String getLocation() {
		return location;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Period getPeriod() {
		return period;
	}

	public void setUID(int uID) {
		UID = uID;
	}

	public int getUID() {
		return UID;
	}

	public ArrayList<SimpleDate> getRecurring() {
		return recurring;
	}

	public String getRecurringType() {
		return recurringType;
	}

	public ArrayList<Integer> getUIDS() {
		return UIDS;
	}

} //-- Event