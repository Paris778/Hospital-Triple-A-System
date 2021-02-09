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
        runUserInterface();
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

    private String setUserInput() {
        userInput = new Scanner(System.in).nextLine();
        return userInput;
    }

    private String setUserInput(String s){
        String test;
        String nameRegex = "^[a-zA-Z]+$";
        String wordRegex = "^[a-zA-Z]+$";
        String dateRegex = "^[0-9]+$";
        String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";


        switch (s) {
            case "word":
                test = new Scanner(System.in).nextLine();
                if(test.matches(wordRegex)){
                    userInput = test;
                    return test;
                }else{
                    System.out.println("Incorrect Input - Only letters are accepted.");
                    userInput = "incorrect input";
                    return "";
                }



            case "email":
                test = new Scanner(System.in).nextLine();
                if(test.matches(emailRegex)){
                    userInput = test;
                    return test;
                }else{
                    System.out.println("Incorrect Input - Only E-mail addresses are accepted.");
                    userInput = "incorrect input";
                    return "";
                }



            case "name":
                test = new Scanner(System.in).nextLine();
                if(test.matches(nameRegex)){
                    userInput = test;
                    return test;
                }else{
                    System.out.println("Incorrect Input - Only letters are accepted.");
                    userInput = "incorrect input";
                    return "";
                }

            case "date":
                test = new Scanner(System.in).nextLine();
                if(test.matches(dateRegex)){
                    userInput = test;
                    return test;
                }else{
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
    private void runUserInterface() {
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

                case "test":
                    System.out.println("test");
                    registerCommand();
                    break;

                default:
                    System.out.println("> Sorry. Unrecognised command. Check your spelling.\n> Use 'help' for a list of commands");
                    break;
            }
            userInput = "";

            try {
                System.out.println("> Logged in as: " +  server.getRole(email_address));
                if(server.userIsAdmin(email_address)){
                    System.out.println("=========================================================");
                    System.out.println("> You have been verified as SYSTEM ADMIN");
                    System.out.println("> Type 'help' to see all additional admin-only commands");
                    System.out.println("=========================================================");

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            while (loggedIn) {

                System.out.println("> Welcome, Please select and type one of the following options");
                System.out.println("-----------------------------------------------");
                System.out.println("|  register | logout | view | delete | update |");
                System.out.println("-----------------------------------------------");
                System.out.println("> Otherwise type 'help' to see all available commands.");
                setUserInput("word");
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

                    case "help":
                        helpMeCommand(email_address);
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
                System.out.println("=========================================================");
                System.out.println("                  ADMIN ONLY COMMANDS");
                System.out.println("=========================================================");
                System.out.println("> Use command 'seewarnings' to view all warning logs'");
                System.out.println("=========================================================");
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

        String identity = setUserInput("word");;
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
        }else{
            System.out.println("Registration failed - Please try again");
        }

    }

    //////////////////////////////////////
    // Login command function
    //////////////////////////////////////
    public void loginCommand() {
        System.out.print("> Please enter your e-mail:  ");
        String email = setUserInput("email");
        String userPassword;
        if(email ==""){
            userPassword = "";

        }else{
            System.out.print("> Please enter your password:  ");
            userPassword = setUserInput();
        }

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

            if(email_address ==""){
                return null;
            }else{

                if (!server.checkEmailAvailable(email_address)) {
                    System.out.println("This email has already been used to register another user. Please try again.");
                    return null;
                }

            }
            // if the email has been used for register already then break


            //Note from Paris: Also make sure to do type checks and format checks
            //e.g That letters are not passed to integer fields and the date is formatted properly for
            // database storage
            String forename = "", surname = "", year = "", month = "",day = "", date_of_birth = "", address = "";
            System.out.println("> Please enter forename");
            forename = setUserInput("name");
            if(forename ==""){
                return null;

            }else{
                System.out.println("> Please enter your surname");
                surname = setUserInput("name");
                if(surname == ""){
                    return null;

                }else{
                    System.out.println("> Please enter the year you were born");
                    year = setUserInput("date");
                    if(year == ""){
                        return null;

                    }else{
                        System.out.println("> Please enter the month you were born");
                        month = setUserInput("date");
                        if(month == ""){
                            return null;

                        }else{
                            System.out.println("> Please enter the day you were born");
                            day = setUserInput("date");
                            if(day == ""){
                                return null;

                            }else{
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