import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileHandlingInterface extends Remote{
    public String read() throws RemoteException;
    public boolean delete(int linha) throws RemoteException;
    public boolean write(String message) throws RemoteException;

    public boolean lock(String requester) throws RemoteException;

    public boolean unlock(String requester) throws RemoteException;
}