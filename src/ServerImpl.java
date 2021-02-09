import database.DatabaseConnection;
import database.User;
import utility.Constants;
import utility.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

public class ServerImpl extends java.rmi.server.UnicastRemoteObject implements ServerInterface {
    private static final long serialVersionUID = 1L;
    private HashMap<String, Integer> otpTable = new HashMap<String, Integer>();
    private DatabaseConnection dbConnection;
    private Logger logger;

    public ServerImpl() throws RemoteException {
        dbConnection = new DatabaseConnection();
        logger = new Logger(dbConnection);

        logger.logEvent(Constants.USER_ID_SYSTEM, Constants.LOG_SYSTEM_ONLINE, Constants.USER_ID_SYSTEM);

        createFakeLogWarning();
        //();
        //createFakeLogWarning();
        //createFakeLogWarning();
        //
        createFakeLogError();
        //();

        // Tests
        viewLogEntries();
        System.out.println("\n\n\n\n=======================================================\n\n");
        viewLogEntriesWarnings();
        System.out.println("\n\n\n\n=======================================================\n\n");
        viewLogEntriesErrors();
        System.out.println("\n\n\n\n=======================================================\n\n");
        viewLogEntriesWarningsAndErrors();
        System.out.println("\n\n\n\n=======================================================\n\n");
        printUserResponsibility();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////LOGGER METHODS

    @Override
    public void logEvent(int EVENT_ID, int userId,int appendedBy) throws RemoteException {

        this.logger.logEvent(userId,EVENT_ID,appendedBy);
    }


    @Override
    public void viewLogEntriesWarningsAndErrors() {
        dbConnection.viewErrorAndWarningLogEntries(true);
    }


    @Override
    public void printUserResponsibility() {
        Logger.printMap(dbConnection.viewErrorAndWarningLogEntries(false));
    }


    @Override
    public void viewLogEntries() throws RemoteException {
        this.dbConnection.viewLogEntries();
    }

    @Override
    public void viewLogEntriesWarnings() throws RemoteException {
        dbConnection.viewWarningLogEntries();
    }

    @Override
    public void viewLogEntriesErrors() throws RemoteException {
        dbConnection.viewErrorLogEntries();
    }


    public void createFakeLogWarning(){
        System.out.println("Making fake log...");
        dbConnection.createFakeLog(101,"WARNING","999999999");
    }
    public void createFakeLogError(){
        System.out.println("Making fake log...");
        dbConnection.createFakeLog(2000,"ERROR","999999999");
    }


    /////////////////// END OF LOGGER METHODS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkPermissions(String email, String request) {
        return dbConnection.checkPermissions(email, request);
    }

    public void createUser(User user, String plaintext) {
        System.out.println("Creating user in database...");
        dbConnection.createUser(user, plaintext);
    }

    public String viewPatients(String email) {
        if (dbConnection.checkPermissions(email, "view_patient")) {
            return dbConnection.viewPatients(-1);
        }
        return "You do not have the correct permissions to do that.";
    }

    public String viewPatients(String email, int s_id) {
        if (dbConnection.checkPermissions(email, "view_patient")) {
            return dbConnection.viewPatients(s_id);
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String viewStaffs(String email) {
        if (dbConnection.checkPermissions(email, "view_staff")) {
            return dbConnection.viewStaffs(-1);
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String viewStaffs(String email, int s_id) {
        if (dbConnection.checkPermissions(email, "view_staff")) {
            return dbConnection.viewStaffs(s_id);
        }
        return "You do not have the correct permissions to do that.";
    }

    public String deletePatients(String email, int p_id) {
        if (dbConnection.checkPermissions(email, "delete_patient")) {
            return dbConnection.deletePatients(p_id);
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String deleteStaffs(String email, int s_id) {
        if (dbConnection.checkPermissions(email, "delete_staff")) {
            return dbConnection.deletePatients(s_id);
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String updatePatients(String email, int p_id, String command) {
        if (dbConnection.checkPermissions(email, "update_patient")) {
            dbConnection.updatePatients(p_id, command);
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String updateStaffs(String email, int s_id, String command) {
        if (dbConnection.checkPermissions(email, "update_staff")) {
            return dbConnection.updateStaffs(s_id, command);
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public boolean verifyPassword(String plaintext, String email) {
        return dbConnection.verifyPassword(plaintext, email);
    }

    @Override
    public String getRole( String email) {
        return dbConnection.getUserRole(email);
    }

    @Override
    public boolean userIsAdmin(String email){
        return (dbConnection.userIsAdmin(email));
    }

    public void sendOTP(String email) {
        final String username = "scc363auth@gmail.com";
        final String password = "SCC363auth!";

        Properties props = new Properties();
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Authenticate email
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        // Create session
        Session session = Session.getInstance(props, auth);
        try {
            // Generate random 6 digit number
            int code = new Random().nextInt(900000) + 100000;
            String text = "Your verification code is: " + code;

            // Store code in hashmap for verification later on
            otpTable.put(email, code);

            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("scc363auth@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Verification code");
            message.setText(text);

            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyOTP(String email, Integer attempt) {
        Integer otp = otpTable.get(email);
        return attempt.equals(otp);
    }

    ///////////////////
    public void sendWarningEmail(String email) {
        final String username = "scc363auth@gmail.com";
        final String password = "SCC363auth!";

        Properties props = new Properties();
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Authenticate email
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        // Create session
        Session session = Session.getInstance(props, auth);
        try {
            String text = "Your verification code is: " ;


            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("scc363auth@gmail.com"));
            //

            //
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("System Warning");
            message.setText(text);

            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



    ///////////////////

    @Override
    public int getUserId(String email) {
        return dbConnection.getUserId(email);
    }

    public boolean checkEmailAvailable(String email) {
        return dbConnection.checkEmailAvailable(email);
    }

    public String lockAccount(String adminEmail, int accountToLock) {
        // Check user is a system admin before locking account
        if (dbConnection.checkPermissions(adminEmail, "lock_accounts")) {
            return dbConnection.lockAccount(accountToLock);
        }
        return "You do not have the correct permissions to do that.";
    }

    public String updateRole(String adminEmail, int userId, String role) {
        // Check user is a system admin before locking account
        if (dbConnection.checkPermissions(adminEmail, "lock_accounts")) {
            return dbConnection.updateRole(userId, role);
        }
        return "You do not have the correct permissions to do that.";
    }
}