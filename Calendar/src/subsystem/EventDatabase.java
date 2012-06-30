package subsystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import observer.Observer;
import observer.Subject;

/**
 * This class is used to store, manage and organize all the Event objects
 * that are created in the calendar program.
 * All Events are stored in an array that represents the day in which they
 * occurr.
 * Each day array is stored as an entry in a hash map with keys in the
 * following format: YYYYMMDD.
 * each day array will have a capacity of 15 elements.
 * 
 */
class EventDatabase implements Subject
{
	
	private ArrayList<Observer> observers;
	private HashMap<Integer, ArrayList<Event>> eventMap;	

	/**
	 * constructor
	 *
	 */
	EventDatabase(CakeCal c) 
	{
		observers = new ArrayList<Observer>();
		eventMap = 	new HashMap<Integer, ArrayList<Event>>();
		//t.start();
	} //EventDatabase

	/**addEventPriv does the actual adding of events.  It is private because it does not notify 
	 * the observers of EventDatabase once it has added in the event.  The public interface will 
	 * notify observers once the event has been added.
	 * 
	 * @param event The event to add to the event database
	 */
	private void addEventPriv(Event event) 
	{
		event = new Event(event);
		//assert event.isValid();
		int key = event.getRecurring().get(0).toInt();
		//System.out.println( "add key: " + key );
		event.setUID(key * 100);
		ArrayList<Event> t1 = eventMap.get(key);
		if ( t1 != null) {

			event.setUID(event.getUID() + t1.size());
			t1.add(event);
			eventMap.put( key, t1);

		} else {
			
			ArrayList<Event> newList = new ArrayList<Event>();
			newList.add( event );
			eventMap.put( key, newList);
			
		}

		for ( int x = 1; x < event.getRecurring().size(); x++ ) {

			key = event.getRecurring().get(x).toInt();

			if ( eventMap.containsKey(key) ) {

				ArrayList<Event> temp = eventMap.get(key);

				if ( !this.exist(temp, event) ) {

					temp.add(event);
					eventMap.put( key, temp);

				} //end if exist

			} else {
				ArrayList<Event> newList = new ArrayList<Event>();
				newList.add( event );
				eventMap.put( key, newList);
			} // end if contains

		} //end for
		
	} //end addEvent
	
	/**
	 * This method adds a new Event object to the List of Event that
	 * occurs on the same date. if the list does not exist for that
	 * day, a new list is made and the event should be added to that
	 * list and then the list should be added to the map with a key
	 * format of YYYYMMDD
	 * 
	 * @param event the event to be added to the database
	 * @return 		the event added to the map to verify the success of 
	 * 				the process. Also, the event is returned with an 
	 * 				allocated UID.
	 */
	void addEvent(Event event) 
	{
		addEventPriv(event);
		//since this is the only thing that changes the event database, this is
		//the only time that our observers need to know anything
		this.notifyObservers();
	}
	
	/**Only use this method when loading from a file.  This
	 * does not cause the GUI to update, and does not return
	 * information that the loader doesn't need
	 * 
	 * @param event The event to load into the EventDatabase
	 */
	void loadEvent(Event event)
	{
		addEventPriv(event);
	}


	/**
	 * This function checks if an event already exists in a particular day(ArrayList)
	 * 
	 * @param temp   the ArrayList that represents the day
	 * @param event  the event object to be searched in the day
	 * @return       true if the event exists in the ArrayList, otherwise false
	 */
	boolean exist( ArrayList<Event> temp, Event event) {

		if (temp.contains(event)) {
			return true;
		}
		else
			return false;
	} // end exist


	/**
	 * This method deletes an event from the database given a key that
	 * represents the date and the event itself.
	 * 
	 * @param event the event to be deleted from the database
	 * @return true if the deletion was success, otherwise false.
	 */
	boolean deleteEvent( Event event ) 
	{
		
			if (event.getUID() != 0) {
				if ( event.getRecurringType().equals("0") || event.getRecurringType().equals(null) ) {
					int key = event.getUID()/100;
					ArrayList<Event> temp = eventMap.get(key);
					temp.set( (event.getUID() % 100), new Event()); // DON'T CHANGE THIS. IT'S JUST SETTING THE EVENT INTO AN EMPTY SPACE
					eventMap.put(key, temp);
				} else {
					
					int u = event.getUID();
					ArrayList<SimpleDate> list = event.getRecurring();
					ArrayList<Event> dayEvent;
					for ( SimpleDate d : list) {
						
						dayEvent = eventMap.get( d.toInt() );
						
						if ( dayEvent != null) {
						
							for ( int i = 0; i < dayEvent.size(); i++ ) {

								if ( dayEvent.get(i).getUID() == u ) {

									dayEvent.set(i, new Event() );
									eventMap.put(d.toInt() , dayEvent);
									break;

								}

							}

						}
						
					}
					
				}
				
				
			} else {
				System.err.println("You are trying to delete an event that doesn't have a UID\n" 
						+ " associated with it which makes deleteing impossible.\n"
						+ "Error in EventDatabase:deleteEvent" );
				
			} //end if else UID != 0

			notifyObservers();
		return true;
		
	} //end deleteEvent

