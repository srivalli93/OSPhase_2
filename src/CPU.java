
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


/*
 *This class contains the cpu() method that takes starting address and tracebit as arguments and keeps looping indefinitely
 * until encountered a HLT() method or an error.This class furthur contains 4 other methods that represent each type of instruction.
 * Each type again has methods to process the instruction.
 */
 /**/
public class CPU {

    public static int ioClock;
    public static String EA = "0000";
    public static String arithmeticRegister = "";
    public static String trace = null;
    public static int comFlag = 0;
    public static boolean comArth = false;
    public static int currentJob = 0;
    static int jobID;
    static int PC;
    static int baseAddress;
    static int endAddress;
    static String startAddress;
    static int timeEntered;
    static int executionTime;
    static int timeCompleted;
    static int quantumClock = 35;
    static PCB p;
    static int outputAddress = 0;
    static int inputSize;
    /*to store input size*/
    static int inputSizeFlag;
    static int ioTime;
    static ArrayList<Integer> jobsInitiated = new ArrayList<Integer>();
    static int traceCounter = 0;
    static File file = new File("progress_file.txt");

    /*stoes the io time for the job*/

 /*cpu() method takes start address and trace bit as input, retrieves the instruction 
         * one-by-one from the memory, categorizes the instruction into type-1 or type-2 or
         * type-3 or type-4 based on the opcode and processes the instruction accordingly.
         * cpu() method also checks for the trace bit. If the trace bit is one it writes a 
         * trace file and doesn't if otherwise.         
     */
    public static void cpu(String start, String trace) throws IOException {

        /*Scheduling*/
        if (!Scheduler.readyQueue.isEmpty()) {

            p = Scheduler.readyQueue.remove();

            

            /*inifinite loop detection and terminating the job*/
            if (p.runTime > 20000) {

                SYSTEM.infiniteJobCount++;
                SYSTEM.infiniteJobID.add(p.jobID);
                SYSTEM.infiniteJobsTime = SYSTEM.infiniteJobsTime + p.runTime;
                MEMORY.memoryManager(p.baseAddress);
                ERROR_HANDLER.Error_Handler(29);
                MEMORY.memoryManager(p.baseAddress);
                Scheduler.checkForBlock();
            }

            updateToCPU();
            if (SYSTEM.clock % 200 == 0) {
                /*This prints the snapshot of the system for every 100 virtual units of */
                snapshot();
            }
            p.quantumRemainder = quantumClock;

        } else {
            if (!Scheduler.intermediate.isEmpty()) 
            {
            SYSTEM.idleTime = SYSTEM.idleTime + 10;
            SYSTEM.clock = SYSTEM.clock + 10;
            Scheduler.checkForBlock();
            
        }
            else{
                System.exit(0);
                
            }
        }
        if (!jobsInitiated.contains(jobID)) {

            FileWriter f1 = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(f1);

            writer.write("Job " + jobID + " has been initiated");
            writer.newLine();
            writer.close();
            f1.close();
            f1.close();
            jobsInitiated.add(jobID);
        }

        

        if (CPU.PC == 0) {
            start = p.startAddress;
        } else {
            start = Integer.toBinaryString(p.PC);
        }
        while (start.length() < 12) {

            start = 0 + start;
        }

        SYSTEM.reg[2] = binAdition(start, Integer.toBinaryString(CPU.baseAddress));

        String flag = "";
        String contentEA = "";

        if (!trace.equals("1") && !trace.equals("0")) {
            ERROR_HANDLER.Error_Handler(11);
        }
        while (true) {
            /*the loop is set to run indefinitely until it reaches halt*/

            SYSTEM.reg[3] = MEMORY.memory(0, Integer.parseInt(SYSTEM.reg[2], 2), SYSTEM.reg[3]);

            /*reading the contents in program counter to instruction*/
            SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");

            String temp = SYSTEM.reg[3].substring(1, 4);
            /* reading the opcode iinto a temp*/
            char indirectBit = SYSTEM.reg[3].charAt(0);

            if (SYSTEM.reg[3].charAt(4) == '0') {
                flag = "R5";
                arithmeticRegister = SYSTEM.reg[5];
            } else if (SYSTEM.reg[3].charAt(4) == '1') {
                flag = "R4";
                arithmeticRegister = SYSTEM.reg[4];
            }
            contentEA = MEMORY.memory(0, Integer.parseInt(EA, 2), contentEA);
            if ("1".equals(CPU.trace)) {
                PCB.traceFileContent.append(Integer.toHexString(Integer.parseInt(SYSTEM.reg[2], 2)).toUpperCase() + "\t\t\t" + Integer.toHexString(Integer.parseInt(SYSTEM.reg[3], 2)).toUpperCase() + "\t\t\t" + flag + " " + Integer.toHexString(Integer.parseInt(EA, 2)).toUpperCase() + "\t\t\t" + Integer.toHexString(Integer.parseInt(arithmeticRegister, 2)).toUpperCase() + " " + Integer.toHexString(Integer.parseInt(contentEA, 2)).toUpperCase());
            }

            switch (temp) {
                case "000":
                    type1(SYSTEM.reg[3]);
                    break;
                case "001":
                    type1(SYSTEM.reg[3]);
                    break;
                case "010":
                    type1(SYSTEM.reg[3]);
                    break;
                case "011":
                    type1(SYSTEM.reg[3]);
                    break;
                case "100":
                    type1(SYSTEM.reg[3]);
                    break;
                case "101":
                    type1(SYSTEM.reg[3]);
                    break;
                case "110":
                    type2(SYSTEM.reg[3]);
                    break;
                case "111":
                    if (indirectBit == '0') {
                        type3(SYSTEM.reg[3]);
                    } else if (indirectBit == '1') {
                        type4(SYSTEM.reg[3]);
                    }
                    break;
                default:
                    ERROR_HANDLER.Error_Handler(22);
                    MEMORY.memoryManager(p.baseAddress);
                    Scheduler.checkForBlock();

            }
            if (SYSTEM.reg[3].charAt(4) == '0') {
                flag = "R5";
                arithmeticRegister = SYSTEM.reg[5];
            } else if (SYSTEM.reg[3].charAt(4) == '1') {
                flag = "R4";
                arithmeticRegister = SYSTEM.reg[4];
            }

            if ("1".equals(CPU.trace)) {
                PCB.traceFileContent.append("\t\t\t\t\t" + Integer.toHexString(Integer.parseInt(arithmeticRegister, 2)).toUpperCase() + " " + Integer.toHexString(Integer.parseInt(SYSTEM.reg[3], 2)).toUpperCase());
                PCB.traceFileContent.append(System.getProperty("line.separator"));
            }

        }
    }
    
     

