/**
 * Lift Controller class
 */
import java.util.ArrayList;
import java.util.List;

public class LiftController implements Subject {
    
    //Instance variables   
    private static final int NULL_LIFT = -1;
    private static final String NULL_DIRECTION = "";
    private static final String METHOD_ORIGIN_FROM_LIFT = "methodFromLift";
    private static final String METHOD_ORIGIN_FROM_FLOOR = "methodFromFloor";
    private static final String ORIGIN_FROM_LIFT = "fromLift";
    private static final String ORIGIN_FROM_FLOOR = "fromFloor";
    private static final String ORIGIN_FROM_LC = "fromLC";
    private static final String SELECT_UP = "UP";
    private static final String SELECT_DOWN = "DOWN";
    //private static final int MAX_ITERATIONS = 10000000;   //This is just for debugging- Controls how many times the LC loop can execute
   
    private List<Observer> observers;  //This is the list of observer objects from the GUI
    private final Object MUTEX= new Object();
    private boolean changed;    //keep track of the change in the state of LC and used in notifying observers
    
    private int numberOfFloors;
    private int numberOfLifts;
    private int matchingAlgorithm;
    private double distanceBetweenFloors;
    private double buildingHeight;
    private Floor[] floors;
    private Lift[] lifts;
    private boolean[][] selectionsFromFloor;      //This array has numberOfFloors rows x 2 cols (UP/DOWN). It tells us whether a floor has selected the UP/DOWN button. true == selected, false == unselected
    private boolean[][] selectionsFromLift;       //This array has numberOfFloors rows x numberOfLifts cols.  It tells us whether a lift has selected a floor. true == selected, false == unselected
    private Queue<Instruction> instructionLog;    //This will be a queue of all instructions passed to the Lift Controller. Useful for debugging.
    private Queue<Node>[] liftNodeQueue;          //An array which stores a queue for each lift. The queue holds nodes which tell the lift where to go. This queue is the result from mapping the selection arrays to a set of floors for the lift to visit
    private int[][] currentAndNextFloorsForLifts; //This array has 2 rows x numberOfLifts cols. It tells us which floor a lift is currently at (row 1) and what is the next floor for it to visit (row 2)    
    
    /**
     * Private class Node
     * To tell a lift where to go, we will maintain a queue of nodes for that lift.
     * The node tells the lift the floor it has to go to, but also where the instruction came from (via selections arrays)
     */
    private static class Node {
        
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
    
    /**
     * Constructor: Build the LC, floors and lifts
     */
    public LiftController(int numberOfFloors, int numberOfLifts, double distanceBetweenFloors, double velocity, double maxDoorOpenDistance, int liftStartFloor, double doorOpenCloseThreshold, int doorOpenCloseTime, int matchingAlgorithm) {
        
        int i;
        this.observers = new ArrayList<Observer>();
        this.numberOfFloors = numberOfFloors;
        this.numberOfLifts = numberOfLifts;
        this.distanceBetweenFloors = distanceBetweenFloors;
        this.matchingAlgorithm = matchingAlgorithm;    
        buildingHeight = numberOfFloors*distanceBetweenFloors;
        
        //Build the floors- Set the 0th element to be null for simplicity.
        floors = new Floor[numberOfFloors + 1];
        for (i = 1; i <= numberOfFloors; i++) {
            floors[i] = new Floor(i, numberOfFloors, numberOfLifts, liftStartFloor);
        }
        
        //Build the lifts- Set the 0th element to be null for simplicity.
        //Also build the lift node queues (the mapping from the selection arrays to the queue of floors to visit for each lift)
        lifts = new Lift[numberOfLifts + 1];
        liftNodeQueue = new Queue[numberOfLifts + 1]; //(Queue<Node>[]) new Object[numberOfLifts + 1];
        for (i = 1; i <= numberOfLifts; i++) {
            lifts[i] = new Lift(numberOfFloors, distanceBetweenFloors, velocity, maxDoorOpenDistance, liftStartFloor, doorOpenCloseThreshold, doorOpenCloseTime);
            liftNodeQueue[i] = new Queue<Node>();
        }
        
        //Initialise the selections arrays: the "+1's" are to eg allow us to reference floor 1 with row 1 (rather than row 0)
        selectionsFromFloor = new boolean[numberOfFloors + 1][2];
        selectionsFromLift = new boolean[numberOfFloors + 1][numberOfLifts + 1];
        
        //Initialise the instruction queue
        instructionLog = new Queue<Instruction>();
        
        //Build the currentAndNextFloorsForLifts array- this is to be used for debugging
        currentAndNextFloorsForLifts = new int[2][numberOfLifts + 1];            
    }
    
