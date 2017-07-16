
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 This class handles all the warnings and errors that occurs during the load and execution
 of instructions. In case of warning, the Error_Handler method displays the warning message
 and in case of error it calls the dump method and halts the execution.
 */

 /*
 *All the case numbers starting with 1 cate warnings and those starting with 2 indicate errors. 
 */
public class ERROR_HANDLER {

    public static String sample = "";

    public static void Error_Handler(int x){
        try {
            switch (x) {
                case 21:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Invalid hex format";
                    
                    
                    break;
                case 22:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Invalid opcode";
                    
                    break;
                case 23:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Memory location out of range";
                    
                    break;
                case 24:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Input file not found/empty";
                    
                    break;
                case 25:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Start address for execution doesn't exist";
                    
                    break;
                case 26:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Invalid loader format";
                    
                    break;
                case 27:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Invalid start address";
                    
                    break;
                case 28:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Illegal control signal passed to Memory";
                    
                    
                    break;
                case 29:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Execution has entered a suspectedly infinite loop";
                    
                    break;
                case 210:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Invalid instruction sequence";
                    
                    break;
                case 211:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Invalid input for arithmetic register bit";
                    
                    break;
                case 212:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Entered input results in output overflow";
                    
                    break;
                case 213:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Missing the ** JOB record";
                   
                    break;
                case 214:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Missing the ** DATA record";
                    break;
                case 215:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Missing records";
                    break;
                case 11:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Bad trace flag";
                    
                    break;
                case 12:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Program size is not specified";
                    
                    break;
                case 13:
                    SYSTEM.terminationMsg = "Normal Termination";
                    SYSTEM.error = "Trace bit not specified";
                    
                    break;
                case 14:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Unnecessary memory allocation";
                    
                    break;
                 case 15:
                    SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = "Missing loader format and input lines";
                    SYSTEM.noOfAbTJobs++;
                    SYSTEM.noOfJobs++;
                     
                    Scheduler.skipErrorJob();
                    break;
                    
            }
            if (x / 10 == 2 || x/100 == 2) {
                SYSTEM.noOfAbTJobs++;
                
                SYSTEM.noOfJobs++;
                Scheduler.skipErrorJob();
                
            }
        } catch (IOException ex) {
            SYSTEM.terminationMsg = "Abnormal Termination";
                    SYSTEM.error = String.valueOf(ex);
                    
        }
    }

}