    /*The controls comes to this method when opcode of the instruction is of type1. This method furthur has several methods
         that are used to process the input instruction*/
    public static void type1(String address) throws IOException {
        SYSTEM.clock = SYSTEM.clock + 1;
        PCB.runTime++;
        executionTime++;
        if (SYSTEM.clock % 200 == 0) {
                /*This prints the snapshot of the system for every 100 virtual units of */
                snapshot();
            }
        char i1 = address.charAt(0);
        /*indirection bit*/
        String opCode = address.substring(1, 4);
        /*opcode*/
        char register = address.charAt(4);
        /*arithmetic register*/
        char indexBit = address.charAt(5);
        /*indexbit*/
        EA = address.substring(6);
        /*address*/
        while (EA.length() < 12) {
            EA = "0" + EA;
        }
        while (SYSTEM.reg[2].length() < 12) {
            SYSTEM.reg[2] = "0" + SYSTEM.reg[2];
        }

        EA = binAdition(EA, SYSTEM.reg[2]);
        EA = EA.substring(6);
        EA = "000000" + EA;
        EA = Integer.toBinaryString(Integer.parseInt(EA, 2) + CPU.baseAddress);

        if (Integer.parseInt(EA, 2) > endAddress) {

            EA = Integer.toBinaryString(Integer.parseInt(EA, 2) - CPU.baseAddress);

        }

        /*Below are the 4 different types of effective address calculations - Direct,Indirect,Indirect+Indexing and Indexing*/
        if (i1 == '0' && indexBit == '0') {
            /*direct addressing*/

            while (EA.length() < 12) {
                EA = "0" + EA;
            }
        } else if (i1 == '1' && indexBit == '0') {
            /*indirect addressing*/
            String tempVar = "";
            EA = MEMORY.memory(0, Integer.parseInt(EA, 2), tempVar);
            EA = "000000" + EA.substring(6, 12);
            EA = Integer.toBinaryString(Integer.parseInt(EA, 2) + p.baseAddress);
        } else if (i1 == '1' && indexBit == '1') {
            /*indirection+indexing*/
            String tempVar = "";
            String eaContents = "";

            tempVar = SYSTEM.reg[4];
            eaContents = MEMORY.memory(0, Integer.parseInt(EA, 2), eaContents);
            tempVar = MEMORY.memory(0, Integer.parseInt(tempVar, 2), tempVar);
            EA = binAdition(eaContents, tempVar);
            EA = EA.substring(6);
        } else if (i1 == '0' && indexBit == '1') {
            /*indexing*/
            String tempVar = "";
            tempVar = SYSTEM.reg[4];
            tempVar = MEMORY.memory(0, Integer.parseInt(tempVar, 2), tempVar);
            EA = binAdition(EA, tempVar);
            EA = EA.substring(6);
        } else {
            SYSTEM.error = "Invalid input for index bit and indirect bit";
            ERROR_HANDLER.Error_Handler(1);
        }

        switch (opCode) {
            case "000":
                /*AND*/
                switch (register) {
                    case '0':

                        SYSTEM.reg[5] = AND(register, MEMORY.memory(0, Integer.parseInt(EA, 2), SYSTEM.reg[5]));

                        break;
                    case '1':
                        SYSTEM.reg[4] = AND(register, MEMORY.memory(0, Integer.parseInt(EA, 2), SYSTEM.reg[4]));

                        break;
                    default:
                        SYSTEM.error = "Invalid input for arithmetic register bit";
                        ERROR_HANDLER.Error_Handler(2);
                        break;
                }
                break;
            case "001":
                /*ADD*/
                switch (register) {
                    case '0':
                        SYSTEM.reg[5] = ADD(register, EA);

                        break;

                    case '1':
                        SYSTEM.reg[4] = ADD(register, EA);

                        break;

                    default:
                        SYSTEM.error = "Invalid input for arithmetic register bit";
                        ERROR_HANDLER.Error_Handler(2);
                        break;

                }
                break;
            case "010":

                STR(register, EA);
                quantumClock--;

                break;
            case "011":

                LD(register, EA);
                quantumClock--;

                break;
            case "100":

                JMP(register, EA);
                quantumClock--;

                break;
            case "101":

                JPL(register, EA);
                quantumClock--;

                break;
            default:
                break;
        }
    }

