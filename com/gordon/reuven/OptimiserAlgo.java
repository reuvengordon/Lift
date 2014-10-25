package com.gordon.reuven;
import java.util.Queue;

/**
 * OptimiserAlgo object- Define all the details which we will use to perform the optimisation.
 * Implement subclasses of this class to actually perform various optimisation algorithms *
 */
public class OptimiserAlgo implements Optimise {

	//Instance variables
	private int numberOfLifts;
	private int numberOfFloors;
	private boolean[][] selectionsFromLift;
	private boolean[][] selectionsFromFloor;
	private int algoNumber;
	private Queue<Node>[] liftNopoll;
	
	/**
	 * Constructor
	 */
	public OptimiserAlgo(int numberOfLifts, int numberOfFloors, boolean[][] selectionsFromLift, boolean[][] selectionsFromFloor, int algoNumber) {
		this.numberOfLifts = numberOfLifts;
		this.numberOfFloors = numberOfFloors;
		this.selectionsFromLift = selectionsFromLift;
		this.selectionsFromFloor = selectionsFromFloor;
		this.algoNumber = algoNumber;
	}
	
	public int getNumberOfLifts() {
		return numberOfLifts;
	}

	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	public boolean[][] getSelectionsFromLift() {
		return selectionsFromLift;
	}

	public boolean[][] getSelectionsFromFloor() {
		return selectionsFromFloor;
	}
	
	public int getAlgoNumber() {
		return algoNumber;
	}

	/* (non-Javadoc)
	 * @see Optimise#optimise()
	 */
	@Override
	public void optimise() {
		switch(algoNumber) {
			//stupid matching algo
			case 1: {
				liftNopoll = new OptimiserAlgo1(getNumberOfLifts(), getNumberOfFloors(), getSelectionsFromLift(), getSelectionsFromFloor(), getAlgoNumber()).getOptimisedNodesAll();				
				break;
			}
			//optimise just for lift buttons being pressed
			case 2: {
				liftNopoll = new OptimiserAlgo2(getNumberOfLifts(), getNumberOfFloors(), getSelectionsFromLift(), getSelectionsFromFloor(), getAlgoNumber()).getOptimisedNodesAll();				
				break;
			}
		}
	}

	@Override
	public Queue<Node>[] getOptimisedNodesAll() {
		return liftNopoll;
	}

}
