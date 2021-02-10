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

    ///////////////////////////////////////////
    //LOGGER ADMIN  METHODS
    ///////////////////////////////////////////

    void logEvent(int userId, int EVENT_ID, int appendedBy) throws RemoteException;

    String viewLogEntries() throws RemoteException;

    String viewRecentLogs(int numberOfLogs) throws  RemoteException;

    String viewLogEntriesWarnings() throws RemoteException;

    String viewLogEntriesErrors() throws RemoteException;

    String viewLogEntriesWarningsAndErrors() throws RemoteException;

    String printUserResponsibility() throws RemoteException;

    String inspectSpecificUser(String userId) throws RemoteException;

    String databaseEncryption(int mode, String inputFile, String outputFile) throws  RemoteException;

    String viewLockedAccounts() throws RemoteException;

    //
    void sendWarningEmail(String id) throws RemoteException;

    String lockAccountManual(String id) throws RemoteException;

    String kickAndLockUserAutomatic(String userId) throws RemoteException;

    String unlockAccount(String email) throws RemoteException;

    boolean isAccountUnlocked(String email) throws RemoteException;
}