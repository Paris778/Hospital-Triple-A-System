import java.rmi.RemoteException;


public class Serverimpl extends java.rmi.server.UnicastRemoteObject implements Serverinterface {
    private static final long serialVersionUID = 1L;


    public Serverimpl() throws RemoteException {

    }

    public void createUser(User user) {
        System.out.println("hello");
        DatabaseConnection dbConnection = new DatabaseConnection();
        dbConnection.createUser(user);
    }
}