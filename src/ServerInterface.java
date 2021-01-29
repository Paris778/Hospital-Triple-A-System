import database.User;

import java.rmi.RemoteException;

public interface ServerInterface extends java.rmi.Remote {

    void createUser(User user, String password) throws RemoteException;

    void createFakeUser() throws RemoteException;

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

    int getUserId(String emial_address,Boolean isPatient) throws  RemoteException;

    boolean verifyPassword(String plaintext, String email, boolean isPatient) throws RemoteException;
}