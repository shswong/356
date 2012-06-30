package subsystem;

import java.util.ArrayList;

/**
 * Describes a time frame, or period, which is used throughout the program to 
 * complete tasks.  The format is YYYY.MM.DD:HH.MM-YYYY.MM.DD:HH.MM
 * 
 */

public class Period 
{
	public SimpleDateTime start, end;
	
	/**
	 * Constructor
	 */
	public Period() 
	{
		start = new SimpleDateTime();
		end=new SimpleDateTime();
	}
	
	/**
	 * Copy constructor
	 */
	public Period(Period p)
	{
		this.start = new SimpleDateTime( p.start );
		this.end = new SimpleDateTime( p.end );
	}
	
	/**
	 * Overloaded constructor.
	 * 
	 * @param start beginning of the period
	 * @param end end of the period
	 */
	public Period(SimpleDateTime start, SimpleDateTime end) 
	{
		this.start = start;
		this.end = end;
	}
	
	
	/**
	 * Returns whether or not the period is valid
	 * 
	 * @return true if valid, false otherwise
	 */
	public boolean isValid() {
		
		return start.isValid() && end.isValid();
		
	}
	
	/**
	 * Parses a 'period-stamp' and returns a Period object containing the right
	 * data.  
	 * 
	 * @param s 'period-stamp' to parse
	 * @return Period derived from the 'period-stamp'
	 */
	public static Period parse(String s) {
		String dtS = s.substring(0,s.indexOf('-'));
		String dtE = s.substring(s.indexOf('-') + 1, s.length());
		return new Period(SimpleDateTime.parse(dtS),SimpleDateTime.parse(dtE));
	}
	
	/**Parse a period from the given parameters
	 * 
	 * @param beginYear
	 * @param beginMonth
	 * @param beginDay
	 * @param beginHour
	 * @param beginMin
	 * @param endYear
	 * @param endMonth
	 * @param endDay
	 * @param endHour
	 * @param endMin
	 * @return
	 */
	public static Period parse(int beginYear, int beginMonth, int beginDay, int beginHour, int beginMin,
			int endYear, int endMonth, int endDay, int endHour, int endMin){
		//YYYY.MM.DD:HH.MM-YYYY.MM.DD:HH.MM
		return Period.parse(beginYear + "." + beginMonth + "." + beginDay + ":" + 
				beginHour + "." + beginMin + "-" + endYear + "." + endMonth + "." +
				endDay + ":" + endHour + "." + endMin );
	}
	
	/**Return a period which has these beginning and ending values.  The period starts at 0:00 on the 
	 * specified start day, and ends at 23:59 on the specified end day.
	 * 
	 * @param beginYear
	 * @param beginMonth
	 * @param beginDay
	 * @param endYear
	 * @param endMonth
	 * @param endDay
	 * @return
	 */
	public static Period parse(int beginYear, int beginMonth, int beginDay, 
			int endYear, int endMonth, int endDay){
		return Period.parse(beginYear, beginMonth, beginDay, 0, 0, endYear, endMonth, endDay, 23, 59);
	}
	
	/**
	 * Returns a list of days in the specified period.
	 * 
	 * @param p period to inspect
	 * @return list of days in the period
	 */
	public static ArrayList<Period> splitIntoDays(Period p) {
		  ArrayList<Period> list = new ArrayList<Period>();
		  
		  int years = p.end.date.year - p.start.date.year;
		  int months;
		  
		  if (years == 0) {
			  months = p.end.date.month - p.start.date.month;
		  } else {
			  if (years > 1)
				  months = (12 - p.start.date.month) + (p.end.date.month) + ((years-1)*12);
			  else months = (12 - p.start.date.month) + (p.end.date.month);
		  }
		  
		  if (years == 0 && months == 0 && p.start.date.day - p.end.date.day == 0) {
			  list.add(p);
			  return list;
		  }
		  
		  int k = p.start.date.month-1;
		  for (int i = 0; i <= years; i++) {
			  int x = 0;
			  int year = p.start.date.year + i;
			  int[] m = CakeCal.getMonths(i+p.start.date.year);
			  if (year == p.start.date.year) x = p.start.date.day-1;
			  if (i == years) {
				  for(;k<=p.end.date.month-1;k++) {
					  int month = k+1;
					  int e = m[k];
					  if (k == p.end.date.month-1 && i == years) e = p.end.date.day;
					  for (; x < e; x++) {
						  int day = x+1;
						  Period temp = Period.parse(year + "." + month + "." + day + ":" + p.start.time.hour +
								  "." + p.start.time.minutes + "-" + year + "." + month + "." + day + ":" +
								  p.end.time.hour + "." + p.end.time.minutes);
						  list.add(temp);
					  }
					  x = 0;
				  }
				  break;
			  }
			  for(;k<=11;k++) {
				  int month = k+1;
				  int e = m[k];
				  if (k == p.end.date.month-1 && i == years) e = p.end.date.day;
				  for (; x < e; x++) {
					  int day = x+1;
					  Period temp = Period.parse(year + "." + month + "." + day + ":" + p.start.time.hour +
							  "." + p.start.time.minutes + "-" + year + "." + month + "." + day + ":" +
							  p.end.time.hour + "." + p.end.time.minutes);
					  list.add(temp);
				  }
				  x = 0;
			  }
			  k=0;
		  }
		  
		  return list;
	  }
	
