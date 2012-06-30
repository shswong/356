/////////////////////////////////////////////////////////////////////////////////////////////////
//Programmer: Antony Lam
//Controller Class
//Decides which floor request goes to which elevator.
/////////////////////////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.util.Vector;
import java.util.Random;
import java.util.ArrayList;


class FloorRequestTuple
{
   public int floorNum;
   public char direction;

   public FloorRequestTuple(int floorNum, char direction)
   {
      this.floorNum = floorNum;
      this.direction = direction;
   }
}


class State
{
   public int currentPos;
   public char[] jobs;
   public char direction;
   public boolean available;

   public State(int numFloors)
   {
      currentPos = 0;
      jobs = new char[numFloors];
      for(int i = 0; i<numFloors; i++) jobs[i] = '0';
      direction = 'n'; //u = up, d = down, n = neutral
      available = true;
   }
}


public class Controller
{
	private int numOfFloors;
    private static int numOfElevators;
    private static ArrayList<Elevator> listOfElevators = new ArrayList();
    private static int[] allCurrentFloor; //used in getAllCurrentFloor()
    private static char[] allCurrentDirection; //used in getAllCurrentDirection()
    private static int[] allDistance; //used in getAllDistance
   private static int numElevators, numParams, numFloors;
   private static State[] states;
   private static Elevator[] elevators;
   private static Vector requestQueue;
   private static double[] costs;
   private static Random rand;

   public Controller(Elevator[] elevators)
   {
      this.elevators = elevators;
      this.numFloors = 10;
      this.numElevators = elevators.length;
      states = new State[numElevators];
      for(int i = 0; i<numElevators; i++) states[i] = new State(numFloors);
      requestQueue = new Vector(10,10);
      costs = new double[numElevators]; //To store cost values of each elevator.
      rand = new Random();
      
   }

   public Controller(Elevator[] elevators, int numElevators, int numFloors)
   {
	   numOfFloors = numFloors;
       numOfElevators = numElevators;
       allCurrentFloor = new int[numOfElevators];
       allCurrentDirection = new char[numOfElevators];
       allDistance = new int[numOfElevators];
       
      this.elevators = elevators;
      this.numFloors = numFloors;
      this.numElevators = numElevators;
      states = new State[numElevators];
      for(int i = 0; i<numElevators; i++) states[i] = new State(numFloors);
   }
   //////////////////////////////////////////////////////////////////////////////////////
   public ArrayList<Elevator> getListOfElevators () {
       return listOfElevators;
   }

   public static int getNumOfElevators () {
       return numOfElevators;
   }

   public void setNumOfElevators (int val) {
       this.numOfElevators = val;
   }

   public int getNumOfFloors () {
       return numOfFloors;
   }

   public void setNumOfFloors (int val) {
       this.numOfFloors = val;
   }

   public void addElevator () {
       listOfElevators.add(new Elevator());
   }
   private static boolean getAllIsIdle () {
       // scans through all elevators and
       // returns true if all the elevators are idle, false if
       // at least one elevator is not idle
       boolean b = false;
       for (int i = 0; i < numOfElevators; i++)
       {
           if (listOfElevators.get(i).getAvailable() == true)
           {
               b = true;
           }
           else
           {
               b = false;
           }
       }
       return b;
   }

   private static void getAllCurrentFloor () {
       // using class array allCurrentFloor
       // stores current floors for all elevators at a
       // single point in time for helping getClosestElevator
       for (int i = 0; i < numOfElevators; i++)
       {
           allCurrentFloor[i] = listOfElevators.get(i).getCurrentLevelInt();
       }
   }

   private void getAllCurrentDirection () {
       //using class array allCurrentDirection
       // stores current directions for all elevators at a
       // single point in time for helping chooseElevator
       for (int i = 0; i < numOfElevators; i++)
       {
           allCurrentDirection[i] = listOfElevators.get(i).getMovingDirection();
       }
   }

   private static void getAllDistance (int floor) {
       // using class array allDistance
       // stores current distances for all elevators
       // from the paramter floor at a single point in time
       getAllCurrentFloor();
       for (int i = 0; i < numOfElevators; i++)
       {
           allDistance[i] = Math.abs(floor - allCurrentFloor[i]);
       }
   }

   private static Elevator getShortestElevator (Elevator e, int floor, int min) {
       for (int i = 0; i < numOfElevators; i++)
       {
           if (allDistance[i] == 999)
           {
               min = allDistance[0];
               for (int k = 1; i < numOfElevators; i++)
               {
                   if (min > allDistance[k])
                   {
                       min = allDistance[k];
                   }
               }
           }
       }

       if (e.getAvailable() && e.getCurrentLevelInt() == floor)
       {
           return e;
       }
       else if (!(e.getAvailable()))
       {
           if ((e.getCurrentLevelInt() < floor && e.getMovingDirection() == 'u') || (e.getCurrentLevelInt() > floor && e.getMovingDirection() == 'd'))
           {
               return e;
           }
           else
           {
               allDistance[min] = 999; // eg. unusable
               getShortestElevator(e, floor, min);
           }
       }
       return e;
   }

