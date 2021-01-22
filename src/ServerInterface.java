import java.rmi.RemoteException;

public interface ServerInterface extends java.rmi.Remote {

    void createUser(User user) throws RemoteException;

    void viewPatients() throws RemoteException;
}