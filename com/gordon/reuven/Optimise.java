package com.gordon.reuven;
import java.util.Queue;

/**
 * Interface to define how a matching object must optimise a set of selections of lift and floor buttons
 */

public interface Optimise {
	
	//optimise the nodes for the lifts to visit based on a set of selections of lift and floor buttons
	public void optimise();
	
	//return an array of optimised node queues for all lifts to visit
	public Queue<Node>[] getOptimisedNodesAll();
	
}
