import database.Patient;
import database.Staff;
import database.User;
import utility.Constants;
import utility.PasswordHandler;

import javax.crypto.Cipher;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    private int tlsAuth; // condition of authentication ,1 means authentication completed,using
    private boolean loggedIn = false;
    private String email_address;
    private String userInput;
    private final Scanner input = new Scanner(System.in);
    private final PasswordHandler passwordHandler = new PasswordHandler();
    private ServerInterface server;

    // Misc Variables
    private int wrongPasswordCounter = 0;
    private int wrongOtpCounter = 0;


    //////////////////////////////////////////////////
    //Main Method
    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        connectToServer();

        try {
            server.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_NEW_SERVER_CONNECTION, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            runUserInterface();
        } catch (Exception e) {
            try {
                server.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_ERROR_EXCEPTION_THROWN, Constants.USER_ID_SYSTEM);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void connectToServer() {
        System.setProperty("javax.net.ssl.trustStore", "client_truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099, new SslRMIClientSocketFactory());
            server = (ServerInterface) registry.lookup("HelloServer");
            tlsAuth = 1;
        } catch (Exception e) {
            tlsAuth = 0;
            e.printStackTrace();
            try {
                server.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_ERROR_EXCEPTION_THROWN, Constants.USER_ID_SYSTEM);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        }
    }

    private String setUserInput() {
        userInput = new Scanner(System.in).nextLine();
        return userInput;
    }

    private String setUserInput(String s) {
        String test;
        String nameRegex = "^[a-zA-Z]+$";
        String wordRegex = "^[a-zA-Z]+$";
        String dateRegex = "^[0-9]+$";
        String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";


        switch (s) {
            case "word":
                test = new Scanner(System.in).nextLine();
                if (test.matches(wordRegex)) {
                    userInput = test;
                    return test;
                } else {
                    System.out.println("Incorrect Input - Only letters are accepted.");
                    userInput = "incorrect input";
                    return "";
                }


            case "email":
                test = new Scanner(System.in).nextLine();
                if (test.matches(emailRegex)) {
                    userInput = test;
                    return test;
                } else {
                    System.out.println("Incorrect Input - Only E-mail addresses are accepted.");
                    userInput = "incorrect input";
                    return "";
                }


            case "name":
                test = new Scanner(System.in).nextLine();
                if (test.matches(nameRegex)) {
                    userInput = test;
                    return test;
                } else {
                    System.out.println("Incorrect Input - Only letters are accepted.");
                    userInput = "incorrect input";
                    return "";
                }

            case "date":
                test = new Scanner(System.in).nextLine();
                if (test.matches(dateRegex)) {
                    userInput = test;
                    return test;
                } else {
                    System.out.println("Incorrect Input - Only numbers are accepted.");
                    userInput = "incorrect input";
                    return "";
                }


            default:
                System.out.println("NO regex found - testing");
                return "";

        }

    }

    ////////////////////////////////////////////////
    //This method runs the main user interface
    private void runUserInterface() throws RemoteException {

        System.out.println("=========================================================");
        System.out.println("     Welcome to  the Hospital Service System");
        System.out.println("=========================================================");
        while (tlsAuth == 1) {
            System.out.println("=========================================================");
            System.out.println("> If you already have an account,use command 'login'");
            System.out.println("> Otherwise type 'help' to see all available commands.");
            System.out.println("=========================================================");


            switch (setUserInput("word").toLowerCase()) {
                case "login":
                    System.out.println("login");
                    loginCommand();
                    break;

                case "help":
                    System.out.println("help");
                    helpCommand();
                    break;

                case "forgotpw":
                    System.out.println("forgot pw");
                    forgotPasswordCommand();
                    break;

                default:
                    System.out.println("> Sorry. Unrecognised command. Check your spelling.\n> Use 'help' for a list of commands");
                    break;
            }
            userInput = "";
            if(loggedIn){
                backup(1);
            }
            while (loggedIn) {
                try {
                    System.out.println("> Logged in as: " + server.getRole(email_address));
                    if (server.userIsAdmin(email_address)) {
                        System.out.println("==========================================================");
                        System.out.println("> You have been verified as |SYSTEM ADMIN|");
                        System.out.println("> Type 'help' to see all additional admin-only commands");
                        System.out.println("==========================================================");

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    server.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_ERROR_EXCEPTION_THROWN, Constants.USER_ID_SYSTEM);
                }
                System.out.println("> Welcome, Please select and type one of the following options");
                System.out.println("-----------------------------------------------");
                System.out.println("|  register | logout | view | delete | update |");
                System.out.println("-----------------------------------------------");
                System.out.println("> Otherwise type 'help' to see all available commands.");

                switch (setUserInput("word").toLowerCase()) {
                    case "register":
                        System.out.println("register");
                        registerCommand();
                        break;

                    case "logout":
                        System.out.println("logout");
                        logoutCommand();
                        break;

                    case "view":
                        System.out.println("view");
                        viewCommand();
                        break;

                    case "delete":
                        System.out.println("delete");
                        deleteCommand();
                        break;

                    case "update":
                        System.out.println("update");
                        updateCommand();
                        break;

                    case "help":
                        helpMeCommand(email_address);
                        break;

                    //////////////////////////
                    // ADMIN ONLY COMMANDS
                    /////////////////////////

                    case "viewalllogs":
                        viewAllLogsCommand();
                        break;

                    case "viewrecent":
                        viewRecentLogsCommand();
                        break;

                    case "responsibility":
                        userResponsibilityCommand();
                        break;

                    case "viewwarnings":
                        viewWarnings();
                        break;

                    case "viewerrors":
                        viewErrors();
                        break;

                    case "viewsuspicious":
                        viewSuspiciousActivity();
                        break;

                    case "inspectuser":
                        inspectUserCommand();
                        break;

                    case "backup":
                        backup(1);
                        break;

                    case "restore":
                        backup(2);
                        break;

                    case "lockaccount":
                        lockAccountCommand();
                        break;

                    case "unlockaccount":
                        unlockAccountCommand();
                        break;

                    case "viewlocked":
                        viewLockedAccountsCommand();
                        break;

                    default:
                        System.out.println("> Sorry. Unrecognised command. Check your spelling.\n> Use 'help' for a list of commands");
                        userInput = "";
                        break;
                }
                userInput = "";
            }
            userInput = "";
        }

        if (tlsAuth == 0) {
            System.out.println("unable to connect to server, GoodBye!");
            System.exit(0);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // User input commands !!
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////
    //ADMIN Commands START
    ///////////////////////

    private void viewAllLogsCommand() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println(server.viewLogEntries());
            } else {
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                server.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_ERROR_EXCEPTION_THROWN, Constants.USER_ID_SYSTEM);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        }
    }

    //
    private void viewRecentLogsCommand() {
        try {
            if (server.userIsAdmin(email_address)) {
                boolean validInput = false;
                int numberOfLogs = 10;
                while (!validInput) {
                    System.out.println("> Please specify how many logs you'd like to view. Default is 10");
                    setUserInput();

                    try {
                        numberOfLogs = Integer.valueOf(userInput);
                    } catch (Exception e) {
                        System.out.println("> Invalid Input. Input must be an integer number lower than 50.");
                        server.logEvent(server.getUserId(email_address),Constants.LOG_INVALID_INPUT, Constants.USER_ID_SYSTEM);
                        continue;
                    }
                    if (numberOfLogs < 50) {
                        System.out.println(server.viewRecentLogs(numberOfLogs));
                        validInput = true;
                    } else {
                        System.out.println("> Invalid Input. Input must be an integer number lower than 50.");
                        server.logEvent(server.getUserId(email_address),Constants.LOG_INVALID_INPUT, Constants.USER_ID_SYSTEM);
                    }
                }
            } else {
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            try {
                server.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_ERROR_EXCEPTION_THROWN, Constants.USER_ID_SYSTEM);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void backup(int x) {
        try {
            //
            if (server.userIsAdmin(email_address)) {
                if (x == 1) {
                    System.out.println(server.databaseEncryption(Cipher.ENCRYPT_MODE, "new-database.db", "encryptedBackup.db"));
                } else if (x == 2) {
                    System.out.println(server.databaseEncryption(Cipher.DECRYPT_MODE, "encryptedBackup.db", "new2-database.db"));
                }
            } else {
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            try {
                server.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_ERROR_EXCEPTION_THROWN, Constants.USER_ID_SYSTEM);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
            e.printStackTrace();
        }


    }

    private void userResponsibilityCommand() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println(server.printUserResponsibility());
            } else {
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            try {
                server.logEvent(Constants.USER_ID_SYSTEM,Constants.LOG_ERROR_EXCEPTION_THROWN, Constants.USER_ID_SYSTEM);
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    //
    private void inspectUserCommand() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println("> Please type the ID of the user you'd like to inspect.");
                setUserInput();
                System.out.println(server.inspectSpecificUser(userInput));
            } else {
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void viewWarnings() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println(server.viewLogEntriesWarnings());
            } else {
                server.logEvent(server.getUserId(email_address),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void viewErrors() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println(server.viewLogEntriesErrors());
            } else {
                server.logEvent(server.getUserId(email_address),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void viewSuspiciousActivity() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println(server.viewLogEntriesWarningsAndErrors());
            } else {
                server.logEvent(server.getUserId(email_address),Constants.LOG_USER_DENIED_ACCESS, Constants.USER_ID_SYSTEM);
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /// New Commands

    private void lockAccountCommand() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println("> Please type the ID of the user you'd like to LOCK the account of.");
                setUserInput();
                System.out.println(server.lockAccountManual(userInput));
            } else {
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unlockAccountCommand() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println("> Please type the ID of the user you'd like to UNLOCK the account of.");
                setUserInput();
                System.out.println(server.unlockAccount(userInput));
            } else {
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void viewLockedAccountsCommand() {
        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println(server.viewLockedAccounts());
            } else {
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////
    //ADMIN Commands END
    ///////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////
    // Help command function
    //////////////////////////////////////
    private void helpCommand() {
        System.out.println("=========================================================");
        System.out.println("> Use command 'login' if you're already registered.");
        System.out.println("> Use command 'help' to find this page.");
        System.out.println("> Use command 'forgotpw' to reset your password.");
        System.out.println("=========================================================");
    }

    ////////////////////////////////////////////////
    // HelpMe command function | Used after log in
    ////////////////////////////////////////////////
    private void helpMeCommand(String email_address) {

        System.out.println("===================================================================================");
        System.out.println("> Use command 'register' if you're a new user.");
        System.out.println("> Use command 'logout' in order to safely log out.");
        System.out.println("> Use command 'view' to view records (APPROPRIATE LEVEL OF ACCESS REQUIRED)");
        System.out.println("> Use command 'delete' to delete records (APPROPRIATE LEVEL OF ACCESS REQUIRED)");
        System.out.println("> Use command 'update' to update records (APPROPRIATE LEVEL OF ACCESS REQUIRED)");
        System.out.println("===================================================================================");

        try {
            if (server.userIsAdmin(email_address)) {
                System.out.println("===================================================================");
                System.out.println("                       ADMIN ONLY COMMANDS");
                System.out.println("====================================================================");
                System.out.println("> Use command 'viewAllLogs' to view all logs'"); //Done and Tested
                System.out.println("> Use command 'viewRecent' to view most recent logs'"); //Done and Tested
                System.out.println("> Use command 'responsibility' to see which users...\n       ...are responsible for suspicious events.'"); //Done and Tested
                System.out.println("> Use command 'viewWarnings' to view all Warning logs'"); //Done and Tested
                System.out.println("> Use command 'viewErrors' to view all Error logs'"); //Done and Tested
                System.out.println("> Use command 'viewSuspicious' to view all suspicious logs'"); //Done and Tested
                System.out.println("> Use command 'inspectUser' to inspect a user's activity'"); //Done and Tested
                System.out.println("> Use command 'lockAccount' to manually lock a user's account'");     //Done and Tested
                System.out.println("> Use command 'unlockAccount' to manually unlock a user's account'");  //Done and Tested
                System.out.println("> Use command 'viewLocked' to see all locked Accounts'");   //Done and Tested

                // Send warning email                           //Done and Tested
                // Lcoked users can't login                    // Done and Tested
                // Counter to send warnings and lock/kick      // Done and Tested
                // Check if locked                              //Done and Tested
                // Warned if account is locked                  //Done and Tested
                // Users who are locked can't access account    //Done and tested

                // TO:DO
                // Fix UserIsAdmin MEthod in dbConnect          //Done adn Tested
                // Put logs where they need to go
                System.out.println("====================================================================");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////
    // Register command function
    //////////////////////////////////////
    private void registerCommand() {
        String password = null;
        try {
            server.logEvent(server.getUserId(email_address),Constants.LOG_USER_ENTERED_WRONG_OTP, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println("> Enter -staff- to register as staff or enter -patient- to register for a patient if you are the admin");
        // String identity = input.nextLine();

        String identity = setUserInput("word");
        //Integrity stuff
        identity = identity.toLowerCase();
        User user = null;

        // Print error message if no valid identity entered
        if (!identity.equals("patient") && !identity.equals("staff")) {
            System.out.println("> Please enter an valid identity");
        } else {
            user = register(identity.equals("patient"));
        }

        // if information all valid
        if (user != null) {

            //Register and validate password
            while (true) {
                boolean passwordEval = false;
                while (!passwordEval) {
                    System.out.println("> Please set your password");
                    System.out.println("> The length of the password should be more than 8 characters which must include a capital letter, a lower-case letter, a number and a special symbol");
                    password = input.nextLine();
                    passwordEval = passwordHandler.checkPasswordStrength(password) >= Constants.INTERMEDIATE_PASSWORD;
                    // password evaluation
                    if (passwordEval) {
                        System.out.println("> Strong Password !");
                        break;
                    } else {
                        try {
                            server.logEvent(server.getUserId(email_address),Constants.LOG_USER_WEAK_PASSWORD, Constants.USER_ID_SYSTEM);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        passwordHandler.printPasswordImprovementSuggestions(); //Prints suggestions
                        System.out.println("> Suggested strong password: " + passwordHandler.getStrongPassword());
                    }
                }
                //
                System.out.println("> Please confirm your password (type again)");
                String temp2 = input.nextLine();
                // Satisfy password requirements
                if (password.equals(temp2)) {
                    System.out.println("> Password Confirmed !!!!");
                    // Create user and store hashed password in DB
                    try {
                        server.createUser(user, password);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    password = null; //Safety stuff
                    temp2 = null; //Safety stuff
                    break;
                } else
                    System.out.println("> The received password is different from the previous one.");
            }
        } else {
            System.out.println("Registration failed - Please try again");
            try {
                server.logEvent(server.getUserId(email_address),Constants.LOG_FAILED_REGISTRATION, Constants.USER_ID_SYSTEM);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    }

    //////////////////////////////////////
    // Login command function
    //////////////////////////////////////
    public void loginCommand() {
        System.out.print("> Please enter your e-mail:  ");
        String email = setUserInput("email");
        String userPassword;
        if (email == "") {
            userPassword = "";

        } else {
            System.out.print("> Please enter your password:  ");
            userPassword = setUserInput();
        }

        try {
            if (server.isAccountUnlocked(email)) {
                // Verify that OTP matches OTP sent by server
                if (server.verifyPassword(userPassword, email)) {
                    int verfiy = 1;
                    while (verfiy == 1) {

                        email_address = email;
                        System.out.print("If your e-mail is valid an OTP will been sent your email, please enter the code: ");
                        server.sendOTP(email);
                        Integer otp = new Scanner(System.in).nextInt();
                        if (server.verifyOTP(email_address, otp)) {
                            loggedIn = true;
                            verfiy = 0;
                            System.out.println("> Logged in successfully.");
                            server.logEvent(server.getUserId(email_address),Constants.LOG_USER_LOGGED_IN, Constants.USER_ID_SYSTEM);
                        } else {
                            System.out.println("OTP incorrect, please try again");
                            this.wrongOtpCounter++;
                            server.logEvent(server.getUserId(email_address),Constants.LOG_USER_ENTERED_WRONG_OTP, Constants.USER_ID_SYSTEM);

                            if(detectMalicious()) {
                                kickAndLock();
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("> Incorrect. Please try again.");
                    this.wrongPasswordCounter++;
                    server.logEvent(server.getUserId(email_address),Constants.LOG_USER_ENTERED_WRONG_PASSWORD, Constants.USER_ID_SYSTEM);
                    if(detectMalicious()) {
                        kickAndLock();
                    }
                }
            }
            //IF ACCOUNT IS LOCKED.
            else {
                System.out.println("> We apologise but this account doesn't exist or has been LOCKED due to security concerns.\n" +
                        "> If your account is LOCKED, a System Administrator will review your case shortly.\n" +
                        "> For Further enquiries please message customer support");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean detectMalicious(){
        if(wrongOtpCounter >= Constants.MAX_ALLOWED_FALSE_ENTRIES || wrongPasswordCounter >= Constants.MAX_ALLOWED_FALSE_ENTRIES){
            return true;
        }
        return false;
    }

    //If user enters password or OTP wrong multiple times , their account gets locked.
    // System admins can review locked accounts.
    private void kickAndLock(){
        try {
            server.logEvent(server.getUserId(email_address),Constants.LOG_USER_KICKED, Constants.USER_ID_SYSTEM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        logoutCommand();
        wrongOtpCounter = 0;
        wrongPasswordCounter = 0;
        System.out.println("\n\n-----------------------------------------------------------------------------------------------");
        System.out.println("> We apologise !!! But malicious activity has been detected in this account.\n" +
                "> Your account has been locked and you have been logged out." +
                "\n> A system admin will review your case shortly");
        System.out.println("-----------------------------------------------------------------------------------------------");
        //Lock account and send warning email
        try {
            server.kickAndLockUserAutomatic(email_address);
            server.sendWarningEmail(email_address);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////
    // Logout command function
    //////////////////////////////////////
    public void logoutCommand() {
        loggedIn = false;
        wrongPasswordCounter = 0;
        wrongOtpCounter = 0;
    }

    //////////////////////////////////////
    // Forgot Password command function
    //////////////////////////////////////
    public void forgotPasswordCommand() {

        System.out.println("> Please enter your email address and a one time password (OTP) will be sent");
        String email = setUserInput("email");
        String password = null;
        // sent email
        try {
            server.sendOTP(email);
            System.out.print("If your e-mail is valid an OTP will been sent your email, please enter the code: ");
            Integer otp = new Scanner(System.in).nextInt();
            if (server.verifyOTP(email, otp)) {
                System.out.println("> OTP Correct - You can now change reset your password");

                while (true) {
                    boolean passwordEval = false;
                    while (!passwordEval) {
                        System.out.println("> Please set your password");
                        System.out.println("> The length of the password should be more than 8 characters which must include a capital letter, a lower-case letter, a number and a special symbol");
                        password = input.nextLine();
                        passwordEval = passwordHandler.checkPasswordStrength(password) >= Constants.INTERMEDIATE_PASSWORD;
                        // password evaluation
                        if (passwordEval) {
                            System.out.println("> Strong Password !");
                            break;
                        } else {
                            passwordHandler.printPasswordImprovementSuggestions(); //Prints suggestions
                            System.out.println("> Suggested strong password: " + passwordHandler.getStrongPassword());
                        }
                    }
                    //
                    System.out.println("> Please confirm your password (type again)");
                    String temp2 = input.nextLine();
                    // Satisfy password requirements
                    if (password.equals(temp2)) {
                        System.out.println("> Password Confirmed !!!!");
                        // Create user and store hashed password in DB
                        try {
                            System.out.println("needs method for updating password");
                            //need method to update user password.
                            //
                            //
                            //

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        password = null; //Safety stuff
                        temp2 = null; //Safety stuff
                        break;
                    } else
                        System.out.println("> The received password is different from the previous one.");
                }
            } else {
                System.out.println("OTP incorrect, please try again");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void viewCommand() {

        try {
            System.out.println("What type of data do you want to view? myprofile/patient/staff");
            String temp = input.nextLine();
            int id;
            switch (temp) {
                case "myprofile":
                    id = server.getUserId(email_address);
                    System.out.println(server.viewPatients(email_address, id));
                    break;
                case "patient":
                    System.out.println("Enter a valid id to view patient information or type");
                    id = input.nextInt();
                    if (id <= 0)
                        System.out.println(server.viewPatients(email_address));
                    else
                        System.out.println(server.viewPatients(email_address, id));
                    break;
                case "staff":
                    System.out.println("Enter a valid id to view patient information or type");
                    id = input.nextInt();
                    if (id <= 0)
                        System.out.println(server.viewStaffs(email_address));
                    else
                        System.out.println(server.viewStaffs(email_address, id));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deleteCommand() {

        try {
            System.out.println("What kind of user data do you want to delete? patient/staff");
            String temp = input.nextLine();
            int id;
            switch (temp) {
                case "patient":
                    System.out.println("Enter a valid id to view patient information or type");
                    id = input.nextInt();
                    if (id <= 0)
                        System.out.println("id invalid");
                    else
                        System.out.println(server.viewPatients(email_address, id));
                    System.out.println("print y to confirm");
                    if (input.nextLine().equals("y")) {
                        System.out.println(server.deletePatients(email_address, id));
                    }
                    break;
                case "staff":
                    System.out.println("Enter a valid id to view patient information or type");
                    id = input.nextInt();
                    if (id <= 0)
                        System.out.println("id invalid");
                    else
                        System.out.println(server.viewStaffs(email_address, id));
                    System.out.println("print y to confirm");
                    if (input.nextLine().equals("y")) {
                        System.out.println(server.deleteStaffs(email_address, id));
                    }
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateCommand() {

        try {
            System.out.println("What kind of user data do you want to update? patient/staff/roles");
            String temp = input.nextLine();
            int id;
            String command;
            switch (temp) {
                case "patient" -> {
                    System.out.println("Enter a valid id to view patient information or type");
                    id = input.nextInt();
                    // TODO: check if ID exists in database
                    if (id <= 0)
                        System.out.println("id invalid");
                    else {
                        System.out.println(server.viewPatients(email_address, id));
                        System.out.println("enter valid command to update the information e.g. forename = 'a',surname = 'b'");
                        command = input.nextLine();
                        System.out.println(server.updatePatients(email_address, id, command));
                    }
                }
                case "staff" -> {
                    System.out.println("Enter a valid id to view patient information or type");
                    id = input.nextInt();
                    // TODO: check if ID exists in database
                    if (id <= 0)
                        System.out.println("id invalid");
                    else {
                        System.out.println(server.viewStaffs(email_address, id));
                        System.out.println("enter valid command to update the information e.g. forename = 'a',surname = 'b'");
                        command = input.nextLine();
                        System.out.println(server.updateStaffs(email_address, id, command));
                    }
                }
                case "roles" -> {
                    System.out.println("Enter a valid id of the user whose role you want to change");
                    id = input.nextInt();
                    // TODO: check if ID exists in database
                    if (id <= 0)
                        System.out.println("Id invalid");
                    else {
                        System.out.println("Enter the role you want to assign to this user");
                        String role = input.nextLine();
                        System.out.println(server.updateRole(email_address, id, role));
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private User register(boolean isPatient) {
        User user = null;
        try {
            System.out.println("> Please enter your email to register");
            String email_address = setUserInput("email");

            if (email_address == "") {
                return null;
            } else {

                if (!server.checkEmailAvailable(email_address)) {
                    System.out.println("This email has already been used to register another user. Please try again.");
                    return null;
                }

            }
            // if the email has been used for register already then break


            //Note from Paris: Also make sure to do type checks and format checks
            //e.g That letters are not passed to integer fields and the date is formatted properly for
            // database storage
            String forename = "", surname = "", year = "", month = "", day = "", date_of_birth = "", address = "";
            System.out.println("> Please enter forename");
            forename = setUserInput("name");
            if (forename == "") {
                return null;

            } else {
                System.out.println("> Please enter your surname");
                surname = setUserInput("name");
                if (surname == "") {
                    return null;

                } else {
                    System.out.println("> Please enter the year you were born");
                    year = setUserInput("date");
                    if (year == "") {
                        return null;

                    } else {
                        System.out.println("> Please enter the month you were born");
                        month = setUserInput("date");
                        if (month == "") {
                            return null;

                        } else {
                            System.out.println("> Please enter the day you were born");
                            day = setUserInput("date");
                            if (day == "") {
                                return null;

                            } else {
                                System.out.println("> Please enter your address");
                                address = input.nextLine();
                            }
                        }
                    }
                }
            }


            date_of_birth = year + "-" + month + "-" + day;

            // Register patient
            if (isPatient) {
                user = new Patient(forename, surname, date_of_birth, address, email_address, "patient");
            } else {
                // Register staff
                System.out.println("> Please enter your sector (clinical or admin)");
                String sector = input.nextLine();
                System.out.println("> Please enter your phone number");
                String phone_number = input.nextLine();
                user = new Staff(forename, surname, date_of_birth, address, email_address, "staff", sector, phone_number);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return user;
    }
}