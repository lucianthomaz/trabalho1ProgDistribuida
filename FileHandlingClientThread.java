import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileHandlingClientThread extends Thread {
    protected String connectLocation;
    private int amountOfReads;
    private int amountOfWrites;
    private int amountOfDeletes;
    private static final String AMOUNT_OF_EACH_OPERATION_PATH = "amountOfEachOperation.txt";

    public FileHandlingClientThread(String connectLocation) {
        this.connectLocation = connectLocation;
    }

    public void run() {
        readAmountOfEachOperation(AMOUNT_OF_EACH_OPERATION_PATH);
        try {
            Random rand = new Random();
            FileHandlingInterface fileHandling = (FileHandlingInterface) Naming.lookup(connectLocation);
            while (amountOfReads > 0 || amountOfWrites > 0 || amountOfDeletes > 0) {
                System.out.println("Amount of reads remaining: " + amountOfReads);
                System.out.println("Amount of writes remaining: " + amountOfWrites);
                System.out.println("Amount of deletes remaining: " + amountOfDeletes);
                int randomOperation = rand.nextInt(3);
                switch (randomOperation) {
                    case 0:
                        if (amountOfReads > 0)
                            readFromFile(fileHandling);
                        break;
                    case 1:
                        if (amountOfWrites > 0)
                            writeToFile(fileHandling);
                        break;
                    case 2:
                        if (amountOfDeletes > 0)
                            deleteFromFile(fileHandling);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("FileHandlingServer failed:");
            e.printStackTrace();
        }
    }

    private void readFromFile(FileHandlingInterface fileHandling) throws RemoteException {
        System.out.println("Content read from file:");
        System.out.println(fileHandling.read());
        amountOfReads -= 1;
    }

    private void writeToFile(FileHandlingInterface fileHandling) throws RemoteException {
        System.out.println("Adding new line to file");
        System.out.println(fileHandling.write("Written by: " + connectLocation));
        amountOfWrites -= 1;
    }

    private void deleteFromFile(FileHandlingInterface fileHandling) throws RemoteException {
        System.out.println("Deleting line from file");
        System.out.println(fileHandling.delete(1));
        amountOfDeletes -= 1;

    }

    private void readAmountOfEachOperation(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line1 = br.readLine(); // Read the first line (we don't need it)
            String line2 = br.readLine(); // Read the second line
            br.close();

            // Split the second line into values separated by commas
            String[] valueStrings = line2.split(",");
            amountOfReads = Integer.parseInt(valueStrings[0]);
            amountOfWrites = Integer.parseInt(valueStrings[1]);
            amountOfDeletes = Integer.parseInt(valueStrings[2]);
        } catch (IOException e) {
            System.out.println("An error occurred while reading file: " + e.getMessage());
        }
    }

}