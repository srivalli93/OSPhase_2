/* Name             : Srivalli Kanchibotla
 * Course Number    : CS5323
 * Assignment Title : Phase-2
 * Date             : 04-25-2017
 */
 /*
 * SYSTEM class is the main class that has the control of execution of entire project.
 * The project is the implementation of specifications given .i.e., it reads the input file line by line,
 * executes all the instructions and returns the output for an entered input.
 */
 /* GLOBAL VARIABLES
 * The 'buffer' arraylist acts as buffer to read and store the input file line by line.
 * 'loaderInput' stores the path of file entered as argument while executing the program.
 * 'firstline' stores the firstline of input file (supposedly size of program).'startAddress' has the address from where the program execution should start.
 * 'tracebit' is used to store the tracebit.'error' is used to print the error message in case of any.'terminationMsg' prints if the termination is normal or abnormal.
 * 'jobID' stores and prints the current jobID.'result' is used to print the final result of the errorless execution.'reg' array has the registers from 0-9.
 */


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class SYSTEM {

    public static int clock;
    public static ArrayList<String> buffer = new ArrayList<String>();
    public static LinkedList<Integer> jobQueue = new LinkedList<Integer>();
    public static String loaderInput = "";
    public static String firstLine, startAddress = "0", traceBit = "0";
    public static String error = "";
    public static String terminationMsg = "Normal Termination";
    public static int result;
    public static String[] reg = {"0", "1", "0", "0", "0", "0", "0", "0", "0", "0"};
    static int noOfJobs = 0;
    /*has the count of total number of jobs*/
    static int runTime = 0;
    /*stores the summation of run time of job*/
    static int execTimeSum = 0;
    /*stores the summation of execution time of all jobs*/
    static int ioTimeSum = 0;
    /*stores the summation of io time of all jobs*/
    static int systemTimeSum = 0;
    /*has the sum of system time of all jobs*/
    static int noOfAbTJobs = 0;
    /*time lost due to abnormal terminations*/
    static int timeLostAbT = 0;
    /*to store the idle time*/
    static int idleTime = 0;

    static int infiniteJobsTime = 0;
    /*Total time spent on execution of suspected infinite loops*/
    static int infiniteJobCount = 0;
    /*Total jobs that entered a  suspected infinite loop*/
    static ArrayList<Integer> infiniteJobID = new ArrayList<Integer>();
    static int meanInternalFragmentation = 0;

    /*all job ids' that entered suspected infinite loop*/

    public static void main(String args[]) throws IOException {
        MEMORY mem = new MEMORY();
        File file = new File("progress_file.txt");
        if (file.exists()) {
            file.delete();
        }
        SYSTEM.loaderInput = args[0];
        try{
        LOADER.loader(startAddress, traceBit);

        Scheduler.schedule();
        }
        catch(Exception e){
            CPU.statistics();
           
            
        }

    }

}
