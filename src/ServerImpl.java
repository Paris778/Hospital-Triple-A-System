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
    }

    public boolean checkPermissions(String email, String request) {
        return dbConnection.checkPermissions(email, request);
    }

    public void createUser(User user, String plaintext) {
        System.out.println("Creating user in database...");
        dbConnection.createUser(user, plaintext);
    }

    public String viewPatients() {
        return dbConnection.viewPatients(-1);
    }

    public String viewPatients(int s_id) {
        return dbConnection.viewPatients(s_id);
    }

    @Override
    public String viewStaffs() {
        return dbConnection.viewStaffs(-1);
    }

    @Override
    public String viewStaffs(int s_id) {
        return dbConnection.viewStaffs(s_id);
    }

    public void deletePatients(int p_id) {
        dbConnection.deletePatients(p_id);
    }

    @Override
    public void deleteStaffs(int s_id) {
        dbConnection.deletePatients(s_id);
    }

    @Override
    public void updatePatients(int p_id, String command) {
        dbConnection.updatePatients(p_id, command);
    }

    @Override
    public void updateStaffs(int s_id, String command) {
        dbConnection.updateStaffs(s_id, command);
    }

    @Override
    public boolean verifyPassword(String plaintext, String email) {
        return dbConnection.verifyPassword(plaintext, email);
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
}