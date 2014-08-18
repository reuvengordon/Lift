/**
 * Lift Door class
 */
public class LiftDoor {
    
    //Instance variables
    private int doorOpenCloseTime; //set the door open/close time to be Xms
    private double maxWidth;
    private boolean isOpen;        //true -> open, false -> closed
    private boolean isOpening;
    private boolean isClosing;
    private double openAmount;     //amount the door is open (0 == closed)    
    
    /**
     * Constructor
     * @param maxWidth double maximum width the door can open
     */
    public LiftDoor(double maxWidth, int doorOpenCloseTime) {
        this.maxWidth = maxWidth;
        isOpen = false;   //initialise the door to be closed
        isOpening = false;
        isClosing = false;
        this.doorOpenCloseTime = doorOpenCloseTime;
        openAmount = 0;
    }
    
    /**
     * Open the door
     * Initially- Ignore the door opening and closing.  Just make it be open or closed instantaneously
     */
    public void open() {
        isOpen = true;        
        openAmount = maxWidth;
        //Have a delay to make the door open/close be more realistic
        liftSleep(doorOpenCloseTime);
        System.out.printf("Door is now open\n");
    }
    
    /**
     * Close the door
     * Initially- Ignore the door opening and closing.  Just make it be open or closed instantaneously
     */
    public void close() {
        isOpen = false;
        openAmount = 0;
        //Have a delay to make the door open/close be more realistic
        liftSleep(doorOpenCloseTime);
        System.out.printf("Door is now closed\n");
    }
    
    /**
     * Getters
     */
    public double doorOpenPosition() {
        return openAmount;
    }
    
    //Don't need an isClosed() method as if the door isn't open, it's closed (under the instantaneous door movement assumption)
    public boolean isOpen() {
        return isOpen;
    }
    public boolean isOpening() {
        return isOpening;
    }
    public boolean isClosing() {
        return isClosing;
    }   
    
    public static void liftSleep(int sleeptime) {
        try {
            Thread.sleep(sleeptime);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
