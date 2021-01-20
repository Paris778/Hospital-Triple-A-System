import utility.*;

public class Main {

    public static void main(String args[]) {

        //Make new instance of password handler. We can make it static if we want to.
        PasswordHandler pass = new PasswordHandler();

        //Checks the strength of the passed string . Returns a password score
        int score = pass.checkPasswordStrength("Password");
        System.out.println("Password score: " + score); //We could use it to print a strength meter (*****-------) or something

        //If the password is not good, we print suggestions (more special characters etc)
        pass.printPasswordImprovementSuggestions();

        //We can also suggest strong passwords
        System.out.println("Suggested strong password: " + pass.getStrongPassword());
    }
}
