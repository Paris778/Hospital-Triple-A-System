import database.User;

import java.rmi.RemoteException;

public interface ServerInterface extends java.rmi.Remote {

    boolean checkPermissions(String email, String request) throws RemoteException;

    void createUser(User user, String password) throws RemoteException;

    String viewPatients(String email) throws RemoteException;

    String viewPatients(String email, int s_id) throws RemoteException;

    String viewStaffs(String email) throws RemoteException;

    String viewStaffs(String email, int s_id) throws RemoteException;

    String deletePatients(String email, int p_id) throws RemoteException;

    String deleteStaffs(String email, int s_id) throws RemoteException;

    String updatePatients(String email, int p_id, String command) throws RemoteException;

    String updateStaffs(String email, int s_id, String command) throws RemoteException;

    void sendOTP(String email_address) throws RemoteException;

    boolean verifyOTP(String email, Integer attempt) throws RemoteException;

    int getUserId(String email_address) throws RemoteException;

    boolean verifyPassword(String plaintext, String email) throws RemoteException;

    boolean checkEmailAvailable(String email) throws RemoteException;

    String updateRole(String adminEmail, int userId, String role) throws RemoteException;

    String getRole(String email_address) throws RemoteException;

    boolean userIsAdmin(String email) throws RemoteException;

    //Logger Methods

    abstract public void logEvent(int userId, int EVENT_ID, int appendedBy) throws RemoteException;

    void viewLogEntries() throws RemoteException;

    void viewLogEntriesWarnings() throws RemoteException;

    void viewLogEntriesErrors() throws RemoteException;

    void viewLogEntriesWarningsAndErrors() throws RemoteException;

    void printUserResponsibility() throws RemoteException;
}