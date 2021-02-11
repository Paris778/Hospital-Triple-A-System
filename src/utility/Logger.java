package utility;

import database.DatabaseConnection;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Logger {

    private final DatabaseConnection connection;

    public Logger(DatabaseConnection connection){
        this.connection = connection;
    }

    public static String printMap(LinkedHashMap<String, LinkedList<Integer>> map) {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        builder.append("\n------------------------------------------------------------------------------------------------------------------");
        for(String user : map.keySet()){
            builder.append(String.format("%3d .  |   USER ID: %-20s |   WARNINGS: %3d  |   ERRORS: %3d   |",i,user,map.get(user).get(0),map.get(user).get(1)));
            builder.append("\n");
            i++;
        }
       builder.append("------------------------------------------------------------------------------------------------------------------\n");

        return builder.toString();
    }

    //This is the main function to log events.
    // Takes in a CONSTANT for the type of event and the database.User ID if applicable
    // The logEvent method is super charged for various logging types (future thing)
    public void logEvent(int userId, int EVENT_ID, int appendedBy){
        System.err.println("Values got at logger : Id:" + userId + " Ev:" + EVENT_ID);
        //

        // Might need to make a separate method for these
        switch (EVENT_ID) {
            case (Constants.LOG_SYSTEM_ONLINE) -> {
                System.out.println("Trying to make online log");
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "Server is online.", //Event description,
                        appendedBy
                );
            }
            case (Constants.LOG_USER_REGISTERED) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has been registered.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_LOGGED_IN) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has logged in.", //Event description
                    appendedBy
            );
            case (Constants.LOG_NEW_SERVER_CONNECTION) -> {
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "A new connection was made to the server.", //Event description
                        appendedBy
                );
            }
            case (Constants.LOG_USER_LOGGED_OUT) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has logged out.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_ENTERED_WRONG_OTP) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " entered wrong OTP.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_STARTEDREGISTER) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has started registering a new user.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_WEAK_PASSWORD) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " tried to use a weak password.", //Event description
                    appendedBy
            );
            case (Constants.LOG_FAILED_REGISTRATION) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " failed to register a user.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_KICKED) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has kicked from the system and their account has been locked.", //Event description
                    appendedBy
            );
            case (Constants.LOG_DATABASE_BACKEDUP) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "Database has been backed up by an ADMIN.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_CREATED_IN_DB) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "A new user was created in the database...", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_DENIED_ACCESS) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " tried to access data without the appropriate permissions.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_ACCESSED_DATA) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has been granted access to view data.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_MODIFIED_DATA) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " modified data from a database table.", //Event description
                    appendedBy
            );
            case (Constants.LOG_USER_DELETED_DATA) -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " deleted data from a database table.", //Event description
                    appendedBy
            );
            case Constants.LOG_USER_CHANGED_PASSWORD -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has changed their password.", //Event description
                    appendedBy
            );
            case Constants.LOG_USER_ENTERED_WRONG_PASSWORD -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has entered the wrong password.", //Event description
                    appendedBy
            );
            case Constants.LOG_USER_TIMEDOUT_WRONG_PASSWORDS -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has been timed out due to repeated wrong password.", //Event description
                    appendedBy
            );
            case Constants.LOG_USER_IS_ADMIN -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User " + userId + " has logged in and authenticated as SYSTME ADMIN", //Event description
                    appendedBy
            );
            case Constants.OTP_WAS_SENT -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    ("An OTP was sent to the email address of user : " + userId), //Event description
                    appendedBy
            );
            case Constants.LOG_WARNING_EMAIL_SENT -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "A Warning email was sent to SYSTEM ADMIN user : " + userId, //Event description
                    appendedBy
            );
            case Constants.LOG_USER_ACCOUNT_LOCKED_MANUALLY -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User account #" + userId + " was locked manually by a SYSTEM ADMIN", //Event description
                    appendedBy
            );
            case Constants.LOG_ACCOUNT_UNLOCKED -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User account #" + userId + " was unlocked manually by a SYSTEM ADMIN", //Event description
                    appendedBy
            );
            case Constants.LOG_TRIED_TO_ADMIN_COMMAND -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    ("User account #" + userId + " tried to execute a SYSTEM ADMIN command but access was not granted."), //Event description
                    appendedBy
            );
            case Constants.LOG_ERROR_EXCEPTION_THROWN -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    " A catch block was executed. This could potentially disrupt the operation of the system.", //Event description
                    appendedBy
            );
            case Constants.LOG_INVALID_INPUT -> connection.appendLog(
                    userId,
                    this.getEventType(EVENT_ID),
                    "User account #" + userId + " entered invalid input", //Event description
                    appendedBy
            );
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Support Methods
    private  String getEventType(int EVENT_ID){

        if (EVENT_ID > 400){
            return ("UNKNOWN EVENT");
        }
        else if (EVENT_ID > 300){
            return ("ERROR");
        }
        else if (EVENT_ID > 200){
            return ("WARNING");
        }
        else if (EVENT_ID > 100){
            return ("ACTIVITY");
        }
        else{
            return ("NON-REGISTERED EVENT");
        }
    }
}