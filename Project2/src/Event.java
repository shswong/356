public class Event
{
    private int eventID;
    private String eventName;
    private String[] startTime;
    private String[] endTime;
    private String eventType;
    private int classNumber;
    private int priority;
    private String marked;
    
    public Event(){
    	
    }
    public Event(String eventName, String[] startTime, String[] endTime, String eventType, int classNumber, int priority)
    {
        //this.eventID = eventID;
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
        this.classNumber = classNumber;
        this.priority = priority;
        marked = "false";
    }
    public Event(int eventID, String eventName, String[] startTime, String[] endTime, int priority)
    {
        this.eventID = eventID;
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        marked = "false";
    }
    public Event(int eventID, String eventName, String[] startTime, String[] endTime, String eventType, int classNumber, int priority)
    {
        this.eventID = eventID;
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
        this.classNumber = classNumber;
        this.priority = priority;
        marked = "false";
    }
    public int getEventID() { return eventID; }
    public String getEventName() { return eventName; }
    public String[] getStartTime() { return startTime; }
    public String[] getEndTime() { return endTime; }
    public String getEventType() { return eventType; }
    public int getClassNumber() { return classNumber; }
    public int getPriority() { return priority; }
    public int getStartHour() { return Integer.parseInt(startTime[3]); }
    public int getStartMin() { return Integer.parseInt(startTime[4]); }
    public int getStartSecond() { return Integer.parseInt(startTime[5]); }
    public String getStartAMPM() { return startTime[6]; }
    public int getEndHour() { return Integer.parseInt(endTime[3]); }
    public int getEndMin() { return Integer.parseInt(endTime[4]); }
    public int getEndSecond() { return Integer.parseInt(endTime[5]); }
    public String getEndAMPM() { return endTime[6]; }
    public int getStartMonth() { return Integer.parseInt(startTime[0]); }
    public int getStartDay() { return Integer.parseInt(startTime[1]); }
    public int getStartYear() { return Integer.parseInt(startTime[2]); }
    public int getEndMonth() { return Integer.parseInt(endTime[0]); }
    public int getEndDay() { return Integer.parseInt(endTime[1]); }
    public int getEndYear() { return Integer.parseInt(endTime[2]); }
    public void setMarked(String val) { marked = val; }
    public String getMarked() { return marked; }
    public String[] getTimeArr(String s){
    	int start = 0, end = 0;
        String [] startTimeArr = new String[7];
        end = s.indexOf('-', start);
        startTimeArr[0] = s.substring(start, end);
        start = end+1;
        end = s.indexOf('-', start);
        startTimeArr[1] = s.substring(start, end);
        startTimeArr[2] = s.substring(end+1, end+3);
        start = s.indexOf(':', end);
        startTimeArr[3] = s.substring(end+4, start);
        start += 1;
        end = s.indexOf(':', start);
        startTimeArr[4] = s.substring(start, end);
        start = end + 1;
        startTimeArr[5] = s.substring(start, s.length());
        //startTimeArr[6] = s.substring(end+1, s.length());
        
    	return startTimeArr;
    }

    public String toString(){
    	
    	return null;
    }
}