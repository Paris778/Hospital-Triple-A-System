import java.rmi.RemoteException;

public interface Serverinterface extends java.rmi.Remote {


    abstract public boolean storeHashedPassword(byte[] hash) throws RemoteException;
    abstract public boolean varifyPassword(byte[] hash,int clientId ) throws RemoteException;
    
}