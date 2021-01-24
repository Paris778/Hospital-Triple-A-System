import java.rmi.RemoteException;
import java.util.HashMap;

import utility.*;

public class ServerImpl extends java.rmi.server.UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    private static final PasswordHandler passwordHandler = new PasswordHandler();
    private HashMap<String,Integer> otp = new HashMap<String,Integer>();
    private DatabaseConnection dbConnection;

    public ServerImpl() throws RemoteException {
        dbConnection = new DatabaseConnection();
    }

    public void createUser(User user, byte[] hashedPassword) {
        System.out.println("Creating user in database...");
        dbConnection.createUser(user, hashedPassword);
    }

    public void createFakeUser() {
        System.out.println("Creating user in database...");
        dbConnection.createFakeUser();
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

    public void sendEmail(String email_address) throws RemoteException {
        otp.put(email_address,SendEmail.send(email_address));
    }
}