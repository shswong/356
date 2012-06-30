/*
TODO
4)first thing the program does is have the user define the quarter/term with starting and ending dates

DONE
1)make checkConflict look only at events with the date being passed to it
2) find index in arraylist of the object with classType = class
3)fix getting date from Event object
5)make a add method for regular events to return a boolean
6)calculate # classes for goblalClassNumer
7)finish addEvent method if statements
8)provide suggestions to user when conflicts 
9)teacher preptime
10)check enddate does not occure before startdate

-autoscheduled study events should look at the day of class, if cannot find opening, look at next day
-study times should be an hour long
-a study event should be made for each class day (class 3x a week, 3 study events)

-in general for events being made, if conflict occurs, ask user to reschedule or have events overlap



AutoScheduler
is called automatically when a classType = class event is added

1. Go through db and make all rows with eventType = class into Event objects
    1.1 put Event objects into ArrayList
2. set study time at 8:00 AM on same day as class
    2.1 while loop study time  > than 10PM (22)
        2.1.1 for loop going through arraylist
            2.1.1.1 use check method passing study time, study time + 1 hour
                2.1.1.1.1 if true, increment study time by an hour
                2.1.1.1.2 if false, make a study event for 1 hour
                    2.1.1.1.2.1 return;



 */
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.sql.*;
import java.util.*;

public class taskManager
{
        private ArrayList<Event> listOfEvents; 
        private int globalClassNumber; //this is incremented everytime a classType = "class" is added to db
        DatabaseConnection db;

        public taskManager() throws Exception {
                listOfEvents = new ArrayList<Event>();
                globalClassNumber = 0;//used to give new class events a unique classNumber
                db = new DatabaseConnection();
                db.countClasses();
        }

        //e is the the radio buttons for student/TA
        private void autoSchedule(ActionEvent e) throws Exception {

                db.getAllEvents(listOfEvents);

                if (e.getActionCommand().equalsIgnoreCase("student"))
                {
                        //find study times for new class events
                        for (int i = 0; i < listOfEvents.size(); i++)
                        {
                                if (listOfEvents.get(i).getEventType().equalsIgnoreCase("class") && listOfEvents.get(i).getMarked().equalsIgnoreCase("false"))
                                {
                                        int timer = 8;
                                        int month = listOfEvents.get(i).getStartMonth();
                                        int day = listOfEvents.get(i).getStartDay();
                                        int year = listOfEvents.get(i).getStartYear();
                                        boolean flag = true;
                                        while (flag)
                                        {
                                                if (timer > 22)
                                                {
                                                        day++;
                                                        timer = 8;
                                                }
                                                if (checkConflict(timer, timer + 1, month, day,listOfEvents.get(i).getStartAMPM(),listOfEvents.get(i).getEndAMPM()))
                                                        timer++;

                                                else
                                                {
                                                        String AMPM = "AM";
                                                        if (timer > 12)
                                                        {
                                                                AMPM = "PM";
                                                                timer = timer - 12;
                                                        }
                                                        if (timer == 12) { AMPM = "PM"; }

                                                        String[] studyStartTime = new String[6];
                                                        dateFill(studyStartTime, Integer.toString(timer), "0", AMPM, Integer.toString(month), Integer.toString(day), Integer.toString(year));

                                                        String[] studyEndTime = new String[6];
                                                        dateFill(studyEndTime,Integer.toString(timer+1), "0", AMPM, Integer.toString(month), Integer.toString(day), Integer.toString(year));
                                                        //MAKE STUDY EVENT
                                                        addStudyEvent("Study for "+listOfEvents.get(i).getEventName(), dateString(studyStartTime), dateString(studyEndTime), "study", listOfEvents.get(i).getClassNumber(), 2);
                                                        listOfEvents.get(i).setMarked("true");
                                                        //use edit method to mark the row true
                                                        db.editMarked(listOfEvents.get(i).getEventID());
                                                        return;
                                                }
                                        }
                                }  
                        }

                }//end if student


                if (e.getActionCommand().equalsIgnoreCase("TA"))
                {
                        for (int i = 0; i < listOfEvents.size(); i++)
                        {
                                if (listOfEvents.get(i).getEventType().equalsIgnoreCase("classTaught") && listOfEvents.get(i).getMarked().equalsIgnoreCase("false"))
                                {
                                        int prepHour = listOfEvents.get(i).getStartHour() - 1;
                                        int month = listOfEvents.get(i).getStartMonth();
                                        int day = listOfEvents.get(i).getStartDay();
                                        int year = listOfEvents.get(i).getStartYear();
                                        boolean flag = true;
                                        while (flag)
                                        {
                                                if (prepHour > 22)
                                                {
                                                        day++;
                                                        prepHour = listOfEvents.get(i).getStartHour() - 1;
                                                }
                                                if (checkConflict(prepHour, prepHour + 1, month, day,listOfEvents.get(i).getStartAMPM(),listOfEvents.get(i).getEndAMPM())) { prepHour--; }
                                                else
                                                {
                                                        String AMPM = "AM";
                                                        if (prepHour > 12)
                                                        {
                                                                AMPM = "PM";
                                                                prepHour-=12;
                                                        }
                                                        if (prepHour == 12) { AMPM = "PM"; }

                                                        String[] prepStartTime = new String[6];
                                                        dateFill(prepStartTime, Integer.toString(prepHour), "30", AMPM, Integer.toString(month), Integer.toString(day), Integer.toString(year));

                                                        String[] prepEndTime = new String[6];
                                                        dateFill(prepEndTime,Integer.toString(listOfEvents.get(i).getStartHour()), "0", AMPM, Integer.toString(month), Integer.toString(day), Integer.toString(year));
                                                        //MAKE STUDY EVENT
                                                        addStudyEvent("Prepare for "+listOfEvents.get(i).getEventName(), dateString(prepStartTime), dateString(prepEndTime), "preparation", listOfEvents.get(i).getClassNumber(), 2);
                                                        listOfEvents.get(i).setMarked("true");
                                                        //use edit method to mark the row true
                                                        db.editMarked(listOfEvents.get(i).getEventID());
                                                        return;
                                                }
                                        }//while
                                }
                        }
                }
        }

