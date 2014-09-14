import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the stupid matching algorithm which gets lifts to go to floors selected from the lift in ascending order.
 * All floor buttons pressed from the floor are assigned to lift 1 *
 */
public class OptimiserAlgo1 extends OptimiserAlgo {

	public OptimiserAlgo1(int numberOfLifts, int numberOfFloors, boolean[][] selectionsFromLift, boolean[][] selectionsFromFloor, int algoNumber) {
		super(numberOfLifts, numberOfFloors, selectionsFromLift, selectionsFromFloor, algoNumber);
	}
	
	/**
	 * Determine the optimized queue of nodes to visit for each lift
	 */
	public Queue<Node>[] getOptimisedNodesAll() {
		
		Queue<Node>[] liftNopoll = new Queue[getNumberOfLifts() + 1];
		
		//for each lift, check all floors selected by the lift. add to the node queue for that lift
        for(int i = 1; i <= getNumberOfLifts(); i++) {
            //Store a copy of the existing queue for that lift as it may be useful
            //Now remove the existing queue for this lift as we are going to recalculate it
            liftNopoll[i] = new LinkedList<Node>();
            
            for (int j = 1; j <= getNumberOfFloors(); j++) {
                if (getSelectionsFromLift()[j][i]) {
                    //add a node: Node(int floor, boolean fromLift, boolean fromFloorUP, boolean fromFloorDOWN)
                    liftNopoll[i].add(new Node(j, true, false, false));
                }
            }
        }
        //all selections from floors to be assigned to lift #1
        for (int j = 1; j <= getNumberOfFloors(); j++) {
            if (getSelectionsFromFloor()[j][0]) {
                liftNopoll[1].add(new Node(j, false, true, false));
            }
            if (getSelectionsFromFloor()[j][1]) {
                liftNopoll[1].add(new Node(j, false, false, true));
            }
        }
        //Debugging
        for(int i = 1; i <= getNumberOfLifts(); i++) {
        	System.out.printf("Size of queue for lift %d = %d\n", i, liftNopoll[i].size());
        }
		
        return liftNopoll;
	}
}
