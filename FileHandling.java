import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files


// Classe remota para o exemplo "Hello, world!"
public class FileHandling extends UnicastRemoteObject implements FileHandlingInterface {
    private static final long serialVersionUID = 7896795898928782846L;
    private String message;

    // Constroi um objeto remoto armazenando nele o String recebido
    public FileHandling (String msg) throws RemoteException {
        message = msg;
    }

    // Implementa o metodo invocavel remotamente, que retorna a mensagem armazenada no objeto
    public String read() throws RemoteException {
        readFile();
        return "test";
    }

    public boolean write(String message) {
        return true;
    }

    public boolean delete(int linha){
        return true;
    }

    private void readFile() {
        try {
            File myObj = new File("filename.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

