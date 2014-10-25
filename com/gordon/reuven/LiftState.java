package com.gordon.reuven;
/**
 * Class to describe the state of the lift at any time
 */
public class LiftState {
    
    //Instance variables
    private static final String UP = "UP";
    private static final String DOWN = "DOWN";
    private static final String STATIONARY = "STAT";
    
    private boolean stationary;
    private boolean goingUp;
    private boolean goingDown;
    private double positionFromBottom;
    private double distanceTravelled;  //keep track of the total distance travelled by this lift
    
    /**
     * Constructor
     * @param positionFromBottom double the distance of the lift from the ground in m 
     */
    public LiftState(double positionFromBottom) {
        stationary = true;
        goingUp = false;
        goingDown = false;
        this.positionFromBottom = positionFromBottom;       
        distanceTravelled = 0;
    }
    
    /**
     * Setters
     */
    public void setStationary() {
        stationary = true;
        goingUp = false;
        goingDown = false;
    }
    
    public void setGoingUp() {
        stationary = false;
        goingUp = true;
        goingDown = false;
    }
    
    public void setGoingDown() {
        stationary = false;
        goingUp = false;
        goingDown = true;
    }
    
    public void setPositionFromBottom(double positionFromBottom) {
        this.positionFromBottom = positionFromBottom;
    }
    
    public void addDistanceTravelled(double distance) {
        distanceTravelled += distance;
    }
    
    /**
     * Getters
     */
    public boolean isStationary() {
        return stationary;
    }
    
    public boolean isGoingUp() {
        return goingUp;
    }
    
    public boolean isGoingDown() {
        return goingDown;
    }  
    public double positionFromBottom() {
        return positionFromBottom;
    }
    public double distanceTravelled() {
        return distanceTravelled;
    }
    
    public String direction() {
        if (isStationary()) return STATIONARY;        
        if (isGoingUp())    return UP;
        else                return DOWN;
    }
}
