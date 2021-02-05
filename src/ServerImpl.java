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
        logger.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_SYSTEM_ONLINE, Constants.USER_ID_SYSTEM);
        //createFakeLogWarning();
        //();
        //createFakeLogWarning();
        //createFakeLogWarning();
        //
        //createFakeLogError();
        //();

        // Tests
        viewLogEntries();
        System.out.println("\n\n\n\n=======================================================\n\n");
        viewLogEntriesWarnings();
        System.out.println("\n\n\n\n=======================================================\n\n");
        viewLogEntriesErrors();
        System.out.println("\n\n\n\n=======================================================\n\n");
        viewLogEntriesWarningsAndErrors();
        System.out.println("\n\n\n\n=======================================================\n\n");
        printUserResponsibility();

    }

    @Override
    public void viewLogEntriesWarningsAndErrors() {
        dbConnection.viewErrorAndWarningLogEntries(true);
    }


    @Override
    public void printUserResponsibility() {
        Logger.printMap(dbConnection.viewErrorAndWarningLogEntries(false));
    }

    public void createUser(User user, String plaintext) {
        System.err.println(user.getId() + "****************");
        System.err.println("Creating user in database...");
        dbConnection.createUser(user, plaintext);
        try {
            logEvent(user.getId(),Constants.LOG_USER_REGISTERED,Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void createFakeUser() {
        System.out.println("Creating user in database...");
        dbConnection.createFakeUser();
    }

    ///////////////////////////////////////////////////////////////////////////



    public void createFakeLogWarning(){
        System.out.println("Making fake log...");
        dbConnection.createFakeLog(101,"WARNING","999999999");
    }
    public void createFakeLogError(){
        System.out.println("Making fake log...");
        dbConnection.createFakeLog(2000,"ERROR","999999999");
    }

    /////////////////////////////////////////////////////////

    @Override
    public void logEvent(int EVENT_ID, int userId,int appendedBy) throws RemoteException {

        this.logger.logEvent(userId,EVENT_ID,appendedBy);
    }

    @Override
    public void viewLogEntries() throws RemoteException {
        this.dbConnection.viewLogEntries();
    }

    @Override
    public void viewLogEntriesWarnings() throws RemoteException {
        dbConnection.viewWarningLogEntries();
    }

    @Override
    public void viewLogEntriesErrors() throws RemoteException {
        dbConnection.viewErrorLogEntries();
    }

    public void viewPatients() throws RemoteException {
        dbConnection.viewPatients(-1);
    }

    public void viewPatients(int s_id) throws RemoteException {
        dbConnection.viewPatients(s_id);
    }

    @Override
    public void viewStaffs() throws RemoteException {
        dbConnection.viewStaffs(-1);
    }

    @Override
    public void viewStaffs(int s_id) throws RemoteException {
        dbConnection.viewStaffs(s_id);
    }

    public void deletePatients(int p_id) throws RemoteException {
        dbConnection.deletePatients(p_id);
    }

    @Override
    public void deleteStaffs(int s_id) throws RemoteException {
        dbConnection.deletePatients(s_id);
    }

    @Override
    public void updatePatients(int p_id, String command) throws RemoteException {
        dbConnection.updatePatients(p_id, command);
    }

    @Override
    public void updateStaffs(int s_id, String command) throws RemoteException {
        dbConnection.updateStaffs(s_id, command);
    }

    @Override
    public boolean verifyPassword(String plaintext, String email, boolean isPatient) {
        return dbConnection.verifyPassword(plaintext, email, isPatient);
    }

    public void sendOTP(String email_address) {
        //otpTable.put(email_address, SendEmail.send(email_address));
    }

    public boolean verifyOTP(String email, Integer attempt) {
        //Integer otp = otpTable.get(email);
        return attempt.equals(otp);
    }

    @Override
    public int getUserId(String email_address, Boolean isPatient) throws RemoteException {
        return dbConnection.getUserId(email_address, isPatient);
    }
}