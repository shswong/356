import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This Class does everything with the database, no other classes should interact with the database
 * @author mtmargala
 *
 */
public class DatabaseConnection {

	Connection con;

	/**
	 * Constructor for our database connection
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public DatabaseConnection() throws ClassNotFoundException, SQLException {
		String url = "jdbc:odbc:cs356test";
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		con = DriverManager.getConnection(url, "myLogin", "myPassword");
	}

	/**
	 * Use this method to see if there are conflicting Events before adding an event
	 * @param e
	 * @return A list of all conflicting Event Objects
	 */
	public List<Event> getConflictingEvents(Event e){
		List<Event> list = new LinkedList<Event>();
		//Logic still needs to be implemented

		return list;
	}

	/**
	 * taskManager uses this to look for conflicts
	 * @param a
	 * @throws Exception
	 */
	public ArrayList<Event> getAllEvents(ArrayList<Event> a) throws Exception {
		Statement stat = con.createStatement();
		String query = "SELECT * FROM Table1";//gets everything from db
		ResultSet rs = stat.executeQuery(query); 

		int eventID;
		String eventName;
		String startTime;
		String endTime;
		String eventType;
		int classNumber;
		int priority;
		String marked;

		//get all the "class" events to find their start and end times
		while(rs.next()) //goes through each row in db
		{
			eventID = rs.getInt("eventID");
			eventName = rs.getString("eventName");
			startTime = rs.getString("startTime");
			endTime = rs.getString("endTime");
			priority = rs.getInt("priority");
			eventType = rs.getString("eventType");
			marked = rs.getString("marked");
			taskManager tm = new taskManager();

			if (eventType.equalsIgnoreCase("class") || eventType.equalsIgnoreCase("study") || eventType.equalsIgnoreCase("classTaught"))
			{  
				classNumber = rs.getInt("classNumber"); 
				a.add(new Event(eventID, eventName, tm.splitString(startTime), tm.splitString(endTime), eventType, classNumber, priority));
			}
			else
			{
				a.add(new Event(eventID, eventName, tm.splitString(startTime), tm.splitString(endTime), priority));
			}
		}
		return a;
	}

	/**
	 * Generate a LinkedList of all Event's during a specified time frame
	 * 
	 * @param start
	 * @param end
	 * @return A LinkedList of all Event's in that time frame
	 * @throws SQLException 
	 */
	public List<Event> getAllEventsInTimePeriod(String start, String end) throws SQLException {
		List<Event> list = new LinkedList<Event>();
		String query = "SELECT eventID FROM Table1 WHERE startDate > DateValue(\'"
			+ start + "\') AND endDate < DateValue(\'" + end + "\');";
		Statement stat = con.createStatement();
		ResultSet rs = stat.executeQuery(query);
		while (rs.next()){
			list.add(generateEvent(rs.getInt("eventID")));
		}
		return list;
	}

	/**
	 * Method to generate a Event Object from an eventID
	 * 
	 * @param eventID
	 * @return Event Object
	 * @throws SQLException 
	 */
	public Event generateEvent(int eventID) throws SQLException {
		String query = "SELECT * FROM Table1 where eventID=" + eventID;
		Statement stat = con.createStatement();
		ResultSet rs = stat.executeQuery(query);
		Event e = new Event();
		if (rs.next())
		{
			return new Event(rs.getInt("eventID"),rs.getString("eventName"),e.getTimeArr(rs.getString("startTime")),e.getTimeArr(rs.getString("endTime")),rs.getString("eventType"),rs.getInt("classNumber"),rs.getInt("priority"));
		}

		return null;
	}

	/**
	 * Adds the event 'e' to the database
	 * @param e
	 * @return
	 * @throws Exception
	 */
	public boolean add(Event e) throws Exception {

		String query = "INSERT INTO Table1 (eventName,startTime,endTime,eventType,classNumber,priority,startDate,endDate)"
			+ " VALUES (" + "'"
			+ e.getEventName()
			+ "', "
			+ "'"
			+ e.getStartMonth()
			+ '-'
			+ e.getStartDay()
			+ '-'
			+ e.getStartYear()
			+ ' '
			+ e.getStartHour()
			+ ':'
			+ e.getStartMin()
			+ ' '
			+ e.getStartAMPM()
			+ "', "
			+ "'"
			+ e.getEndMonth()
			+ '-'
			+ e.getEndDay()
			+ '-'
			+ e.getEndYear()
			+ ' '
			+ e.getEndHour()
			+ ':'
			+ e.getEndMin()
			+ ' '
			+ e.getEndAMPM()
			+ "', "
			+ "'"
			+ e.getEventType()
			+ "', "
			+ e.getClassNumber()
			+ ", " + e.getPriority() + "', "
			+ "'"
			+ e.getStartMonth()
			+ '-'
			+ e.getStartDay()
			+ '-'
			+ e.getStartYear()
			+ ' '
			+ e.getStartHour()
			+ ':'
			+ e.getStartMin()
			+ ' '
			+ e.getStartAMPM()
			+ "', "
			+ "'"
			+ e.getEndMonth()
			+ '-'
			+ e.getEndDay()
			+ '-'
			+ e.getEndYear()
			+ ' '
			+ e.getEndHour()
			+ ':'
			+ e.getEndMin()
			+ ' '
			+ e.getEndAMPM()
			+ ")";

		System.out.println(query);

		Statement stat = con.createStatement();

		stat.executeUpdate(query);

		return true;
	}

