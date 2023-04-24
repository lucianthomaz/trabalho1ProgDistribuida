import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

class FileHandlingServer extends UnicastRemoteObject implements FileHandlingInterface {

	private static final String SERVER_LIST_PATH = "serverList.txt";
	private static final String SHARED_FILE = "sharedFile.txt";
	private String serverName;
	private static final String FILE_LOCATION = "fileLocation.txt";
	private static final boolean IS_LOCALHOST_TESTING = false;
	private boolean isMasterServer;
	private boolean isLocked = false;
	private FileHandlingInterface remoteServer;
	private static final int TIME_TO_WAIT_IN_MILLIS = 1000;
	private static final int TIME_TO_LOCK_IN_MILLIS = 3000;
	private String lockedBy;

	protected FileHandlingServer(String serverName, boolean isMasterServer) throws RemoteException {
		this.serverName = serverName;
		this.isMasterServer = isMasterServer;
		if (!isMasterServer) {
			remoteServer = getRemoteServer();
		}
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: java FileHandlingClient <ip> <port> <true,false(isMasterServer)>");
			System.exit(1);
		}
		bootUpEnvironment(args);
	}

	private static void bootUpEnvironment(String[] args) {
		initServerManually(args[0], Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]));
//			initMasterServer();
//			initOtherServers();
	}

	private static List<String> readFileToList(String filePath) throws Exception {
		List<String> lines = new ArrayList<>();
		Files.lines(Paths.get(filePath)).forEach(line -> lines.add(line));
		return lines;
	}

	public String read() throws RemoteException {
		System.out.println("[server " + serverName + " ] Reading from file file...");
		try {
			Thread.sleep(TIME_TO_LOCK_IN_MILLIS);
			return IS_LOCALHOST_TESTING || isMasterServer ? readFileAsString() : readRemoteFile(serverName.split(",")[0], getRemoteFileLocation());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return e.getMessage();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "";
		}
	}

	public boolean write(String message) {
		while (!lock(serverName)) {
			System.out.println("Cannot write because resource already locked! Will try again in " + TIME_TO_WAIT_IN_MILLIS + " milliseconds");
			try {
				Thread.sleep(TIME_TO_WAIT_IN_MILLIS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[server " + serverName + " ] Writing to file...");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(SHARED_FILE, true))) {
			Thread.sleep(TIME_TO_LOCK_IN_MILLIS);
			writer.newLine();
			writer.write(message);
			unlock(serverName);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(int numeroLinha) {
		while (!lock(serverName)) {
			System.out.println("Cannot delete because resource already locked! Will try again in " + TIME_TO_WAIT_IN_MILLIS + " milliseconds");
			try {
				Thread.sleep(TIME_TO_WAIT_IN_MILLIS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[server " + serverName + " ] Deleting from file...");
		List<String> linhas = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(SHARED_FILE))) {
			Thread.sleep(TIME_TO_LOCK_IN_MILLIS);
			String linha;
			while ((linha = br.readLine()) != null) {
				linhas.add(linha);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (numeroLinha < 1 || numeroLinha > linhas.size()) {
			System.out.println("Número de linha inválido.");
			return false;
		}
		linhas.remove(numeroLinha - 1);

		try (PrintWriter pw = new PrintWriter(new FileWriter(SHARED_FILE))) {
			for (String linha : linhas) {
				pw.println(linha);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		unlock(serverName);
		return true;
	}

	public String readFileAsString() throws IOException {
		byte[] encodedBytes = Files.readAllBytes(Paths.get(SHARED_FILE));
		return new String(encodedBytes, StandardCharsets.UTF_8);
	}

	public static String readRemoteFile(String ipAddress, String filePath) throws IOException {
		System.out.println("Reading from remote machine!!!");
		// Construct the URL for the remote file
		String url = "file:////" + getSplitMasterAddress().get(0) + "/" + filePath;
		URL remoteFileUrl = new URL(url);

		// Open a connection to the remote file and create a reader to read its contents
		BufferedReader reader = new BufferedReader(new InputStreamReader(remoteFileUrl.openStream()));

		// Read the contents of the file into a StringBuilder
		StringBuilder contents = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			contents.append(line);
			contents.append(System.lineSeparator());
		}

		// Close the reader and return the contents of the file as a string
		reader.close();
		return contents.toString();
	}

	public static String getRemoteFileLocation() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(FILE_LOCATION));
		String line = br.readLine();
		br.close();
		return line;
	}

	public boolean lock(String requester) {
		if (isMasterServer) {
			if (!isLocked) {
				isLocked = true;
				lockedBy = requester;
				System.out.println("Resource LOCKED() by: " + lockedBy);
				return true;
			} else {
				return false;
			}
		} else {
			return requestLockFromRemoteMaster(requester);
		}
	}

	public boolean unlock(String requester) {
		if (isMasterServer) {
			if (isLocked && requester.equals(lockedBy)) {
				isLocked = false;
				System.out.println("Resource UNLOCKED() by: " + lockedBy);
				return true;
			} else {
				return false;
			}
		} else {
			return requestUnlockFromRemoteMaster(requester);
		}
	}

	public boolean requestLockFromRemoteMaster(String requester) {
		try {
			return remoteServer.lock(requester);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean requestUnlockFromRemoteMaster(String requester) {
		try {
			return remoteServer.unlock(requester);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private FileHandlingInterface getRemoteServer() {
		System.out.println("Trying to connect to remote master at + " + getMasterAddress());
		String connectLocation = "rmi://" + getMasterAddress() + "/FileHandling";
		try {
			return (FileHandlingInterface) Naming.lookup(connectLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void initServerManually(String ip, int port, boolean isMasterServer) {
		String server = "rmi://" + ip + ":" + port + "/FileHandling";
		try {
			System.setProperty("java.rmi.server.hostname", ip);
			LocateRegistry.createRegistry(port);
			Naming.rebind(server, new FileHandlingServer(server, isMasterServer));
			System.out.println(ip + ":" + port + " is ready.");
		}catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("RMI registry already running.");
		}
		catch (Exception e) {
			System.out.println("An ERROR occurred while trying to INIT SERVER MANUALLY");
			e.printStackTrace();
		}
	}

	private static void initMasterServer() {
		String server = "rmi://" + getMasterAddress() + "/FileHandling";
		System.out.println("Trying to init master at: " + server);
		String ip = getSplitMasterAddress().get(0);
		int port = Integer.parseInt(getSplitMasterAddress().get(1));
		try {
			System.setProperty("java.rmi.server.hostname", ip);
			LocateRegistry.createRegistry(port);
			Naming.rebind(server, new FileHandlingServer(server, true));
			System.out.println(ip + ":" + port + " is ready.");
		}catch (RemoteException e) {
			System.out.println("RMI registry already running.");
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("An ERROR occurred while trying to INIT MASTER");
			e.printStackTrace();
		}
	}

	private static void initOtherServers() {
		try {
			List<String> availableServers = readFileToList(SERVER_LIST_PATH);
			for (String serverDetails : availableServers) {
				String[] details = serverDetails.split(",");
				String ip = details[0];
				int port = Integer.parseInt(details[1]);
				boolean isMasterServer = Boolean.parseBoolean(details[2]);
				if (!isMasterServer){
					System.setProperty("java.rmi.server.hostname", ip);
					LocateRegistry.createRegistry(port);
					System.out.println("RMI registry ready.");
					String server = "rmi://" + ip + ":" + port + "/FileHandling";
					Naming.rebind(server, new FileHandlingServer(ip + ":" + port, false));
					System.out.println(ip + ":" + port + " is ready.");
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("RMI registry already running.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<String> getSplitMasterAddress() {
		try {
			List<String> availableServers = readFileToList(SERVER_LIST_PATH);
			for (String serverDetails : availableServers) {
				String[] details = serverDetails.split(",");
				List<String> masterAddress = new ArrayList<>();
				masterAddress.add(details[0]);
				masterAddress.add(details[1]);
				boolean isMasterServer = Boolean.parseBoolean(details[2]);
				if (isMasterServer)
					return masterAddress;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private static String getMasterAddress() {
		return getSplitMasterAddress().get(0) + ":" + getSplitMasterAddress().get(1);
	}


}

