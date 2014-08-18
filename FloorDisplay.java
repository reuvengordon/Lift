/**
 * FloorDisplay class
 */
public class FloorDisplay {
    
    //Instance variables
    private static final int ETA_STATIONARY = -1;
    
    private int floorNumber;
    private int liftNumber;
    private int currentLiftFloor;
    private int eta;  //seconds to get to this floor. -1 if stationary
    private String direction;  //direction of the lift at the moment (set by the LC)
    
    /**
     * Constructor
     */
    public FloorDisplay(int floorNumber, int liftNumber, int liftStartFloor) {
        this.floorNumber = floorNumber;
        this.liftNumber = liftNumber;
        //initialise the current floor of the lift
        currentLiftFloor = liftStartFloor;
        //initiliase the ETA as -1
        eta = ETA_STATIONARY;
        direction = "";
    }
    
    /**
     * Allow the LC to update the current floor of this lift
     */
    public void currentFloorOfLift(int currentLiftFloor) {
        this.currentLiftFloor = currentLiftFloor;
    }
    
    /**
     * Allow the LC to update the current direction of this lift
     */
    public void currentDirectionOfLift(String direction) {
        this.direction = direction;
    }
    
    /**
     * Allow the LC to estimate the ETA for the lift to arrive at this floor [s]
     */
    public void eta(int eta) {
        this.eta = eta;
    }
    
    /**
     * Getter: floorNumber
     */
    public int getfloorNumber() {
        return floorNumber;
    }
           
    /**
     * Getter: liftNumber
     */
    public int getliftNumber() {
        return liftNumber;
    }
    
    /**
     * Getter: currentLiftFloor
     */
    public int getcurrentLiftFloor() {
        return currentLiftFloor;
    }
    
    /**
     * Getter: eta
     */
    public int geteta() {
        return eta;
    }
    
    /**
     * Getter: lift direction
     */
    public String getDirection() {
        return direction;
    }
}
