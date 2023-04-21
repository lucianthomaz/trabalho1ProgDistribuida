import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

class FileHandlingClient {
	// Programa cliente para o exemplo "Hello, world!"
	public static void main (String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java FileHandlingClient <machine>");
			System.exit(1);
		}

		new FileHandlingClientThread(args, "t1").run();
		new FileHandlingClientThread(args, "t2").run();
		new FileHandlingClientThread(args, "t3").run();
	}
}

