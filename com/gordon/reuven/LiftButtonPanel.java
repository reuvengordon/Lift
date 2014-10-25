package com.gordon.reuven;
/**
 * Lift button panel class
 */
public class LiftButtonPanel {
    
    //Instance variables
    private int numberOfFloors;
    private boolean[] selectedFloors;  //if a floor in the lift has been selected, mark it as true
    
    /**
     * Constructor
     */
    public LiftButtonPanel(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
        selectedFloors = new boolean[numberOfFloors + 1]; //the +1 is to avoid having to subtract 1 from the level arguement as the ground floor = level 1
    }
    
    /**
     * Select a floor.
     */
    public void selectFloor(int floor) {
        
        //Input checking
        checkFloor(floor);
        
        if (selectedFloors[floor]) {
            System.out.printf("floor %d has already been selected\n", floor);
        }
        else selectedFloors[floor] = true;
    }
    
    /**
     * Unselect a floor.
     */
    public void unselectFloor(int floor) {
        
        //Input checking
        checkFloor(floor);
        
        if (!selectedFloors[floor]) {
            System.out.printf("floor %d has already been UNselected\n", floor);
        }
        else selectedFloors[floor] = false;
    }
    
    /**
     * Getter: return the array of floors
     */
    public boolean[] floorsArray() {
        return selectedFloors;
    }
    
    /**
     * Floor checker
     * @param input floor to select/unselect
     */
    public void checkFloor(int floor) {
        
        if (floor <= 0 || floor > numberOfFloors) {
            System.out.printf("floor %d is outside the allowed range\n", floor);
            throw new IllegalArgumentException("floor invalid");
        }
    }
}
