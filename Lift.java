/**
 * Lift class
 */
public class Lift {
    
    //Instance variables
    private static final String UP = "UP";
    private static final String DOWN = "DOWN";
    private static final String BELL = "\007";
    private static final int GROUNDFLOOR = 1;
    private static final long NANOMULTIPLIER = 1000000000;
    
    private int numberOfFloors;
    private double distanceBetweenFloors;
    private double doorOpenCloseThreshold;  //This is the distance between the lift and the floor for the doors to be allowed to open
    private double velocity;       //[m/s]
    private double buildingHeight;
    private long timeLastCalled;
    private LiftState lState;      //this defines whether the lift is stationary, going up or going down and its position from ground
    private LiftDoor door;
    private LiftMotor motor;
    private LiftButtonPanel liftButtonPanel;

    
    /**
      * Constructor
      * @param numberOfFloors int number of floors in building
      * @param distanceBetweenFloors double distance between floors in m
      * @param velocity double velocity of lift in m/s
      * @param maxDoorOpenDistance double maximum distance for door to open in m
      * @param liftStartFloor int which floor the lift should start from
      * @param doorOpenCloseThreshold double the min distance from a floor for which the doors can open
      * @param doorOpenCloseTime int the time it takes to open and close the lift door
      */
    public Lift(int numberOfFloors, double distanceBetweenFloors, double velocity, double maxDoorOpenDistance, int liftStartFloor, double doorOpenCloseThreshold, int doorOpenCloseTime) {
        
        //input parameter checking
        if (numberOfFloors <= 1) {
            System.out.printf("numberOfFloors %d invalid\n", numberOfFloors);
            throw new IllegalArgumentException("numberOfFloors < 1");
        }
        if (distanceBetweenFloors < 0) {
            System.out.printf("distanceBetweenFloors %g invalid\n", distanceBetweenFloors);
            throw new IllegalArgumentException("distanceBetweenFloors < 0");
        }
        if (velocity < 0) {
            System.out.printf("velocity %g invalid\n", velocity);
            throw new IllegalArgumentException("velocity < 0");
        }
        if (maxDoorOpenDistance < 0) {
            System.out.printf("maxDoorOpenDistance %g invalid\n", maxDoorOpenDistance);
            throw new IllegalArgumentException("maxDoorOpenDistance < 0");
        }
        if (liftStartFloor < 1 || liftStartFloor > numberOfFloors) {
            System.out.printf("liftStartFloor %d invalid\n", liftStartFloor);
            throw new IllegalArgumentException("liftStartFloor < 0 OR liftStartFloor > numberOfFloors");
        }
        if (doorOpenCloseThreshold < 0 || doorOpenCloseThreshold > distanceBetweenFloors) {
            System.out.printf("doorOpenCloseThreshold %g invalid. FYI distanceBetweenFloors = %g\n", doorOpenCloseThreshold, distanceBetweenFloors);
            throw new IllegalArgumentException("doorOpenCloseThreshold < 0 OR doorOpenCloseThreshold > distanceBetweenFloors");
        }
        
        //set the lift params
        this.numberOfFloors = numberOfFloors;
        this.distanceBetweenFloors = distanceBetweenFloors;
        this.velocity = velocity;    
        this.doorOpenCloseThreshold = doorOpenCloseThreshold;
        //the building height: if there are say 5 floors and each floor is 3m apart, the 5th floor is at height 12m and the building height = 15m
        buildingHeight = numberOfFloors*distanceBetweenFloors;
        
        //Build the door
        door = new LiftDoor(maxDoorOpenDistance, doorOpenCloseTime);
        
        //Build the motor
        motor = new LiftMotor();
        
        //Build the lift button panel
        liftButtonPanel = new LiftButtonPanel(numberOfFloors);
        
        //Initialisation
        timeLastCalled = System.nanoTime();       
                   
        //Set the lift state. input arguement is the position from bottom
        lState = new LiftState((liftStartFloor - 1) * distanceBetweenFloors);
    }
    
    /**
     * Simple version of the update state where it doesn't 
     */
    public LiftState updateStateSinceLastCalledSimple() {
        
        int multiplier;  //this is either +1 if lift going up or -1 if lift going down
        
        //find time elapsed since last called, and update the timeLastCalled
        long elapsedTime = System.nanoTime() - timeLastCalled;
        timeLastCalled = System.nanoTime();
        //System.out.printf("time elapsed = %ds\n", elapsedTime/NANOMULTIPLIER);
        //Sanity check that elapsed time is positive
        if (timeLastCalled < 0) {
            System.out.printf("elapsed time <0, setting to 0\n");
            timeLastCalled = 0;
        }
        
        /** udpate the state of the lift based on the time elapsed **/
        //update state of the lift if it was stationary
        if (lState.isStationary()) {
            return lState;
        }
        
        //update multiplier depending on whether going UP or DOWN
        if (lState.isGoingUp()) multiplier = 1;                    
        else                    multiplier = -1;
                
        //udpate the position and return the state
        double distance = velocity*elapsedTime/NANOMULTIPLIER;        
        lState.setPositionFromBottom(lState.positionFromBottom() + multiplier*distance);
        
        //update the distance travelled
        lState.addDistanceTravelled(distance);
        return lState;
    }
    
