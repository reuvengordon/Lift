/**
 * Lift motor class
 */
public class LiftMotor {
    
    //Instance variables
    private boolean motorState;     //TRUE -> in motion, FALSE -> stopped
    private String motorDirection;  //"UP" or "DOWN"
    
    /**
     * Constructor
     */
    public LiftMotor() {
        motorState = false;
        motorDirection = null;
    }
    
    /**
     * Setters
     */
    public void stopMotor() {
        motorState = false;
        motorDirection = null;
    }
    
    public void motorUp() {
        motorState = true;
        motorDirection = "UP";
    }
    
    public void motorDown() {
        motorState = true;
        motorDirection = "DOWN";
    }
    
    /**
     * Getters
     */
    public boolean getMotorState() {
        return motorState;
    }
    
    public String getMotorDirection() {
        return motorDirection;
    }
}