    /*The control reaches this method if the opcode matches type2.
           Type2 instructions are the ones that contain IO operations to be performed and the halt*/
    public static void type2(String address) throws IOException {
        SYSTEM.clock = SYSTEM.clock + 1;
        executionTime++;
        PCB.runTime++;
        if (SYSTEM.clock % 200 == 0) {
                /*This prints the snapshot of the system for every 100 virtual units of */
                snapshot();
            }
        String opCode = address.substring(1, 4);
        /*opcode*/
        char register = address.charAt(4);
        /*register*/
        if ((address.charAt(5) == '1' && address.charAt(6) == '1') || (address.charAt(5) == '1' && address.charAt(7) == '1') || (address.charAt(7) == '1' && address.charAt(6) == '1') || (address.charAt(5) == '1' && address.charAt(6) == '1' && address.charAt(7) == '1')) {
            /*read operation*/
            SYSTEM.error = "Invalid input for arithmetic register bit";
            ERROR_HANDLER.Error_Handler(1);
        } else if (address.charAt(5) == '1' && address.charAt(6) == '0' && address.charAt(7) == '0') {
            /*read operation*/

            RD(register);
        } else if (address.charAt(6) == '1' && address.charAt(5) == '0' && address.charAt(7) == '0') {
            /*write operation*/

            WR(register);
        } else if (address.charAt(7) == '1' && address.charAt(6) == '0' && address.charAt(5) == '0') {
            /*halt operation*/

            HLT();
        }
    }

    /*Control reaches this method when the opcode matches 111 and the instruction type specifier(first bit) is zero.
    Type 3 instructions perform operations on registers.*/
    public static void type3(String address) throws IOException {
        SYSTEM.clock = SYSTEM.clock + 1;
        executionTime++;
        PCB.runTime++;
        if (SYSTEM.clock % 200 == 0) {
                /*This prints the snapshot of the system for every 100 virtual units of */
                snapshot();
            }
        String opCode = address.substring(1, 4);
        /*opcode*/
        char register = address.charAt(4);
        /*register*/
        String operation = address.substring(5, 12);
        if (operation.equals("1000000")) {
            /*clear operation-Clear bit is set*/

            CLR(register);
            quantumClock--;

        } else if (operation.equals("0100000")) {
            /*increment bit is set*/

            INC(register);
            quantumClock--;

        } else if (operation.equals("0010000")) {
            /*complement bit is set*/

            COM(register);
            quantumClock--;

        } else if (operation.equals("0001000")) {
            /*byte swap bit is set*/

            BSW(register);
            quantumClock--;

        } else if (operation.equals("0000100")) {
            /*rotate left by 1 bit*/

            RTL(register, 0);
            quantumClock--;

        } else if (operation.equals("0000101")) {
            /*rotate left by 2 bits*/

            RTL(register, 1);
            quantumClock--;

        } else if (operation.equals("0000011")) {
            /*rotate right by 2 bits*/

            RTR(register, 1);
            quantumClock--;

        } else if (operation.equals("0000010")) {
            /*rotate right by 1 bit*/

            RTR(register, 0);
            quantumClock--;

        } else {

        }
    }

    /*Control reaches this method when the opcode matches 111 and the instruction type specifier(first bit) is one
    Type 4 instructions compare the contents of register with zero and performs the operations accordingly.*/
    public static void type4(String address) throws IOException {
        SYSTEM.clock = SYSTEM.clock + 1;
        executionTime++;
        PCB.runTime++;
        String opCode = address.substring(1, 4);
        /*opcode*/
        char register = address.charAt(4);
        /*register*/
        String operation = address.substring(5, 8);
        switch (operation) {
            case "000":

                NSK();
                quantumClock--;

                break;
            case "001":

                GTR(register);
                quantumClock--;

                break;
            case "010":

                LSS(register);
                quantumClock--;

                break;
            case "011":

                NEQ(register);
                quantumClock--;

                break;
            case "100":

                EQL(register);
                quantumClock--;

                break;
            case "101":

                GRE(register);
                quantumClock--;

                break;
            case "110":

                LSE(register);
                quantumClock--;

                break;
            case "111":

                USK();
                quantumClock--;

                break;
            default:

                ERROR_HANDLER.Error_Handler(210);
                break;
        }
    }

    private static String AND(char x, String y) throws IOException {

        String var1 = "";
        var1 = y;
        int size = var1.length();
        String var2 = "";
        String result = "";
        switch (x) {
            case '0': {
                var2 = SYSTEM.reg[5];
                break;
            }
            case '1': {
                var2 = SYSTEM.reg[4];
                break;
            }
            default:

                ERROR_HANDLER.Error_Handler(211);
                break;
        }
        while (var2.length() < 12) {
            var2 = "0" + var2;
        }
        for (int i = 0; i < size; i++) {
            if (var1.charAt(i) == var2.charAt(i)) {
                result = result + '1';
            } else {
                result = result + '0';
            }
        }
        return result;
    }

    private static String ADD(char x, String y) throws IOException {

        int temp = Integer.parseInt(y, 2);
        String var1 = "";
        int carry = 0;
        var1 = MEMORY.memory(0, temp, var1);
        int size = var1.length();
        String var2 = "";
        String result = "";
        switch (x) {
            case '0': {
                var2 = SYSTEM.reg[5];
                break;
            }
            case '1': {
                var2 = SYSTEM.reg[4];
                break;
            }
            default:

                ERROR_HANDLER.Error_Handler(211);
                break;
        }
        if (!var2.isEmpty()) {
            result = CPU.binAdition(var1, var2);
            if (result.length() > 12) {
                result = result.substring(1);
            }
        }

        return result;
    }