    /**
     * Update status of lift based on its actions since last called
     * TO DO! This method is stopping the lift based on its position. shouldn't this be done by the Lift Controller?
     */
    public LiftState updateStateSinceLastCalled() {
        
        //find time elapsed since last called, and update the timeLastCalled
        long elapsedTime = System.nanoTime() - timeLastCalled;
        timeLastCalled = System.nanoTime();
        System.out.printf("time elapsed = %ds\n", elapsedTime/NANOMULTIPLIER);
        //Sanity check that elapsed time is positive
        if (timeLastCalled < 0) {
            System.out.printf("elapsed time <0, setting to 0\n");
            timeLastCalled = 0;
        }
        
        /** udpate the state of the lift based on the time elapsed **/
        //update state of the lift if it was stationary
        if (lState.isStationary()) {
            return lState;
        }
        
        //update state of the lift if it was going UP
        if (lState.isGoingUp()) {
            lState.setPositionFromBottom(lState.positionFromBottom() + velocity*elapsedTime/NANOMULTIPLIER);
            //if the lift hits the top floor, stop it
            if (lState.positionFromBottom() >=  buildingHeight) {
                System.out.printf("lift position >= building height. Stopping Lift!\n");
                lState.setPositionFromBottom(buildingHeight);
                //stop the lift. But may want to do this from the LC
                stop();
            }
            return lState;
        }
        
        //update state of the lift if it was going DOWN
        if (lState.isGoingDown()) {
            lState.setPositionFromBottom(lState.positionFromBottom() - velocity*elapsedTime/NANOMULTIPLIER);
            //if the lift hits the bottom floor, stop it
            if (lState.positionFromBottom() <=  0) {
                System.out.printf("lift position <= ground floor. Stopping Lift!\n");
                lState.setPositionFromBottom(0);
                //stop the lift. But may want to do this from the LC
                stop();
            }
            return lState;
        } 
        return lState;
    }
    
    /**
     * Go up
     * @return true if the lift can be made to go up (if it was stationary or already going up)
     * @return false if the motor is currently going down. we enforce that you cannot make it go up in this state
     */
    public boolean goUp() {

        //check for inconsistencies between lift state and motor
        if (!motorAndLiftStateCheck(lState, motor)) {
            throw new IllegalArgumentException("motor and lift state are inconsistent");
        }
        
        //If the lift is stationary, start the motor in the UP direction
        if (lState.isStationary()) {
            motor.motorUp();
            lState.setGoingUp();
            return true;
        }
        
        //if the lift is already going up, do nothing
        if (lState.isGoingUp()) {
            return true;
        }
        
        //if the lift is already going down, do nothing BUT return false (as cannot change the lift direction)
        if (lState.isGoingDown()) {
            System.out.printf("Trying to make the lift go up when it's going down. Fail!\n");            
        }        
        return false;
    }
    
    /**
     * Go down
     * @return true if the lift can be made to go up (if it was stationary or already going up)
     * @return false if the motor is currently going down. we enforce that you cannot make it go up in this state
     */
    public boolean goDown() {

        //check for inconsistencies between lift state and motor
        if (!motorAndLiftStateCheck(lState, motor)) {
            throw new IllegalArgumentException("motor and lift state are inconsistent");
        }
        
        //If the lift is stationary, start the motor in the DOWN direction
        if (lState.isStationary()) {
            motor.motorDown();
            lState.setGoingDown();
            return true;
        }
        
        //if the lift is already going down, do nothing
        if (lState.isGoingDown()) {
            return true;
        }
        
        //if the lift is already going up, do nothing BUT return false (as cannot change the lift direction)
        if (lState.isGoingUp()) {
            System.out.printf("Trying to make the lift go down when it's going up. Fail!\n");    
        }   
        return false;
    }
    
    /**
     * stop the lift.
     * turn off the motor, set the state to stationary
     */
    public void stop() {
        motor.stopMotor();
        lState.setStationary();
    }
    
    /**
     * Open the door
     * Can only open the door if the lift is stationary AND the lift is at a floor
     * @return true if door can be opened
     */
    public boolean openDoor() {
        if (lState.isStationary() && canOpenCloseDoorAtFloor(getCurrentFloor())) {
            door.open();
            return true;
        }
        else {
            System.out.printf("cannot open door!!\n");
            System.out.printf("lState.isStationary() = %b, getCurrentFloor() = %d, canOpenCloseDoorAtFloor(getCurrentFloor()) = %b\n", lState.isStationary(), getCurrentFloor(), canOpenCloseDoorAtFloor(getCurrentFloor()));
            return false;
        }
    }
    
