import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface FileHandlingInterface extends Remote{
    public String read() throws RemoteException;
    public boolean delete(int linha) throws RemoteException;
    public boolean write(String message) throws RemoteException;
}