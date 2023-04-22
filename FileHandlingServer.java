import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

class FileHandlingServer {

	private static final String SERVER_LIST_PATH ="serverList.txt";

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java FileHandling <machine>");
			System.exit(1);
		}

		try {
			List<String> availableServers = readFileToList(SERVER_LIST_PATH);
			for (String serverDetails: availableServers) {
				String[] details = serverDetails.split(",");
				String ip = details[0];
				int port = Integer.parseInt(details[1]);
				boolean isMasterServer = Boolean.parseBoolean(details[2]);
				System.setProperty("java.rmi.server.hostname", ip);
				LocateRegistry.createRegistry(port);
				System.out.println("RMI registry ready.");
				String server = "rmi://" + ip + ":" + port + "/FileHandling";
				Naming.rebind(server, new FileHandling(ip + ":" + port));
				System.out.println(ip + ":" + port +" is ready.");
			}
		} catch (RemoteException e) {
			System.out.println("RMI registry already running.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

		} catch (Exception e) {
			System.out.println("HelloServer failed:");
			e.printStackTrace();
		}
	}

	private static List<String> readFileToList(String filePath) throws Exception {
		List<String> lines = new ArrayList<>();
		Files.lines(Paths.get(filePath)).forEach(line -> lines.add(line));
		return lines;
	}
}