    private static void STR(char x, String y) throws IOException {
        switch (x) {
            case '0': {
                int temp = Integer.parseInt(y, 2);
                MEMORY.memory(1, temp, SYSTEM.reg[5]);

                break;
            }
            case '1': {
                int temp = Integer.parseInt(y, 2);
                MEMORY.memory(1, temp, SYSTEM.reg[4]);

                break;
            }
            default:

                ERROR_HANDLER.Error_Handler(211);
                break;
        }
    }

    private static void LD(char x, String y) throws IOException {
        switch (x) {
            case '0': {
                int temp = Integer.parseInt(y, 2);
                SYSTEM.reg[5] = MEMORY.memory(0, temp, SYSTEM.reg[5]);

                break;
            }
            case '1': {
                int temp = Integer.parseInt(y, 2);
                SYSTEM.reg[4] = MEMORY.memory(0, temp, SYSTEM.reg[4]);

                break;
            }
            default:

                ERROR_HANDLER.Error_Handler(211);
                break;
        }

    }

    private static void JMP(char x, String y) {
        SYSTEM.reg[2] = y;
    }

    private static void JPL(char x, String y) throws IOException {
        switch (x) {
            case '0': {
                String temp = SYSTEM.reg[2];
                SYSTEM.reg[5] = temp;
                SYSTEM.reg[2] = y;

                break;
            }
            case '1': {
                String temp = SYSTEM.reg[2];
                SYSTEM.reg[4] = temp;
                SYSTEM.reg[2] = y;
                break;
            }
            default:

                ERROR_HANDLER.Error_Handler(211);
                break;
        }
    }

    private static void RD(char x) throws IOException {

        ioClock = ioClock + 10;
        quantumClock--;
        ioTime = ioTime + 10;
        SYSTEM.clock = SYSTEM.clock + 10;
        PCB.runTime = PCB.runTime + 10;
        int r5 = CPU.inputSizeFlag;
        int r4 = CPU.inputSizeFlag;
        if (x == '0') {

            SYSTEM.reg[5] = MEMORY.memory(0, CPU.endAddress - CPU.inputSizeFlag, SYSTEM.reg[5]);
            CPU.inputSizeFlag--;
        } else if (x == '1') {
            SYSTEM.reg[4] = MEMORY.memory(0, CPU.endAddress - CPU.inputSizeFlag, SYSTEM.reg[4]);
            CPU.inputSizeFlag--;
        }
        File file = new File("progress_file.txt");
        FileWriter f1 = new FileWriter(file, true);
        BufferedWriter writer = new BufferedWriter(f1);

        writer.write("Job " + jobID + " is going to read operation");
        writer.newLine();
        writer.close();
        f1.close();
        f1.close();
        onStoppingExecution();
        Scheduler.update(p);
    }

    private static void WR(char x) throws IOException {

        ioClock = ioClock + 10;
        SYSTEM.clock = SYSTEM.clock + 10;
        ioTime = ioTime + 10;
        PCB.runTime = PCB.runTime + 10;
        quantumClock--;

        switch (x) {
            case '0':
                if (SYSTEM.reg[5].length() < 12) {
                    SYSTEM.reg[5] = "0" + SYSTEM.reg[5];
                    SYSTEM.result = Integer.parseInt(SYSTEM.reg[5], 2);

                    MEMORY.memory(1, outputAddress, SYSTEM.reg[5]);
                    outputAddress++;
                    p.outputAddress = outputAddress;
                } else if (SYSTEM.reg[5].charAt(0) == '1') {
                    String newTemp = SYSTEM.reg[5];
                    while (newTemp.length() < 32) {
                        newTemp = "1" + newTemp;
                    }
                    long l = Long.parseLong(newTemp, 2);
                    SYSTEM.result = (int) l;

                    MEMORY.memory(1, outputAddress, SYSTEM.reg[5]);

                    outputAddress++;
                    p.outputAddress = outputAddress;
                } else {
                    SYSTEM.result = Integer.parseInt(SYSTEM.reg[5], 2);

                    MEMORY.memory(1, outputAddress, SYSTEM.reg[5]);

                    outputAddress++;
                    p.outputAddress = outputAddress;

                }

                break;
            case '1':

                if (SYSTEM.reg[4].length() < 12) {

                    SYSTEM.reg[4] = "0" + SYSTEM.reg[4];
                    
                    
                    SYSTEM.result = Integer.parseInt(SYSTEM.reg[4], 2);

                    MEMORY.memory(1, outputAddress, SYSTEM.reg[4]);
                    outputAddress++;
                    p.outputAddress = outputAddress;

                } else if (SYSTEM.reg[4].charAt(0) == '1') {
                    String newTemp = SYSTEM.reg[4];
                    while (newTemp.length() < 32) {
                        newTemp = "1" + newTemp;
                    }
                    long l = Long.parseLong(newTemp, 2);
                    SYSTEM.result = (int) l;

                    MEMORY.memory(1, outputAddress, SYSTEM.reg[4]);

                    outputAddress++;
                    p.outputAddress = outputAddress;
                } else {
                    SYSTEM.result = Integer.parseInt(SYSTEM.reg[4], 2);

                    MEMORY.memory(1, outputAddress, SYSTEM.reg[4]);

                    outputAddress++;
                    p.outputAddress = outputAddress;
                }

                break;
            default:

                ERROR_HANDLER.Error_Handler(211);
                MEMORY.memoryManager(p.baseAddress);
                break;
        }
        FileWriter f1 = new FileWriter(file, true);
        BufferedWriter writer = new BufferedWriter(f1);

        writer.write("Job " + jobID + " is going to write operation");

        writer.newLine();
        writer.close();
        f1.close();
        f1.close();
        onStoppingExecution();

        Scheduler.update(p);

    }

