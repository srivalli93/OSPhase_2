import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sri
 */

public class Scheduler {

    public static Queue<PCB> readyQueue = new LinkedList<PCB>();
    public static Queue<PCB> blockQueue = new LinkedList<PCB>();
    static ArrayList<PCB> intermediate = new ArrayList<PCB>();
    /*replica of blockQ*/
    static ArrayList<PCB> readyForReadyQueue = new ArrayList<PCB>();
    /*elements are put into readyForReadyQ as soon as they complete the 10 units in blockQ*/
    static ArrayList<Integer> temp = new ArrayList<Integer>();
    static String startAddress ="";
    static int traceBit = 0;
    
    static void schedule() {

        try {
            CPU.cpu(startAddress, String.valueOf(traceBit));

        } catch (IOException ex) {
            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static void update(PCB p) {
        PCB blockedpcb = new PCB();
        blockedpcb = p;
        blockQueue.add(blockedpcb);
        intermediate.add(blockQueue.remove());
        checkForBlock();
        schedule();
    }
    
     
    public static void skipErrorJob() throws IOException {
        File file = new File("progress_file.txt");
        FileWriter f1 = new FileWriter(file, true);
        BufferedWriter writer = new BufferedWriter(f1);
        LOADER.jobIDFlag++;
        writer.write("Job " + (LOADER.jobIDFlag + 1) + " is terminated abnormally");
        writer.newLine();
        writer.newLine();
        writer.write("----------------------------------------------------");
        writer.newLine();
        writer.write("Job Id:  " + (LOADER.jobIDFlag + 1) + "(DEC)");
        writer.newLine();
        writer.newLine();
        writer.write("The part of partition occupied by job");
        writer.newLine();
        writer.write("Input lines  ");
        
        writer.newLine();
        if (!"".equals(SYSTEM.error)) {
            writer.write("Error Message:  " + SYSTEM.error);
            writer.newLine();
            SYSTEM.error = null;
        }
        writer.write("Job termination:  " + SYSTEM.terminationMsg);
        writer.newLine();

        writer.write("----------------------------------------------------");
        writer.newLine();

        writer.close();
        f1.close();

        SYSTEM.error = "";
        SYSTEM.terminationMsg = "Normal Termination";
    }

    public static void checkForBlock() {
        int index = 0;
        for (int x = 0; x < intermediate.size(); x++) {
            if (SYSTEM.clock - intermediate.get(x).sysClockAtBlock >= 10) {
                readyForReadyQueue.add(intermediate.get(x));
                intermediate.remove(x);
            }
        }
        for (int y = 0; y < readyForReadyQueue.size(); y++) {
            int qr = readyForReadyQueue.get(y).quantumRemainder;
            temp.add(y, qr);

        }
        if (!temp.isEmpty()) {
            index = temp.indexOf(Collections.max(temp));
            readyQueue.add(readyForReadyQueue.get(index));
            readyForReadyQueue.remove(index);
            temp.remove(index);
        }
        schedule();
    }
}