
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
 * This class stores the jobID,copy of registers, time the job has entered
 * the SYSTEM, CPU time used by job, time of completion of current I/O operation,
 * reminder of last quantum for each job.
 */

/**
 *
 * @author srivall
 */

public class PCB {

    public int jobID=0;
    /*should be in HEX*/
    public int PC=0;
    public int baseAddress=0;
    /*This stores the starting address of a job*/
    public int endAddress=0;
    /*this stores the end address of a job*/
    public int executionTime=0;
    /*this stores the cpu time used by the job*/

    public int quantumRemainder=0;
    /*This stores the quantum remaining after a job execution */
    public int traceBit=0;
    /*This stores the trace bit */
    public String startAddress = new String();
    /*This stores the trace bit */
    public int inputSize=0;
    /*This stores the number of lines of input*/
    public int inputSizeFlag=0;
    /*keeps a copy of number of input lines*/
    public int ioTime=0;
    /*io time for the job*/
     
    public int outputAddress=0;
    /*points to the last output of the job*/
    public int sysClockAtBlock=0;
    /*clock time when the process is blocked*/
    public int outputsize=0;
    /*number of output lines*/
    public int startTime=0;
    /*time the job has entered the system*/
    public int partitionNo=0;
    /*the block number from 1-7 where the job is stored*/
    public static StringBuilder traceFileContent = new StringBuilder();
    public String[] reg = {"0", "1", "0", "0", "0", "0", "0", "0", "0", "0"};
    /*holds the error message of the job if any*/
   public  String errorMessage = "";
   /*holds the termination type message: Normal or Abnormal*/
   public  String terminationMsg=""; 
   public static int fileCounter=0;
   /*to check for a suspected inifinite loop*/
   public static int runTime=0;
   
    public static void printToTraceFile(PCB p) throws IOException{
        
         fileCounter++;
         File file = new File("Trace_file"+fileCounter+ ".txt") ;
         if(file.exists()){
             file.delete();
         }
         file.createNewFile();
         FileWriter fw = new FileWriter(file);
         
          BufferedWriter traceWriter = new BufferedWriter(fw);
          traceWriter.write("PC(HEX)\t\t" + "Instruction(HEX)\t\t" + "R and EA(HEX)\t\t" + "Contents of R(HEX) and EA(HEX) BE \t\t" + "Contents of R(HEX) and EA(HEX) AE");
          traceWriter.newLine();
          traceWriter.write(traceFileContent.toString());
      
          traceWriter.close();
          fw.close();
        
    }   
}