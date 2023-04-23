import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class FileHandling extends UnicastRemoteObject implements FileHandlingInterface {
    private static final long serialVersionUID = 7896795898928782846L;
    private String server;

    public FileHandling (String server) throws RemoteException {
        this.server = server;
    }

    public String read() throws RemoteException {
        System.out.println("[server " + server + " ] Reading from file file...");
        try {
            return readFileAsString("test");
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public boolean write(String message) throws RemoteException {
        System.out.println("[server " + server + " ] Writing to file...");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("filename.txt", true))) {
            writer.write(message);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int numeroLinha) throws RemoteException {
        System.out.println("[server " + server + " ] Deleting from file...");
        List<String> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("filename.txt"))) {
            Thread.sleep(1000);
            String linha;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    public String readFileAsString(String filePath) throws IOException {
        byte[] encodedBytes = Files.readAllBytes(Paths.get("filename.txt"));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }
}

