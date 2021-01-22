import java.rmi.RemoteException;
import utility.*;


public class Serverimpl extends java.rmi.server.UnicastRemoteObject implements Serverinterface {
    private static final long serialVersionUID = 1L;
    private static final PasswordHandler passwordHandler = new PasswordHandler();


    public Serverimpl() throws RemoteException {

    
    }

    @Override
    public boolean storeHashedPassword(byte[] hash) throws RemoteException {
        
        return false;
    }

    @Override
    public boolean varifyPassword(byte[] hash, int clientId) throws RemoteException {
        
        return false;
    }


}