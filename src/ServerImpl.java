import java.rmi.RemoteException;
import java.util.HashMap;

import database.DatabaseConnection;
import database.User;

public class ServerImpl extends java.rmi.server.UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    private HashMap<String, Integer> otpTable = new HashMap<String, Integer>();
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
        otpTable.put(email_address, SendEmail.send(email_address));
    }

    public boolean verifyOTP(String email, Integer attempt) {
        Integer otp = otpTable.get(email);
        return attempt.equals(otp);
    }

    @Override
    public int getUserId(String email_address, Boolean isPatient) throws RemoteException {
        return dbConnection.getUserId(email_address, isPatient);
    }
}