        private boolean checkConflict(int startHour, int endHour, int month, int day, String startAMPM, String endAMPM) {
                for (int i = 0; i < listOfEvents.size(); i++)
                {
                        if (listOfEvents.get(i).getStartHour() == startHour && listOfEvents.get(i).getEndHour() == endHour && listOfEvents.get(i).getStartMonth() == month && listOfEvents.get(i).getStartDay() == day && startAMPM.equalsIgnoreCase(listOfEvents.get(i).getStartAMPM()) && endAMPM.equalsIgnoreCase(listOfEvents.get(i).getEndAMPM()))
                        {
                                return true; // there is a conflict
                        }
                }
                return false; // found no conflict
        }

        //suggest new time for user when theres a conflict; not 100% sure about this method
        public void suggestion(int startHour, String ampm)
        {
                for (int i = 0; i < listOfEvents.size(); i++)
                {
                        if (listOfEvents.get(i).getStartHour() == startHour || listOfEvents.get(i).getStartHour()+1 == startHour+1 && startHour < 12 && !ampm.equalsIgnoreCase("AM") && listOfEvents.get(i).getEndHour() > startHour+2)
                        {
                                startHour+=2;
                                if (startHour == 13) { startHour = 1; }
                                else if (startHour == 14) { startHour = 2; }
                                //ask if user would like to reschedule to this time
                                //suggest a time 2 hours ahead, override or let user decide new time
                        }
                }
        }

        public String[] splitDate(String date)
        {
                String[] temp1 = date.split(" "); // split by space
                String[] temp2 = temp1[0].split("/"); // split by /
                //5/21/2010 10:00 AM
                return temp2; // return 5 21 2010 in one array
        }

        public String[] splitTime(String date)
        {
                String[] temp1 = date.split(" "); // split by space
                String[] temp2 = temp1[1].split(":"); // split by :
                //5/21/2010 10:00 AM
                String[] temp3 = {temp2[0], temp2[1], temp1[2]};
                return temp3; // return 10 00 AM in one array
        }

        public String[] splitString(String date)
        {
                String[] temp1 = splitDate(date);
                String[] temp2 = splitTime(date);
                String[] temp3 = new String[6];
                return dateFill(temp3, temp2[0], temp2[1], temp2[2], temp1[0], temp1[1], temp1[2]);
        }

