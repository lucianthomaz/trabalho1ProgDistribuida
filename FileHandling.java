import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileReader;
import java.util.Scanner; // Import the Scanner class to read text files
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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
        try {
            File f1 = new File("filename.txt");
            FileWriter fileWritter = new FileWriter(f1.getName(),true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write("\n");
            bw.write(message);
            bw.close();
            System.out.println("Done");
        } catch(IOException e){
            e.printStackTrace();
        }
        return true;
    }

    public boolean delete(int numeroLinha){
        List<String> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("filename.txt"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (numeroLinha < 1 || numeroLinha > linhas.size()) {
            System.out.println("Número de linha inválido.");
            return false;
        }
        linhas.remove(numeroLinha - 1);

        try (PrintWriter pw = new PrintWriter(new FileWriter("filename.txt"))) {
            for (String linha : linhas) {
                pw.println(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

