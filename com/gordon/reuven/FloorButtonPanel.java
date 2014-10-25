package com.gordon.reuven;
/**
 * Floor Button Panel class
 * There is an up and down button on each floor (down on 1st and up on Top floor shouldn't work)
 * Buttons can be toggled on/off and call the lift
 * Button should have a display to indicate a button is pressed
 */
public class FloorButtonPanel {
    
    //Instance variables
    private boolean selectedUp;
    private boolean selectedDown;
    private int floorNumber;
    private int numberOfFloors;
    
    /**
     * Constructor
     * @param floorNumber int cannot go down on the 1st floor or up on the top floor
     * @param numberOfFloors int tells us what the top floor number is
     */
    public FloorButtonPanel(int floorNumber, int numberOfFloors) {
        selectedUp = false;
        selectedDown = false;      
        this.floorNumber = floorNumber;
        this.numberOfFloors = numberOfFloors;
    }
    
    /**
     * Select the lift to go up
     * @return true if its not yet been selectedUp, else return false
     */
    public boolean selectUp() {
        if (floorNumber == numberOfFloors) {
            System.out.printf("On the top floor so can't selectUp!\n");
            return false;
        }
        else if (selectedUp) {
            System.out.printf("Already selected button panel to go up\n");
            return false;
        }
        else {
            selectedUp = true;
            return true;
        }
    }
    
    /**
     * Select the lift to go down
     * @return true if its not yet been selectedDown, else return false
     */
    public boolean selectDown() {
        if (floorNumber == 1) {
            System.out.printf("On the bottom floor so can't selectDown!\n");
            return false;
        }
        else if (selectedDown) {
            System.out.printf("Already selected button panel to go down\n");
            return false;
        }
        else {
            selectedDown = true;
            return true;
        }
    }
    
    /**
     * once the lift has arrived at the floor and is going up, unselect up
     */
    public void unselectUp() {
        selectedUp = false;
    }
    
    /**
     * once the lift has arrived at the floor and is going down, unselect down
     */
    public void unselectDown() {
        selectedDown = false;
    }
    
    /**
     * Getter: going up?
     * @return TRUE if the panel is already selected to go UP, else FALSE
     */
    public boolean isSelectUp() {
        return selectedUp;
    }
    
    /**
     * Getter: going down?
     * @return TRUE if the panel is already selected to go DOWN, else FALSE
     */
    public boolean isSelectDown() {
        return selectedDown;
    }
}
