Lift
====
software to simulate a number of lifts

to run this program, compile all files then type:

java ExecuteLift2 paramsX.txt instructionsY.txt

where X is a version of the lift params file and Y is the version of the instructions file.  note that Y can contain no instructions as these are simply to provide the lift with a few button calls on startup

14/09/2014: From eclipse, simply run ExecuteLift2.java .  i've commented out the code in this class that reads in a config file (as described above).

// TODO 
Implement various algorithms to optimise the lift movement.  I plan to do this in a few steps to break down the problem

1) Assume lift buttons only are being pressed (ie no-one presses buttons on a floor).  In this assumption, every lift is independent of each other.  This problem can be reduced to finding the shortest Hamiltonian path in a digraph, where vertices correspond to floors to be visited and the current lift position, and the edges are the vertical distances between each vertex.  This is essentially the travelling salesman problem (TSP).

2) Assume floor buttons only are being pressed.

3) Assume any ofblift and/or floor buttons are being pressed.
