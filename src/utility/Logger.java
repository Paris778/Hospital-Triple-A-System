package utility;

import database.DatabaseConnection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Logger {

    private DatabaseConnection connection;

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
        //System.err.println("Values got at logger : " + EVENT_ID + " " + userId);
        switch (EVENT_ID){
            //
            case Constants.LOG_SYSTEM_ONLINE:
                System.out.println("Trying to make online log");
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "Server is online.", //Event description,
                        appendedBy
                );
                break;
            //
            case Constants.LOG_USER_REGISTERED:
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "User " + userId + " has been registered.", //Event description
                        appendedBy
                );
                break;
            //
            case Constants.LOG_USER_LOGGED_IN:
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "User " + userId + " has logged in.", //Event description
                        appendedBy
                );
                break;
            //
            case Constants.LOG_USER_LOGGED_OUT:
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "User " + userId + " has logged out.", //Event description
                        appendedBy
                );
                break;
            //////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////
            // Might need to make a separate method for these
            case Constants.LOG_USER_ACCESSED_DATA:
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "User " + userId + " has been granted access to view data.", //Event description
                        appendedBy
                );
                break;
            case Constants.LOG_USER_MODIFIED_DATA:
                break;
            case Constants.LOG_USER_DELETED_DATA:
                break;
            case Constants.LOG_USER_CHANGED_PASSWORD:
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "User " + userId + " has changed their password.", //Event description
                        appendedBy
                );
                break;
            case Constants.LOG_USER_ENTERED_WRONG_PASSWORD:
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "User " + userId + " has entered the wrong password.", //Event description
                        appendedBy
                );
                break;
            case Constants.LOG_USER_TIMEDOUT_WRONG_PASSWORDS:
                connection.appendLog(
                        userId,
                        this.getEventType(EVENT_ID),
                        "User " + userId + " has been timed out due to repeated wrong password.", //Event description
                        appendedBy
                );
                break;
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