   public static Elevator elevatorAlgorithm (int floor) {
       if (getAllIsIdle())
       {
           return randomElevator(floor);
       }

       getAllDistance(floor);
       int min = allDistance[0];

       for (int i = 1; i < numOfElevators; i++)
       {
           if (min > allDistance[i])
           {
               min = allDistance[i];
           }
       }

       Elevator e = listOfElevators.get(min);

       return getShortestElevator(e, floor, min);

   }

   public static Elevator randomElevator (int floor) {
       int counter = 0;//# of elevators on floor we are looking for
       int current = 0;//if theres only one elevator on that floor, this is to know here it is in the arraylist
       int elevatorsOnFloor[] = new int[getNumOfElevators()];
       //for loop goes through all the elevators and checks if they are on the floor that we pass through the parameter
       for (int i=1; i <= getNumOfElevators(); i++)
       {
           //for loop starts at 1
           //if an elevator is on floor, add it to elevatorsOnFloor array and set current to its index
           if (listOfElevators.get(i).getCurrentLevel() == floor)
           {
               elevatorsOnFloor[counter++] = i;
               current = i;
           }
       }

       if (counter == 0)
       {
           //if theres no elevators on the floor
           return null;
       }
       //if counter > 1, pick a random number of size counter, then return the random number as elevator
       if (counter > 1)
       {
           Random generator = new Random();
           int rand = generator.nextInt(counter) + 1;
           return listOfElevators.get(elevatorsOnFloor[rand]);
       }
       else
       {
           //if only one elevator on floor, return it
           return listOfElevators.get(current);
       }
   }
///////////////////////////////////////////////
	public static void reset() {requestQueue.removeAllElements();}

	private static void updateStates()
	{
		for(int i = 0; i<numElevators; i++)
		{
			char currentDirection = elevators[i].getMovingDirection();
			boolean available = elevators[i].getAvailable();
			double currentPos = elevators[i].getCurrentLevel();
			boolean internalRequests[] = elevators[i].getStopRequests();
			boolean floorRequests[] = elevators[i].getPickUpRequests();
			
			if(currentPos-(int)currentPos>0 && currentDirection=='u')
				states[i].currentPos = (int)currentPos+1;
			else 
				states[i].currentPos = (int)currentPos;

			states[i].available = available;
			states[i].direction = currentDirection;
			
			for(int j = 0; j<numFloors; j++)
			{
				if(!floorRequests[j]) states[i].jobs[j] = '0';
				if(internalRequests[j] && states[i].jobs[j]=='0') states[i].jobs[j] = 'n';
				else if(!internalRequests[j] && states[i].jobs[j]=='n') states[i].jobs[j] = '0';
			}
		}
	}


   public static void retryQueuedRequests()
   {
		if(requestQueue.size() > 0)
		{
			FloorRequestTuple request = (FloorRequestTuple)requestQueue.elementAt(0);
			int minIndex = findMinCostIndex(request.floorNum,request.direction);

			if(minIndex>-1)
			{
				if(costs[minIndex] < 500) 
				{
					assignJob(request.floorNum,request.direction,minIndex);
					requestQueue.removeElementAt(0); //i--;
			   	}
			}
		}
   }


   public static int decide(int destination, char floorDir) //floorDir = floor's direction
   {
   		int num;

   		for(int i = 0; i < elevators.length; i++) 
   			elevators[i].stop();

      	int minCostIndex = findMinCostIndex(destination,floorDir);

		if(minCostIndex<0) 
			return -1;
      	
		if(costs[minCostIndex]>=500)
      	{
	 		requestQueue.add(new FloorRequestTuple(destination,floorDir));
	     	for(int i = 0; i < elevators.length; i++) 
   				elevators[i].start();

   			return -1;
      	}

        num = assignJob(destination, floorDir, minCostIndex);

       	for(int i = 0; i < elevators.length; i++)
   			elevators[i].start();
       	
   		return num;
   }


	private static int findMinCostIndex(int destination, char floorDir)
	{
		updateStates(); //Update internal states to match external elevators.
		int minCostIndex = 0;
		for(int i = 0; i<numElevators; i++)
		{
			if(states[i].jobs[destination]!=floorDir)
				costs[i] = cost(states[i],destination,floorDir);
			//If an elevator is already going to fulfill this request reject it to prevent duplication of requests.
			else 
				return -1;
			if(costs[minCostIndex]>costs[i]) 
				minCostIndex = i;
		}

		//Random element, pick other equally low cost elevators sometimes.
		for(int i = 0; i<numElevators; i++)
			if(costs[minCostIndex]==costs[i] && rand.nextBoolean()) 
				minCostIndex = i;

		return minCostIndex;
		
	}