    /**
     * Implement the Subject interface
     */
    @Override
    public void register(Observer obj) {
        if(obj == null) throw new NullPointerException("Null Observer");
        synchronized (MUTEX) {
        if(!observers.contains(obj)) observers.add(obj);
        }
    }
    
    @Override
    public void unregister(Observer obj) {
        synchronized (MUTEX) {
        observers.remove(obj);
        }
    }
    
    @Override
    public void notifyObservers() {
        List<Observer> observersLocal = null;
        //synchronization is used to make sure any observer registered after message is received is not notified
        synchronized (MUTEX) {
            if (!changed)
                return;
            observersLocal = new ArrayList<Observer>(this.observers);
            this.changed = false;
        }
        for (Observer obj : observersLocal) {
            obj.update();
        }
    }
    
    /**
     * This is the method to call when the UP/DOWN button is (UN)selected at a certain floor
     * @param thisFloor is the floor that the button is pressed
     * @param direction is the direction you want to go
     * @param selection tells whether to set the selection to be true (selected) or false (UNselected)
     * @return the selection choice. this can be used in the GUI to indicate whether the button is pressed or not
     */
    public boolean selectFromFloor(int thisFloor, String direction, boolean selection) {
        
        //Check the floor
        floorCheck(thisFloor);
        
        //Check the direction
        directionCheck(direction);
        
        String origin;  //This is where the origin of the instruction comes from. if selection == true, it must come from the floor, otherwise it comes from the LC
        
        //Add to the instruction log 
        if (selection) origin = ORIGIN_FROM_FLOOR;
        else origin = ORIGIN_FROM_LC;    
        instructionLog.enqueue(new Instruction(METHOD_ORIGIN_FROM_FLOOR, origin, thisFloor, NULL_LIFT, direction, selection));
        
        //Update the display on the floor
        if (selection) {
            if (direction.equals(SELECT_UP)) {
                floors[thisFloor].selectUp();
            }
            else floors[thisFloor].selectDown();
        }
        else {
            if (direction.equals(SELECT_UP)) {
                floors[thisFloor].unselectUp();
            }
            else floors[thisFloor].unselectDown();
        }
        
        //Update the selections arrays
        if (direction.equals(SELECT_UP)) {
            //Check for the top floor
            if (thisFloor != numberOfFloors) {
                selectionsFromFloor[thisFloor][0] = selection;
            }
            else System.out.printf("Can't go UP from the top floor!\n");
        }
        else {
            //Check for the bottom floor
            if (thisFloor != 1) {
                selectionsFromFloor[thisFloor][1] = selection;
            }
            else System.out.printf("Can't go DOWN from the bottom floor!\n");
        }
        
        //Run the matching algorithm to map the selections arrays to the queues of nodes to visit for each lift  
        //System.out.printf("matchingAlgorithm = %d\n", matchingAlgorithm);
        doMatching(matchingAlgorithm);
        
        //use this to notify observers of a change
        changed = true;
        
        return selection;
    }
    
