/**
 * Floor class
 * Creates a list of information displays for each floor (for each lift)
 * Creates a button panel for each floor
 * There's not much to this class, it's really just a container for the floorDisplays array and the button panel
 */
public class Floor {
    
    //Instance variables
    private FloorDisplay[] floorDisplays;
    private FloorButtonPanel panel;
    private int floorNumber;
    
    /**
     * Constructor
     * builds the information displays for each lift and a button panel for the floor
     * @param floorNumber int the floor number of this floor
     * @param numberOfFloors int the number of floors in the building
     * @param numberOfLifts int the number of lifts in the building
     */
    public Floor(int floorNumber, int numberOfFloors, int numberOfLifts, int liftStartFloor) {
        
        //Input parameter validation
        if (floorNumber <= 0 || floorNumber > numberOfFloors) {
            System.out.printf("floornumber = %d, numberOfFloors = %d\n", floorNumber, numberOfFloors);
            throw new IllegalArgumentException("floorNumber invalid");
        }
        if (numberOfFloors <= 0) {
            System.out.printf("numberOfFloors = %d\n", numberOfFloors);
            throw new IllegalArgumentException("numberOfFloors invalid");
        }
        if (numberOfLifts <= 0) {
            System.out.printf("numberOfLifts = %d\n", numberOfLifts);
            throw new IllegalArgumentException("numberOfLifts invalid");
        }
        
        //Initialise instance variables
        this.floorNumber = floorNumber;
        
        //Create an array of floor displays (one for each lift) on this floor
        floorDisplays = new FloorDisplay[numberOfLifts + 1];
        for (int i = 1; i <= numberOfLifts; i++) {
            floorDisplays[i] = new FloorDisplay(floorNumber, i, liftStartFloor);
        }
        
        //Create a button panel for each floor
        panel = new FloorButtonPanel(floorNumber, numberOfFloors);        
    }
    
    /**
     * Allow the lift controller to select the button to go up
     */
    public void selectUp() {
        panel.selectUp();
    }
    
    /**
     * Allow the lift controller to select the button to go down
     */
    public void selectDown() {
        panel.selectDown();
    }
    
    /**
     * Allow the lift controller to UNselect the button to go up
     */
    public void unselectUp() {
        panel.unselectUp();
    }
    
    /**
     * Allow the lift controller to UNselect the button to go down
     */
    public void unselectDown() {
        panel.unselectDown();
    }
    
    /**
     * Update the display for a given lift
     * The display will show the current floor that the lift is on (stationary) or heading to (moving)
     * @param liftNumber the given lift
     * @param currentFloorOfLift where the lift currently is or the next floor it will move past (from Lift.getNextFloor())
     */
    public void setFloorDisplayLiftFloor(int liftNumber, int currentFloorOfLift) {
        floorDisplays[liftNumber].currentFloorOfLift(currentFloorOfLift);
    }
    
    /**
     * Update the display for a given lift
     * The display will show the ETA for the lift to get to this floor
     * @param liftNumber the given lift
     * @param eta is the eta for the lift to get to this floor
     */
    public void setFloorDisplayLiftETA(int liftNumber, int eta) {
        floorDisplays[liftNumber].eta(eta);
    }
    
    /**
     * Update the display for a given lift
     * The display will show the current direction of the lift
     * @param liftNumber the given lift
     * @param direction of the given lift
     */
    public void setFloorDisplayLiftDirection(int liftNumber, String direction) {
        floorDisplays[liftNumber].currentDirectionOfLift(direction);
    }
    
    /**
     * Getter: returns the floornumber of this floor
     */
    public int floorNumber() {
        return floorNumber;
    }
    
    /**
     * Getter: return TRUE if the panel is already selected to go UP, else FALSE
     */
    public boolean isSelectUp() {
        return panel.isSelectUp();
    }
    
    /**
     * Getter: return TRUE if the panel is already selected to go DOWN, else FALSE
     */
    public boolean isSelectDown() {
        return panel.isSelectDown();
    }
    
    /**
     * Return which floor a given lift is currently at
     * @param liftNumber
     * @return current lift floor
     */
    public int getFloorDisplayLiftFloor(int liftNumber) {
        return floorDisplays[liftNumber].getcurrentLiftFloor();
    }
    
    /**
     * Return the ETA for a given lift to get to this floor
     * @param liftNumber
     * @return eta in [s]
     */
    public int getFloorDisplayLiftETA(int liftNumber) {
        return floorDisplays[liftNumber].geteta();
    }
    
    /**
     * Return the direction for a given lift at the moment
     * @param liftNumber
     * @return direction string
     */
    public String getFloorDisplayLiftDirection(int liftNumber) {
        return floorDisplays[liftNumber].getDirection();
    }
    
    /**
     * Return the floor display object for a given lift
     * @param liftNumber
     * @return FloorDisplay for that lift
     */
    public FloorDisplay getFloorDisplay(int liftNumber) {
        return floorDisplays[liftNumber];
    }
}