    public static void HLT() throws IOException {
        int x = p.outputsize;
        int y = p.outputAddress - x;

        while (x != 0) {
            String var = MEMORY.memory(0, y, "");
            y++;
            x--;

        }

        /*summation of system time of all jobs*/
        SYSTEM.systemTimeSum = SYSTEM.systemTimeSum + (SYSTEM.clock - p.startTime);

        FileWriter f1 = new FileWriter(file, true);
        BufferedWriter writer = new BufferedWriter(f1);
        writer.write("Job " + jobID + " is terminated");
        writer.newLine();
        writer.newLine();
        writer.write("----------------------------------------------------");
        writer.newLine();
        writer.write("Job Id:  " + p.jobID + "(DEC)");
        writer.newLine();
        writer.newLine();
        writer.write("The part of partition occupied by job");
        writer.newLine();
        partitionContents(writer);
        writer.write("Input lines :");
        writer.newLine();
        for (int i = p.endAddress - p.inputSize; i < p.endAddress; i++) {
            writer.write("\t\t" + Integer.toHexString(Integer.parseInt(MEMORY.memory(0, i, ""), 2)).toUpperCase() + " (HEX)");
            writer.newLine();
        }
        writer.newLine();
        writer.write("Output lines :");
        writer.newLine();
        for (int i = p.outputAddress - p.outputsize; i < p.outputAddress; i++) {
            writer.write("\t\t" + Integer.toHexString(Integer.parseInt(MEMORY.memory(0, i, ""), 2)).toUpperCase() + " (HEX)");
            writer.newLine();
        }

        writer.write("Partition number and size occupied : " + p.partitionNo + " (DEC)  " + (p.outputsize + (p.endAddress - p.baseAddress)) + " (DEC)");

        writer.newLine();

        writer.write("\t\t\t\t  " + (p.outputAddress - p.baseAddress) + " (DEC)");
        writer.newLine();
        writer.write("Time the job entered the System:  " + Integer.toHexString(p.startTime).toUpperCase() + " (HEX)");
        writer.newLine();
        writer.write("Time the job is leaving the System:  " + Integer.toHexString(SYSTEM.clock).toUpperCase() + " (HEX)");
        writer.newLine();
        writer.write("Execution time :  " + executionTime + " (DEC)");
        writer.newLine();
        writer.write("Time spent doing IO:  " + ioTime + " (DEC)");
        writer.newLine();
        writer.write("Total run time:  " + (executionTime + ioTime) + " (DEC)");
        writer.newLine();
        if (!"".equals(p.errorMessage) && p.errorMessage != null) {
            writer.write("Warning Message:  " + p.errorMessage);
            writer.newLine();
        }
        SYSTEM.error = null;
        writer.write("Job termination:  " + SYSTEM.terminationMsg);
        writer.newLine();

        writer.write("----------------------------------------------------");
        writer.newLine();

        writer.close();
        f1.close();

        SYSTEM.ioTimeSum = SYSTEM.ioTimeSum + p.ioTime;

        SYSTEM.execTimeSum = SYSTEM.execTimeSum + p.executionTime;

        if (CPU.trace.equals("1")) {
            PCB.printToTraceFile(p);
        }
        MEMORY.memoryManager(baseAddress);

        if (Scheduler.readyQueue.isEmpty() && Scheduler.readyForReadyQueue.isEmpty() && Scheduler.intermediate.isEmpty()) {

            statistics();

            System.exit(0);/*Normal termination*/


        } else {
            if (Scheduler.readyQueue.isEmpty()) {
                SYSTEM.idleTime++;
            }
            LOADER.readInput(SYSTEM.loaderInput);
            Scheduler.checkForBlock();
        }

    }

    public static String binAdition(String a, String b) {
        /*performs binary addition when two binary numbers are passed*/
        int sizeA = a.length();
        int sizeB = b.length();

        int carry = 0;
        if (sizeA < 12) {
            while (a.length() < 12) {
                a = "0" + a;
            }
        }
        if (sizeB < 12) {
            while (b.length() < 12) {
                b = "0" + b;
            }
        }
        String addResult = new String();
        if (a.charAt(0) == '1' || b.charAt(0) == '1' && comArth == true) {
            comArth = false;
            if (a.charAt(0) == '1') {
                while (a.length() < 32) {
                    a = "1" + a;
                }
                long l = Long.parseLong(a, 2);
                int temp1 = (int) l;
                int tempResult = temp1 + Integer.parseInt(b, 2);

                addResult = Integer.toBinaryString(tempResult);
            } else if (b.charAt(0) == '1') {
                while (b.length() < 32) {
                    b = "1" + b;
                }
                long l = Long.parseLong(b, 2);

                int tempResult = (int) l + Integer.parseInt(a, 2);
                addResult = Integer.toBinaryString(tempResult);
            }
        } else {

            sizeA = a.length();

            sizeB = b.length();
            if (sizeA > 12 || sizeB > 12) {

            }
            for (int i = sizeA - 1; i >= 0; i--) {

                int sum = Integer.parseInt(a.substring(i, i + 1)) + Integer.parseInt(b.substring(i, i + 1)) + carry;

                switch (sum) {
                    case 0:
                        addResult = "0" + addResult;
                        break;
                    case 1:
                        addResult = "1" + addResult;
                        if (carry == 1) {
                            carry = 0;
                        }
                        break;
                    case 2:
                        addResult = "0" + addResult;
                        carry = 1;
                        break;
                    case 3:
                        addResult = "1" + addResult;
                        carry = 1;
                        break;
                    default:
                        break;
                }

            }
            if (carry == 1) {
                addResult = "1" + addResult;
            }

        }
        if (addResult.length() > 12) {
            addResult = addResult.substring(addResult.length() - 12);
        }
        return addResult;
    }

