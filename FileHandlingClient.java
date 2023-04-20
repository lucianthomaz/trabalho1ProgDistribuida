import java.rmi.Naming;

class FileHandlingClient {
	// Programa cliente para o exemplo "Hello, world!"
	public static void main (String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java FileHandlingClient <machine>");
			System.exit(1);
		}

		String connectLocation = "rmi://" + args[0] + ":1099/FileHandling";

		try {
			FileHandlingInterface fileHandling = (FileHandlingInterface) Naming.lookup (connectLocation);
			System.out.println (fileHandling.read());
		} catch (Exception e) {
			System.out.println ("HelloClient failed:");
			e.printStackTrace();
		}
	}
}

