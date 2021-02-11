import database.DatabaseConnection;
import database.User;
import utility.Constants;
import utility.Logger;

import javax.crypto.Cipher;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.KeyStore;
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
        //
        logger.logEvent(Constants.USER_ID_SYSTEM, Constants.LOG_SYSTEM_ONLINE, Constants.USER_ID_SYSTEM);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////LOGGER METHODS

    @Override
    public void logEvent(int userId,int EVENT_ID, int appendedBy) throws RemoteException {
        this.logger.logEvent(userId, EVENT_ID, appendedBy);
    } //Done

    @Override
    public String viewLogEntriesWarningsAndErrors() {
        try {
            logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return dbConnection.viewErrorAndWarningLogEntries(true);
    } //Done

    @Override
    public String printUserResponsibility() {
        try {
            logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return dbConnection.viewErrorAndWarningLogEntries(false);
    } //Done

    @Override
    public String inspectSpecificUser(String userId) throws RemoteException {
        logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
        return dbConnection.inspectSpecificUser(userId);
    }

    @Override
    public String databaseEncryption(int mode, String inputFileName, String outputFileName) throws RemoteException {
        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);

        try {


            String password = "password";
            //InputStream keystoreStream = new FileInputStream("keystore.jceks");
            KeyStore keystore = KeyStore.getInstance("jks");
            keystore.load(new FileInputStream("keystore.jceks"), password.toCharArray());
            Key key = keystore.getKey("dbkey", password.toCharArray());
            //keystoreStream.close();

            Cipher cipher = Cipher.getInstance("AES");

         /*   if(mode == "encrypt"){

                cipher.init(Cipher.ENCRYPT_MODE, key);

            }else{
                cipher.init(Cipher.DECRYPT_MODE, key);
            }*/

            cipher.init(mode, key);

            FileInputStream inputStream = new FileInputStream(inputFile);

            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

            logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_DATABASE_BACKEDUP, Constants.USER_ID_SYSTEM);
            return "Function Completed";

        } catch (Exception e) {
            e.printStackTrace();
            //return "Function Failed";
        }

        return "error";

    }

    @Override
    public String viewLogEntries() throws RemoteException {
        logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
        return dbConnection.viewLogEntries();
    }

    @Override
    public String viewRecentLogs(int numberOfLogs) throws RemoteException {
        logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
        return dbConnection.viewRecentLogs(numberOfLogs);
    }

    @Override
    public String viewLogEntriesWarnings() throws RemoteException {
        logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
        return dbConnection.viewWarningLogEntries();
    }

    @Override
    public String viewLogEntriesErrors() throws RemoteException {
        logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
        return dbConnection.viewErrorLogEntries();
    }

    /////////////////////////////////////
    /// Testing methods please ignore

    public void createFakeLogWarning() {
        System.out.println("Making fake log...");
        dbConnection.createFakeLog(101, "WARNING", "999999999");
    }

    public void createFakeLogError() {
        System.out.println("Making fake log...");
        dbConnection.createFakeLog(2000, "ERROR", "999999999");
    }


    /////////////////// END OF LOGGER METHODS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkPermissions(String email, String request) {
        return dbConnection.checkPermissions(email, request);
    }

    public void createUser(User user, String plaintext) {
        try {
            logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_CREATED_IN_DB, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Creating user in database...");
        dbConnection.createUser(user, plaintext);
    }

    public String viewPatients(String email) {
        if (dbConnection.checkPermissions(email, "view_patient")) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return dbConnection.viewPatients(-1);
        }
        try {
            logEvent(getUserId(email),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

    public String viewPatients(String email, int s_id) {
        if (dbConnection.checkPermissions(email, "view_patient")) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return dbConnection.viewPatients(s_id);
        }
        try {
            logEvent(getUserId(email),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String viewStaffs(String email) {
        if (dbConnection.checkPermissions(email, "view_staff")) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return dbConnection.viewStaffs(-1);
        }
        try {
            logEvent(getUserId(email),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String viewStaffs(String email, int s_id) {
        if (dbConnection.checkPermissions(email, "view_staff")) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_ACCESSED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return dbConnection.viewStaffs(s_id);
        }
        try {
            logEvent(getUserId(email),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

    public String deletePatients(String email, int p_id) {
        if (dbConnection.checkPermissions(email, "delete_patient")) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_DELETED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return dbConnection.deletePatients(p_id);
        }
        try {
            logEvent(getUserId(email),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String deleteStaffs(String email, int s_id) {
        if (dbConnection.checkPermissions(email, "delete_staff")) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_DELETED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return dbConnection.deletePatients(s_id);
        }
        try {
            logEvent(getUserId(email),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String updatePatients(String email, int p_id, String command) {
        if (dbConnection.checkPermissions(email, "update_patient")) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_MODIFIED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            dbConnection.updatePatients(p_id, command);
        }
        try {
            logEvent(getUserId(email),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public String updateStaffs(String email, int s_id, String command) {
        if (dbConnection.checkPermissions(email, "update_staff")) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_MODIFIED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return dbConnection.updateStaffs(s_id, command);
        }
        try {
            logEvent(getUserId(email),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

    @Override
    public boolean verifyPassword(String plaintext, String email) {
        return dbConnection.verifyPassword(plaintext, email);
    }

    @Override
    public String getRole(String email) {
        return dbConnection.getUserRole(email);
    }

    @Override
    public boolean userIsAdmin(String email) {
        if(dbConnection.userIsAdmin(email)) {
            try {
                logEvent(getUserId(email),Constants.LOG_USER_IS_ADMIN, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
        try {
            logEvent(getUserId(email),Constants.LOG_TRIED_TO_ADMIN_COMMAND, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
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
            try {
                logEvent(getUserId(email),Constants.OTP_WAS_SENT, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyOTP(String email, Integer attempt) {
        Integer otp = otpTable.get(email);
        return attempt.equals(otp);
    }

    ///////////////////
    @Override
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
            String text = "Dear Admin, \n\nUser #" + dbConnection.getUserId(email) + " has been flagged for suspicious and potentially harmful activity." +
                    "\n\nTheir account has been temporarily locked." +
                    "\n\nPlease Log In and review their activity." +
                    "\n\n\n \t-This is an automated email generated by the system";


            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("scc363auth@gmail.com"));
            //

            //
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dbConnection.getAdminEmailAddress()));
            message.setSubject("System Warning User: " + getUserId(email));
            message.setText(text);

            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully");
            try {
                logEvent(getUserId(email),Constants.LOG_WARNING_EMAIL_SENT, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String viewLockedAccounts() {
        try {
            logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return dbConnection.viewLockedAccounts();
    }

    @Override
    public boolean isAccountUnlocked(String email) {
        return dbConnection.isAccountUnlocked(email);
    }

    ///////////////////

    @Override
    public int getUserId(String email) {
        return dbConnection.getUserId(email);
    }

    public boolean checkEmailAvailable(String email) {
        return dbConnection.checkEmailAvailable(email);
    }

    @Override
    public String lockAccountManual(String userId) {
        try {
            logEvent(Integer.valueOf(userId),Constants.LOG_USER_ACCOUNT_LOCKED_MANUALLY, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return dbConnection.lockAccount(Integer.valueOf(userId));
    }

    @Override
    public String kickAndLockUserAutomatic(String emailAddress) {
        return dbConnection.lockAccount(getUserId(emailAddress));
    }

    @Override
    public String unlockAccount(String id) {
        try {
            logEvent(Integer.valueOf(id),Constants.LOG_ACCOUNT_UNLOCKED, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return dbConnection.unlockAccount(id);
    }

    public String updateRole(String adminEmail, int userId, String role) {
        // Check user is a system admin before locking account
        if (dbConnection.checkPermissions(adminEmail, "lock_accounts")) {
            try {
                logEvent(getUserId(adminEmail),Constants.LOG_USER_MODIFIED_DATA, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return dbConnection.updateRole(userId, role);
        }
        try {
            logEvent(getUserId(adminEmail),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "You do not have the correct permissions to do that.";
    }

}