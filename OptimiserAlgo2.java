import java.util.Queue;

// TODO OptimiserAlgo2
public class OptimiserAlgo2 extends OptimiserAlgo {

	public OptimiserAlgo2(int numberOfLifts, int numberOfFloors, boolean[][] selectionsFromLift, boolean[][] selectionsFromFloor, int algoNumber) {
		super(numberOfLifts, numberOfFloors, selectionsFromLift, selectionsFromFloor, algoNumber);
	}
	
	/**
	 * Determine the optimized queue of nodes to visit for each lift
	 */
	public Queue<Node>[] getOptimisedNodesAll() {
		return null;
	}
	
}