        public String[] dateFill(String[] date, String hours, String minutes, String ampm, String month, String day, String year) {
                date[0] = month;
                date[1] = day;
                date[2] = year;
                date[3] = hours;
                date[4] = minutes;
                date[5] = ampm;
                return date;
        }

        public String dateString(String[] date) {
                //return month+" "+day+", "+year+" "+hours+":"+minutes+" "+ampm
                //5/21/2010 10:00 AM
                return date[3]+"/"+date[4]+"/ "+date[5]+" "+date[0]+":"+date[1]+" "+date[2];
        }

        //ItemEvent buttons is buttons for recurring
        //ActionEvent radio is for student/TA for autoschduler
        public boolean addEvent(Event e) throws Exception {

                GregorianCalendar startCal = new GregorianCalendar();
                startCal.setLenient(true);
                String[] start = e.getStartTime();   
                //this sets year, month day
                startCal.set(Integer.parseInt(start[2]),Integer.parseInt(start[0])-1,Integer.parseInt(start[1]));
                startCal.set(GregorianCalendar.HOUR, Integer.parseInt(start[3]));
                startCal.set(GregorianCalendar.MINUTE, Integer.parseInt(start[4]));
                if (start[5].equalsIgnoreCase("AM")) { startCal.set(GregorianCalendar.AM_PM, 0); }
                else { startCal.set(GregorianCalendar.AM_PM, 1); }

                GregorianCalendar endCal = new GregorianCalendar();
                endCal.setLenient(true);
                String[] end = e.getEndTime();
                endCal.set(Integer.parseInt(end[2]),Integer.parseInt(end[0])-1,Integer.parseInt(end[1]));
                endCal.set(GregorianCalendar.HOUR, Integer.parseInt(end[3]));
                endCal.set(GregorianCalendar.MINUTE, Integer.parseInt(end[4]));
                if (end[5].equalsIgnoreCase("AM")) { endCal.set(GregorianCalendar.AM_PM, 0); }
                else { endCal.set(GregorianCalendar.AM_PM, 1); }

                if ((endCal.get(Calendar.HOUR) < startCal.get(Calendar.HOUR) && startCal.get(Calendar.AM_PM) == 1) || startCal.get(Calendar.DAY_OF_YEAR) > endCal.get(Calendar.DAY_OF_YEAR))
                {
                        // bring up prompt, end date cant be before start date
                        return false;
                }

                //if event is transient
                if (startCal.get(Calendar.MONTH) != endCal.get(Calendar.MONTH))
                {
                        if (checkConflict(startCal.get(Calendar.HOUR),endCal.get(Calendar.HOUR),Integer.parseInt(start[0]),Integer.parseInt(start[1]),start[5],end[5]))
                        {
                                //report to user there is a conflict
                        		return false;
                        }
                        else
                        {
                                db.add(e);
                                return true;
                        }
                }

                //if event is recurring
                /*
                if (startCal.get(Calendar.MONTH) != endCal.get(Calendar.MONTH) || (startCal.get(Calendar.DATE) != endCal.get(Calendar.DATE)))
                {
                        Object source = buttons.getItemSelectable();
                        for (int i = startCal.get(Calendar.DAY_OF_YEAR); i < endCal.get(Calendar.DAY_OF_YEAR); i++)
                        {
                                startCal.set(Calendar.DAY_OF_YEAR, i);
                                if (source == Sunday)
                                {
                                        if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                                        {
                                                if (checkConflict(startCal.get(Calendar.HOUR),endCal.get(Calendar.HOUR),Integer.parseInt(start[0]),i,start[5],end[5]))
                                                {
                                                        //report to user there is a conflict
                                                }
                                                else
                                                {
                                                        //add to db
                                                        String[] startDate = new String[6];
                                                        startDate = dateFill(startDate,Integer.toString(startCal.get(Calendar.HOUR)),Integer.toString(startCal.get(Calendar.MINUTE)),Integer.toString(startCal.get(Calendar.AM_PM)),Integer.toString(startCal.get(Calendar.MONTH)),Integer.toString(startCal.get(Calendar.DATE)),Integer.toString(startCal.get(Calendar.YEAR)));

                                                        String[] endDate = new String[6];
                                                        endDate = dateFill(startDate,Integer.toString(endCal.get(Calendar.HOUR)),Integer.toString(endCal.get(Calendar.MINUTE)),Integer.toString(endCal.get(Calendar.AM_PM)),Integer.toString(endCal.get(Calendar.MONTH)),Integer.toString(endCal.get(Calendar.DATE)),Integer.toString(endCal.get(Calendar.YEAR)));

                                                        if (e.getEventType().equalsIgnoreCase("class") || e.getEventType().equalsIgnoreCase("classTaught"))
                                                        {
                                                                db.add(new Event(e.getEventName(),startDate,endDate,e.getEventType(),globalClassNumber,e.getPriority()));
                                                                autoSchedule(e);
                                                        }
                                                        else
                                                                db.add(new Event(eventName,startDate,endDate,eventType,0,priority));
                                                }
                                        }
                                }

                                if (source == Monday)
                                {
                                        if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
                                        {
                                                if (checkConflict(startCal.get(Calendar.HOUR),endCal.get(Calendar.HOUR),Integer.parseInt(start[0]),i,start[5],end[5]))

                                                {
                                                        //report to user there is a conflict
                                                }
                                                else
                                                {
                                                        //add to db
                                                        String[] startDate = new String[6];
                                                        dateFill(startDate,Integer.toString(startCal.get(Calendar.HOUR)),Integer.toString(startCal.get(Calendar.MINUTE)),Integer.toString(startCal.get(Calendar.AM_PM)),Integer.toString(startCal.get(Calendar.MONTH)),Integer.toString(startCal.get(Calendar.DATE)),Integer.toString(startCal.get(Calendar.YEAR)));
                                                        String[] endDate = new String[6];
                                                        dateFill(startDate,Integer.toString(endCal.get(Calendar.HOUR)),Integer.toString(endCal.get(Calendar.MINUTE)),Integer.toString(endCal.get(Calendar.AM_PM)),Integer.toString(endCal.get(Calendar.MONTH)),Integer.toString(endCal.get(Calendar.DATE)),Integer.toString(endCal.get(Calendar.YEAR)));

                                                        if (eventType.equalsIgnoreCase("class") || eventType.equalsIgnoreCase("classTaught"))
                                                        {
                                                                db.add(new Event(eventName,startDate,endDate,eventType,globalClassNumber,priority));
                                                                autoSchedule(e);
                                                        }
                                                        else
                                                                db.add(new Event(eventName,startDate,endDate,eventType,0,priority));
                                                }
                                        }
                                }

                                if (source == Tuesday)
                                {
                                        if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
                                        {
                                                if (checkConflict(startCal.get(Calendar.HOUR),endCal.get(Calendar.HOUR),Integer.parseInt(start[0]),i,start[5],end[5]))

                                                {
                                                        //report to user there is a conflict
                                                }
                                                else
                                                {
                                                        //add to db
                                                        String[] startDate = new String[6];
                                                        dateFill(startDate,Integer.toString(startCal.get(Calendar.HOUR)),Integer.toString(startCal.get(Calendar.MINUTE)),Integer.toString(startCal.get(Calendar.AM_PM)),Integer.toString(startCal.get(Calendar.MONTH)),Integer.toString(startCal.get(Calendar.DATE)),Integer.toString(startCal.get(Calendar.YEAR)));
                                                        String[] endDate = new String[6];
                                                        dateFill(startDate,Integer.toString(endCal.get(Calendar.HOUR)),Integer.toString(endCal.get(Calendar.MINUTE)),Integer.toString(endCal.get(Calendar.AM_PM)),Integer.toString(endCal.get(Calendar.MONTH)),Integer.toString(endCal.get(Calendar.DATE)),Integer.toString(endCal.get(Calendar.YEAR)));

                                                        if (eventType.equalsIgnoreCase("class") || eventType.equalsIgnoreCase("classTaught"))
                                                        {
                                                                db.add(new Event(eventName,startDate,endDate,eventType,globalClassNumber,priority));
                                                                autoSchedule(e);
                                                        }
                                                        else
                                                                db.add(new Event(eventName,startDate,endDate,eventType,0,priority));
                                                }
                                        }
                                }

                                if (source == Wednesday)
                                {
                                        if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
                                        {
                                        	if (checkConflict(startCal.get(Calendar.HOUR),endCal.get(Calendar.HOUR),Integer.parseInt(start[0]),i,start[5],end[5]))

                                                {
                                                        //report to user there is a conflict
                                                }
                                                else
                                                {
                                                        //add to db
                                                        String[] startDate = new String[6];
                                                        dateFill(startDate,Integer.toString(startCal.get(Calendar.HOUR)),Integer.toString(startCal.get(Calendar.MINUTE)),Integer.toString(startCal.get(Calendar.AM_PM)),Integer.toString(startCal.get(Calendar.MONTH)),Integer.toString(startCal.get(Calendar.DATE)),Integer.toString(startCal.get(Calendar.YEAR)));
                                                        String[] endDate = new String[6];
                                                        dateFill(startDate,Integer.toString(endCal.get(Calendar.HOUR)),Integer.toString(endCal.get(Calendar.MINUTE)),Integer.toString(endCal.get(Calendar.AM_PM)),Integer.toString(endCal.get(Calendar.MONTH)),Integer.toString(endCal.get(Calendar.DATE)),Integer.toString(endCal.get(Calendar.YEAR)));

                                                        if (eventType.equalsIgnoreCase("class") || eventType.equalsIgnoreCase("classTaught"))
                                                        {
                                                                db.add(new Event(eventName,startDate,endDate,eventType,globalClassNumber,priority));
                                                                autoSchedule(e);
                                                        }
                                                        else
                                                                db.add(new Event(eventName,startDate,endDate,eventType,0,priority));
                                                }
                                        }
                                }

                                if (source == Thursday)
                                {
                                        if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
                                        {
                                        	if (checkConflict(startCal.get(Calendar.HOUR),endCal.get(Calendar.HOUR),Integer.parseInt(start[0]),i,start[5],end[5]))

                                                {
                                                        //report to user there is a conflict
                                                }
                                                else
                                                {
                                                        //add to db
                                                        String[] startDate = new String[6];
                                                        dateFill(startDate,Integer.toString(startCal.get(Calendar.HOUR)),Integer.toString(startCal.get(Calendar.MINUTE)),Integer.toString(startCal.get(Calendar.AM_PM)),Integer.toString(startCal.get(Calendar.MONTH)),Integer.toString(startCal.get(Calendar.DATE)),Integer.toString(startCal.get(Calendar.YEAR)));
                                                        String[] endDate = new String[6];
                                                        dateFill(startDate,Integer.toString(endCal.get(Calendar.HOUR)),Integer.toString(endCal.get(Calendar.MINUTE)),Integer.toString(endCal.get(Calendar.AM_PM)),Integer.toString(endCal.get(Calendar.MONTH)),Integer.toString(endCal.get(Calendar.DATE)),Integer.toString(endCal.get(Calendar.YEAR)));

                                                        if (eventType.equalsIgnoreCase("class") || eventType.equalsIgnoreCase("classTaught"))
                                                        {
                                                                db.add(new Event(eventName,startDate,endDate,eventType,globalClassNumber,priority));
                                                                autoSchedule(e);
                                                        }
                                                        else
                                                                db.add(new Event(eventName,startDate,endDate,eventType,0,priority));
                                                }
                                        }
                                }

                                if (source == Friday)
                                {
                                        if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                                        {
                                        	if (checkConflict(startCal.get(Calendar.HOUR),endCal.get(Calendar.HOUR),Integer.parseInt(start[0]),i,start[5],end[5]))

                                                {
                                                        //report to user there is a conflict
                                                }
                                                else
                                                {
                                                        //add to db
                                                        String[] startDate = new String[6];
                                                        dateFill(startDate,Integer.toString(startCal.get(Calendar.HOUR)),Integer.toString(startCal.get(Calendar.MINUTE)),Integer.toString(startCal.get(Calendar.AM_PM)),Integer.toString(startCal.get(Calendar.MONTH)),Integer.toString(startCal.get(Calendar.DATE)),Integer.toString(startCal.get(Calendar.YEAR)));
                                                        String[] endDate = new String[6];
                                                        dateFill(startDate,Integer.toString(endCal.get(Calendar.HOUR)),Integer.toString(endCal.get(Calendar.MINUTE)),Integer.toString(endCal.get(Calendar.AM_PM)),Integer.toString(endCal.get(Calendar.MONTH)),Integer.toString(endCal.get(Calendar.DATE)),Integer.toString(endCal.get(Calendar.YEAR)));

                                                        if (eventType.equalsIgnoreCase("class") || eventType.equalsIgnoreCase("classTaught"))
                                                        {
                                                                db.add(new Event(eventName,startDate,endDate,eventType,globalClassNumber,priority));
                                                                autoSchedule(e);
                                                        }
                                                        else
                                                                db.add(new Event(eventName,startDate,endDate,eventType,0,priority));
                                                }
                                        }
                                }

                                if (source == Saturday)
                                {
                                        if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                                        {
                                        	if (checkConflict(startCal.get(Calendar.HOUR),endCal.get(Calendar.HOUR),Integer.parseInt(start[0]),i,start[5],end[5]))
                                                {
                                                        //report to user there is a conflict
                                                }
                                                else
                                                {
                                                        //add to db
                                                        String[] startDate = new String[6];
                                                        dateFill(startDate,Integer.toString(startCal.get(Calendar.HOUR)),Integer.toString(startCal.get(Calendar.MINUTE)),Integer.toString(startCal.get(Calendar.AM_PM)),Integer.toString(startCal.get(Calendar.MONTH)),Integer.toString(startCal.get(Calendar.DATE)),Integer.toString(startCal.get(Calendar.YEAR)));
                                                        String[] endDate = new String[6];
                                                        dateFill(startDate,Integer.toString(endCal.get(Calendar.HOUR)),Integer.toString(endCal.get(Calendar.MINUTE)),Integer.toString(endCal.get(Calendar.AM_PM)),Integer.toString(endCal.get(Calendar.MONTH)),Integer.toString(endCal.get(Calendar.DATE)),Integer.toString(endCal.get(Calendar.YEAR)));

                                                        if (eventType.equalsIgnoreCase("class") || eventType.equalsIgnoreCase("classTaught"))
                                                        {
                                                                db.add(new Event(eventName,startDate,endDate,eventType,globalClassNumber,priority));
                                                                autoSchedule(e);
                                                        }
                                                        else
                                                                db.add(new Event(eventName,startDate,endDate,eventType,0,priority));
                                                }
                                        }
                                }
                        }
                }//end if recurring
                */
                db.getAllEvents(listOfEvents);
                return true;      
        }
        
