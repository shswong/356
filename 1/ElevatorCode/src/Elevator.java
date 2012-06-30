import java.util.Queue;
import java.util.LinkedList;

public class Elevator {

    private boolean isIdle;
    private int currentFloor;
    private char direction; //should only be 'u' for "up," 'd' for "down"
    private Queue q;	//queue of floor this elevator is going to

    public Elevator () {
        isIdle = true;
        currentFloor = 1;
        q = new LinkedList();
        direction = 'u'; //elevator originally starts at ground floor, therefore should be up
    }

    public int getCurrentFloor () {
        return currentFloor;
    }

    public void setCurrentFloor (int val) {
        this.currentFloor = val;
    }

    public char getDirection () {
        return direction;
    }

    public void setDirection (char val) {
        this.direction = val;
    }

    public boolean getIsIdle () {
        return isIdle;
    }

    public void setIsIdle (boolean val) {
        this.isIdle = val;
    }

    public void enQueue (int floor) {
        q.add(floor);
    }

    public void deQueue () {
        q.remove();
    }

    //call the GUI
    public void openDoor () {
        
    }

    //call the GUI
    public void closeDoor () {
        
    }

}

