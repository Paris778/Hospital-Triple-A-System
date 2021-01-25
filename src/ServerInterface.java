import database.User;

import java.rmi.RemoteException;

public interface ServerInterface extends java.rmi.Remote {

    void createUser(User user, byte[] hashedPassword) throws RemoteException;

    void createFakeUser() throws RemoteException;

    void viewPatients() throws RemoteException;

    void sendEmail(String email_address) throws RemoteException;

    abstract public boolean storeHashedPassword(byte[] hash) throws RemoteException;

    abstract public void logEvent(int EVENT_ID, int userId) throws RemoteException;

    abstract public boolean verifyPassword(byte[] hash, int clientId) throws RemoteException;
}