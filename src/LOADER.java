
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileWriter;
import java.io.BufferedWriter;

/*
*  This class is called by SYSTEM class.This loader() method is responsible for reading the data from the 
* input file, converting it from hex format to binary and storing it in main memory through a buffer. 
* 
 */
public class LOADER {

    public int count = 0;
    public static int jobIDFlag = -1;
    /*keeps the track of job id*/
    public static int instrCount = 0;
    /*stores the instruction count for current job*/
    static String firstLine;
    /*stores the firstline of the job*/
    public static int index = -4, p = 0;
    public static String[] input;
    /*stores the input lines in hex format*/
    public static List<String> inputPrograms = new ArrayList<String>();
    /*to store the input program read from the file*/
    public static String tempTraceBit;
    /*to store the trace bit*/
    public static String tempStartAddress = new String();
    /*stores the start address of the job*/
    public static int currentProgramSize = 0, flag = 0;
    static int programCount = 0;
    static boolean inputComplete = false;
    /*this flag will be set true when all the entire
    input file is read into the system*/
    static int outputSize = 0;
    static String dataLine;

    public static void loader(String startAddress, String trace) throws IOException {

        loaderFormat(SYSTEM.loaderInput);
        readInput(SYSTEM.loaderInput);

    }

    /*to read the entire input file into a list*/
    public static void loaderFormat(String file) {

        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            List<String> list = new ArrayList<String>();
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
            reader.close();
            inputPrograms.addAll(list);

        } catch (FileNotFoundException ex) {

            ERROR_HANDLER.Error_Handler(24);
        } catch (IOException ex) {
            ERROR_HANDLER.Error_Handler(24);
        }

    }

    /*this function sorts the entire list job by job and loads them into memory*/
    public static void readInput(String file) throws IOException {

        /*keeps the count of program number*/
        int size = inputPrograms.size();
        /*to temporarily store the current job*/
        List<String> temp = new ArrayList<String>();
        /*to temporarily store input*/
        List<String> inputTemp = new ArrayList<String>();
        /*a variable to stores the currentline*/
        String currentLine = null;
        int programSize = 0;
        /*temp variable to store the size of job*/

        while (flag < size - 1) {
            int flagBeforeTemp = flag;

            PCB pcb = new PCB();
            tempStartAddress = null;
            String firstLine = inputPrograms.get(flag);
            if (!firstLine.contains("** JOB")) {
                int x = flag;
                while (!inputPrograms.get(x++).contains("** JOB")) {
                    flag++;
                }
                ERROR_HANDLER.Error_Handler(213);
                jobIDFlag++;
            } else {
                tempTraceBit = null;
                currentLine = inputPrograms.get(flag);
                int currentBlock = 0;
                /*loader format of a job is put stored in a temp*/
                if (currentLine.contains("** JOB")) {

                    String[] jobSplit = currentLine.split(" ");

                    pcb.inputSize = Integer.parseInt(jobSplit[jobSplit.length - 2], 16);
                    pcb.inputSizeFlag = pcb.inputSize;
                    outputSize = Integer.parseInt(jobSplit[jobSplit.length - 1], 16);
                    pcb.outputsize = outputSize;
                    flag++;
                    currentLine = inputPrograms.get(flag);
                    while (!(currentLine.contains("** END") || currentLine.contains("** DATA")) && size != 0 && flag < size) {
                        /*to count number of words in program*/
                        temp.add(currentLine);
                        flag++;
                        currentLine = inputPrograms.get(flag);
                        dataLine = inputPrograms.get(flag);
                    }
                    if (!dataLine.contains("** DATA")) {

                        ERROR_HANDLER.Error_Handler(214);
                        /*call errorHandler when there is no ** Data*/
                        jobIDFlag++;
                        /*increment jobIDFlag*/
                        temp.clear();
                        /*Clear the instructions stored in temp*/
                        while (!inputPrograms.get(flag).contains("** JOB") && flag < size - 1) {
                            /*move the cursor to the start of next*/
                            flag++;
                        }

                        continue;
                        /*go back to while(flag<size-1)*/
                    }
                }

                                    if (currentLine.contains("** DATA")) {
                        /*if the loader format has data*/
                        flag++;
                        while (!inputPrograms.get(flag).contains("** END") && flag < size - 1) {
                            currentLine = inputPrograms.get(flag);
                            inputTemp.add(currentLine);
                            /*adding the input to inputtemp*/
                            flag++;
                        }
                        
                        if(flag<size-1){
                        currentLine = inputPrograms.get(flag);
                        }
                    }
                    if (inputPrograms.get(flag).contains("** END")) {
                        if(flag<size-1){
                            flag = flag+2;
                        }
                    }
            
            
                /*To check if the instructions hex format is valid*/
                if (!temp.isEmpty()) {
                    for (int i = 0; i < temp.size() - 1; i++) {
                        if (!temp.get(i).matches(("[0-9A-Fa-f ]+"))) {
                            ERROR_HANDLER.Error_Handler(21);
                            jobIDFlag++;
                            temp.clear();
                            return;
                        }
                    }
                }

                if (!temp.isEmpty()) {

                    String tempSize = hexToBin(temp.get(0));

                    String getTemp = temp.get(temp.size() - 1);

                    if (getTemp.contains(" ")) {
                        String[] splitTemp = getTemp.split(" ");
                        tempTraceBit = splitTemp[splitTemp.length - 1];
                        /*storing the start address in binary format*/
                        tempStartAddress = hexToBin(splitTemp[0]);
                        if (!tempStartAddress.matches("[0-9A-Fa-f ]+")) {
                            ERROR_HANDLER.Error_Handler(21);
                            jobIDFlag++;
                            temp.clear();
                            return;
                        }
                    } else {
                        tempStartAddress = getTemp;
                        if (!tempStartAddress.matches("[0-9A-Fa-f ]+")) {
                            ERROR_HANDLER.Error_Handler(21);
                            jobIDFlag++;
                            temp.clear();
                            return;
                        }
                    }
                    if (tempTraceBit == null || (!"1".equals(tempTraceBit) && !"0".equals(tempTraceBit))) {
                        ERROR_HANDLER.Error_Handler(13);
                        tempTraceBit = "0";
                    }
                    if (tempTraceBit.length() != 1) {
                        ERROR_HANDLER.Error_Handler(26);
                        jobIDFlag++;
                        temp.clear();
                        return;
                    }
                    
                    currentProgramSize = temp.size();
                    /*to remove the line that contains input*/
                    /*to remove the line that contains start address and trace bit*/
                    temp.remove(temp.size() - 1);
                    /*to remove the line that holds the size of the job*/
                    temp.remove(0);

                    for (int x = 0; x < temp.size(); x++) {

                        temp.set(x, hexToBin(temp.get(x)));
                        if (temp.get(x).length() < 12) {
                            System.out.println("Invalid loader format : Error");
                        }
                    }
                    /*newTempList will contain 12 bits of binary loader format*/

                    for (int x = 0; x < temp.size(); x++) {

                        switch (temp.get(x).length()) {
                            case 12:
                                instrCount++;
                                break;
                            case 24:
                                instrCount = instrCount + 2;
                                break;
                            case 36:
                                instrCount = instrCount + 3;
                                break;
                            case 48:
                                instrCount = instrCount + 4;
                                break;
                            default:
                                break;
                        }

                    }
                    /*to check the validity of hexformat of input lines*/
                    if (!inputTemp.isEmpty()) {
                        for (int i = 0; i < inputTemp.size(); i++) {
                            if (!inputTemp.get(i).matches(("[0-9A-Fa-f ]+"))) {
                                ERROR_HANDLER.Error_Handler(21);
                                jobIDFlag++;
                                inputTemp.clear();
                                temp.clear();
                                return;
                            }
                        }
                    }

                    for (int x = 0; x < inputTemp.size(); x++) {
                        inputTemp.set(x, hexToBin(inputTemp.get(x)));
                        instrCount++;
                    }

                    temp.addAll(inputTemp);
                    inputTemp.clear();

                    /*block1 and block 2*/
                    if (instrCount + outputSize <= 32 && (MEMORY.blockCheck[0] == false || MEMORY.blockCheck[1] == false)) {

                        if (MEMORY.blockCheck[0] == false) {
                            /*block1*/
                            int endAdd = 0, memoryLocation = 0;

                            for (int x = 0; x < temp.size(); x++) {

                                MEMORY.memory(1, MEMORY.block1 + memoryLocation, temp.get(x));
                                memoryLocation = memoryLocation + (temp.get(x).length() / 12);
                                endAdd = MEMORY.block1 + memoryLocation;
                            }
                            MEMORY.blockCheck[0] = true;
                            SYSTEM.meanInternalFragmentation = SYSTEM.meanInternalFragmentation + (32-instrCount-outputSize);
                            currentBlock = endAdd;
                            pcb.baseAddress = MEMORY.block1;
                            pcb.endAddress = endAdd;
                            pcb.outputAddress = endAdd;
                            pcb.quantumRemainder = 35;
                            jobIDFlag++;
                            pcb.jobID = jobIDFlag + 1;
                            pcb.partitionNo = 1;
                            pcb.traceBit = Integer.parseInt(tempTraceBit);
                            pcb.startAddress = Integer.toBinaryString((Integer.parseInt(tempStartAddress, 2) + MEMORY.block1));
                            pcb.startTime = SYSTEM.clock;
                            pcb.errorMessage = SYSTEM.error;
                            SYSTEM.error = null;
                            instrCount = 0;
                            Scheduler.readyQueue.add(pcb);
                            SYSTEM.jobQueue.add(pcb.jobID);
                            SYSTEM.noOfJobs++;
                        } else if (MEMORY.blockCheck[1] == false) {
                            /*block2*/
                            int endAdd = 0, memoryLocation = 0;

                            for (int x = 0; x < temp.size(); x++) {
                                MEMORY.memory(1, MEMORY.block2 + memoryLocation, temp.get(x));
                                memoryLocation = memoryLocation + (temp.get(x).length() / 12);
                                endAdd = MEMORY.block2 + memoryLocation;
                            }
                            MEMORY.blockCheck[1] = true;
                            SYSTEM.meanInternalFragmentation = SYSTEM.meanInternalFragmentation + (32-instrCount-outputSize);
                            currentBlock = endAdd;
                            pcb.baseAddress = MEMORY.block2;
                            pcb.endAddress = endAdd;
                            pcb.outputAddress = endAdd;
                            pcb.quantumRemainder = 35;
                            jobIDFlag++;
                            pcb.jobID = jobIDFlag + 1;
                            pcb.traceBit = Integer.parseInt(tempTraceBit);
                            pcb.startAddress = tempStartAddress;
                            pcb.partitionNo = 2;
                            pcb.startTime = SYSTEM.clock;
                            pcb.errorMessage = SYSTEM.error;
                            SYSTEM.error = null;
                            Scheduler.readyQueue.add(pcb);
                            SYSTEM.noOfJobs++;
                            instrCount = 0;
                        }

                        programCount = programCount + temp.size();
                        if (flag >= size) {
                            inputComplete = true;
                        }
                    } else if (instrCount + outputSize <= 64 && (MEMORY.blockCheck[2] == false || MEMORY.blockCheck[3] == false || MEMORY.blockCheck[4] == false)) {
                        if (MEMORY.blockCheck[2] == false) {
                            /*block3*/
                            int endAdd = 0, memoryLocation = 0;

                            for (int x = 0; x < temp.size(); x++) {

                                MEMORY.memory(1, MEMORY.block3 + memoryLocation, temp.get(x));
                                memoryLocation = memoryLocation + (temp.get(x).length() / 12);
                                endAdd = MEMORY.block3 + memoryLocation;

                            }

                            MEMORY.blockCheck[2] = true;
                            SYSTEM.meanInternalFragmentation = SYSTEM.meanInternalFragmentation + (64-instrCount-outputSize);
                            currentBlock = endAdd;
                            pcb.baseAddress = MEMORY.block3;
                            pcb.endAddress = endAdd;
                            pcb.outputAddress = endAdd;
                            pcb.quantumRemainder = 35;
                            jobIDFlag++;
                            pcb.jobID = jobIDFlag + 1;
                            pcb.traceBit = Integer.parseInt(tempTraceBit);
                            pcb.startAddress = tempStartAddress;
                            pcb.partitionNo = 3;
                            pcb.startTime = SYSTEM.clock;
                            pcb.errorMessage = SYSTEM.error;
                            SYSTEM.error = null;
                            Scheduler.readyQueue.add(pcb);
                            SYSTEM.noOfJobs++;
                            instrCount = 0;
                        } else if (MEMORY.blockCheck[3] == false) {
                            /*block4*/
                            int endAdd = 0, memoryLocation = 0;

                            for (int x = 0; x < temp.size(); x++) {

                                MEMORY.memory(1, MEMORY.block4 + memoryLocation, temp.get(x));
                                memoryLocation = memoryLocation + (temp.get(x).length() / 12);
                                endAdd = MEMORY.block4 + memoryLocation;

                            }

                            MEMORY.blockCheck[3] = true;
                            currentBlock = endAdd;
                            SYSTEM.meanInternalFragmentation = SYSTEM.meanInternalFragmentation + (64-instrCount-outputSize);
                            pcb.baseAddress = MEMORY.block4;
                            pcb.endAddress = endAdd;
                            pcb.outputAddress = endAdd;
                            pcb.quantumRemainder = 35;
                            jobIDFlag++;
                            pcb.jobID = jobIDFlag + 1;
                            pcb.traceBit = Integer.parseInt(tempTraceBit);
                            pcb.startAddress = tempStartAddress;
                            pcb.partitionNo = 4;
                            pcb.startTime = SYSTEM.clock;
                            pcb.errorMessage = SYSTEM.error;
                            SYSTEM.error = null;
                            Scheduler.readyQueue.add(pcb);
                            SYSTEM.noOfJobs++;
                            instrCount = 0;
                        } else if (MEMORY.blockCheck[4] == false) {
                            /*block5*/
                            int endAdd = 0, memoryLocation = 0;

                            for (int x = 0; x < temp.size(); x++) {

                                MEMORY.memory(1, MEMORY.block5 + memoryLocation, temp.get(x));
                                memoryLocation = memoryLocation + (temp.get(x).length() / 12);
                                endAdd = MEMORY.block5 + memoryLocation;
                            }

                            MEMORY.blockCheck[4] = true;
                            currentBlock = endAdd;
                            SYSTEM.meanInternalFragmentation = SYSTEM.meanInternalFragmentation + (64-instrCount-outputSize);
                            pcb.baseAddress = MEMORY.block5;
                            pcb.endAddress = endAdd;
                            pcb.outputAddress = endAdd;
                            pcb.quantumRemainder = 35;
                            jobIDFlag++;
                            pcb.jobID = jobIDFlag + 1;
                            pcb.traceBit = Integer.parseInt(tempTraceBit);
                            pcb.startAddress = tempStartAddress;
                            pcb.startTime = SYSTEM.clock;
                            pcb.errorMessage = SYSTEM.error;
                            SYSTEM.error = null;
                            pcb.partitionNo = 5;
                            Scheduler.readyQueue.add(pcb);
                            SYSTEM.noOfJobs++;
                            instrCount = 0;
                        }

                        programCount = programCount + temp.size();
                        if (flag >= size) {
                            inputComplete = true;
                        }
                    } else if (instrCount + outputSize <= 128 && (MEMORY.blockCheck[5] == false || MEMORY.blockCheck[6] == false)) {
                        if (MEMORY.blockCheck[5] == false) {
                            /*block6*/
                            int endAdd = 0, memoryLocation = 0;

                            for (int x = 0; x < temp.size(); x++) {

                                MEMORY.memory(1, MEMORY.block6 + memoryLocation, temp.get(x));
                                memoryLocation = memoryLocation + (temp.get(x).length() / 12);
                                endAdd = MEMORY.block6 + memoryLocation;

                            }
                            MEMORY.blockCheck[5] = true;
                            currentBlock = endAdd;
                            SYSTEM.meanInternalFragmentation = SYSTEM.meanInternalFragmentation + (128-instrCount-outputSize);
                            pcb.baseAddress = MEMORY.block6;
                            pcb.endAddress = endAdd;
                            pcb.outputAddress = endAdd;
                            pcb.quantumRemainder = 35;
                            jobIDFlag++;
                            pcb.jobID = jobIDFlag + 1;
                            pcb.traceBit = Integer.parseInt(tempTraceBit);
                            pcb.startAddress = tempStartAddress;
                            pcb.startTime = SYSTEM.clock;
                            pcb.errorMessage = SYSTEM.error;
                            SYSTEM.error = null;
                            pcb.partitionNo = 6;
                            Scheduler.readyQueue.add(pcb);
                            SYSTEM.noOfJobs++;
                            instrCount = 0;
                            if (flag >= size) {
                                inputComplete = true;
                            }
                        } else if (MEMORY.blockCheck[6] == false) {
                            /*block7*/
                            int endAdd = 0, memoryLocation = 0;

                            for (int x = 0; x < temp.size(); x++) {

                                MEMORY.memory(1, MEMORY.block7 + memoryLocation, temp.get(x));
                                memoryLocation = memoryLocation + (temp.get(x).length() / 12);
                                endAdd = MEMORY.block7 + memoryLocation;
                            }

                            MEMORY.blockCheck[6] = true;
                            currentBlock = endAdd;
                            SYSTEM.meanInternalFragmentation = SYSTEM.meanInternalFragmentation + (128-instrCount-outputSize);
                            pcb.baseAddress = MEMORY.block7;
                            pcb.endAddress = endAdd;
                            pcb.outputAddress = endAdd;
                            pcb.quantumRemainder = 35;
                            jobIDFlag++;
                            pcb.jobID = jobIDFlag + 1;
                            pcb.traceBit = Integer.parseInt(tempTraceBit);
                            pcb.startAddress = tempStartAddress;
                            pcb.startTime = SYSTEM.clock;
                            pcb.errorMessage = SYSTEM.error;
                            SYSTEM.error = null;
                            pcb.partitionNo = 7;
                            Scheduler.readyQueue.add(pcb);
                            SYSTEM.noOfJobs++;
                            instrCount = 0;
                        }

                        programCount = programCount + temp.size();
                    } else {

                        flag = flagBeforeTemp;
                        instrCount = 0;
                        //temp.clear();
                        break;
                    }
                } else if (temp.isEmpty()) {
                    ERROR_HANDLER.Error_Handler(15);
                }

                if (flag >= size) {

                    break;
                } else {
                    currentLine = inputPrograms.get(flag);
                }
                temp.clear();
            }
        }
    }

    static String hexToBin(String s) {
        String hexToBinary = "";
        hexToBinary = new BigInteger(s, 16).toString(2);
        int x = s.length() * 4;
        int y = hexToBinary.length();
        if (y < x) {
            /*to pad zeroes inorder to make it 12-bit word*/
            for (int i = 0; i < x - y; i++) {
                hexToBinary = "0" + hexToBinary;
            }
        }
        return hexToBinary;
    }
}
