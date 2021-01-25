import java.rmi.RemoteException;
import java.util.HashMap;

import utility.*;

public class ServerImpl extends java.rmi.server.UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    private HashMap<String,Integer> otp = new HashMap<String,Integer>();
    private DatabaseConnection dbConnection;

    public ServerImpl() throws RemoteException {
        dbConnection = new DatabaseConnection();
    }

    public void createUser(User user, String plaintext) {
        System.out.println("Creating user in database...");
        dbConnection.createUser(user, plaintext);
    }

    public void createFakeUser() {
        System.out.println("Creating user in database...");
        dbConnection.createFakeUser();
    }

    public void viewPatients() {
        dbConnection.viewPatients();
    }

    @Override
    public boolean verifyPassword(String plaintext, String email, boolean isPatient) {
        return dbConnection.verifyPassword(plaintext, email, isPatient);
    }

    public void sendEmail(String email_address) {
        otp.put(email_address,SendEmail.send(email_address));
    }
}