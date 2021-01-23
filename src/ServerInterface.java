import java.rmi.RemoteException;

public interface ServerInterface extends java.rmi.Remote {

    void createUser(User user, byte[] hashedPassword) throws RemoteException;

    void createFakeUser() throws RemoteException;

    void viewPatients() throws RemoteException;

    abstract public boolean storeHashedPassword(byte[] hash) throws RemoteException;

    abstract public boolean varifyPassword(byte[] hash, int clientId) throws RemoteException;
}