        public void absoluteAdd(Event e) throws Exception
        {
        	db.add(e);
        }

        //this is called by autoscheduler to add study events, finds open slot, does not report conflict to user
        public void addStudyEvent(String eventName, String startTime, String endTime, String eventType, int classNumber, int priority) throws Exception {
                String[] startDate = new String[6]; 
                startDate = splitString(startTime);
                String[] endDate = new String[6]; 
                endDate = splitString(endTime);

                //add check for conflicts
                //
                //

                db.add(new Event(eventName, startDate, endDate,eventType,classNumber,priority));
                //increment classNumber after adding to db
                if (eventType.equalsIgnoreCase("class") || eventType.equalsIgnoreCase("classTaught"))
                {
                        globalClassNumber++;
                }
                db.getAllEvents(listOfEvents);
        }

        public void edit(int ID, Event e) throws Exception {
                for (int i = 0; i < listOfEvents.size(); i++)
                {
                        if (listOfEvents.get(i).getEventID() == ID)
                        {
                                for (int j = 0; j < listOfEvents.size(); j++)
                                {
                                        if (!(checkConflict(e.getStartHour(),e.getEndHour(),e.getStartMonth(),e.getStartDay(),e.getStartAMPM(),e.getEndAMPM())))
                                        {
                                            db.editEvent(ID,e);
                                        }
                                }
                        }
                }
                db.getAllEvents(listOfEvents);
        }
        
        public void delete(int ID) throws Exception {
                db.remove(ID);
                //delete all study events when you delete a event with eventType = class or classTaught, but we decide not to do it
                /*for (int i = 0; i < listOfEvents.size(); i++)
                {
                        if (listOfEvents.get(i).getEventID() == ID && (listOfEvents.get(i).getEventType().equals("class")) || listOfEvents.get(i).getEventType().equals("classTaught"))
                        {
                                //delete all study events made if this is a class
                                for (int j = 0; j < listOfEvents.size(); j++)
                                {
                                        if (listOfEvents.get(j).getClassNumber() == listOfEvents.get(i).getClassNumber())
                                                db.remove(listOfEvents.get(j).getEventID());
                                }  
                        }
                }*/
                db.getAllEvents(listOfEvents);
        }
}
