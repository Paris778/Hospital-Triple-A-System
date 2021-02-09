import database.Patient;
import database.Staff;
import database.User;
import utility.Constants;
import utility.PasswordHandler;

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


    //////////////////////////////////////////////////
    //Main Method
    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        connectToServer();
        try {
            runUserInterface();
        } catch (RemoteException e) {
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
        }
    }

    private void setUserInput() {
        userInput = new Scanner(System.in).nextLine();
    }

    ////////////////////////////////////////////////
    //This method runs the main user interface
    private void runUserInterface() throws RemoteException {

        while (tlsAuth == 1) {
            System.out.println("=========================================================");
            System.out.println("     Welcome to  the Hospital Service System");
            System.out.println("=========================================================");
            System.out.println("> If you already have an account,use command 'login'");
            System.out.println("> Otherwise type 'help' to see all available commands.");
            System.out.println("=========================================================");

            setUserInput();

            switch (userInput.toLowerCase()) {
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

            while (loggedIn) {
                try {
                    System.out.println("> Logged in as: " +  server.getRole(email_address));
                    if(server.userIsAdmin(email_address)){
                        System.out.println("==========================================================");
                        System.out.println("> You have been verified as |SYSTEM ADMIN|");
                        System.out.println("> Type 'helpme' to see all additional admin-only commands");
                        System.out.println("==========================================================");

                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                System.out.println("> Welcome, Please select and type one of the following options");
                System.out.println("-----------------------------------------------");
                System.out.println("|  register | logout | view | delete | update |");
                System.out.println("-----------------------------------------------");
                System.out.println("> Otherwise type 'helpme' to see all available commands.");
                setUserInput();
                switch (userInput.toLowerCase()) {
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

                    case "helpme":
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


                    default:
                        System.out.println("> Sorry. Unrecognised command. Check your spelling.\n> Use 'helpme' for a list of commands");
                        break;
                }
            }
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
            if(server.userIsAdmin(email_address)){
                System.out.println(server.viewLogEntries());
            } else{
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //
    private void viewRecentLogsCommand() {
        try {
            if(server.userIsAdmin(email_address)){
                boolean validInput = false;
                int numberOfLogs = 10;
                while(!validInput){
                    System.out.println("> Please specify how many logs you'd like to view. Default is 10");
                    setUserInput();

                    try{
                        numberOfLogs = Integer.valueOf(userInput);
                    } catch (Exception e){
                        System.out.println("> Invalid Input. Input must be an integer number lower than 50.");
                        continue;
                    }
                    if( numberOfLogs < 50 ){
                        System.out.println(server.viewRecentLogs(numberOfLogs));
                        validInput = true;
                    } else {
                        System.out.println("> Invalid Input. Input must be an integer number lower than 50.");
                    }
                }
            } else{
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void userResponsibilityCommand() {
        try {
            if(server.userIsAdmin(email_address)){
                System.out.println(server.printUserResponsibility());
            } else{
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //
    private void inspectUserCommand() {
        try {
            if(server.userIsAdmin(email_address)){
                System.out.println("> Please type the ID of the user you'd like to inspect.");
                setUserInput();
                System.out.println(server.inspectSpecificUser(userInput));
            } else{
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void viewWarnings() {
        try {
            if(server.userIsAdmin(email_address)){
                System.out.println(server.viewLogEntriesWarnings());
            } else{
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void viewErrors() {
        try {
            if(server.userIsAdmin(email_address)){
                System.out.println(server.viewLogEntriesErrors());
            } else{
                System.out.println("> Sorry. You don't have access to this command.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void viewSuspiciousActivity() {
        try {
            if(server.userIsAdmin(email_address)){
                System.out.println(server.viewLogEntriesWarningsAndErrors());
            } else{
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
        System.out.println("> Use command 'register' if you're a new user.");
        System.out.println("> Use command 'login' if you're already registered.");
        System.out.println("> Use command 'logout' to logout.");
        System.out.println("> Use command 'forgotpw' to reset your password.");
        System.out.println("=========================================================");
    }

    ////////////////////////////////////////////////
    // HelpMe command function | Used after log in
    ////////////////////////////////////////////////
    private void helpMeCommand(String email_address){

        System.out.println("===================================================================================");
        System.out.println("> Use command 'register' if you're a new user.");
        System.out.println("> Use command 'logout' in order to safely log out.");
        System.out.println("> Use command 'view' to view records (APPROPRIATE LEVEL OF ACCESS REQUIRED)");
        System.out.println("> Use command 'delete' to delete records (APPROPRIATE LEVEL OF ACCESS REQUIRED)");
        System.out.println("> Use command 'update' to update records (APPROPRIATE LEVEL OF ACCESS REQUIRED)");
        System.out.println("===================================================================================");

        try {
            if(server.userIsAdmin(email_address)){
                System.out.println("=============================================================");
                System.out.println("                    ADMIN ONLY COMMANDS");
                System.out.println("=============================================================");
                System.out.println("> Use command 'viewAllLogs' to view all logs'"); //Done and Tested
                System.out.println("> Use command 'viewRecent' to view most recent logs'"); //Done and Tested
                System.out.println("> Use command 'responsibility' to see which users\n  are responsible for suspicious events.'"); //Done and Tested
                System.out.println("> Use command 'viewWarnings' to view all Warning logs'"); //Done and Tested
                System.out.println("> Use command 'viewErrors' to view all Error logs'"); //Done and Tested
                System.out.println("> Use command 'viewSuspicious' to view all suspicious logs'"); //Done and Tested
                System.out.println("> Use command 'inspectUser' to inspect a user's activity'"); //Done and Tested
                //TO:DO
                System.out.println("> Use (NEED TO IMPLEMENT) command 'lockAccount' to inspect a user's activity'");
                System.out.println("> Use (NEED TO IMPLEMENT) command 'unlockAccount' to inspect a user's activity'");
                System.out.println("=============================================================");
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

        System.out.println("> Enter -staff- to register as staff or enter -patient- to register for a patient if you are the admin");
        // String identity = input.nextLine();
        String identity = input.nextLine();
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
            // generate private keys and public key for user
            // ---------------------------------------------


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
        }

    }

    //////////////////////////////////////
    // Login command function
    //////////////////////////////////////
    public void loginCommand() {
        System.out.print("> Please enter your e-mail:  ");
        setUserInput();
        String email = userInput;
        System.out.print("> Please enter your password:  ");
        setUserInput();
        String userPassword = userInput;
        try {

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
                    } else {
                        System.out.println("OTP incorrect, please try again");
                    }
                }
            } else {
                System.out.println("> Incorrect. Please try again.");
                loggedIn = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////
    // Logout command function
    //////////////////////////////////////
    public void logoutCommand() {
        loggedIn = false;
    }

    //////////////////////////////////////
    // Forgot Password command function
    //////////////////////////////////////
    public void forgotPasswordCommand() {
        System.out.println("> Please enter your email address and a one time password (OTP) will be sent");
        String email_address = input.nextLine();
        // sent email
        try {
            server.sendOTP(email_address);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("> Please enter your one time password (OTP)");
        String otp = input.nextLine();
        // verify the otp
        // --------------

        while (true) {
            System.out.println("> Please set your password");
            System.out.println("> The length of the password should be more than 8 characters which must include a capital letter, a lower-case letter, a number and a special symbol");
            String temp1 = input.nextLine();

            // password evaluation
            // -------------------

            System.out.println("> Confirm your password");
            String temp2 = input.nextLine();
            if (temp1.equals(temp2))// &&satisfy password requirements
            {
                break;
            } else {
                System.out.println("> The received password is different from the previous one.");
            }
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
            String email_address = input.nextLine();

            // if the email has been used for register already then break
            boolean emailAvailable = server.checkEmailAvailable(email_address);
            if (!emailAvailable) {
                System.out.println("This email has already been used to register another user. Please try again.");
                return null;
            }

            //Note from Paris: Also make sure to do type checks and format checks
            //e.g That letters are not passed to integer fields and the date is formatted properly for
            // database storage
            System.out.println("> Please enter forename");
            String forename = input.nextLine();
            System.out.println("> Please enter your surname");
            String surname = input.nextLine();
            System.out.println("> Please enter your date of birth in format XXXX-XX-XX");
            String date_of_birth = input.nextLine();
            System.out.println("> Please enter your address");
            String address = input.nextLine();

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