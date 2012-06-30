package subsystem;


import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import observer.Observer;
import observer.Subject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;


public class CakeCal implements Subject, Observer
{
	//-- NL is a cross-platform line feed; it will work on any OS (in theory).
	public static final String NL = System.getProperty("line.separator"); 	/**The new line seperator.  System-dependent*/
	public static final String TAB = "    ";								/** preferred tab length*/
	public static final String UNTITLED = "New Calendar";					/**"New Calendar"  Default name.*/
	public boolean modified;												/**True if the calendar has been modified 
																				since it was last saved.*/
	private EventDatabase eventDatabase;
	private CalendarSettings settings;

	private ArrayList<Observer> observers;

	private int currentYear = getDate().year;
	private int currentMonth = getDate().month;
	private int currentDay = getDate().day;


	//---Public Methods------------------------------------------------------//
	/**Main constructor.  Called unless there is an argument when Cake is invoked to open a file as a command line argument.
	 * @throws Exception 
	 * 
	 */
	public CakeCal() throws Exception 
	{
		this.init();		
	}

	/**Called by the constructors to do common tasks.
	 * @throws Exception 
	 * 
	 */
	private void init() throws Exception
	{
		 settings = new CalendarSettings();
         eventDatabase = new EventDatabase(this);
         settings.setName(UNTITLED);
         settings.setDateCreated(CakeCal.getDate());
         modified = false;
         observers = new ArrayList<Observer>();
         eventDatabase.attachObserver(this);
	}

	/**Return the settings used by this calendar.
	 * 
	 * @return The settings class used by this calendar.
	 */
	public CalendarSettings getSettings() {
		return settings;
	}

	/**Set the settings used by this calendar.
	 * 
	 * @param newSettings The new settings to be used for this calendar.
	 */
	public void setSettings(CalendarSettings newSettings) {
		settings = newSettings;
		this.updateData();
	}

	/**Load a calendar from the specified document
	 * 
	 * @param document The document to load from
	 * @param merge True if we are merging calendars, false otherwise
	 * 
	 * @return True if the loading was successful, false otherwise.
	 */
	private boolean loadCal(Document document, boolean merge)
	{
		try {
			document.getDocumentElement().normalize();
			NodeList settingsNodes = document.getElementsByTagName("settings");

			//-- First, lets load the settings
			if(!merge){
				//if we are merging two calendars, we don't want to overwrite our settings
				for (int i = 0; i < settingsNodes.getLength(); i++) {
					CalendarSettings temp = new CalendarSettings();
					Element element = (Element) settingsNodes.item(i);

					Element nameE = (Element) element.getElementsByTagName("name").
					item(0);
					NodeList name = nameE.getChildNodes();
					temp.setName(name.item(0).getNodeValue());

					Element datecreatedE = (Element) element.
					getElementsByTagName("datecreated").item(0);
					NodeList datecreated = datecreatedE.getChildNodes();
					temp.setDateCreated(SimpleDate.parse(datecreated.item(0).
							getNodeValue()));

					Element ownerE = (Element) element.
					getElementsByTagName("owner").item(0);
					NodeList owner = ownerE.getChildNodes();
					if (owner.getLength() > 0) temp.setOwner(owner.item(0).getNodeValue());
					else temp.setOwner( "");

					settings = temp;
				}
			}

			NodeList eventNodes = document.getElementsByTagName("event");

			//-- Now we load the events.
			for (int i = 0; i < eventNodes.getLength(); i++) {
				String tempTitle;
				String tempDesc;
				String tempRecu;
				String tempLoc;
				Period tempPd;

				Element element = (Element) eventNodes.item(i);

				Element titleE = (Element) element.getElementsByTagName("title")
				.item(0);
				NodeList title = titleE.getChildNodes();
				tempTitle = title.item(0).getNodeValue();

				Element descE = (Element) element.getElementsByTagName("desc").
				item(0);
				NodeList desc = descE.getChildNodes();
				if (desc.getLength() > 0) tempDesc = desc.item(0).getNodeValue();
				else tempDesc = "";

				Element recuE = (Element) element.getElementsByTagName("recu").
				item(0);
				NodeList recu = recuE.getChildNodes();
				if (recu.getLength() > 0) tempRecu = recu.item(0).getNodeValue();
				else tempRecu = "0";

				Element locationE = (Element) element.getElementsByTagName("location").item(0);
				NodeList location = locationE.getChildNodes();
				if (location.getLength() > 0) tempLoc = location.item(0).getNodeValue();
				else tempLoc = "";

				Element periodE = (Element) element.getElementsByTagName("period")
				.item(0);
				NodeList period = periodE.getChildNodes();
				tempPd = Period.parse(period.item(0).getNodeValue());

				eventDatabase.loadEvent(new Event(tempPd,tempTitle,tempDesc,tempRecu,tempLoc));
			}
		} catch (Exception e){
			e.printStackTrace();
			e.getMessage();
			return false;
		}
		return true;
	}

