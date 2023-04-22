import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileHandlingClientThread extends Thread {
    protected String[] thread_args;
    protected String thread_name;
    private static final String SERVER_LIST_PATH = "serverList.txt";
    private static final String FILE_LOCATION = "fileLocation.txt";

    public FileHandlingClientThread(String[] args, String name) {
        thread_args = args;
        thread_name = name;
    }

    public void run() {
        String connectLocation = getConnectLocationForRandomServer();

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

    private String getConnectLocationForRandomServer() {
        try {
            List<String> availableServers = readFileToList(SERVER_LIST_PATH); // Lista dos servers disponíveis
            Random rand = new Random();
            int randomServer = rand.nextInt(5);// Random pra escolher um server aleatório
            String[] details = availableServers.get(randomServer).split(",");
            String ip = details[0];
            int port = Integer.parseInt(details[1]);
            return "rmi://" + ip + ":" + port + "/FileHandling";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private static List<String> readFileToList(String filePath) throws Exception {
        List<String> lines = new ArrayList<>();
        Files.lines(Paths.get(filePath)).forEach(line -> lines.add(line));
        return lines;
    }

    public static String getRemoteFileLocation() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FILE_LOCATION));
        String line = br.readLine();
        br.close();
        return line;
    }
}