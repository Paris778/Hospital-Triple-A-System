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
    private final String host = "localhost";
    private final int port = 1099; // default port
    private int Authenticcondition = -1; // condition of authentication ,1 means authentication completed,using
    private int tlsAuth; // condition of authentication ,1 means authentication completed,using
    // int can expand more options
    private String email_address;
    private String userInput;
    private Scanner input = new Scanner(System.in);
    private final PasswordHandler passwordHandler = new PasswordHandler();
    private ServerInterface server;


    //////////////////////////////////////////////////
    //Main Method
    public static void main(String[] args) {
        new Client();
    }

    public Client(){


        connectToServer();
        runUserInterface();


    }

    private void connectToServer(){

        System.setProperty("javax.net.ssl.trustStore", "client_truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099, new SslRMIClientSocketFactory());
            server = (ServerInterface) registry.lookup("HelloServer");
            tlsAuth = 1;
        } catch (Exception e) {
            tlsAuth = 0;
            System.out.println(e);
        }
    }
    private void setUserInput(){

        userInput = new Scanner(System.in).nextLine();

    }
    ////////////////////////////////////////////////
    //This method runs the main user interface
    private void runUserInterface() {

        while (tlsAuth == 1) {
                System.out.println("> Welcome to  the Hospital Service System");
                System.out.println("> If you already have an account,use command 'login'");
                System.out.println("> Otherwise type 'help' to see all available commands.");

                setUserInput();

                switch(userInput.toLowerCase()){
                    case "login":
                        System.out.println("login");
                        loginCommand();
                        break;

                    case "help":
                        System.out.println("help");
                        helpCommand();
                        break;

                    case "register":
                        System.out.println("register");
                        registerCommand();
                        break;

                    case "logout":
                        System.out.println("logout");
                        logoutCommand();
                        break;

                    case "forgotpw":
                        System.out.println("forgot pw");
                        forgotPasswordCommand();
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

                    default:
                        System.out.println("> Sorry. Unrecognised command. Check your spelling.\n> Use 'help' for a list of commands");
                        break;
                }
                userInput = null;
            }
            if(tlsAuth == 0){
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
        System.out.println("> Use command 'register' if you're a new user.");
        System.out.println("> Use command 'login' if you're already registered.");
        System.out.println("> Use command 'logout' to logout.");
        System.out.println("> Use command 'forgotpw' to reset your password.");
    }

    //////////////////////////////////////
    // Register command function
    //////////////////////////////////////
    private void registerCommand() {
        String password = null;
        if (Authenticcondition < 0) {
            System.out.println("> Enter -staff- to register as staff or enter -patient- to register for a patient if you are the admin");
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
        } else
            System.out.println("> Please log out first");
    }

    //////////////////////////////////////
    // Login command function
    //////////////////////////////////////
    public void loginCommand() {
        if (Authenticcondition > 0) {
            System.out.println("> Please log out first");
        } else {
            System.out.println("> Choose what identity you want to login as  (staff/patient/regulator/admin)");
            String identity = input.nextLine();
            int index = -1;
            // Print error message if no valid identity entered
            for (int i = 0; i < Constants.ROLE_LIST.length; i++) {
                if (identity.equals(Constants.ROLE_LIST[i])) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                System.out.println("> Please enter an valid identity");
            } else {
                ////////////////////////////////////////
                //need to be updated
                login(identity.equals("patient"));
                ///////////////////////////////////////
            }
        }




















    }

    private void loginUI(){

        System.out.println("> Choose what identity you want to login as  (staff/patient/regulator/admin)");

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////
    // Logout command function
    //////////////////////////////////////
    public void logoutCommand() {
        if (Authenticcondition > 0) {
            Authenticcondition = -1;
        } else {
            System.out.println("> Please log in first");
        }
    }

    //////////////////////////////////////
    // Forgot Password command function
    //////////////////////////////////////
    public void forgotPasswordCommand() {
        if (Authenticcondition < 0) {

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
        } else {
            System.out.println("> Please log out first");
        }
    }

    public void viewCommand() {
        if (Authenticcondition > 0) {
            try {
                System.out.println("What type of data do you want to view? myprofile/patient/staff");
                String temp = input.nextLine();
                int id;
                switch (temp) {
                    case "myprofile":
                        id = server.getUserId(email_address);
                        server.viewPatients(id);
                        break;
                    case "patient":
                        System.out.println("Enter a valid id to view patient information or type");
                        id = input.nextInt();
                        if (id <= 0)
                            server.viewPatients();
                        else
                            server.viewPatients(id);
                        break;
                    case "staff":
                        System.out.println("Enter a valid id to view patient information or type");
                        id = input.nextInt();
                        if (id <= 0)
                            server.viewStaffs();
                        else
                            server.viewStaffs(id);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("> Please log out first");
    }

    public void deleteCommand() {
        if (Authenticcondition > 0) {
            try {
                System.out.println("What kind of user data do you want to delete? patient/staff");
                String temp = input.nextLine();
                int id;
                switch (temp) {
                    case "patient":
                        if (server.checkPermissions(email_address, "delete_patient")) {
                            System.out.println("Enter a valid id to view patient information or type");
                            id = input.nextInt();
                            if (id <= 0)
                                System.out.println("id invalid");
                            else
                                server.viewPatients(id);
                            System.out.println("print y to confirm");
                            if (input.nextLine().equals("y")) {
                                server.deletePatients(id);
                            }
                        } else {
                            System.out.println("You do not have the correct permissions to do that.");
                        }
                        break;
                    case "staff":
                        if (server.checkPermissions(email_address, "delete_staff")) {
                            System.out.println("Enter a valid id to view patient information or type");
                            id = input.nextInt();
                            if (id <= 0)
                                System.out.println("id invalid");
                            else
                                server.viewStaffs(id);
                            System.out.println("print y to confirm");
                            if (input.nextLine().equals("y")) {
                                server.deleteStaffs(id);
                            }
                        } else {
                            System.out.println("You do not have the correct permissions to do that.");
                        }
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("> Please log out first");
    }

    public void updateCommand() {
        if (Authenticcondition > 0) {
            try {

                System.out.println("What kind of user data do you want to update? patient/staff");
                String temp = input.nextLine();
                int id;
                String command;
                switch (temp) {
                    case "patient" -> {
                        // Check user has correct permissions before allowing update
                        if (server.checkPermissions(email_address, "update_patient")) {
                            System.out.println("Enter a valid id to view patient information or type");
                            id = input.nextInt();
                            // TODO: check if ID exists in database
                            if (id <= 0)
                                System.out.println("id invalid");
                            else {
                                server.viewPatients(id);
                                System.out.println("enter valid command to update the information e.g. forename = 'a',surname = 'b'");
                                command = input.nextLine();
                                server.updatePatients(id, command);
                            }
                        } else {
                            System.out.println("You do not have the correct permissions to do that.");
                        }
                    }
                    case "staff" -> {
                        // Check user has correct permissions before allowing update
                        if (server.checkPermissions(email_address, "update_staff")) {
                            System.out.println("Enter a valid id to view patient information or type");
                            id = input.nextInt();
                            // TODO: check if ID exists in database
                            if (id <= 0)
                                System.out.println("id invalid");
                            else {
                                server.viewStaffs(id);
                                System.out.println("enter valid command to update the information e.g. forename = 'a',surname = 'b'");
                                command = input.nextLine();
                                server.updateStaffs(id, command);
                            }
                        } else {
                            System.out.println("You do not have the correct permissions to do that.");
                        }
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("> Please log out first");
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

    private void login(boolean isPatient) {
        try {
            System.out.println("> Please enter your e-mail. ");
            String email_address = input.nextLine();
            System.out.println("> Do you want to log in with an email verification code? (Type 'y' to use OTP / type 'n' to use password)");
            if (input.nextLine().toLowerCase().equals("y")) {
                // Send OTP email
                server.sendOTP(email_address);

                System.out.println("> Enter your verification code");
                Integer otp = input.nextInt();
                // Verify that OTP matches OTP sent by server
                if (server.verifyOTP(email_address, otp)) {
                    System.out.println("OTP verified!");
                } else {
                    System.out.println("The OTP you entered does not match the one sent to your email address");
                }
            } else {
                System.out.println("> Enter your password");
                String password = input.nextLine();

                // Check password matches password hash in database
                if (server.verifyPassword(password, email_address)) {
                    System.out.println("> Logged in successfully.");
                    Authenticcondition = 1;
                    this.email_address = email_address;
                    String identity = (isPatient) ? "patient" : "staff";
                } else {
                    System.out.println("> Incorrect. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
