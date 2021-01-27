import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.rmi.NotBoundException;

import database.Patient;
import database.Staff;
import database.User;
import utility.*;

public class Client {
    private final String host = "localhost";
    private final int port = 1099; // default port
    private int Authenticcondition = -1; // condition of authentication ,1 means authentication completed,using
    // int can expand more options
    private User user; // a class encapsulate user information
    static Scanner input = new Scanner(System.in);
    private final PasswordHandler passwordHandler = new PasswordHandler();
    private ServerInterface server;

    //////////////////////////////////////////////////
    //Main Method
    public static void main(String[] args) {
        Client client = new Client();
        client.runUserInterface();
    }

    ////////////////////////////////////////////////
    //This method runs the main user interface
    private void runUserInterface(){
        String op = "";// record the command
        byte[] hash = null;

        try {
            // link to server
            server = (ServerInterface) Naming.lookup("rmi://" + host + ":" + port + "/Service");
            System.out.println("> Connected to server successfully");

            server.viewPatients(); //Just a test thingy

            System.out.println("> Welcome to  the Hospital Service System");
            System.out.println("> If you don't have an account,use command 'register'.");
            System.out.println("> If you already have an account,use command 'login' to login as stuff/admin/patient");
            System.out.println("> Type 'help' to see all available commands.");

            while (true) {
                op = input.nextLine();
                //Input integrity thing
                op = op.toLowerCase();

                //////////////////////////////////////
                //Help Command
                if (op.contains("help")) {
                    helpCommand();
                }
                //////////////////////////////////////
                //Register command
                else if (op.contains("register")) {
                    registerCommand();
                }
                //////////////////////////////////////
                //Login command
                else if (op.contains("login")) {
                    loginCommand();
                }
                //////////////////////////////////////
                //Logout command
                else if (op.contains("logout")) {
                    logoutCommand();
                }
                //////////////////////////////////////
                //Forgot Password command
                else if (op.contains("forgotpw") || op.contains("forgot")) {
                    forgotPasswordCommand();
                }
                /////////////////////////////////////
                // Unrecognised Command
                else {
                    System.out.println("> Sorry. Unrecognised command. Check your spelling.\n> Use 'help' for a list of commands");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // User input commands !!
    ////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////
    // Help command function
    //////////////////////////////////////
    private void helpCommand(){
        System.out.println("> Use command 'register' if you're a new user.");
        System.out.println("> Use command 'login' if you're already registered.");
        System.out.println("> Use command 'logout' to logout.");
        System.out.println("> Use command 'forgotpw' to reset your password.");
    }

    private void registerCommand(){
        String password = null;
        if (Authenticcondition < 0) {
            System.out.println("Choose what identity you want to register as staff/patient");
            String identity = input.nextLine();
            //Integrity stuff
            identity = identity.toLowerCase();

            switch (identity) {
                case "staff" -> user = staff_register();
                case "patient" -> user = patient_register();
                default -> System.out.println("please enter an valid identity");
            }

            // if information all valid
            if (user != null) {
                // generate private keys and public key for user
                // ---------------------------------------------


                //Register and validate password
                while (true) {
                    boolean passwordEval = false;
                    while (!passwordEval) {
                        System.out.println("Please set your password");
                        System.out.println("The length of the password should be more than 8 characters which must include a capital letter, a lower-case letter, a number and a special symbol");
                        password = input.nextLine();
                        passwordEval = passwordHandler.checkPasswordStrength(password) >= Constants.INTERMEDIATE_PASSWORD;
                        // password evaluation
                        if (passwordEval) {
                            System.out.println("Strong Password !");
                            break;
                        } else {
                            passwordHandler.printPasswordImprovementSuggestions(); //Prints suggestions
                            System.out.println("Suggested strong password: " + passwordHandler.getStrongPassword());
                        }
                    }
                    //
                    System.out.println("Please confirm your password (type again)");
                    String temp2 = input.nextLine();
                    // Satisfy password requirements
                    if (password.equals(temp2)) {
                        System.out.println("Password Confirmed !!!!");
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
                        System.out.println("The entered password is different from the previous one or the format doesn't match the requirements");
                }
            }
        } else
            System.out.println("please log out first");
    }

    //////////////////////////////////////
    // Login command function
    //////////////////////////////////////
    public void loginCommand() {
        if (Authenticcondition > 0) {
            System.out.println("please log out first");
        } else {
            System.out.println("Choose what identity you want to login as  staff/patient");
            String identity = input.nextLine();
            switch (identity) {
                case "staff" -> user = staff_login();
                case "patient" -> user = patient_login();
            }
        }
    }

    //////////////////////////////////////
    // Logout command function
    //////////////////////////////////////
    public void logoutCommand(){
        if (Authenticcondition > 0) {
            Authenticcondition = -1;
        } else {
            System.out.println("please log in first");
        }
    }

    //////////////////////////////////////
    // Forgot Password command function
    //////////////////////////////////////
    public void forgotPasswordCommand() {
        if (Authenticcondition < 0) {

            System.out.println("please enter your email address and a one time password will be sent");
            String email_address = input.nextLine();
            // sent email
            try {
                server.sendEmail(email_address);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            System.out.println("please enter your one time password");
            String otp = input.nextLine();
            // verify the otp
            // --------------
            while (true) {
                System.out.println("please set your password");
                System.out.println("The length of the password should be more than 8 characters which must include a capital letter, a lower-case letter, a number and a special symbol");
                String temp1 = input.nextLine();

                // password evaluation
                // -------------------

                System.out.println("confirm your password");
                String temp2 = input.nextLine();
                if (temp1.equals(temp2))// &&satisfy password requirements
                {
                    break;
                } else {
                    System.out.println("The entered password is different from the previous one or the format doesn't match the requirements");
                }
            }
        } else {
            System.out.println("please log out first");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private User staff_register() {
        System.out.println("Please enter your email to register");
        String email_address = input.nextLine();

        // if the email has been used for register already then break
        // --------------------------------------------
        // --------------------------------------------
        // --------------------------------------------
        System.out.println("please enter your role title");
        String role_title = input.nextLine();
        System.out.println("please enter your phone number");
        String phone_number = input.nextLine();
        System.out.println("please enter forename");
        String forename = input.nextLine();
        System.out.println("please enter your surname");
        String surname = input.nextLine();
        System.out.println("please enter your date of birth in format XXXX-XX-XX");
        String date_of_birth = input.nextLine();
        System.out.println("please enter your address");
        String address = input.nextLine();
        return new Staff(forename, surname, date_of_birth, address, email_address, "staff", role_title, phone_number);

    }

    private User staff_login() {
        try {
            System.out.println("enter your mail ");
            String email_address = input.nextLine();
            System.out.println(
                    "Do you want to log in with an email verification code?  type y to use OTP / type n to use password");
            if (input.nextLine().equals("y")) {
                // send email:
                server.sendEmail(email_address);

                System.out.println("enter your verification code");
                String otp = input.nextLine();
            } else {
                System.out.println("enter your password");
                String password = input.nextLine();

                // Check password matches password hash in database
                if (server.verifyPassword(password, email_address, false)) {
                    System.out.println("Login successful.");
                } else {
                    System.out.println("Incorrect. Please try again.");
                }
            }
            System.out.println("type y to confirm / n to cancel logging ");

            if (input.nextLine().equals("y")) {
                // check if the user has been register or been logged in on other client
                // ---------------------------------------------------------------------
                // check which method was used when logging in
                // ---------------------------------------------------------------------
                // authentication
                // ---------------------------------------------------------------------
                // and get the user data from DB and pack as staff type
                // ---------------------------------------------------------------------
                Authenticcondition = 1;
                return null;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private User patient_register() {
        System.out.println("Please enter your email to register");
        String email_address = input.nextLine();

        // if the email has been used for register already then break
        // --------------------------------------------
        // --------------------------------------------
        // --------------------------------------------
        System.out.println("please enter forename");
        String forename = input.nextLine();
        System.out.println("please enter your surname");
        String surname = input.nextLine();
        System.out.println("please enter your date of birth in format XXXX-XX-XX");
        String date_of_birth = input.nextLine();
        System.out.println("please enter your address");
        String address = input.nextLine();
        return new Patient(forename, surname, date_of_birth, address, email_address, "patient");

    }

    private User patient_login() {
        try {
            System.out.println("Enter your email");
            String email_address = input.nextLine();
            System.out.println("Do you want to log in with an email verification code?  type y to use OTP / type n to use password");
            if (input.nextLine().equals("y")) {
                // send email:
                server.sendEmail(email_address);

                System.out.println("Enter your verification code");
                String otp = input.nextLine();
            } else {
                System.out.println("Enter your password");
                String password = input.nextLine();

                // Check password matches password hash in database
                if (server.verifyPassword(password, email_address, true)) {
                    System.out.println("Login successful.");
                } else {
                    System.out.println("Incorrect. Please try again.");
                }
            }

            System.out.println("type y to confirm / n to cancel logging ");
            if (input.nextLine().equals("y")) {
                // check if the user has been register or been logged in on other client
                // ---------------------------------------------------------------------
                // check which method was used when logging in
                // ---------------------------------------------------------------------
                // authentication
                // ---------------------------------------------------------------------
                // and get the user data from DB and pack as patient type
                // ---------------------------------------------------------------------
                Authenticcondition = 1;
                return null;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
