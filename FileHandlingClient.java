import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class FileHandlingClient {

	private static final String SERVER_LIST_PATH = "serverList.txt";

	public static void main (String[] args) {
		new FileHandlingClientThread(getConnectLocationForRandomServer()).run();
		new FileHandlingClientThread(getConnectLocationForRandomServer()).run();
	}

	private static String getConnectLocationForRandomServer() {
		try {
			List<String> availableServers = readFileToList(SERVER_LIST_PATH); // Lista dos servers disponíveis
			Random rand = new Random();
			int randomServer = rand.nextInt(2);// Random pra escolher um server aleatório
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

}