    private static void RTL(char register, int i) {
        if (register == '0') {

            if (i == 0) {

                SYSTEM.reg[5] = Integer.toBinaryString(Integer.parseInt(SYSTEM.reg[5], 2) << 1);

            } else if (i == 1) {

                SYSTEM.reg[5] = Integer.toBinaryString(Integer.parseInt(SYSTEM.reg[5], 2) << 2);

            }
        } else if (register == '1') {

            if (i == 0) {

                SYSTEM.reg[4] = Integer.toBinaryString(Integer.parseInt(SYSTEM.reg[4], 2) << 1);

            } else if (i == 1) {

                SYSTEM.reg[4] = Integer.toBinaryString(Integer.parseInt(SYSTEM.reg[4], 2) << 2);

            }
        }
    }

    private static void RTR(char register, int i) {
        if (register == '0') {

            if (i == 0) {
                SYSTEM.reg[5] = Integer.toBinaryString(Integer.parseInt(SYSTEM.reg[5], 2) >> 1);

            } else {
                SYSTEM.reg[5] = Integer.toBinaryString(Integer.parseInt(SYSTEM.reg[5], 2) >> 2);

            }

        } else if (register == '1') {

            if (i == 0) {
                SYSTEM.reg[4] = Integer.toBinaryString(Integer.parseInt(SYSTEM.reg[4], 2) >> 1);

            } else {
                SYSTEM.reg[4] = Integer.toBinaryString(Integer.parseInt(SYSTEM.reg[4], 2) >> 2);

            }
        }
    }

    private static void BSW(char register) {
        if (register == '0') {
            String temp = SYSTEM.reg[5].substring(0, 6);
            /*byte swap by 6 bits (left circular shift)*/
            SYSTEM.reg[5] = SYSTEM.reg[5].substring(6, 12);
            SYSTEM.reg[5] = SYSTEM.reg[5].concat(temp);

        } else if (register == '1') {
            String temp = SYSTEM.reg[4].substring(0, 6);
            SYSTEM.reg[4] = SYSTEM.reg[4].substring(6, 12);
            SYSTEM.reg[4] = SYSTEM.reg[4].concat(temp);
        }
    }

    private static void COM(char register) {
        comFlag = Integer.parseInt(SYSTEM.reg[2], 2);
        if (register == '0') {
            /*one's complement*/
            SYSTEM.reg[5] = SYSTEM.reg[5].replaceAll("0", "2");
            SYSTEM.reg[5] = SYSTEM.reg[5].replaceAll("1", "0");
            SYSTEM.reg[5] = SYSTEM.reg[5].replaceAll("2", "1");

        } else if (register == '1') {
            SYSTEM.reg[4] = SYSTEM.reg[4].replaceAll("0", "2");
            SYSTEM.reg[4] = SYSTEM.reg[4].replaceAll("1", "0");
            SYSTEM.reg[4] = SYSTEM.reg[4].replaceAll("2", "1");
        }
    }

    private static void INC(char register) {
        if (Integer.parseInt(SYSTEM.reg[2], 2) == (comFlag + 1)) {
            comArth = true;
        }

        if (register == '0') {
            
            SYSTEM.reg[5] = CPU.binAdition(SYSTEM.reg[5], "000000000001");

            if (comArth == true && SYSTEM.reg[5].charAt(0) == '1') {

            }
        } else if (register == '1') {
            SYSTEM.reg[4] = CPU.binAdition(SYSTEM.reg[4], "000000000001");
            if (comArth == true && SYSTEM.reg[4].charAt(0) == '1') {

            }
        }

    }

    private static void CLR(char register) {
        if (register == '0') {
            SYSTEM.reg[5] = "000000000000";
        } else if (register == '1') {
            SYSTEM.reg[4] = "000000000000";
        }
    }

    private static void NSK() {
        /*no operation;*/
    }

