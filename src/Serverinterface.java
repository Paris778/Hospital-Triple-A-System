import java.rmi.RemoteException;

public interface Serverinterface extends java.rmi.Remote {

    public void createUser(User user) throws RemoteException;
}