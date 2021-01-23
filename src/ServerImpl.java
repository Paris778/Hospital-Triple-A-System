import java.rmi.RemoteException;

public class ServerImpl extends java.rmi.server.UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
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
}