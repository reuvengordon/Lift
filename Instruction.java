/**
 * Instruction class
 * Instructions passed to the LC (or decided by the LC) can be stored as instruction
 * objects
 */
public class Instruction {
    
    //Instance variables
    //private static final int NULL_LIFT = -1;
    private static final String NULL_DIRECTION = "";
    private static final String METHOD_ORIGIN_FROM_LIFT = "methodFromLift";
    private static final String METHOD_ORIGIN_FROM_FLOOR = "methodFromFloor";
    private static final String ORIGIN_FROM_LIFT = "fromLift";
    private static final String ORIGIN_FROM_FLOOR = "fromFloor";
    private static final String ORIGIN_FROM_LC = "fromLC";
    
    private String methodOrigin;       //Keeps track of which method would have been used to issue this instruction
    private String instructionOrigin;  //Keeps track of where the instruction came from
    private int floorNumber;
    private int liftNumber;
    private String direction;   
    private boolean selection;
    
    /** Constructor
      * @param methodOrigin refers to which method would have been used to issue the instruction
      * @param instructionOrigin refers to where the instruction was called from ("fromLift", "fromFloor", "fromLC")
      * @param floorNumber corresponding to this instruction (could be hitting UP/DOWN on a floor or selecting a floor in a lift
      * @param liftNumber is the lift this instruction was called from. If not called from a lift (ie floor), set to null
      * @param direction is the direction chosen when calling the lift from a floor "UP" or "DOWN"
      * @param selection tells whether to select (true) or unselect (false) the event
      */
    public Instruction(String methodOrigin, String instructionOrigin, int floorNumber, int liftNumber, String direction, boolean selection) {
        
        //Input arguement checking
        if (!methodOrigin.equals(METHOD_ORIGIN_FROM_LIFT) && !methodOrigin.equals(METHOD_ORIGIN_FROM_FLOOR)) {
            System.out.printf("Method Origin: %s is invalid\n", methodOrigin);
            throw new IllegalArgumentException("methodOrigin invalid");
        }
        if (!instructionOrigin.equals(ORIGIN_FROM_LIFT) && !instructionOrigin.equals(ORIGIN_FROM_FLOOR) && !instructionOrigin.equals(ORIGIN_FROM_LC)) {
            System.out.printf("Instruction Origin: %s is invalid\n", instructionOrigin);
            throw new IllegalArgumentException("instructionOrigin invalid");
        }
        
        if (!direction.equals("UP") && !direction.equals("DOWN") && !direction.equals(NULL_DIRECTION)) {
            System.out.printf("Instruction Direction: %s is invalid\n", direction);
            throw new IllegalArgumentException("direction invalid");
        }
        
        //Log the instruction origin
        this.methodOrigin = methodOrigin;
        this.instructionOrigin = instructionOrigin;
        this.floorNumber = floorNumber;
        this.liftNumber = liftNumber;
        this.direction = direction;    
        this.selection = selection;
    }
    
    /**
     * Getter: Return which method the instruction came from
     */
    public String getmethodOrigin() {
        return methodOrigin;
    }
    
    /**
     * Getter: Return where the instruction came from
     */
    public String getinstructionOrigin() {
        return instructionOrigin;
    }
    
    /**
     * Getter: Return the floorNumber corresponding to this instruction (could be hitting UP/DOWN on a floor or selecting a floor in a lift
     */
    public int getfloorNumber() {
        return floorNumber;
    }
    
    /**
     * Getter: Return the liftNumber corresponding to this instruction.
     * if the instruction was called from a floor, return null
     */
    public int getliftNumber() {
        return liftNumber;
    }
    
    /**
     * Getter: Return the direction corresponding to this instruction.
     * if the instruction was called from a lift, return null
     */
    public String getdirection() {
        return direction;
    }    
    
    /**
     * Getter: Return the selection corresponding to this instruction.
     */
    public boolean getselection() {
        return selection;
    }    
    
    /**
     * return the instruction as a string 
     */
    public String toString() {
        return "\nmethodOrigin: " + getmethodOrigin() + "\ninstructionOrigin: " + getinstructionOrigin() + "\nfloorNumber: " + 
            getfloorNumber() + "\nliftNumber: " + getliftNumber() + "\ndirection: " + getdirection() + "\nselection: " + getselection();
    }
}
