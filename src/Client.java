import java.rmi.Naming;
import java.rmi.RemoteException;
import javax.crypto.IllegalBlockSizeException;
import java.util.Scanner;
import java.io.IOException;
import java.rmi.NotBoundException;

public class Client {
    private static final String host = "localhost";
    private static final int port = 1099;       //default port
    private static int Authenticcondition = -1; //condition of authentication ,1 means authentication completed,using int can expand more options
    private static User user;                   // a class encapsulate user information
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws IllegalBlockSizeException, IOException, Exception {

        String op = "";//record the command
        try {
            // link to server
            Serverinterface c = (Serverinterface) Naming.lookup("rmi://" + host + ":" + port + "/Service");
            System.out.println("link to server");

            System.out.println("Welcome to use the hospital service system");
            System.out.println("If you don't have an account,use command -register register and validate");
            System.out.println("If you already have an account,use command -login to login as stuff/admin/patient");
            System.out.println("type -help to see all the avaliable command");

            while (true) {
                op = input.nextLine();
                switch (op) {
                    case "help":
                        System.out.println("use command -register register and validate");
                        System.out.println("use command -login to make the following command avaliable");
                        System.out.println("use command -logout to logout");
                        System.out.println("use command -forgotpw to find your forgotten password");
                        break;

                    case "register":
                        if (Authenticcondition < 0) {
                            System.out.println("Choose what identity you want to register as staff/patient");
                            String identity = input.nextLine();

                            switch (identity) {
                                case "staff":
                                    user = staff_register();
                                    break;

                                case "patient":
                                    user = patient_register();
                                    break;

                                default:
                                    System.out.println("please enter an valid identity");
                                    break;
                            }

                            //if information all valid
                            if (user != null) {
                                //generate private keys and public key for user
                                //---------------------------------------------
                                while (true) {
                                    System.out.println("please set your password");
                                    System.out.println("The length of the password should be more than 8 characters which must include a capital letter, a lower-case letter, a number and a special symbol");
                                    String temp1 = input.nextLine();

                                    //password evaluation
                                    //-------------------

                                    System.out.println("confirm your password");
                                    String temp2 = input.nextLine();
                                    if (temp1.equals(temp2))//&&satisfy password requirements
                                    {
                                        break;
                                    } else
                                        System.out.println("The entered password is different from the previous one or the format doesn't match the requirements");
                                }
                            }

                            //server store password
                            //---------------------
                        } else
                            System.out.println("please log out first");
                        break;

                    case "login":
                        if (Authenticcondition > 0)
                            System.out.println("please log out first");
                        else {
                            System.out.println("Choose what identity you want to login as  staff/patient");
                            String identity = input.nextLine();
                            switch (identity) {

                                case "staff":
                                    user = staff_login();
                                    break;

                                case "patient":
                                    user = patient_login();
                                    break;

                            }
                        }
                        break;

                    case "forgotpw":
                        if (Authenticcondition < 0) {

                            System.out.println("please enter your email address and a one time password will be sent");
                            String email_address = input.nextLine();
                            //sent otp
                            //--------------
                            System.out.println("please enter your one time password");
                            String otp = input.nextLine();
                            //verify the otp
                            //--------------
                            while (true) {
                                System.out.println("please set your password");
                                System.out.println("The length of the password should be more than 8 characters which must include a capital letter, a lower-case letter, a number and a special symbol");
                                String temp1 = input.nextLine();

                                //password evaluation
                                //-------------------

                                System.out.println("confirm your password");
                                String temp2 = input.nextLine();
                                if (temp1.equals(temp2))//&&satisfy password requirements
                                {
                                    break;
                                } else
                                    System.out.println("The entered password is different from the previous one or the format doesn't match the requirements");
                            }
                        } else
                            System.out.println("please log out first");
                        break;

                    //logout
                    case "logout":
                        if (Authenticcondition > 0) {
                            Authenticcondition = -1;
                        } else
                            System.out.println("please log in first");
                        break;


                }
            }
        } catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        } catch (NotBoundException nbe) {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        } catch (java.lang.ArithmeticException ae) {
            System.out.println();
            System.out.println("java.lang.ArithmeticException");
            System.out.println(ae);
        }
    }

    private static User staff_register() {
        System.out.println("Please enter your email to register");
        String email_address = input.nextLine();

        //if the email has been used for register already then break
        //--------------------------------------------
        //--------------------------------------------
        //--------------------------------------------    
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

    private static User staff_login() {
        System.out.println("enter your mail ");
        String email_address = input.nextLine();
        System.out.println("Do you want to log in with an email verification code?  type y to use OTP / type n to use password");
        if (input.nextLine().equals("y")) {
            //send email:
            //---------------
            System.out.println("enter your password");
            String otp = input.nextLine();
        } else {
            System.out.println("enter your password");
            String password = input.nextLine();
        }
        System.out.println("type y to confirm / n to cancel logging ");

        if (input.nextLine().equals("y")) {
            //check if the user has been register or been logged in on other client
            //---------------------------------------------------------------------
            //check which method was used when logging in
            //---------------------------------------------------------------------
            //authentication
            //---------------------------------------------------------------------
            //and get the user data from DB and pack as staff type
            //---------------------------------------------------------------------     
            Authenticcondition = 1;
            return null;
        } else
            return null;

    }

    private static User patient_register() {
        System.out.println("Please enter your email to register");
        String email_address = input.nextLine();

        //if the email has been used for register already then break
        //--------------------------------------------
        //--------------------------------------------
        //--------------------------------------------    
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

    private static User patient_login() {
        System.out.println("enter your mail ");
        String email_address = input.nextLine();
        System.out.println("Do you want to log in with an email verification code?  type y to use OTP / type n to use password");
        if (input.nextLine().equals("y")) {
            //send email:
            //---------------
            System.out.println("enter your password");
            String otp = input.nextLine();
        } else {
            System.out.println("enter your password");
            String password = input.nextLine();
        }

        System.out.println("type y to confirm / n to cancel logging ");
        if (input.nextLine().equals("y")) {
            //check if the user has been register or been logged in on other client
            //---------------------------------------------------------------------
            //check which method was used when logging in
            //---------------------------------------------------------------------
            //authentication
            //---------------------------------------------------------------------
            //and get the user data from DB and pack as patient type
            //---------------------------------------------------------------------
            Authenticcondition = 1;
            return null;
        } else
            return null;

    }
}
            
            
            
             
           
       
	   
        
            
    

        
  
        

    


