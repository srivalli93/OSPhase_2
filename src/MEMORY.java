
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * This class has the memory() method. memory() method has 3 arguments.
 * The first one is the control signal that determines read,write or dump operation.
 * Second one holds the effective address and third parameter is a variable.
 * The memory() is called from loader() method initially to populate main memory where write opeartion takes place.
 * It is called by cpu to fetch the instructions where read operations take place.
 */
public class MEMORY {

    public static String[] mainMemory = new String[512];
    public static int block1 = 0;
    public static int block2 = 32;
    /*32*/
    public static int block3 = 64;
    /*32*/
    public static int block4 = 128;
    /*64*/
    public static int block5 = 192;
    /*64*/
    public static int block6 = 256;
    /*128*/
    public static int block7 = 384;
    /*128*/
    public static boolean[] blockCheck = {false, false, false, false, false, false, false};
    public static ArrayList<String> inputData = new ArrayList<String>();

    MEMORY() {
        /*This should be in memory manager*/
        Arrays.fill(mainMemory, "zero");
    }

    /*In the memory function x determines the read,write and dump control.Assuming x=0 is read, x=1 is write and x=2 is dump*/
 /*y determines the location to which the variable z has to be written or from which location to read into z*/
    public static String memory(int x, int y, String z) throws IOException {
        if (y > 512) {

            ERROR_HANDLER.Error_Handler(23);

        }
        switch (x) {
            case 0:
                /*read to z*/
                z = mainMemory[y];
                return z;
            case 1:
                /*write to memory */
                if (z.length() >= 12) {
                    for (int i = 0; i < z.length() - 1; i += 12) {
                        /*this splits the instruction into words of 12bits each and writes them to memory*/
                        mainMemory[y] = z.substring(i, i + 12);
                        y++;
                    }
                } else if (z.length() < 12) {
                    while (z.length() < 12) {
                        z = "0" + z;
                    }
                    mainMemory[y] = z;
                }

                return null;
            case 2:
                dump();

                return null;
            default:
                ERROR_HANDLER.Error_Handler(28);
                return null;
        }

    }

    private static void dump() throws IOException {
        SYSTEM.noOfAbTJobs++;
        SYSTEM.timeLostAbT = SYSTEM.timeLostAbT + CPU.executionTime + CPU.ioTime;
        File newFile = new File("output.txt");

        newFile.createNewFile();
        FileWriter f = new FileWriter(newFile, true);
        BufferedWriter writer = new BufferedWriter(f);
        writer.write("Error is : " + SYSTEM.error);

        writer.write("0000\t");
        for (int i = 0; i < 256; i++) {

            String hex = "";
            if (mainMemory[i].equals("zero")) {
                hex = "null";
            } else {
                hex = Integer.toHexString(Integer.parseInt(mainMemory[i], 2)).toUpperCase();
            }
            while (hex.length() < 4) {
                hex = "0" + hex;
            }
            writer.write("word" + hex + "\t");

            if ((i + 1) % 8 == 0 && (i + 1) < 256) {
                writer.newLine();
                String x = Integer.toHexString(i + 1);
                while (x.length() < 4) {
                    x = "0" + x;
                }
                writer.write(x + "\t");
            }
        }
        writer.close();
        f.close();
    }

    public static void memoryManager(int baseaddress) {

        if (baseaddress == block1) {
            blockCheck[0] = false;

        } else if (baseaddress == block2) {
            blockCheck[1] = false;
        } else if (baseaddress == block3) {
            blockCheck[2] = false;
        } else if (baseaddress == block4) {
            blockCheck[3] = false;
        } else if (baseaddress == block5) {
            blockCheck[4] = false;
        } else if (baseaddress == block6) {
            blockCheck[5] = false;
        } else if (baseaddress == block7) {
            blockCheck[6] = false;
        }
    }
}