    /**
     * (UN)Select a floor to visit from a given lift
     * @param thisLift is the lift that the floor button is being pushed in
     * @param floorToVisit is the floor button being pushed in the given lift
     * @param selection tells whether to set the selection to be true (selected) or false (UNselected)
     * @return the selection choice. this can be used in the GUI to indicate whether the button is pressed or not
     */
    public boolean selectFromLift(int thisLift, int floorToVisit, boolean selection) {
        
        String origin;  //This is where the origin of the instruction comes from. if selection == true, it must come from the lift, otherwise it comes from the LC
        
        //Validate the inputs
        if (thisLift <= 0 || thisLift > numberOfLifts) {
            System.out.printf("thisLift %d is invalid\n", thisLift);
            throw new IllegalArgumentException("thisLift is not valid");
        }
        
        if (floorToVisit <= 0 || floorToVisit > numberOfFloors) {
            System.out.printf("floorToVisit %d is invalid\n", floorToVisit);
            throw new IllegalArgumentException("floorToVisit is not valid");
        }
        
        //Add to the instruction log   
        if (selection) origin = ORIGIN_FROM_LIFT;
        else origin = ORIGIN_FROM_LC;            
        instructionLog.enqueue(new Instruction(METHOD_ORIGIN_FROM_LIFT, origin, floorToVisit, thisLift, NULL_DIRECTION, selection));
        
        //Update the display in the lift
        if (selection) {
            lifts[thisLift].selectFloor(floorToVisit);
        }
        else lifts[thisLift].unselectFloor(floorToVisit);
        
        //Update the selections arrays
        selectionsFromLift[floorToVisit][thisLift] = selection;
        
        //Run the matching algorithm to map the selections arrays to the queues of nodes to visit for each lift       
        //System.out.printf("matchingAlgorithm = %d\n", matchingAlgorithm);
        doMatching(matchingAlgorithm);
        
        //use this to notify observers of a change
        changed = true;
        
        return selection;
    }        
    
    /**
     * Run the lift program: Once the GUI works, as the user hits buttons this will call methods to issue instructions to the LC
     * Allow an input with an instruction log that will be executed.  if we dont want to use this, input an empty queue
     */
    public void StartLiftController(Queue<Instruction> instructionLogInput) {  //note the arguement can be empty. supplying an iterable allows me to test many selections without sensors.
        
        System.out.printf("instructionLogInput size = %d\n", instructionLogInput.size());
        
        //if an input arguement was supplied, add the instructions into the lift controller
        if (!instructionLogInput.isEmpty()) {
            for (Instruction ins : instructionLogInput) {
                
                executeInstruction(ins);
            }
        }
        
        //Print the selections arrays
       /* for (int i = 1; i <= numberOfFloors; i++) {
            System.out.printf("Floor %d: UP = %b | DOWN = %b", i, selectionsFromFloor[i][0], selectionsFromFloor[i][1]);
            for (int j = 1; j <= numberOfLifts; j++) {
                System.out.printf(" | Lift %d = %b\n", j, selectionsFromLift[i][j]);
            }
        }*/
        
        boolean stopRunning = false;
        //Now run the infinite loop to call the various LC tasks
        while (!stopRunning) {
            runInEachLoop(); 
            
            //update the observers
            
            notifyObservers();
        }
    }
    
