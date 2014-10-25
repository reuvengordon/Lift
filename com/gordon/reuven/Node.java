package com.gordon.reuven;
/**
 * Private class Node
 * To tell a lift where to go, we will maintain a queue of nodes for that lift.
 * The node tells the lift the floor it has to go to, but also where the instruction came from (via selections arrays)
 */
public class Node {
    
    private int floor;
    private boolean fromLift;
    private boolean fromFloorUP;
    private boolean fromFloorDOWN;
    
    public Node(int floor, boolean fromLift, boolean fromFloorUP, boolean fromFloorDOWN) {
        this.floor = floor;
        this.fromLift = fromLift;
        this.fromFloorUP = fromFloorUP;
        this.fromFloorDOWN = fromFloorDOWN;
    }
    
    /**
     * Getters
     */
    public int getFloor() {
        return floor;
    }
    public boolean getFromLift() {
        return fromLift;
    }
    public boolean getfromFloorUP() {
        return fromFloorUP;
    }
    public boolean getfromFloorDOWN() {
        return fromFloorDOWN;
    }
}