	private static int assignJob(int destination, char floorDir, int minCostIndex)
	{
		states[minCostIndex].jobs[destination] = floorDir;
		elevators[minCostIndex].setPickUpRequests(destination);

		if(elevators[minCostIndex].getMovingDirection()=='n')
		{
			if(elevators[minCostIndex].getCurrentLevel()==destination)
				elevators[minCostIndex].setMovingDirection(floorDir);
			else if(elevators[minCostIndex].getCurrentLevel()<destination)
				elevators[minCostIndex].setMovingDirection('u');
			else if(elevators[minCostIndex].getCurrentLevel()>destination)
				elevators[minCostIndex].setMovingDirection('d');
		}
		
		// added by Robert
		elevators[minCostIndex].setElevatorButtonMovingDirection(floorDir);

		// added by Robert
		elevators[minCostIndex].animateElevator();
		
		return minCostIndex;
	}


	private static double cost(State s, int destination, char floorDir)
	{
		//Return value of 500 indicates rejection of request.
		double rejectVal = 500;
		if(!s.available) return rejectVal;
		if(floorDir=='u' && s.jobs[destination]=='d') 
			return 500;

		if(floorDir=='d' && s.jobs[destination]=='u') 
			return 500;

		if(floorDir=='u' && hasLessThan(s,destination,'d')>-1) 
			return rejectVal;

		if(floorDir=='d' && hasGreaterThan(s,destination,'u')>-1) 
			return rejectVal;

		if(floorDir=='u' && hasGreaterThan(s,destination,'d')>-1) 
			return rejectVal;

		if(floorDir=='d' && hasLessThan(s,destination,'u')>-1) 
			return rejectVal;

		if(floorDir=='d' && hasGreaterThan(s,destination,'d')>-1) 
			return rejectVal;

		if(floorDir=='u' && hasLessThan(s,destination,'u')>-1) 
			return rejectVal;

		if(floorDir=='u' && s.currentPos>destination && hasGreaterThan(s,destination,'d')>-1)
			return rejectVal;

		if(floorDir=='d' && s.currentPos<destination && hasLessThan(s,destination,'u')>-1)
			return rejectVal;

		if(floorDir=='u' && s.currentPos>destination && hasLessThan(s,s.currentPos+1,'u')>-1)
			return rejectVal;

		if(floorDir=='d' && s.currentPos<destination && hasGreaterThan(s,s.currentPos-1,'d')>-1)
			return rejectVal;

		if(floorDir=='u' && hasLessThan(s,destination,'n')>-1)
			return rejectVal;

		if(floorDir=='d' && hasGreaterThan(s,destination,'n')>-1)
			return rejectVal;
		//Return value of 500 indicates rejection of request.

		if(s.currentPos==destination) return 0;

		int dist = 0;
		int numStops = 0;
		if(s.direction=='u')
		{
		for(int i = s.currentPos; i<destination; i++)
		{
		dist++;
		if(s.jobs[i]!='0') numStops++;
		}
		if(dist==0) return rejectVal; //Since s.currentPos!=destination here
		}					   //dist==0 => reject request
		else if(s.direction=='d')
		{
		for(int i = s.currentPos; i>destination; i--)
		{
		dist++;
		if(s.jobs[i]!='0') numStops++;
		}
		if(dist==0) return rejectVal; //Same as above comment.
		}
		else if(s.direction=='n') //If elevator is neutral (idle), set appropriate direction and
		{				  //find cost using recursive call.
		double tempCost = 0;
		if(s.currentPos<destination) s.direction = 'u';
		else if(s.currentPos>destination) s.direction = 'd';
		tempCost = cost(s,destination,floorDir);
		s.direction = 'n'; //Reset elevator direction to 'neutral' after cost calculation.
		return tempCost;
		}
		return dist+2.5*numStops; //Place more weight on stops than distance.
	}


//-----------------------------------------------------------------------------------------------
   //Return true if lower floor job (on elevator) than source has the indicated direction.
   private static int hasLessThan(State s, int source, char direction)
   {
      for(int i = source-1; i>-1; i--) if(s.jobs[i]==direction) return i;
      return -1;
   }
//-----------------------------------------------------------------------------------------------
   //Return true if higher floor job (on elevator) than source has the indicated direction.
   private static int hasGreaterThan(State s, int source, char direction)
   {
      for(int i = source+1; i<numFloors; i++) if(s.jobs[i]==direction) return i;
      return -1;
   }
//-----------------------------------------------------------------------------------------------
   public static void setElevator(int elevatorNum, int pos, char[] jobs, char direction)
   {
      states[elevatorNum].currentPos = pos;
      for(int i = 0; i<numFloors; i++) states[elevatorNum].jobs[i] = jobs[i];
      states[elevatorNum].direction = direction;
   }
//-----------------------------------------------------------------------------------------------
   public static void setElevator(int elevatorNum, int pos) {states[elevatorNum].currentPos = pos;}
//-----------------------------------------------------------------------------------------------
   public static void display()
   {
      //for(int i = 0; i<numElevators; i++) System.out.print(states[i].currentPos+"	");
      //System.out.println();
   }
//-----------------------------------------------------------------------------------------------
} // end class Controller
/////////////////////////////////////////////////////////////////////////////////////////////////
