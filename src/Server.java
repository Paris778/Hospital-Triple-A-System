import java.rmi.Naming;
public class Server {
    private static final String host = "localhost";  
    private static final int port = 1099;
    
    public Server() 
    {
        try 
        {
            Serverimpl c = new Serverimpl();
            //generate the link for rmi to bind
            Naming.rebind("rmi://"+host+":"+port+"/Service", c);
        } 
        catch (Exception e) 
        {
            System.out.println("Server Error: " + e);
        }
    
	}
	
   public static void main(String args[]) 
   {
	    new Server();
   }
}