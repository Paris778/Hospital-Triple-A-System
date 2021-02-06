import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public String[] protocol = new String[]{"TLSv1.3"};
    public String[] cipherSuite = new String[]{"TLS_AES_256_GCM_SHA384"};

    public Server() {
        System.setProperty("javax.net.ssl.keyStore", "server_keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

        try {
            Registry registry = LocateRegistry.createRegistry(1099, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory(cipherSuite, protocol, false));

            ServerImpl s = new ServerImpl();
            registry.bind("HelloServer", s);
            System.out.println("Running...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new Server();
    }
}