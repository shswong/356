import java.util.ArrayList;
import java.util.Random;

public class ControlUnit {

    private int numOfFloors;
    private int numOfElevators;
    private ArrayList<Elevator> listOfElevators = new ArrayList();
    private ElevatorControlPanel ECP = new ElevatorControlPanel();
    private int[] allCurrentFloor; //used in getAllCurrentFloor()
    private char[] allCurrentDirection; //used in getAllCurrentDirection()
    private int[] allDistance; //used in getAllDistance

    public ControlUnit () {
    }

    public ControlUnit (int floors, int elevators)
    {
        numOfFloors = floors;
        numOfElevators = elevators;
        allCurrentFloor = new int[numOfElevators];
        allCurrentDirection = new char[numOfElevators];
        allDistance = new int[numOfElevators];
    }

    public ArrayList<Elevator> getListOfElevators () {
        return listOfElevators;
    }

    public int getNumOfElevators () {
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

    private boolean getAllIsIdle () {
        // scans through all elevators and
        // returns true if all the elevators are idle, false if
        // at least one elevator is not idle
        boolean b = false;
        for (int i = 0; i < numOfElevators; i++)
        {
            if (listOfElevators.get(i).getIsIdle() == true)
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

    private void getAllCurrentFloor () {
        // using class array allCurrentFloor
        // stores current floors for all elevators at a
        // single point in time for helping getClosestElevator
        for (int i = 0; i < numOfElevators; i++)
        {
            allCurrentFloor[i] = listOfElevators.get(i).getCurrentFloor();
        }
    }

    private void getAllCurrentDirection () {
        //using class array allCurrentDirection
        // stores current directions for all elevators at a
        // single point in time for helping chooseElevator
        for (int i = 0; i < numOfElevators; i++)
        {
            allCurrentDirection[i] = listOfElevators.get(i).getDirection();
        }
    }

    private void getAllDistance (int floor) {
        // using class array allDistance
        // stores current distances for all elevators
        // from the paramter floor at a single point in time
        getAllCurrentFloor();
        for (int i = 0; i < numOfElevators; i++)
        {
            allDistance[i] = Math.abs(floor - allCurrentFloor[i]);
        }
    }

    private Elevator getShortestElevator (Elevator e, int floor, int min) {
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

        if (e.getIsIdle() && e.getCurrentFloor() == floor)
        {
            return e;
        }
        else if (!(e.getIsIdle()))
        {
            if ((e.getCurrentFloor() < floor && e.getDirection() == 'u') || (e.getCurrentFloor() > floor && e.getDirection() == 'd'))
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

    public Elevator elevatorAlgorithm (int floor) {
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


            /*
            if (listOfElevators.get(i).getIsIdle() && listOfElevators.get(i).getCurrentFloor() == floor)
            {
                return listOfElevators.get(i);
            }
            else if (!(listOfElevators.get(i).getIsIdle()))
            {
                if ((allCurrentFloor[i] < floor && allCurrentDirection[i] == 'u') || (allCurrentFloor[i] > floor && allCurrentDirection[i] == 'd'))
                {
                    return listOfElevators.get(i);
                }
                else
                {
                    allDistance[i] = 999; // eg. unusable
                }
            }*/
    }

    public Elevator randomElevator (int floor) {
        int counter = 0;//# of elevators on floor we are looking for
        int current = 0;//if theres only one elevator on that floor, this is to know here it is in the arraylist
        int elevatorsOnFloor[] = new int[getNumOfElevators()];
        //for loop goes through all the elevators and checks if they are on the floor that we pass through the parameter
        for (int i=1; i <= getNumOfElevators(); i++)
        {
            //for loop starts at 1
            //if an elevator is on floor, add it to elevatorsOnFloor array and set current to its index
            if (listOfElevators.get(i).getCurrentFloor() == floor)
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

}

/*          
    public Elevator getClosestElevator (int floor) {
        int[] calculations = new int[numOfElevators];
        // temporary array to arrange calculations for
        // finding closest
        getAllCurrentFloor();
        for (int i = 0; i < numOfElevators; i++)
        {
            // finds which floor is closest to the parameter floor
            // will also return randomElevator if all elevators idle
            calculations[i] = Math.abs(floor - allCurrentFloor[i]);
            if (getAllIsIdle() && i == numOfElevators-1)
            {
                return randomElevator(floor);
            }
        }
        int min = calculations[0];
        for (int i = 1; i < numOfElevators; i++)
        {
            // finds which is the closest elevator from the
            // floor parameter
            if (min > calculations[i])
            {
                min = calculations[i];
            }
        }
        return listOfElevators.get(min);
    }

    private Elevator chooseElevator (int floor) {
        //if elevator is not idle but going towards the floor, use that elevator
        //if elevator is idle, get closest elevator (if more than one to choose from, get random
        boolean check = false;
        //if all elevators are idle, get a random elevator from floor
        if (getAllIsIdle())
        {
            return randomElevator(floor);
        }
        else
        {
            //if there is an elevator idle on that floor
            for (int i = 0; i < numOfElevators; i++)
            {
                //if and elevator is idle and the floor the elevator is currently on the floor we are looking for, return a random elevator from floor
                if (listOfElevators.get(i).getIsIdle() && listOfElevators.get(i).getCurrentFloor() == floor)
                {
                    return randomElevator(floor);
                }
            }
        }
        //if above does not return an elevator
        if (check == false)
        {
            getClosestElevator(floor);
        }
        return null;
    }
 */