import java.rmi.RemoteException;
import java.util.HashMap;

import database.DatabaseConnection;
import database.User;
import utility.*;

public class ServerImpl extends java.rmi.server.UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    private static final PasswordHandler passwordHandler = new PasswordHandler();
    private HashMap<String,Integer> otp = new HashMap<String,Integer>();
    private DatabaseConnection dbConnection;
    private Logger logger;

    public ServerImpl() throws RemoteException {
        dbConnection = new DatabaseConnection();
        logger = new Logger(dbConnection);
        logger.logEvent(Constants.LOG_SYSTEM_ONLINE,0);
    }

    public void createUser(User user, byte[] hashedPassword) {
        System.err.println(user.getId() + "****************");
        System.err.println("Creating user in database...");
        dbConnection.createUser(user, hashedPassword);
        try {
            logEvent(Constants.LOG_USER_REGISTERED,user.getId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
    public void logEvent(int EVENT_ID, int userId) throws RemoteException {
        this.logger.logEvent(EVENT_ID, userId);
    }

    @Override
    public boolean verifyPassword(byte[] hash, int clientId) throws RemoteException {
        return false;
    }

    public void sendEmail(String email_address) throws RemoteException {
        otp.put(email_address,SendEmail.send(email_address));
    }
}