	/**Load a calendar from a specified input stream
	 * 
	 * @param is The input stream to load from
	 * @return True if succesful, false otherwise.
	 */
	public boolean loadCal(InputStream is){
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(is);
			return loadCal(document, false);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Loads a calendar into this CakeCal.
	 * 
	 * @param filename name of the file to load
	 * @param merge true if we are merging calendars, false otherwise.
	 * 
	 * @return true if successful, false otherwise
	 */
	public boolean loadCal(String filename, boolean merge) {
		try {
			File file = new File(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(file);
			return loadCal(document, merge);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns the day of a week a date occurs on. Does this in O(c), which helps
	 * the calendar render speedily.
	 * 
	 * @param d the date to get the weekday of
	 * @return the day of the week
	 */
	public static int getDayOfWeek(SimpleDate d) {
		return Cakeday.getDayOfWeek(d);
	}

	/**
	 * Returns an array of months for a given year.
	 * 
	 * @param year which year to get months for
	 * @return array of months for the year 
	 */
	public static int[] getMonths(int year) {
		return Cakeday.getMonths(year);
	}

	/**
	 * Returns a SimpleDate object containing the current date.
	 * 
	 * @return SimpleDate of the current date.
	 */
	public static SimpleDate getDate() {
		String df = "yyyy.MM.dd";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(df);
		return SimpleDate.parse(sdf.format(cal.getTime()));
	}

	/**
	 * Returns a list of events within a certain period.
	 * 
	 * @param p the period to use
	 * @return an ArrayList containing the events within that period
	 */
	public ArrayList<Event> getEvents(Period p) {
		return eventDatabase.getEvents(p);
	}

	/**
	 * Adds an event to the event database. 
	 * 
	 * @param e the event to add to the database
	 */
	public void addEvent(Event e) {
		modified = true;
		eventDatabase.addEvent(e);
	}

	/**
	 * Updates an event in the EventDatabase, and returns the event updated,
	 * (with has the updated UID)
	 * 
	 * @param e event to update
	 */
	public void updateEvent(Event e) {
		eventDatabase.updateEvent(e);
	}

	/**
	 * Deletes an equivalent event from the event database.
	 * 
	 * @param e event to delete
	 * @return whether or not it was successful
	 */
	public boolean deleteEvent(Event e) {
		return eventDatabase.deleteEvent(e);
	}

	/**defined by subject
	 * 
	 * @param o
	 */
	public void attachObserver(Observer o) {
		observers.add(o);
	}

	/**Defined by subject
	 * 
	 */
	public void notifyObservers() {
		Iterator<Observer> i = observers.iterator();
		while( i.hasNext() ){
			Observer o = i.next();
			o.updateData();
		}

	}

	/**defined by observer
	 * 
	 */
	public void updateData() {
		//we only observe EventDatabase.  when something changes there, we want to 
		//set the modified variable(as something has changed) and tell our observers
		//that something has changed, so that they can get the new data from the
		//EventDatabase.
		this.modified = true;
		this.notifyObservers();

	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	public int getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentDay(int currentDay) {
		this.currentDay = currentDay;
	}

	public int getCurrentDay() {
		return currentDay;
	}

	/**Export this calendar as a PDF file.  This is just essentially a flat-file dump,
	 * in this format:
	 * 
	 * +--------------------------------------------------+
	 * | Date       Time    Description                   |
	 * | <date>    <time>   <desc> @ <location>           |
	 * |           <time2>  <desc> @ <location>           |
	 * | <date2>   <time>   <desc> @ <location>           |
	 * +--------------------------------------------------+
	 * 
	 * etc...
	 * 
	 * @param p The period to get the events for that the PDF is generated from
	 * @param filename The filename that the user would like to save as.
	 */
	public void exportAsPDF(Period p, String filename, Rectangle pageSize)
	{
		/*
		
		ArrayList<Event> events = new ArrayList<Event>();
		ArrayList<Period> thePeriods = Period.splitIntoDays(p);
		for( Period day : thePeriods){
			events.addAll(eventDatabase.getEvents(day));
		}

		try {			
			com.itextpdf.text.Document document = new com.itextpdf.text.Document(pageSize, 0, 0, 20, 20);
			@SuppressWarnings("unused")
			PdfWriter writer = PdfWriter.getInstance(document, 
					new FileOutputStream(filename));

			document.open();

			PdfPCell dateCell = new PdfPCell();
			dateCell.setHeader(true);
			dateCell.setBorder(0001);
			Cell timeCell = new Cell("Time");
			timeCell.setHeader(true);
			timeCell.setBorder(0001);			
			Cell event = new Cell("Event");
			event.setHeader(true);
			event.setBorder(0001);
			
			PdfPTable theTable = new PdfPTable(events.size() + 1);
			theTable.setWidths(new int[]{10,20,70});
			theTable.addCell(dateCell);
			theTable.addCell(timeCell);
			theTable.addCell(event);


			//keep track of the last month/day used so that we only put the date in front
			//of the time for the day ONCE. (see javadoc comment)
			int month = -1;
			int day = -1;
			for( Event e : events){
				//so, first let's get the start date of the event
				Period pd = e.getPeriod();
				String date = pd.start.date.month + "/" + pd.start.date.day;
				if(month == pd.start.date.month && day == pd.start.date.day){
					//if the date is the same as the date for the previous day,
					//we don't want to print it out
					date = "";
				}
				month = pd.start.date.month;
				day = pd.start.date.day;

				//now a string for the time of the event
				String time = pd.start.time.toString() + "-\n" +
				pd.end.time.toString();

				//finally, the string for the description @ location.  the @ is ommitted if 
				//no location is provided. 
				String desc = e.getDescription();
				String loc = e.getLocation();
				String descAndLoc;

				//there's probably a simpler way to do these next few lines of code...
				boolean atSymbolReq = true;
				boolean colonReq = true;
				if (loc.isEmpty()){
					atSymbolReq = false;
				}
				if(desc.isEmpty()){
					colonReq = false;
				}

				if(atSymbolReq){
					descAndLoc = e.getTitle() + ": " + desc + " @ " + loc;
				}else if(colonReq){
					descAndLoc = e.getTitle() + ": " + desc;
				}else{
					descAndLoc = e.getTitle() + desc;
				}

				//we now have the three columns needed:
				//date, time, and event
				Cell tempDate = new Cell(date);
				tempDate.setBorder(0000); //no border on the cell
				Cell tempTime = new Cell(time);
				tempTime.setBorder(0000);
				Cell tempEvent = new Cell(descAndLoc);
				tempEvent.setBorder(0000);

				theTable.addCell(tempDate);
				theTable.addCell(tempTime);
				theTable.addCell(tempEvent);
			}

			document.add(theTable);
			document.close();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		*/
	}

	/**Export the calendar as a PDF, in MonthView
	 * 
	 * @param p The period containing the months to export
	 * @param filename The name to save the PDF as
	 * @param pageSize
	 */
	public void exportAsPDFMonth(Period p, String filename, Rectangle pageSize)
	{
		/*
		
		ArrayList<Period> thePeriods = Period.splitIntoMonths(p);
		//for( Period day : thePeriods){
		//events.addAll(eventDatabase.getEvents(day));
		//}

		try {			
			com.itextpdf.text.Document document = new com.itextpdf.text.Document(pageSize, 0, 0, 20, 20);
			@SuppressWarnings("unused")
			PdfWriter writer = PdfWriter.getInstance(document, 
					new FileOutputStream(filename));

			document.open();

			for( Period month: thePeriods){
				Cell monthCell = new Cell(Cakeday.MONTHS[month.start.date.month-1]);
				monthCell.setColspan(7);
				
				int currentMonthOffset = CakeCal.getDayOfWeek(
						SimpleDate.parse(month.start.date.year + "." + month.start.date.month + "." + 1));
				int[] months = CakeCal.getMonths(month.start.date.year);

				PdfPTable theTable = new PdfPTable(7);
				theTable.setWidths(new int[]{10,10,10,10,10,10,10});
				theTable.addCell(monthCell);
				// end headers
				for( int x = 0; x < 7*6; x++){
					
					Cell temp = new Cell();
					if( x < currentMonthOffset || x > months[month.start.date.month-1]){
						temp.addElement(new Paragraph(""));
					}else{
						Phrase date = new Phrase(((x-currentMonthOffset) + 1) + "", 
								FontFactory.getFont(FontFactory.COURIER, 12.0f, Font.NORMAL, BaseColor.BLUE));
						Paragraph addToCell = new Paragraph(date);
						ArrayList<Event> events = new ArrayList<Event>();
						ArrayList<Period> days = Period.splitIntoDays(month);
						for( Period theDay : days){
							//get all of the events for a certain day
							events.addAll(eventDatabase.getEvents(theDay));
							for( Event eventToadd : events){
								addToCell.add(new Paragraph("\n   " + eventToadd.getTitle() ));
							}
							events.clear();
						}
						temp.addElement(addToCell);
					}
					theTable.addCell(temp);
				}

				//document.add(monthCell);
				document.add(theTable);
			}
			document.close();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		*/
	}
	
} //-- CakeCal
