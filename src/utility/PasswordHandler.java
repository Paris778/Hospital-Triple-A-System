package utility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

//////////////////////////////////////////////////////////////////////////////
// Class : utility.PasswordHandler
//
// Description: 
// This class handles basic password utilities like strength evaluation ,
// improvement suggestion and hashing.
//
//////////////////////////////////////////////////////////////////////////////

public class PasswordHandler{
    
    //Variables
    StringBuilder suggestions = new StringBuilder();

    //Constructor
    public PasswordHandler(){

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int checkPasswordStrength(String password){
        int passwordScore = 0;
        
        //Check the length of password
        if(password.length() < Constants.MIN_PASSWORD_LENGTH){
            this.suggestions.append("- Password should be more than 8 characters long.\n");
        }
        else if (password.length() >= Constants.ADEQUATE_PASSWORD_LENGTH){
            passwordScore += 2;
        }
        else{
            passwordScore++;
        }

        //Check if password contains at least one number
        if( password.matches("(?=.*[0-9]).*")){
            passwordScore += 2;
        }
        else{
            this.suggestions.append("- Password should contain at least one number (123).\n");
        }
        
        //Check if password contains lowercase letters
        if( password.matches("(?=.*[a-z]).*") ){
            passwordScore += 2;
        }
        else{
            this.suggestions.append("- Password should contain at least one lower case letter (abc).\n");
        }
         
        
        //Check if password contains upper case letters
        if( password.matches("(?=.*[A-Z]).*") ){
            passwordScore += 2;
        }
        else{
            this.suggestions.append("- Password should contain at least one upper case letter (ABC).\n");
        }
           
        
        //Check if password contains special characters
        if( password.matches("(?=.*[~!@#$%^&*()_-]).*") ){
            passwordScore += 2;
        }
        else{
            this.suggestions.append("- Password should contain at least one special character (!?<>,.).\n");
        }
           
        password = null; //Just a safety precaution so that the value doesn't stay in memory even though Java garbage collection should get it anyways 
        return passwordScore;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Prints suggestions to strengthen your password
    public void printPasswordImprovementSuggestions(){
        System.out.println(suggestions.toString());
        suggestions.setLength(0); //Clear string builder
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Returns a strong password suggestion
    public String getStrongPassword(){

        String testString = Constants.CAPITAL_LETTERS + Constants.LOWERCASE_LETTERS + Constants.NUMBERS + Constants.SPECIAL_CHARACTERS;
        Random rand = new Random();
        char[] password = new char[Constants.ADEQUATE_PASSWORD_LENGTH]; 

        for (int i = 0; i < Constants.ADEQUATE_PASSWORD_LENGTH; i++) 
        { 
            password[i] = testString.charAt(rand.nextInt(testString.length()));
        } 
        
        System.out.println(password);
        return password.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //HASH PASSWORD SHA-256
    public byte[] hashPassword(String password) throws NoSuchAlgorithmException {

        //Generating a salt
        SecureRandom secRand = new SecureRandom();
        byte[] salt = new byte[16];
        secRand.nextBytes(salt);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt); //Add salt to hash
        byte[] hashed_password = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        password = null; //Another safety precaution even though java garbage collection should get it anyway
        return hashed_password;
    }
}