    /**
     * This is the method that calls various LC tasks each time the loop is run
     * We plan to run this an an infinite loop until the user hits Ctrl+C
     */
    public void runInEachLoop() {
        
        //iterate through each lift and move it towards the nodes in its queue
        for (int i = 1; i <= lifts.length - 1; i++) {

            //Get the lift
            Lift l = lifts[i];
            
            //find the new position of the lift since the last iteration of this method
            //running the simple version does not stop the lift. it just updates its state (which is just its position from bottom)
            l.updateStateSinceLastCalledSimple();
            
            //Do some error checking on the position of the lift to ensure it can't be below ground or above the building height
            //This should throw an error but for the moment let it continue
            if (l.getState().positionFromBottom() >  buildingHeight) {
                System.out.printf("\n\nPROBLEM: lift %d position of %g > building height %g. Stopping Lift!\n", i, l.getState().positionFromBottom(), buildingHeight);
                //System.out.printf("instruction log: %s\n", instructionLog.toString());
                l.stop();
            }
            if (l.getState().positionFromBottom() <  0) {
                System.out.printf("\n\nPROBLEM: lift %d position of %g < ground floor height of 0. Stopping Lift!\n", i, l.getState().positionFromBottom());
                //System.out.printf("instruction log: %s\n", instructionLog.toString());
                l.stop();
            }
            
            //Get the current direction of the lift (can choose floor 1 as the index as the direction of the lift is the same regardless of which floor you are at!)
            String tempDirection = floors[1].getFloorDisplayLiftDirection(i);
            
            //Update the floor displays for this lift
            for (int j = 1; j <= floors.length - 1; j++) {
                //update the current floor of the lift
                //System.out.printf("j = %d, i = %d, l.getCurrentFloor() = %d\n", j, i, l.getCurrentFloor())                
                floors[j].setFloorDisplayLiftFloor(i, l.getCurrentFloor());
                //update the direction of the lift
                
                floors[j].setFloorDisplayLiftDirection(i, l.getState().direction());
                //update the ETA to get to this floor- NOT SURE HOW TO DO THIS. just set it to -1 for the moment
                /**** TO DO ******/
                floors[j].setFloorDisplayLiftETA(i, -1);
                /*****************/
            }
                        
            //if the floor has changed, update the displays
            if (currentAndNextFloorsForLifts[0][i] != l.getCurrentFloor()) {
                changed = true;
                notifyObservers();
            }
            
            //if the direction has changed, update the displays
            if (!tempDirection.equals(l.getState().direction())) {
                changed = true;
                notifyObservers();
            }
             
            //Update the current and next floors for the lift
            currentAndNextFloorsForLifts[0][i] = l.getCurrentFloor();
            currentAndNextFloorsForLifts[1][i] = l.getNextFloor(i); 
            if (currentAndNextFloorsForLifts[0][i] > numberOfFloors ||  currentAndNextFloorsForLifts[1][i] > numberOfFloors) {
                System.out.printf("Lift %d something wrong!\n", i);
            }
            
            //now guide the lift through its queue of nodes to visit
            guideLift(i, l);
        }        
    }
    
    /**
     * Guide a lift through its queue of nodes to visit
     * @param lift specifies which lift is currently being guided
     */
    public void guideLift(int liftNumber, Lift l) {                
        
        //System.out.printf("Running guideLift for lift %d\n", liftNumber);
        
        //Retrieve the queue of nodes to visit for this lift
        Queue<Node> nodeQueue = liftNodeQueue[liftNumber];
        
        //Get the current lift state
        LiftState lState = l.getState();
        boolean isStationary = lState.isStationary();  //save recalculation
        
        //Is the nodeQueue empty? If so, stop the lift and return
        if (nodeQueue.isEmpty()) {
            //System.out.printf("nodeQueue is empty\n");
            //If the lift is not stationary, stop it
            if (!isStationary) {                
                l.stop();
            }
            return;
        }
        
        //At this point, the queue is not empty so we have places to go!
        //Check what the node is that we want to visit
        Node nodeToVisit = nodeQueue.peek();
        int floorToVisit = nodeToVisit.getFloor();
       // System.out.printf("floorToVisit = %d\n", floorToVisit);
        
        //Check if we are in the correct location 
        if (l.canOpenCloseDoorAtFloor(floorToVisit)) {  
            //we are at the right location- Commence arrival procedures
            /*
            System.out.printf("floorToVisit = %d\n", floorToVisit);
            System.out.printf("l.getCurrentFloor() = %d\n", l.getCurrentFloor());
            System.out.printf("l.getPositionFromFloor(%d) = %g\n", floorToVisit, l.getPositionFromFloor(floorToVisit));
            System.out.printf("l.getPositionFromFloor(%d) = %g\n", l.getCurrentFloor(), l.getPositionFromFloor(l.getCurrentFloor()));
            */
            arrivalProcedures(liftNumber, l);
            return;
        }
            
        //calculate how far the lift is from the floor we want to visit. Positive -> lift is above the floor
        double distanceLiftFromFloorToVisit = lState.positionFromBottom() - distanceFromGround(floorToVisit);
        
        //check if lift is stationary or moving
        if (isStationary) {
                //Start the lift going in the correct direction
                if (distanceLiftFromFloorToVisit > 0) {
                    //lift is above the floor. go down
                    l.goDown();
                }
                else l.goUp();
            }
        else { //Lift is moving            
            //if the lift moving in the wrong direction, reverse it- Note that this may be undesirable behaviour but can be controlled by the matching algorithm (it knows the current status of the lift and its queue before it chooses to reshuffle)
            if (distanceLiftFromFloorToVisit > 0 && lState.isGoingUp()) { //lift is above the floor and moving up
                System.out.printf("Lift %d is moving UP but is %gm above floor %d\nChanging direction...\n", liftNumber, distanceLiftFromFloorToVisit, floorToVisit);
                l.stop();
                l.goDown();
            }
            else if (distanceLiftFromFloorToVisit < 0 && lState.isGoingDown()) { //lift is below the floor and moving down
                System.out.printf("Lift %d is moving DOWN but is %gm below floor %d\nChanging direction...\n", liftNumber, -distanceLiftFromFloorToVisit, floorToVisit);
                l.stop();
                l.goUp();
            }
            //if lift is going in the correct direction, nothing more need to be done as the lift will eventually reach the correct floor
        }           
    }
    
