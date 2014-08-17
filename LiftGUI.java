import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.*;
//import java.awt.FlowLayout;
import java.util.ArrayList;
import javax.swing.JToggleButton;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class LiftGUI extends JFrame { 
    
    //Instance variables 
    private static final String UP = "UP";
    private static final String DOWN = "DOWN";
    private FloorButton[][] buttonsFloor; 
    private LiftButton[][] buttonsLift;
    private LiftController lc;
    private int numberOfFloors;
    private int numberOfLifts;
        
    public LiftGUI(int numberOfFloors, int numberOfLifts, double distanceBetweenFloors, double velocity, double maxDoorOpenDistance, int liftStartFloor, double doorOpenCloseThreshold, int doorOpenCloseTime, int matchingAlgorithm, Queue<Instruction> instructionLog) {
    //public LiftGUI(LiftController lc, int numberOfFloors, int numberOfLifts, double distanceBetweenFloors, double velocity, double maxDoorOpenDistance, int liftStartFloor, double doorOpenCloseThreshold, int doorOpenCloseTime, int matchingAlgorithm, Queue<Instruction> instructionLog) {
        
        super("LiftGUI");
        
        //Build the lift controller (which builds the lifts and floors)
        lc = new LiftController(numberOfFloors, numberOfLifts, distanceBetweenFloors, velocity, maxDoorOpenDistance, liftStartFloor, doorOpenCloseThreshold, doorOpenCloseTime, matchingAlgorithm);
        //this.lc = lc;
        this.numberOfFloors = numberOfFloors;
        this.numberOfLifts = numberOfLifts;
        buttonsFloor = new FloorButton[2][numberOfFloors + 1];
        buttonsLift = new LiftButton[numberOfLifts + 1][numberOfFloors + 1];
        
        //Build the GUI        
        JPanel floorPanel = new JPanel();
        JPanel liftPanel = new JPanel();
        JPanel holdAll = new JPanel();
        Border paneEdge = BorderFactory.createEmptyBorder(10,10,10,10);
        TitledBorder titleFloor, titleLift;
    
        setSize(600, 600);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        //Setup the Floor Buttons panel
        floorPanel.setLayout(new GridLayout(numberOfFloors, 2));
        floorPanel.setBorder(paneEdge);
        floorPanel.setBackground(Color.YELLOW);       
        titleFloor = BorderFactory.createTitledBorder("Floor Buttons");
        floorPanel.setBorder(titleFloor);

        
        //Setup the Lift Buttons panel
        liftPanel.setLayout(new GridLayout(numberOfFloors, numberOfLifts));
        liftPanel.setBorder(paneEdge);
        liftPanel.setBackground(Color.GREEN);
        titleLift = BorderFactory.createTitledBorder("Lift Buttons");
        liftPanel.setBorder(titleLift);
        
        //Setup the hold all panel
        holdAll.add(floorPanel);
        holdAll.add(liftPanel);
        holdAll.setLayout(new FlowLayout());
        
        //Add buttons to the floorPanel
        for (int i = 1; i <= numberOfFloors; i++) {
            int floor = numberOfFloors - i + 1;

            //Add the buttons to the floor panel grid
            buttonsFloor[0][i] = new FloorButton(floor, UP);
            buttonsFloor[1][i] = new FloorButton(floor, DOWN);
            
            floorPanel.add(buttonsFloor[0][i]);
            floorPanel.add(buttonsFloor[1][i]);
            
            //Add the buttons to the lift Panel grid
            for (int j = 1; j <= numberOfLifts; j++) {
                buttonsLift[j][i] = new LiftButton(j, floor);
                liftPanel.add(buttonsLift[j][i]);
            }
        }
        this.add(holdAll);
        setVisible(true);
        
        //Run the Lift controller
        lc.StartLiftController(instructionLog);
    }
    
    /**
     * Allow the LC to set whether a floor button should be UNselected
     */
    public void unSelectFloorButton(int floor, String direction) {
        //work out what x should be in buttonsFloor[][x]
        int x = numberOfFloors - floor + 1;
        int directionInt;
        if(direction.equals(UP)) {
            directionInt = 0;
        }
        else directionInt = 1;
        
        buttonsFloor[directionInt][x].select(false);
    }
    
    /**
     * Inner class to define how the Floor Buttons work
     */
    private class FloorButton extends JToggleButton implements ActionListener {
        
        //Instance variables
        private int floor;
        private String direction;
        private boolean pressed;
        
        /**
         * Constructor
         * @param floor int the current floor
         * @param direction String the direction you want to go
         */
        public FloorButton(int floor, String direction) {
            super(floor + "_" + direction);
            
            this.floor = floor;
            this.direction = direction;
            
            this.addActionListener(this);
        }
        
        public int getFloor() {
            return floor;
        }
        
        public String getDirection() {
            return direction;
        }
        
        /**
         * set whether the button should be selected or not
         * @param choice true == selected, false == unselected
         */
        public void select(boolean choice) {
            setSelected(choice);
        }    
        
        /**
         * Implement the actionListener interface
         */
        public void actionPerformed(ActionEvent e) {
            
            //If the button was unselected, select it
            if(isSelected()) {
                System.out.printf("\nFloor button selected\nFloor: %d, Direction: %s\n", getFloor(), getDirection());
                //Issue an instruction to the lift controller
                lc.executeInstruction(new Instruction("methodFromFloor", "fromFloor", getFloor(), -1, getDirection(), true));
            }
            else {
                System.out.printf("\nFloor button UNselected\nFloor: %d, Direction: %s\n", getFloor(), getDirection());
                //Issue an instruction to the lift controller
                lc.executeInstruction(new Instruction("methodFromFloor", "fromFloor", getFloor(), -1, getDirection(), false));
            }        
        }    
    }
    
    /**
     * Inner class to define how the Lift Buttons work
     */
    private class LiftButton extends JToggleButton implements ActionListener {
        
        //Instance variables
        private int floor;
        private int lift;
        
        /**
         * Constructor
         * @param floor int the current floor
         * @param direction String the direction you want to go
         */
        public LiftButton(int lift, int floor) {
            super("lift " + lift + "_floor " + floor);
            
            this.floor = floor;
            this.lift = lift;
            
            this.addActionListener(this);
        }
        
        public int getFloor() {
            return floor;
        }
        
        public int getLift() {
            return lift;
        }
        
        /**
         * set whether the button should be selected or not
         * @param choice true == selected, false == unselected
         */
        public void select(boolean choice) {
            setSelected(choice);
        }    
        
        /**
         * Implement the actionListener interface
         */
        public void actionPerformed(ActionEvent e) {
            
            //If the button was unselected, select it
            if(isSelected()) {
                System.out.printf("\nLift button selected\nLift: %d, Floor: %d\n", getLift(), getFloor());
                //Issue an instruction to the lift controller
                lc.executeInstruction(new Instruction("methodFromLift", "fromLift", getFloor(), getLift(), "", true));
            }
            else {
                System.out.printf("\nLift button UNselected\nLift: %d, Floor: %d\n", getLift(), getFloor());
                //Issue an instruction to the lift controller
                lc.executeInstruction(new Instruction("methodFromLift", "fromLift", getFloor(), getLift(), "", false));
            }        
        }    
    }
}
