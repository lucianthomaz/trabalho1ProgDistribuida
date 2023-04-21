import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class FileHandlingClientThread extends Thread {
	protected String[] thread_args;
	protected String thread_name;
	
	public FileHandlingClientThread(String[] args, String name) {
		thread_args = args;
		thread_name = name;
	}

	public void run() {
        String remoteHostName = thread_args[0];
        String connectLocation = "rmi://" + remoteHostName + ":1099/FileHandling";

		try {
			FileHandlingInterface fileHandling = (FileHandlingInterface) Naming.lookup (connectLocation);
			System.out.println("Content read from file:");
			System.out.println (fileHandling.read());
			System.out.println("Adding new line to file");
			System.out.println (fileHandling.write("ADDED LINE"));
			System.out.println("Deleting line from file");
			System.out.println (fileHandling.delete(1));
		} catch (Exception e) {
			System.out.println ("HelloClient failed:");
			e.printStackTrace();
		}
	}
}