    /**
     * Close the door
     * Can only close the door if the lift is stationary
     * @return true if door can be closed
     */
    public boolean closeDoor() {
        if (lState.isStationary()) {
            door.close();
            return true;
        }
        else {
            System.out.printf("cannot close door!!\n");
            return false;
        }
    }
   
    /** 
     * LC to select a floor to go to from the lift
     * @param floor that was just selected
     */
    public void selectFloor(int floor) {
        liftButtonPanel.selectFloor(floor);
    }
    
    /** 
     * LC to UNselect a floor to go to from the lift
     * @param floor that was just unselected
     */
    public void unselectFloor(int floor) {
        liftButtonPanel.unselectFloor(floor);
    }
    
    /**
     * Get the array of selections for all the floors
     */
    public boolean[] floorsArray() {
        return liftButtonPanel.floorsArray();
    }
    
    /**
     * ping the bell
     */
    public void pingBell() {
        // ASCII bell
        System.out.print(BELL);
        System.out.flush();        
    }
    
    /**
     * helper method to consistency check the LiftState and the Motor state
     */
    private static boolean motorAndLiftStateCheck(LiftState l, LiftMotor m) {
        //if stationary, the motor should be off
        if (l.isStationary()) {
            return !m.getMotorState();
        }
        
        //if going UP, the motor should be going up
        if (l.isGoingUp()) {
            return m.getMotorDirection() == UP;
        }
        
        //if going DOWN, the motor should be going down
        if (l.isGoingDown()) {
            return m.getMotorDirection() == DOWN;
        }
        return false;
    }
    
    /**
     * Getter: 
     * @return the state of the lift
     */
    public LiftState getState() {
        return lState;
    }
    
    /**
     * Getter: 
     * @return is the door open (true) or closed (false)
     */
    public boolean getDoorOpenOrClosed() {
        if (door.isOpen()) return true;
        else return false;        
    }
    
    /**
     * Getter: 
     * @return the position of the lift above the ground
     */
    public double getPosition() {
        return lState.positionFromBottom();
    }
    
    /**
     * @return current floor (useful for the display in the lift)
     */
    public int getCurrentFloor() {
        return GROUNDFLOOR + (int) (getPosition()/distanceBetweenFloors);
    }
    
    /**
     * If the position of the lift from a given floor is below a threshold, the lift can stop and open/close it's doors at that floor
     * @param the floor to check
     * @return the distance of the lift above the current floor's opening point     
     */
    public double getPositionFromFloor(int thisFloor) {
        floorCheck(thisFloor);
        
        return getPosition() - (thisFloor - 1) * distanceBetweenFloors;
    }
    
    /**
     * Can the door be open/closed at a given floor right now? Only allow the door to be opened if the lift is at this floor (ie getCurrentFloor() == thisFloor and 
     * the distance of the lift from the floor is < doorOpenCloseThreshold
     * @param the floor to check if the door can be open/closed
     * @return boolean true if the door can be open/closed, false if not
     */
    public boolean canOpenCloseDoorAtFloor(int thisFloor) {
        floorCheck(thisFloor);                
        
        return (getCurrentFloor() == thisFloor && Math.abs(getPositionFromFloor(thisFloor)) < doorOpenCloseThreshold);        
    }
    
    /**
     * @return the next floor the lift will get to if it is currently moving.
     * If not moving, return current floor
     * cannot return a floor < 0 or > numberOfFloors
     */
    public int getNextFloor(int liftNumber) {
        //local variables
        int nextFloorIncrement = 0;
        int result;
        
        if (lState.isStationary()) {
            nextFloorIncrement = 0;
        }
        else if (lState.isGoingUp()) {
            nextFloorIncrement = 1; 
        }
        else nextFloorIncrement = -1;
                
        result = getCurrentFloor() + nextFloorIncrement;
        
        //sanity check- if at the top/bottom floor, simply return this floor
        if (result < 0 || result > numberOfFloors) {
            System.out.printf("\nLift %d: next floor of %d is invalid. current floor = %d\n", liftNumber, result, getCurrentFloor());
            return getCurrentFloor();
        }
        return result;
    }
    
    /**
     * floor number checker
     */
    public void floorCheck(int thisFloor) {
        if (thisFloor <= 0 || thisFloor > numberOfFloors) {
            System.out.printf("thisFloor %d is outside allowed range\n", thisFloor);
            throw new IllegalArgumentException("thisFloor is invalid");
        }
    }
}
