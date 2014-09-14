/**
 * This is the class to execute the lift
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ExecuteLift2 { 
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
    	
//        //Store the program arguements
//        String lcInputs = args[0];
//        String instructionLogInputs = args[1];
//        
//        /*** PARSE the LC inputs from an input text file ***/
//        //Store the values in this double array
//        ArrayList<Double> inputValues = new ArrayList<Double>();
//        
//        //Parse the input
//        try (BufferedReader br = new BufferedReader(new FileReader(lcInputs))) {
//            String line = null;
//            while ((line = br.readLine()) != null) {
//        	String[] field = line.split("="); 
//        	inputValues.add(Double.parseDouble(field[1]));
//            }
//        }
//        
//        //Now assign the values of the input file to the arguements of the LC 
//        int numberOfFloors = inputValues.get(0).intValue();
//        int numberOfLifts = inputValues.get(1).intValue();
//        double distanceBetweenFloors = inputValues.get(2);
//        double velocity = inputValues.get(3);
//        double maxDoorOpenDistance = inputValues.get(4);
//        int liftStartFloor = inputValues.get(5).intValue();
//        double doorOpenCloseThreshold = inputValues.get(6);
//        int doorOpenCloseTime = inputValues.get(7).intValue();
//        int matchingAlgorithm = inputValues.get(8).intValue();        
//        
//        /*** BUILD the instruction queue from the input text file  ***/        
//        Queue<Instruction> instructionLog = new LinkedList<Instruction>();
//        
//	// Parse the input
//	try (BufferedReader br = new BufferedReader(new FileReader(instructionLogInputs))) {
//	    String line = null;
//	    int cnt = 0;
//	    while ((line = br.readLine()) != null) {
//		String[] field = line.split(",");
//		// Ignore the first line
//		if (cnt > 0) {
//		    String methodOrigin = field[0];
//		    String instructionOrigin = field[1];
//		    int floorNumber = Integer.parseInt(field[2]);
//		    int liftNumber = Integer.parseInt(field[3]);
//		    String direction = field[4];
//		    boolean selection = Boolean.parseBoolean(field[5]);
//
//		    // Create a new instruction object and add it to the queue
//		    instructionLog.add(new Instruction(methodOrigin, instructionOrigin, floorNumber, liftNumber, direction, selection));
//		}
//		cnt++;
//	    }
//	}
//
//        //Build the GUI
//        /*
//        SwingUtilities.invokeLater(new Runnable(){            
//            public void run()              
//            {                
//                LiftGUI gui = new LiftGUI(numberOfFloors, numberOfLifts, distanceBetweenFloors, velocity, maxDoorOpenDistance, liftStartFloor, doorOpenCloseThreshold, doorOpenCloseTime, matchingAlgorithm, instructionLog);                         
//            }            
//        });*/
        
        //new LiftGUI(numberOfFloors, numberOfLifts, distanceBetweenFloors, velocity, maxDoorOpenDistance, liftStartFloor, doorOpenCloseThreshold, doorOpenCloseTime, matchingAlgorithm, instructionLog);         
		Queue<Instruction> instructionLog = new LinkedList<Instruction>();	    		
    	new LiftGUI(10, 3, 3, 6, 1.5, 1, .1, 0, 1, instructionLog);
    }
}
    
    
