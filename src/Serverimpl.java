import java.rmi.RemoteException;
import utility.*;

public class ServerImpl extends java.rmi.server.UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    private static final PasswordHandler passwordHandler = new PasswordHandler();
    private DatabaseConnection dbConnection;

    public ServerImpl() throws RemoteException {
        dbConnection = new DatabaseConnection();
    }

    public void createUser(User user) {
        System.out.println("hello");

        dbConnection.createUser(user);
    }

    public void viewPatients() {
        dbConnection.viewPatients();
    }

    @Override
    public boolean storeHashedPassword(byte[] hash) throws RemoteException {
        return false;
    }

    @Override
    public boolean varifyPassword(byte[] hash, int clientId) throws RemoteException {
        return false;
    }
}