	/**Modify the selected event.  Makes a copy of this event and deletes the original.
	 * 
	 * @param ev The event to change
	 * @return The event added to the database
	 */
	void updateEvent( Event ev) 
	{
		Event temp = new Event(ev);
		deleteEvent(ev);
		addEventPriv(temp);
		notifyObservers();
	}

	/**
	 * This method returns an array of ArrayLists for all the event that occurs
	 * on the given period. each element in the list is an ArrayList that
	 * represents a day.
	 * 
	 * @param   p	the period in which all events should be returned.
	 * @return	    an array of ArrayLists for all event occurring on the given period.
	 */
	protected ArrayList<Event> privateGetEvents ( Period p ) 
	{
		// we need to figure out how would we break the period
		// into keys given that not all months have the same
		// number of days.

		ArrayList<Event> myEvent = new ArrayList<Event>();
		int startYear = p.start.date.year;
		int startMonth = p.start.date.month;
		int startDay = p.start.date.day;
		int endYear = p.end.date.year;
		int endMonth = p.end.date.month;
		int endDay = p.end.date.day;

		ArrayList<Event> evAdded = new ArrayList<Event>();//events already added

		for( int year = startYear; year <= endYear; year++){

			for( int month = startMonth; month <= endMonth; month++){

				for ( int i = 0; i <= endDay; i++) {

					int key = ( ( (startYear * 100) + month ) * 100 ) + i;

					ArrayList<Event> tempList;
					if ((tempList = eventMap.get( key ) ) != null )
						for (Event t : tempList) {
							if (  !(t.getTitle().equals(""))  ){
								for( int x = 0; x < t.getRecurring().size(); x++){
									Event temp = new Event( t );
									Period ap = new Period( t.getPeriod() );
									ap.start = new SimpleDateTime(t.getRecurring().get(x),t.getPeriod().start.time);
									ap.end = new SimpleDateTime(t.getRecurring().get(x),t.getPeriod().end.time);
									temp.setPeriod(ap);
									if( !added(evAdded,temp) && 
											temp.getPeriod().start.date.day  >= startDay &&
											temp.getPeriod().end.date.day <= endDay &&
											temp.getPeriod().start.date.month >= month &&
											temp.getPeriod().end.date.month <= month  &&
											temp.getPeriod().start.date.year >= year && 
											temp.getPeriod().end.date.year <= year){
										myEvent.add( temp );
										evAdded.add(temp);
									}

								}
							}

						}
				}
			}
		}
		return myEvent;
	}
	
	ArrayList<Event> getEvents ( Period p ) {
		return privateGetEvents(p);
	}


	/**Checks to see if an event in this event list has already been added.  True if it has.
	 * 
	 * @param eventList The array list to search the event for
	 * @param e The event to search for
	 * @return true if the event exists in the array; false otherwise
	 */
	public boolean added(ArrayList<Event> eventList, Event e)
	{
		for( int x = 0; x < eventList.size(); x++){
			if( eventList.get(x).equals(e)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Accessor method to the eventMap private variable
	 * 
	 * @param map
	 */
	void setEventDatabase ( HashMap<Integer, ArrayList<Event>> map ) 
	{
		eventMap = map;
	}




	@SuppressWarnings("unchecked")
	/**
	 * this function formats the events so that it can be easily populated in many softwares
	 * 
	 * @return String  a string that hold all the event information in XML format
	 */
	String toXML() 
	{
		String xml = "";

		Set entrySet = eventMap.entrySet();
		Iterator iter = entrySet.iterator();
		ArrayList<Event> added = new ArrayList<Event>();
		
		while( iter.hasNext() ) {
			Map.Entry entry = (Map.Entry)iter.next();
			ArrayList<Event> list = (ArrayList<Event>) entry.getValue();
			
			for (Event e : list) {
				//if ( e.isValid() && !e.equals(null) )
				if ( !added.contains(e) )
				{
					xml += e.toXML();
					added.add(e);
				}
			}

		}

		return xml;
	}

	/**Attach an observer to this object.
	 * 
	 */
	public void attachObserver(Observer o) 
	{
		observers.add(o);		
		
	}

	/**Notify the observers of this object that something has changed.
	 * 
	 */
	public void notifyObservers() 
	{
		Iterator<Observer> i = observers.iterator();
		while( i.hasNext() ){
			Observer o = i.next();
			o.updateData();
		}
		
	}
} //EventDatabase
