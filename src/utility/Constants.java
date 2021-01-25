package utility;

//////////////////////////////////////////////////////////////////////////////
// Class : utility.Constants
//
// Description: 
// This class contains constant values used by the whole system
//
//////////////////////////////////////////////////////////////////////////////

public class Constants {
    //PASSWORD CONSTANTS
    public static final int WEAK_PASSWORD = 5;
    public static final int OK_PASSWORD = 8;
    public static final int INTERMEDIATE_PASSWORD = 9;
    public static final int STRONG_PASSWORD = 11;
    //PASSWORD LENGTHS
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int ADEQUATE_PASSWORD_LENGTH = 15;
    //CHARACTERS
    public static final String SPECIAL_CHARACTERS = "-/.^&*_!@%=+>)"; 
    public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
    public static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz"; 
    public static final String NUMBERS = "0123456789";

    //////////////////////////////////////////////////////////////////////////
    //      LOGGER CONSTANTS
    //////////////////////////////////////////////////////////////////////////

    //Normal Activity 1--
    public static final int LOG_USER_REGISTERED = 101;
    public static final int LOG_USER_LOGGED_IN = 102;
    public static final int LOG_USER_LOGGED_OUT = 103;
    public static final int LOG_USER_ACCESSED_DATA = 104;
    public static final int LOG_USER_CHANGED_PASSWORD = 105;
    public static final int LOG_SYSTEM_ONLINE = 106;
    //Warnings 2--
    public static final int LOG_USER_MODIFIED_DATA = 201;
    public static final int LOG_USER_DELETED_DATA = 202;
    public static final int LOG_USER_ENTERED_WRONG_PASSWORD = 203;
    public static final int LOG_USER_TIMEDOUT_WRONG_PASSWORDS = 204;
    public static final int LOG_USER_DENIED_ACCESS = 210;
    //Errors 3--
    public static final int LOG_ERROR_SERVER_DOWN = 301;
    //Unknown Activity 4--
    public static final int LOG_UNKNOWN_EVENT = 401;
}