	/**Split up a period into months
	 * 
	 * @param p The period to split up into months
	 * @return The specified period, split up into months
	 */
	public static ArrayList<Period> splitIntoMonths(Period param){
		Period p = new Period(param);
		ArrayList<Period> temp = new ArrayList<Period>();
		int endYear = p.end.date.year - p.start.date.year;	
		if(p.start.date.month == p.end.date.month){
			//SPECIAL CASE - the start and end months are the same
			//the period is simply whatever it was
			temp.add(p);
			return temp;
		}else{
			//add the first month
			int[] months = CakeCal.getMonths(p.start.date.year);
			temp.add(Period.parse(p.start.date.year, p.start.date.month, p.start.date.day,
					p.start.date.year, p.start.date.month, months[p.start.date.month-1]));
			p.start.date.month++;
			if(p.start.date.month > 12){
				//we were in december; loop to january
				p.start.date.month = 1;
				p.start.date.year++;
			}
			p.start.date.day = 1;
		}
		//at this point the period should be starting on the first of a month
		for( int x = 0; x <= endYear; x++){
			int[] months = CakeCal.getMonths(p.start.date.year);
			int monthToEnd;
			int monthToStart;
			if( endYear == 0){
				//we are in a period which starts in one year and ends in the
				//same year
				monthToEnd = p.end.date.month;
				monthToStart = p.start.date.month;
				
			}else if(endYear == x && endYear != 0){
				//we are in the last iteration of the loop; loop only until we reach
				//the last month in our period
				monthToEnd = p.end.date.month;
				monthToStart = 1;
			}else if(endYear != 0 ){
				//we are in a period which goes over the end of the year
				monthToEnd = 12;
				monthToStart = p.start.date.month;
			}else{
				//we are in a year between the start and end period; loop fully
				monthToEnd = 11;
				monthToStart = 1;
			}
			for(; monthToStart <= monthToEnd; monthToStart++){
				//add a period containing the full month
				temp.add(Period.parse(p.start.date.year, p.start.date.month, 1, 
						p.start.date.year, p.start.date.month, months[p.start.date.month-1]));
				p.start.date.month++;
				if(p.start.date.month == 13){
					p.start.date.month = 1;
					//p.start.date.year++;
					break;
				}
			}
			
			if( endYear == x){
				//last iteration of the loop; add in the last period
				//(which starts at the first of the last month and goes
				//to selected day of the last month)
				temp.add(Period.parse(p.end.date.year, p.end.date.month, 1, 
						p.end.date.year, p.end.date.month, p.end.date.day));
			}
			p.start.date.year++;
			p.start.date.month = 1;
		}
		return temp;
	}
	
	/**Take the specified period, and return an array list of all the years within that period
	 * 
	 * @param p
	 * @return
	 */
	public static ArrayList<Period> splitIntoYears(Period param) {
		Period p = new Period(param);
		ArrayList<Period> temp = new ArrayList<Period>();
		//SPECIAL CASE - the begining and end years are the same - just return
		//the period then
		if(p.start.date.year == p.end.date.year){
			temp.add(p);
			return temp;
		}
		//add the first year - from the begining of the period to the end of the year
		temp.add(Period.parse(p.start.date.year, p.start.date.month, p.start.date.day,
				p.start.date.year, 12, 31));
		//reset the start to the begining of the next year
		p.start.date.year++;
		p.start.date.month = 1;
		p.start.date.day = 1;
		
		int yearsToGo = p.end.date.year - p.start.date.year;
		for(int x = 0; x < yearsToGo; x++){
			//add in any years that are fully included
			temp.add(Period.parse(p.start.date.year, 1, 1, p.start.date.year, 12, 31));
			p.start.date.year++;
		}
		//add the last year
		temp.add(Period.parse(p.start.date.year, 1, 1, 
				p.end.date.year, p.end.date.month, p.end.date.day));
		return temp;
	}
	 
	/**
	 * Returns the number of days in the specified period.
	 * 
	 * @param p Period to inspect
	 * @return number of days in the period
	 */
	public static int numberOfDays(Period p) {
		  int n = 0;
		  
		  int years = p.end.date.year - p.start.date.year;		  
		  
		  int k = p.start.date.month-1;
		  for (int i = 0; i <= years; i++) {
			  int[] m = CakeCal.getMonths(i+p.start.date.year);
			  if (i == years) {
				  for(;k<=p.end.date.month-1;k++) {
					  n += m[k];
				  } 
				  break;
			  }
			  for(;k<=11;k++) {
				  n += m[k];
			  }
			  k=0;
		  }
		  return n;
	}
	
	/**
	 * Formats the Period into a 'period-stamp'
	 * 
	 * @return formatted 'period-stamp' representation of the Period
	 */
	public String format() {
		return start.format() + "-" + end.format();
	}
	
	/**Tests if this period contains the second period.  Does not account for hours or minutes,
	 * only for years/months/days.
	 * 
	 * @param p The period to compare with this period
	 * @return true if p starts at or after this period starts, and if p ends at or before 
	 * this period ends.  false otherwise.
	 */
	public boolean lieInside(Period p){
		//yeah, this is somewhat ugly code... oh well
		if((p.start.date.year < start.date.year) ||
				(p.end.date.year > end.date.year)){
			//first check the years
			return false;
		}
		
		if((p.start.date.month < start.date.month) ||
				(p.end.date.month > end.date.month)){
			//check the months...
			return false;
		}
		
		if(p.start.date.month == start.date.month){
			if(p.start.date.day < start.date.day){
				return false;
			}
		}
		if(p.end.date.month == end.date.month){
			if(p.end.date.day > end.date.day){
				return false;
			}
		}
			
		return true;
	}
} //-- Period