	/**
	 * Updated the event from the eventID with the new event 'e'
	 * @param eventID
	 * @param e
	 * @return
	 * @throws Exception
	 */
	public boolean editEvent(int eventID, Event e) throws Exception {
		String query = "UPDATE Table1 " + "SET eventName=\'" + e.getEventID()
		+ "\'," + "startTime=\'" + e.getStartMonth() + '-' + e.getStartDay()
		+ '-' + e.getStartYear() + ' ' + e.getStartHour() + ':'
		+ e.getStartMin() + ':' + e.getStartSecond() + "\',"
		+ "endTime=\'" + e.getEndMonth() + '-' + e.getEndDay() + '-'
		+ e.getEndYear() + ' ' + e.getEndHour() + ':' + e.getEndMin()
		+ ':' + e.getEndSecond() + "\'," + "eventType=\'"
		+ e.getEventType() + "\'," + "classNumber=\'"
		+ e.getClassNumber() + "\'," + "priority=" + e.getClassNumber() + 
		"\'," + "startDate=\'" + e.getStartMonth() + '-' + e.getStartDay()
		+ '-' + e.getStartYear() + ' ' + e.getStartHour() + ':'
		+ e.getStartMin() + ':' + e.getStartSecond() + "\',"
		+ "endDate=\'" + e.getStartMonth() + '-' + e.getStartDay()
		+ '-' + e.getStartYear() + ' ' + e.getStartHour() + ':'
		+ e.getStartMin() + ':' + e.getStartSecond() + "\'"
		+ "" + " WHERE eventID=" + eventID;

		System.out.println(query);

		Statement stat = con.createStatement();

		stat.executeUpdate(query);

		return true;
	}

	/**
	 * Used by taskManager to mark events true when autoschedule creates an event
	 * @param eventID
	 * @return
	 * @throws Exception
	 */
	public boolean editMarked(int eventID) throws Exception {
		String query = "UPDATE Table1 SET marked=true WHERE eventID=" + eventID;
		System.out.println(query);

		Statement stat = con.createStatement();

		stat.executeUpdate(query);
		return true;
	}

	/**
	 * Removes the event with the eventID from the Database
	 * @param eventID
	 * @return
	 * @throws Exception
	 */
	public boolean remove(int eventID) throws Exception {
		String query = "DELETE FROM Table1 WHERE eventID=" + eventID;
		System.out.println(query);

		Statement stat = con.createStatement();

		stat.executeUpdate(query);
		return true;
	}

	/**
	 * Use this to test the database in the driver
	 * @throws Exception
	 */
	public void printdb() throws Exception {
		String query = "select * from Table1";
		Statement stat = con.createStatement();
		ResultSet rs = stat.executeQuery(query);
		System.out.println(query);

		System.out.println("Table1 :");
		while(rs.next())
		{
			String status = rs.getString("eventID");
			status += "\t" + rs.getString("eventName");
			status += "\t\t" + rs.getString("startTime");
			status += "\t" + rs.getString("endTime");
			status += "\t" + rs.getString("eventType");
			status += "\t" + rs.getString("classNumber");
			status += "\t" + rs.getString("priority");
			status += "\t" + rs.getString("marked");
			status += "\t" + rs.getString("startDate");
			status += "\t" + rs.getString("endDate");
			System.out.println(status);
		}
	}

	/**
	 * taskManager uses this to count how many eventType="class"
	 * @return
	 * @throws Exception
	 */
	public int countClasses() throws Exception {
		Statement stat;
		stat = con.createStatement();
		ResultSet rs;
		String query = "SELECT * FROM Table1";
		rs = stat.executeQuery(query); 

		String eventType;
		int globalClassNumber = 0;

		while(rs.next())
		{
			eventType = rs.getString("eventType");

			if (eventType != null && eventType.equalsIgnoreCase("class"))
				globalClassNumber++;
		}
		return globalClassNumber;    
	}

}
