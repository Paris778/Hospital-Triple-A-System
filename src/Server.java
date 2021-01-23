import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    private static final String host = "localhost";
    private static final int port = 1099;

    public Server() {
        try {
            // Start running rmi registry
            LocateRegistry.createRegistry(1099);
            ServerImpl c = new ServerImpl();

            //generate the link for rmi to bind
            Naming.rebind("rmi://" + host + ":" + port + "/Service", c);
        } catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }

    public static void main(String args[]) {
        new Server();
    }
}