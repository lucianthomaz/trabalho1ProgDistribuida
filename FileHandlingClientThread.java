import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class FileHandlingClientThread extends Thread {
    protected String[] thread_args;
    protected String thread_name;
    private static final String SERVER_LIST_PATH = "serverList.txt";

    public FileHandlingClientThread(String[] args, String name) {
        thread_args = args;
        thread_name = name;
    }

    public void run() {
        String connectLocation = "";
        try {
            List<String> availableServers = readFileToList(SERVER_LIST_PATH);
            Random rand = new Random();
            int randomServer = rand.nextInt(5);
            String[] details = availableServers.get(randomServer).split(",");
            String ip = details[0];
            int port = Integer.parseInt(details[1]);
            boolean isMasterServer = Boolean.parseBoolean(details[2]);
            connectLocation = "rmi://" + ip + ":" + port + "/FileHandling";
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileHandlingInterface fileHandling = (FileHandlingInterface) Naming.lookup(connectLocation);
            System.out.println("Content read from file:");
            System.out.println(fileHandling.read());
            System.out.println("Adding new line to file");
            System.out.println(fileHandling.write("ADDED LINE"));
            System.out.println("Deleting line from file");
            System.out.println(fileHandling.delete(1));
        } catch (Exception e) {
            System.out.println("HelloClient failed:");
            e.printStackTrace();
        }
    }

    private static List<String> readFileToList(String filePath) throws Exception {
        List<String> lines = new ArrayList<>();
        Files.lines(Paths.get(filePath)).forEach(line -> lines.add(line));
        return lines;
    }
}