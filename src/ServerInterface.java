import database.User;

import java.rmi.RemoteException;

public interface ServerInterface extends java.rmi.Remote {

    void createUser(User user, String password) throws RemoteException;

    void createFakeUser() throws RemoteException;

    void viewPatients() throws RemoteException;

    void sendEmail(String email_address) throws RemoteException;

    boolean verifyPassword(String plaintext, String email, boolean isPatient) throws RemoteException;
}