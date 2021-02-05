import database.User;

import java.rmi.RemoteException;

public interface ServerInterface extends java.rmi.Remote {

    boolean checkPermissions(String email, String request) throws RemoteException;

    void createUser(User user, String password) throws RemoteException;

    void viewPatients() throws RemoteException;

    void viewPatients(int s_id) throws RemoteException;

    void viewStaffs() throws RemoteException;

    void viewStaffs(int s_id) throws RemoteException;

    void deletePatients(int p_id) throws  RemoteException;

    void deleteStaffs(int s_id) throws RemoteException;

    void updatePatients(int p_id,String command) throws RemoteException;

    void updateStaffs(int s_id,String command) throws RemoteException;

    void sendOTP(String email_address) throws RemoteException;

    boolean verifyOTP(String email, Integer attempt) throws RemoteException;

    int getUserId(String email_address) throws  RemoteException;

    boolean verifyPassword(String plaintext, String email) throws RemoteException;

    boolean checkEmailAvailable(String email) throws RemoteException;
}