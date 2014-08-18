/**
 * This is the class to execute the lift
 */
import java.util.ArrayList;

public class ExecuteLift2 { 
    
    public static void main(String[] args) {
        
        //Store the program arguements
        String lcInputs = args[0];
        String instructionLogInputs = args[1];
        
        /*** PARSE the LC inputs from an input text file ***/
        //Store the values in this double array
        ArrayList<Double> inputValues = new ArrayList<Double>();
        
        //Parse the input
        In in = new In(lcInputs);
        while (in.hasNextLine()) {
            String[] line = in.readLine().split("="); 
            inputValues.add(Double.parseDouble(line[1]));
        }
        
        //Now assign the values of the input file to the arguements of the LC 
        int numberOfFloors = inputValues.get(0).intValue();
        int numberOfLifts = inputValues.get(1).intValue();
        double distanceBetweenFloors = inputValues.get(2);
        double velocity = inputValues.get(3);
        double maxDoorOpenDistance = inputValues.get(4);
        int liftStartFloor = inputValues.get(5).intValue();
        double doorOpenCloseThreshold = inputValues.get(6);
        int doorOpenCloseTime = inputValues.get(7).intValue();
        int matchingAlgorithm = inputValues.get(8).intValue();        
        
        /*** BUILD the instruction queue from the input text file  ***/        
        Queue<Instruction> instructionLog = new Queue<Instruction>();
        
        //Parse the input
        In in2 = new In(instructionLogInputs);
        int cnt = 0;
        while (in2.hasNextLine()) {
            String[] line = in2.readLine().split(","); 
            //Ignore the first line
            if (cnt > 0) {                
                String methodOrigin = line[0];
                String instructionOrigin = line[1];
                int floorNumber = Integer.parseInt(line[2]);
                int liftNumber = Integer.parseInt(line[3]);
                String direction = line[4];
                boolean selection = Boolean.parseBoolean(line[5]);
                
                //Create a new instruction object and add it to the queue
                instructionLog.enqueue(new Instruction(methodOrigin, instructionOrigin, floorNumber, liftNumber, direction, selection));
            }
            cnt++;
        }

        //Build the GUI
        /*
        SwingUtilities.invokeLater(new Runnable(){            
            public void run()              
            {                
                LiftGUI gui = new LiftGUI(numberOfFloors, numberOfLifts, distanceBetweenFloors, velocity, maxDoorOpenDistance, liftStartFloor, doorOpenCloseThreshold, doorOpenCloseTime, matchingAlgorithm, instructionLog);                         
            }            
        });*/
        
        new LiftGUI(numberOfFloors, numberOfLifts, distanceBetweenFloors, velocity, maxDoorOpenDistance, liftStartFloor, doorOpenCloseThreshold, doorOpenCloseTime, matchingAlgorithm, instructionLog);         
    }
}
    
    