    private static void GTR(char register) {

        if (register == '0') {
            if (SYSTEM.reg[5].length() < 12) {
                while (SYSTEM.reg[5].length() < 12) {
                    SYSTEM.reg[5] = "0" + SYSTEM.reg[5];
                }
                if (Integer.parseInt(SYSTEM.reg[5], 2) > 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (SYSTEM.reg[5].charAt(0) == '1') {
                String newTemp = SYSTEM.reg[5];
                while (newTemp.length() < 32) {
                    newTemp = "1" + newTemp;
                }
                long l = Long.parseLong(newTemp, 2);
                if ((int) l > 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (Integer.parseInt(SYSTEM.reg[5], 2) > 0) {
                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }
        } else if (register == '1') {
            if (SYSTEM.reg[4].length() < 12) {
                SYSTEM.reg[4] = "0" + SYSTEM.reg[4];
            } else if (SYSTEM.reg[4].charAt(0) == '1') {
                String newTemp = SYSTEM.reg[4];
                while (newTemp.length() < 32) {
                    newTemp = "1" + newTemp;
                }
                long l = Long.parseLong(newTemp, 2);
                if ((int) l > 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (Integer.parseInt(SYSTEM.reg[4], 2) > 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }
        }
    }

    private static void LSS(char register) {

        if (register == '0') {
            if (SYSTEM.reg[5].length() < 12) {
                while (SYSTEM.reg[5].length() < 12) {
                    SYSTEM.reg[5] = "0" + SYSTEM.reg[5];
                }
                if (Integer.parseInt(SYSTEM.reg[5], 2) < 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (SYSTEM.reg[5].charAt(0) == '1') {
                String newTemp = SYSTEM.reg[5];
                while (newTemp.length() < 32) {
                    newTemp = "1" + newTemp;
                }
                long l = Long.parseLong(newTemp, 2);

                if ((int) l < 0) {

                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (Integer.parseInt(SYSTEM.reg[5], 2) < 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }

        } else if (register == '1') {
            if (SYSTEM.reg[4].length() < 12) {
                while (SYSTEM.reg[4].length() < 12) {
                    SYSTEM.reg[4] = "0" + SYSTEM.reg[4];
                }
                if (Integer.parseInt(SYSTEM.reg[4], 2) < 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (SYSTEM.reg[4].charAt(0) == '1') {
                String newTemp = SYSTEM.reg[4];
                while (newTemp.length() < 32) {
                    newTemp = "1" + newTemp;
                }
                long l = Long.parseLong(newTemp, 2);
                if ((int) l < 0) {

                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (Integer.parseInt(SYSTEM.reg[4], 2) < 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }
        }
    }

    private static void NEQ(char register) {
        if (register == '0') {
            if (Integer.parseInt(SYSTEM.reg[5], 2) != 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }
        } else if (register == '1') {
            if (Integer.parseInt(SYSTEM.reg[4], 2) != 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }
        }
    }

    private static void EQL(char register) {
        if (register == '0') {
            if (Integer.parseInt(SYSTEM.reg[5], 2) == 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }
        } else if (register == '1') {
            if (Integer.parseInt(SYSTEM.reg[4], 2) == 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }
        }
    }

    private static void GRE(char register) {
        if (register == '0') {

            if (SYSTEM.reg[5].length() < 12) {
                while (SYSTEM.reg[5].length() < 12) {
                    SYSTEM.reg[5] = "0" + SYSTEM.reg[5];
                }
                if (Integer.parseInt(SYSTEM.reg[5], 2) >= 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (SYSTEM.reg[5].charAt(0) == '1' && SYSTEM.reg[5].length() >= 12) {
                String newTemp = SYSTEM.reg[5];
                while (newTemp.length() < 32) {
                    newTemp = "1" + newTemp;
                }
                long l = Long.parseLong(newTemp, 2);
                if ((int) l >= 0) {

                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (Integer.parseInt(SYSTEM.reg[5], 2) >= 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }

        } else if (register == '1') {

            if (SYSTEM.reg[4].length() < 12) {
                while (SYSTEM.reg[4].length() < 12) {
                    SYSTEM.reg[4] = "0" + SYSTEM.reg[4];
                }
                if (Integer.parseInt(SYSTEM.reg[4], 2) >= 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (SYSTEM.reg[4].charAt(0) == '1' && SYSTEM.reg[4].length() >= 12) {
                String newTemp = SYSTEM.reg[4];
                while (newTemp.length() < 32) {
                    newTemp = "1" + newTemp;
                }
                long l = Long.parseLong(newTemp, 2);
                if ((int) l >= 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (Integer.parseInt(SYSTEM.reg[4], 2) >= 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }

        }
    }

    private static void LSE(char register) {

        if (register == '0') {
            if (SYSTEM.reg[5].length() < 12) {
                while (SYSTEM.reg[5].length() < 12) {
                    SYSTEM.reg[5] = "0" + SYSTEM.reg[5];
                }
                if (Integer.parseInt(SYSTEM.reg[5], 2) <= 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (SYSTEM.reg[5].charAt(0) == '1' && SYSTEM.reg[5].length() >= 12) {
                String newTemp = SYSTEM.reg[5];
                while (newTemp.length() < 32) {
                    newTemp = "1" + newTemp;
                }
                long l = Long.parseLong(newTemp, 2);

                if ((int) l <= 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (Integer.parseInt(SYSTEM.reg[5], 2) <= 0) {

                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }

        } else if (register == '1') {
            if (SYSTEM.reg[4].length() < 12) {
                while (SYSTEM.reg[4].length() < 12) {
                    SYSTEM.reg[4] = "0" + SYSTEM.reg[4];
                }
                if (Integer.parseInt(SYSTEM.reg[4], 2) <= 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (SYSTEM.reg[4].charAt(0) == '1') {
                String newTemp = SYSTEM.reg[4];
                while (newTemp.length() < 32) {
                    newTemp = "1" + newTemp;
                }
                long l = Long.parseLong(newTemp, 2);
                if ((int) l <= 0) {
                    SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
                }
            } else if (Integer.parseInt(SYSTEM.reg[4], 2) <= 0) {
                SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
            }
        }
    }

    private static void USK() {

        SYSTEM.reg[2] = binAdition(SYSTEM.reg[2], "000000000001");
    }

    public static String addOperation(String a, String b) {
        /*performs 2's complement addition when two binary numbers are passed*/
        int size1 = a.length() - 1;
        int size2 = b.length() - 1;
        b = b.replaceAll("0", "2");
        /*the below three lines of code gives 1s complement of b*/
        b = b.replaceAll("1", "0");
        b = b.replaceAll("2", "1");

        b = binAdition(b, "000000000001");
        /*this gives the 2s complement of b*/
        String result = new String();

        result = binAdition(a, b);

        return result.substring(result.length() - 12);

    }

    public static void onStoppingExecution() {
        p.PC = Integer.parseInt(SYSTEM.reg[2], 2) - CPU.baseAddress;

        p.jobID = CPU.jobID;
        p.inputSize = CPU.inputSize;
        p.inputSizeFlag = CPU.inputSizeFlag;
        p.baseAddress = CPU.baseAddress;
        p.endAddress = CPU.endAddress;
        p.reg = SYSTEM.reg;
        p.quantumRemainder = quantumClock;
        p.outputAddress = CPU.outputAddress;
        quantumClock = 35;
        p.sysClockAtBlock = SYSTEM.clock;
        p.ioTime = p.ioTime + ioTime;
        p.executionTime = p.executionTime + executionTime;
        ioTime = 0;
        executionTime = 0;
    }

    public static void statistics() throws IOException {
        File file = new File("progress_file.txt");
        FileWriter f1 = new FileWriter(file, true);
        BufferedWriter writer = new BufferedWriter(f1);

        SYSTEM.runTime = SYSTEM.execTimeSum + SYSTEM.ioTimeSum;

        writer.newLine();
        writer.write("----------------------------------------------------");
        writer.newLine();
        writer.write("-------------------STATISTICS-----------------------");
        writer.newLine();
        writer.write("Current value of Clock : (HEX)" + Integer.toHexString(SYSTEM.clock));
        writer.newLine();
        writer.write("Mean user job RUN time : (DEC)" + (float) (SYSTEM.runTime / SYSTEM.noOfJobs));
        writer.newLine();
        writer.write("Mean user job EXECUTION time : (DEC)" + (float) (SYSTEM.execTimeSum / SYSTEM.noOfJobs));
        writer.newLine();
        writer.write("Mean user job IO time : (DEC)" + (float) (SYSTEM.ioTimeSum / SYSTEM.noOfJobs));
        writer.newLine();
        writer.write("Mean user job time in the SYSTEM : (DEC)" + (float) (SYSTEM.systemTimeSum / SYSTEM.noOfJobs));
        writer.newLine();
        writer.write("Total CPU idle time : (DEC)" + ((SYSTEM.clock - SYSTEM.runTime) < 0 ? 0 : (SYSTEM.clock - SYSTEM.runTime)));
        writer.newLine();
        writer.write("Number of Normal Terminations : " + (SYSTEM.noOfJobs - SYSTEM.noOfAbTJobs));
        writer.newLine();
        writer.write("Number of Abnormal Terminations : " + SYSTEM.noOfAbTJobs);
        writer.newLine();
        writer.write("Total time lost due to abnormally terminated jobs : (DEC)" + SYSTEM.timeLostAbT);
        writer.newLine();
        writer.write("Total time lost due to partial processing of inifinite loops : (DEC)" + SYSTEM.infiniteJobsTime);
        writer.newLine();
        writer.write("IDs of jobs considered infinite : " + SYSTEM.infiniteJobID.toString());
        writer.newLine();
        writer.write("Mean internal fragmentation for given jobs : (DEC)" + (SYSTEM.meanInternalFragmentation/SYSTEM.noOfJobs));
        writer.newLine();
        writer.close();
        f1.close();
        f1.close();
    }

    public static void updateToCPU() {
        CPU.endAddress = p.endAddress;
        CPU.inputSize = p.inputSize;
        CPU.inputSizeFlag = p.inputSizeFlag;
        CPU.jobID = p.jobID;
        CPU.baseAddress = p.baseAddress;
        CPU.executionTime = 0;
        CPU.trace = String.valueOf(p.traceBit);
        CPU.PC = p.PC;
        SYSTEM.reg = p.reg;
        CPU.outputAddress = p.outputAddress;
        CPU.ioTime = 0;

    }

    static void partitionContents(BufferedWriter writer) throws IOException {

        String ba = Integer.toHexString(baseAddress);
        while (ba.length() < 4) {
            ba = "0" + ba;
        }
        writer.write(ba + "\t");
        for (int i = baseAddress; i < outputAddress; i++) {
            /*writing the first 256 words in memory*/
            String hex = "";
            if (MEMORY.mainMemory[i].equals("zero")) {
                hex = "null";
            } else {
                hex = Integer.toHexString(Integer.parseInt(MEMORY.mainMemory[i], 2)).toUpperCase();
            }
            while (hex.length() < 4) {
                hex = "0" + hex;
            }
            writer.write("word" + hex + "\t");

            if ((i + 1) % 8 == 0) {
                writer.newLine();
                String x = Integer.toHexString(i + 1);
                while (x.length() < 4) {
                    x = "0" + x;
                }
                writer.write(x + "\t");
            }
        }
        writer.newLine();

    }

    public static void snapshot() throws IOException {
        ArrayList<Integer> readyJobID = new ArrayList<Integer>();
        ArrayList<Integer> blockJobID = new ArrayList<Integer>();
        for (int i = 0; i < Scheduler.intermediate.size(); i++) {
            blockJobID.add(Scheduler.intermediate.get(i).jobID);
        }

        for (Iterator<PCB> it = Scheduler.readyQueue.iterator(); it.hasNext();) {
            int s = it.next().jobID;
            readyJobID.add(s);
        }

        File file = new File("progress_file.txt");
        FileWriter f1 = new FileWriter(file, true);
        BufferedWriter writer = new BufferedWriter(f1);
        writer.write("---------------------SNAPSHOT----------------------------");
        writer.newLine();
        writer.write("Current job executing  " + jobID);
        writer.newLine();
        writer.write("Present block queue :" + blockJobID.toString());
        writer.newLine();
        writer.write("Present ready queue :" + readyJobID.toString());
        writer.newLine();
        writer.write("---------------------------------------------------------");
        writer.newLine();
        writer.close();
        f1.close();

    }

}