    /**
     * Arrival procedures- There are several things we need to do when a lift is at a floor that it wanted to visit
     * We need to: dequeue the node off the lift queue, stop the lift, ping bell, open doors, close doors, issue an instruction to UNselect the relevant lights on floors/lifts, update the selection arrays
     * @param liftNumber tells us which number lift in the building 
     * @param Lift is the lift under consideration
     */
    public void arrivalProcedures(int liftNumber, Lift l) {
    
        System.out.printf("\nInitiating arrival procedures for liftNumber %d at floor %d\n", liftNumber, l.getCurrentFloor());
        //System.out.printf("l.canOpenCloseDoorAtFloor(floorToVisit) = %b\n", l.canOpenCloseDoorAtFloor(l.getCurrentFloor()));
        //System.out.printf("l.getCurrentFloor() = %d\n", l.getCurrentFloor());
        
        //Dequeue the node from the lift queue- Do this first as once issue the UNselect instructions, it re-runs the matching algorithm
        //System.out.printf("Dequeuing the node from the lift queue...\n");
        Node nodeToVisit = liftNodeQueue[liftNumber].dequeue();
        
        //If the lift is not stationary, stop it
        if (!l.getState().isStationary()) {     
            System.out.printf("stopping lift %d\n", liftNumber);
            l.stop();
        }
        
        //Ping bell
        System.out.printf("pinging bell lift %d...\n", liftNumber);
        l.pingBell();
        
        //Open the door
        System.out.printf("opening door lift %d...\n", liftNumber);
        l.openDoor();
        
        //Close the door
        System.out.printf("closing door lift %d...\n", liftNumber);
        l.closeDoor();
        
        //issue an instruction to UNselect the relevant lights on floors/lifts and update the selection arrays
        //note that the selection arrays are updated when the instruction to UNselect is issued
        if (nodeToVisit.getFromLift()) {
            //unselect from lift
            selectFromLift(liftNumber, nodeToVisit.getFloor(), false);
        }
        else if (nodeToVisit.getfromFloorUP()) {
            //unselect from lift UP button
            selectFromFloor(nodeToVisit.getFloor(), SELECT_UP, false);
        }
        else if (nodeToVisit.getfromFloorDOWN()) {
            //unselect from lift DOWN button
            selectFromFloor(nodeToVisit.getFloor(), SELECT_DOWN, false);
        }
        else {
            //throw an error as this node has not been mapped from either the lift or a floor
            System.out.printf("this node has not been mapped from either the lift or a floor\n");
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Matching algorithm to map the selections arrays to the queues of nodes to visit for each lift
     * this algo will look at the selection arrays and update the queues of nodes for each lift
     * @param algoNumber specifies which number algorithm to use for matching
     */
    public void doMatching(int algoNumber) {        
        /*** TO DO ***/
        switch(algoNumber) {
            //Stupid matching. have each lift visit their floors selected by the lift (in ascending order).  then assign the floors selected by floor to lift 1.
            //initially dont care about up or down at a floor.
            //also don't care if a floor is selected more than once
            case 1: {       
                //System.out.printf("Running matching algorithm 1\n"); 
                
                //for each lift, check all floors selected by the lift. add to the node queue for that lift
                for(int i = 1; i <= numberOfLifts; i++) {
                    //Store a copy of the existing queue for that lift as it may be useful
                    Queue<Node> q = liftNodeQueue[i];
                    //Now remove the existing queue for this lift as we are going to recalculate it
                    //liftNodeQueue[i] = null;  //Must comment this out as when i press a button to call the lift, when this line sets liftNodeQueue[i] = null, the guideLift() can be running resulting in a nullPointerException
                    liftNodeQueue[i] = new Queue<Node>();
                    
                    for (int j = 1; j <= numberOfFloors; j++) {
                        if (selectionsFromLift[j][i]) {
                            //add a node: Node(int floor, boolean fromLift, boolean fromFloorUP, boolean fromFloorDOWN)
                            //System.out.printf("adding a node from a lift\n");
                            liftNodeQueue[i].enqueue(new Node(j, true, false, false));
                        }
                    }
                }
                //assign all selections from floors to be assigned to lift #1
                for (int j = 1; j <= numberOfFloors; j++) {
                    if (selectionsFromFloor[j][0]) {
                        //System.out.printf("adding a node UP from a floor\n");
                        liftNodeQueue[1].enqueue(new Node(j, false, true, false));
                    }
                    if (selectionsFromFloor[j][1]) {
                       // System.out.printf("adding a node DOWN from a floor\n");
                        liftNodeQueue[1].enqueue(new Node(j, false, false, true));
                    }
                }
                //Debugging
                for(int i = 1; i <= numberOfLifts; i++) {
                    System.out.printf("Size of queue for lift %d = %d\n", i, liftNodeQueue[i].size());
                }
                break;
            }                        
        }
    }
    
    /**
     * Emergency stop
     * stop the given lift
     * this will not clear the selections of all the other floors
     */
    public void emergencyStop(int liftNumber) {
        System.out.printf("Emergency lift stop for lift %d!\n", liftNumber);        
        //Input checking
        liftCheck(liftNumber);
                       
        //stop the lift
        lifts[liftNumber].stop();        
    }
    
    
    /**
     * Getter: retrieve the instruction log
     * @return the instruction log
     */
    public Iterable<Instruction> getInstructions() {
        return instructionLog;
    }
    
    /**
     * Getter: retreive the selectionsFromFloor array
     * @return the the selectionsFromFloor array
     */
    public boolean[][] getselectionsFromFloor() {
        return selectionsFromFloor;
    }
    
    /**
     * Getter: retreive the selectionsFromLift array
     * @return the selectionsFromLift array
     */
    public boolean[][] getselectionsFromLift() {
        return selectionsFromLift;
    }
    
    /**
     * Getter: retrieve the status (on/off) of a button on a floor
     * @param floor int the floor number
     * @param direction string the UP or DOWN button
     * @return whether the up/down button on this floor is selected
     */
    public boolean getselectionFromFloorDirection(int floor, String direction) {
        int index;
        
        if (direction.equals(SELECT_UP)) {
            index = 0;
        }
        else index = 1;
            
        return selectionsFromFloor[floor][index];
    }
    
    /**
     * Getter: retrieve the status (on/off) of a button on a lift
     * @param floor int the floor number
     * @param lift int the lift number
     * @return whether the floor button on this lift is selected
     */
    public boolean getselectionsFromLiftFloor(int floor, int lift) {        
        return selectionsFromLift[floor][lift];
    }
    
    /**
     * Getter: retrieve the direction of a lift
     */
    public String getDirectionFromLift(int lift) {
        return floors[1].getFloorDisplayLiftDirection(lift);
    }
    
    /**
     * Getter: return lift status for a given lift. NOTE this could cause a problem in that we return the LiftState- this has setter methods which could be exploited
     * @param liftNumber the status for a lift you are interested in
     * @return LiftState for a given lift number
     */
    public LiftState getLiftStatus(int liftNumber) {
        
        //Input checking
        liftCheck(liftNumber);
        
        return lifts[liftNumber].getState();
    }
    
    /**
     * Getter: return number of floors
     */
    public int getnumberOfFloors() {
        return numberOfFloors;
    }
    
    /**
     * Getter: return number of lifts
     */
    public int getnumberOfLifts() {
        return numberOfLifts;
    }
    
    /**
     * Getter: return current or next floor of a given lift
     * @param lift int the lift number
     * @param choice int 0 == current floor, 1 == next floor
     */
    public int currOrNextFloorOfLift(int lift, int choice) {
        
        if (choice != 0 && choice != 1) {
            throw new IllegalArgumentException("Error: choice != 0 or 1");
        }
        return currentAndNextFloorsForLifts[choice][lift];
    }
        
    /**
     * Getter: get a list of floors to visit (in order) for a given lift
     * @param lift int the lift number
     */
    public ArrayList<Integer> getFloorsToVisit(int lift) {
     
        //store the floors to visit in an array list
        ArrayList<Integer> floorsToVisit = new ArrayList<Integer>();
        //retrieve the queue of nodes for the selected lift
        Queue<Node> q = liftNodeQueue[lift];
        //add the floors to visit for each node to the arraylist
        for (Node n : q) {
            floorsToVisit.add(n.getFloor());
        }
        return floorsToVisit;
    }
    
    /**
     * Getter: get a String of floors to visit (in order) for a given lift
     */
    public String getFloorsToVisitString(int lift) {
        
        String listString = "[";
        ArrayList<Integer> floorsToVisit = getFloorsToVisit(lift);
        if (floorsToVisit.isEmpty()) {
            return "";
        }
        
        for (int floor : floorsToVisit) {    
            listString += floor + ", ";
        }
        
        return listString.substring(0, listString.length() - 2) + "]";
    }
    
    /**
     * lift number checker
     */
    public void liftCheck(int liftNumber) {
        if (liftNumber <= 0 || liftNumber > numberOfLifts) {
            System.out.printf("liftNumber %d is invalid\n", liftNumber);
            throw new IllegalArgumentException("liftNumber is not valid");
        }
    }
    
    /**
     * floor number checker
     */
    private void floorCheck(int thisFloor) {
        if (thisFloor <= 0 || thisFloor > numberOfFloors) {
            System.out.printf("thisFloor %d is outside allowed range\n", thisFloor);
            throw new IllegalArgumentException("thisFloor is invalid");
        }
    }
    
    /**
     * direction checker
     */
    private static void directionCheck(String direction) {
        if (!direction.equals(SELECT_UP) && !direction.equals(SELECT_DOWN)) {
            System.out.printf("direction %s is invalid\n", direction);
            throw new IllegalArgumentException("direction is invalid");
        }
    }
    
    /**
     * helper method: calculate the distance of a floor from the ground
     * @param floor number
     */
    private double distanceFromGround(int floorToVisit) {
        return (floorToVisit - 1) * distanceBetweenFloors;
    }
    
    /**
     * helper method to execute an instruction once it's issued
     */
    public void executeInstruction(Instruction ins) {
        //print the instruction details
        System.out.printf("\nInstruction Details: %s\n", ins.toString());
        if (ins.getmethodOrigin().equals(METHOD_ORIGIN_FROM_FLOOR)) {
            //call the method for a floor instruction
            selectFromFloor(ins.getfloorNumber(), ins.getdirection(), ins.getselection());
        }
        else {
            //call the method for a lift instruction
            selectFromLift(ins.getliftNumber(), ins.getfloorNumber(), ins.getselection());
        }                
    }
}
