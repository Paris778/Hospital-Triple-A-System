package database;

import utility.Logger;
import utility.PasswordHandler;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseConnection {
    private final String JDBC_DRIVER = "org.sqlite.JDBC";
    private final String DATABASE_LOCATION = "jdbc:sqlite:";
    private String dbName = "new-database.db";
    private Connection con = null;
    private PreparedStatement p = null;
    private ResultSet results = null;
    private PasswordHandler passwordHandler = new PasswordHandler();
    private final Lock lock = new ReentrantLock();

    ///////////////////////////////////////////////////////////////////////////////////
    // LOGGER METHODS START
    ///////////////////////////////////////////////////////////////////////////////////

    // This method appends a log entry to the event_logs table of the database
    public synchronized void appendLog(int userId, String eventType, String eventDescription, int appendedBy) {
        //Race Condition Control
        lock.lock();
        //
        try {
            String statement = "";

            statement =("INSERT INTO event_logs (u_id, event_type, event_description, appended_by) VALUES ('"
                    + userId + "', '"
                    + eventType + "', '"
                    + eventDescription + "', '"
                    + appendedBy + "'); "
            );

            // Add to statement
            p = con.prepareStatement(statement);
            p.executeUpdate();
            results.close();
            p.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Race Condition Control
        lock.unlock();
        //
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // Creates a fake log in order to test functionality
    //JUST FOR TESTING
    public synchronized void createFakeLog(int userId, String eventType,String appendedBy) {
        //Race Condition Control
        lock.lock();
        //
        try {
            String statement = "";
            String eventDescription = "Warning Something went wrong ";

            statement =("INSERT INTO event_logs (u_id, event_type, event_description, appended_by) VALUES ('"
                    + userId + "', '"
                    + eventType + "', '"
                    + eventDescription + "', '"
                    + appendedBy + "'); "
            );

            // Add to statement
            p = con.prepareStatement(statement);
            p.executeUpdate();
            results.close();
            p.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Race Condition Control
        lock.unlock();
        //
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Prints out all log entries
    public synchronized String viewLogEntries( ) {

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%4s .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |","No.", "USER ID", "TIME OF EVENT", "EVENT TYPE", "DESCRIPTION","APPEND BY"));
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query

            p = con.prepareStatement("SELECT * FROM event_logs");
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("e_id");
                String userId = results.getString("u_id");
                String time = results.getString("time_of_event");
                String event_type = results.getString("event_type");
                String event_description = results.getString("event_description");
                String appended_by = results.getString("appended_by");

                builder.append("\n");
                builder.append(String.format("%4d .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |",id,userId,time,event_type ,event_description,appended_by));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        return builder.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //Prints out all warning log entries
    public synchronized String viewWarningLogEntries( ) {
        //Race condition control
        lock.lock();

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%4s .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |","No.", "USER ID", "TIME OF EVENT", "EVENT TYPE", "DESCRIPTION","APPEND BY"));
        try {
            // Execute SQL query

            p = con.prepareStatement("SELECT * FROM event_logs WHERE event_type IS 'WARNING' ");
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("e_id");
                String userId = results.getString("u_id");
                String time = results.getString("time_of_event");
                String event_type = results.getString("event_type");
                String event_description = results.getString("event_description");
                String appended_by = results.getString("appended_by");

                builder.append("\n");
                builder.append(String.format("%4d .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |",id,userId,time,event_type ,event_description,appended_by));
            }

        } catch (SQLException e) {
            builder = new StringBuilder();
            builder.append("> Oops, something went wrong. Check that your input is correct.");
        } finally {
            //Race condition control
            lock.unlock();
        }
        return builder.toString();
    }

    /////////////////////////////////////////////////////////////////////////////////
    //Prints out all error log entries
    public synchronized String viewErrorLogEntries( ) {

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%4s .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |","No.", "USER ID", "TIME OF EVENT", "EVENT TYPE", "DESCRIPTION","APPEND BY"));
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query

            p = con.prepareStatement("SELECT * FROM event_logs WHERE event_type IS 'ERROR' ");
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("e_id");
                String userId = results.getString("u_id");
                String time = results.getString("time_of_event");
                String event_type = results.getString("event_type");
                String event_description = results.getString("event_description");
                String appended_by = results.getString("appended_by");

                builder.append("\n");
                builder.append(String.format("%4d .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |",id,userId,time,event_type ,event_description,appended_by));
            }

        } catch (SQLException e) {
            builder = new StringBuilder();
            builder.append("> Oops, something went wrong. Check that your input is correct.");
        } finally {
            //Race condition control
            lock.unlock();
        }
        return builder.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //Takes user ID as input and prints all activity from them
    //
    public String inspectSpecificUser(String user_Id){
        lock.lock();
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%4s .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |","No.", "USER ID", "TIME OF EVENT", "EVENT TYPE", "DESCRIPTION","APPEND BY"));
        try {
            // Execute SQL query
            p = con.prepareStatement("SELECT * FROM event_logs WHERE u_id IS "+ user_Id + ";");
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("e_id");
                String userId = results.getString("u_id");
                String time = results.getString("time_of_event");
                String event_type = results.getString("event_type");
                String event_description = results.getString("event_description");
                String appended_by = results.getString("appended_by");
                //
                builder.append("\n");
                builder.append(String.format("%4d .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |",id,userId,time,event_type ,event_description,appended_by));
            }
        } catch (SQLException e) {
            builder = new StringBuilder();
            builder.append("> Oops, something went wrong. Check that your imput is correct and that the user exists");
        } finally {
            //Race condition control
            lock.unlock();
            return builder.toString();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Returns all the suspicious activity of the system (Warnings , errors)
    // If print boolean is true, it prints out all the warnings and errors
    // If print boolean is false, it calculates responsibility and prints out the table

    public  String viewErrorAndWarningLogEntries(boolean printJustLogs) {
        //Race condition control
        lock.lock();
        LinkedHashMap<String, LinkedList<Integer>> userResponsibilityMap = new LinkedHashMap<>();
        StringBuilder builderResponsibility = new StringBuilder();
        StringBuilder builderAllLogs = new StringBuilder();
        try {
            // Execute SQL query

            p = con.prepareStatement("SELECT * FROM event_logs WHERE event_type IN ('WARNING','ERROR');");
            builderAllLogs.append(String.format("%4s .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |","No.", "USER ID", "TIME OF EVENT", "EVENT TYPE", "DESCRIPTION","APPEND BY"));
            results = p.executeQuery();

            LinkedList<Integer> values = new LinkedList<>();
            LinkedList<Integer> newList = new LinkedList();


            // Loop through each row and print
            while (results.next()) {
                newList.clear();
                newList.add(0);
                newList.add(0);
                int id = results.getInt("e_id");
                String userId = results.getString("u_id");
                String time = results.getString("time_of_event");
                String event_type = results.getString("event_type");
                String event_description = results.getString("event_description");
                String appended_by = results.getString("appended_by");
                //
                if(printJustLogs) {
                    //builderAllLogs.append(id + "\t\t" + userId + "\t\t" + time + "\t\t" + event_type + "\t\t" + event_description + "\t\t" + appended_by);
                    builderAllLogs.append("\n");
                    builderAllLogs.append(String.format("%4d .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |",id,userId,time,event_type ,event_description,appended_by));
                }
                // Populate responsibilities map
                else {
                    //If User Already Exists in the Map
                    if (userResponsibilityMap.containsKey(userId)) {
                        if (event_type.contains("WARNING")) {
                            newList.set(0,userResponsibilityMap.get(userId).get(0) + 1);
                            newList.set(1,userResponsibilityMap.get(userId).get(1));
                            userResponsibilityMap.put(userId,new LinkedList<>(newList));
                        }
                        else if (event_type.contains("ERROR")) {
                            newList.set(0,userResponsibilityMap.get(userId).get(0));
                            newList.set(1,userResponsibilityMap.get(userId).get(1) + 1);
                            userResponsibilityMap.put(userId,new LinkedList<>(newList));
                        }
                    }
                    // First time seeing this User Id
                    else{
                        values.clear();
                        if (event_type.contains("WARNING")) {
                            values.add(1);
                            values.add(0);
                        }
                        else if (event_type.contains("ERROR")) {
                            values.add(0);
                            values.add(1);
                        }
                        userResponsibilityMap.put(userId,new LinkedList<>(values));
                        Integer newValue = userResponsibilityMap.get(userId).get(0);
                        //System.out.println(newValue);
                        Logger.printMap(userResponsibilityMap);
                    }
                }
            }
        } catch (SQLException e) {
            builderResponsibility = new StringBuilder();
            builderResponsibility.append("> Oops, something went wrong. Check that your input is correct.");
            return builderResponsibility.toString();
        } finally {
            //Race condition control
            lock.unlock();
        }
        if(printJustLogs){
            return builderAllLogs.toString();
        }
        else{
            int i = 1;
            builderResponsibility.append("\n------------------------------------------------------------------------------------------------------------------\n");
            for(String user : userResponsibilityMap.keySet()){
                builderResponsibility.append(String.format("%3d .  |   USER ID: %-20s |   WARNINGS: %3d  |   ERRORS: %3d   |",i,user,userResponsibilityMap.get(user).get(0),userResponsibilityMap.get(user).get(1)));
                builderResponsibility.append("\n");
                i++;
            }
            builderResponsibility.append("------------------------------------------------------------------------------------------------------------------\n");
            return builderResponsibility.toString();
        }
    }

    public String viewRecentLogs(int amount){
        lock.lock();
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%4s .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |","No.", "USER ID", "TIME OF EVENT", "EVENT TYPE", "DESCRIPTION","APPEND BY"));
        try {
            // Execute SQL query
            p = con.prepareStatement("SELECT * FROM (SELECT * FROM event_logs ORDER BY e_id DESC LIMIT ?) ORDER BY e_id ASC;");
            p.setInt(1, amount);
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("e_id");
                String userId = results.getString("u_id");
                String time = results.getString("time_of_event");
                String event_type = results.getString("event_type");
                String event_description = results.getString("event_description");
                String appended_by = results.getString("appended_by");
                //
                builder.append("\n");
                builder.append(String.format("%4d .  | %-20s |  %-32s |  %-11s  |  %-100s  | %-12s |",id,userId,time,event_type ,event_description,appended_by));
            }
        } catch (SQLException e) {
            builder = new StringBuilder();
            builder.append("> Oops, something went wrong. Check that your input is correct.");
        } finally {
            //Race condition control
            lock.unlock();
            return builder.toString();
        }
    }

    public String getAdminEmailAddress(){
        lock.lock();
        String adminEMail = "";
        try {
            // Execute SQL query

            p = con.prepareStatement("SELECT email FROM users WHERE u_id IS (SELECT u_id FROM admins LIMIT 1);");
            results = p.executeQuery();

            adminEMail = results.getString(1);

        } catch (SQLException e) {
           System.out.println("Something went wrong");
        } finally {
            //Race condition control
            lock.unlock();
        }
        return adminEMail;
    }

    public String lockAccount(int id) {
        lock.lock();
        try {
            p = con.prepareStatement("UPDATE users SET account_locked=1 WHERE u_id= ?");
            p.setInt(1, id);
            p.executeUpdate();
            return "> User #" + id + "'s account has been successfully locked.";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "> Error in locking account of User #" + id;
    }

    public String unlockAccount(String id) {
        lock.lock();
        try {
            p = con.prepareStatement("UPDATE users SET account_locked=0 WHERE u_id= ?");
            p.setString(1, id);
            p.executeUpdate();
            return "> User #" + id + "'s account has been successfully unlocked.";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "> Error in unlocking account of User #" + id;
    }

    public String viewLockedAccounts(){

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("|%4s .  | %-35s |  %-15s |","ID", "USER EMAIL", "ROLE"));
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query

            p = con.prepareStatement("SELECT u_id,email,roles FROM users WHERE account_locked IS 1");
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("u_id");
                String email = results.getString("email");
                String role = results.getString("roles");

                builder.append("\n");
                builder.append(String.format("|%4s .  | %-35s |  %-15s |",id,email,role));
            }

        } catch (SQLException e) {
            builder = new StringBuilder();
            builder.append("> Oops, something went wrong. Check that your input is correct.");
        } finally {
            //Race condition control
            lock.unlock();
        }
        return builder.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //END OF LOGGER METHODS
    //////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkEmailAvailable(String email) {
        lock.lock();
        try {
            p = con.prepareStatement("SELECT * FROM users WHERE email= ? ");
            p.setString(1, email);
            results = p.executeQuery();

            // Returns false if email is already assigned to a user, else true
            return (!results.next());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return true;
    }



    public String updateRole(int u_id, String newRole) {
        lock.lock();
        try {
            p = con.prepareStatement("UPDATE users SET role= ? WHERE u_id= ?");
            p.setString(1, newRole);
            p.setInt(2, u_id);
            p.executeUpdate();
            return "User #" + u_id + "'s role has successfully been updated to " + newRole + ".";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "Error in updating role of User #" + u_id;
    }

    public boolean checkPermissions(String email, String request) {
        lock.lock();
        try {
            p = con.prepareStatement("SELECT * FROM users WHERE email= ? ");
            p.setString(1, email);
            results = p.executeQuery();
            String role = results.getString("roles"); //gets role
            p = con.prepareStatement("SELECT * FROM roles WHERE role_name = ? ");
            p.setString(1, role);
            results = p.executeQuery();
            if (results.next()) {
                return results.getInt(request) == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return false;
    }

    public void createUser(User user, String plaintext) {
        //Race condition control
        lock.lock();
        try {
            // Hash and salt user password
            String salt = passwordHandler.generateSalt();
            String hashedPassword = passwordHandler.hashPassword(plaintext, salt.getBytes());

            // Add password and email to users table
            String statement = "INSERT INTO users(email, password_hash, password_salt, roles) VALUES (?, ?, ?, ?);";
            p = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            p.setString(1, user.getEmail());
            p.setString(2, hashedPassword);
            p.setString(3, salt);
            if (user instanceof Patient) {
                p.setString(4, "patient");
            } else {
                // Check if clinical or admin staff
                if (((Staff) user).getSector().equals("clinical")) {
                    p.setString(4, "clinical_staff");
                } else {
                    p.setString(4, "admin_staff");
                }
            }
            p.executeUpdate();

            // Return user id to add foreign key reference in staff/patient table
            int userId = -1;
            ResultSet results = p.getGeneratedKeys();
            if (results.next()) {
                userId = results.getInt(1);
            }

            // Check if user is patient or staff and add to corresponding table
            if (user instanceof Patient) {
                p = con.prepareStatement("INSERT INTO patients(u_id, forenames, surname, date_of_birth, address) VALUES (?, ?, ?, ?, ?)");
                p.setInt(1, userId);
                p.setString(2, user.getForenames());
                p.setString(3, user.getSurnames());
                p.setString(4, user.getDoB());
                p.setString(5, user.getAddress());
            } else {
                p = con.prepareStatement("INSERT INTO staff(u_id, forenames, surname, date_of_birth, address, sector, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?)");
                p.setInt(1, userId);
                p.setString(2, user.getForenames());
                p.setString(3, user.getSurnames());
                p.setString(4, user.getDoB());
                p.setString(5, user.getAddress());
                p.setString(6, ((Staff) user).getSector());
                p.setString(7, ((Staff) user).getphone_number());
            }
            p.executeUpdate();
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public boolean verifyPassword(String plaintext, String email) {
        //Race condition control
        lock.lock();
        try {
            // Get user id that corresponds to the email
            int id = getUserId(email);

            // Prepare SQL query
            p = con.prepareStatement("SELECT password_hash, password_salt FROM users WHERE u_id= ?");
            p.setInt(1, id);

            // Get correct password hash and salt for that user
            results = p.executeQuery();
            while (results.next()) {
                String passwordHash = results.getString("password_hash");
                String salt = results.getString("password_salt");

                // Hash password attempt using the correct salt
                String attemptHash = passwordHandler.hashPassword(plaintext, salt.getBytes());

                // Compare values
                if (Arrays.equals(passwordHash.getBytes(), attemptHash.getBytes())) {
                    //Finally block will execute before that, don't worry
                    return true;
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        return false;
    }

    public boolean userIsAdmin(String email) {
        //Race condition control
        lock.lock();
        try {
            //////////////////////////////////////////////////////////////
            p = con.prepareStatement("SELECT u_id FROM admins WHERE u_id="+getUserId(email));
            System.out.println(email);
            System.out.println(getUserId(email) + "\n--------");
            //p.setInt(1, getUserId(email));
            results = p.executeQuery();
            //
            while(results.next()) {
                System.out.println(getUserId(email));
                System.out.println(results.getInt(1));
                System.out.println(getUserId(email) == results.getInt(1));
                return (getUserId(email) == results.getInt(1));
            }
            return false;
            //////////////////////////////////////////////////////////////
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public boolean isAccountUnlocked(String email){
        //Race condition control
        lock.lock();
        try {
            p = con.prepareStatement("SELECT email FROM users WHERE account_locked=1 AND u_id=? ");
            p.setString(1, String.valueOf(getUserId(email)));
            results = p.executeQuery();
            return (results.next());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public int getUserId(String email) {
        //Race condition controll
        lock.lock();
        try {
            // Get user id that corresponds to the email
            p = con.prepareStatement("SELECT u_id FROM users WHERE email=?");
            p.setString(1, email);
            results = p.executeQuery();
            return results.getInt("u_id");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public String getUserRole(String email) {
        //Race condition control
        lock.lock();
        try {
            // Get user id that corresponds to the email
            p = con.prepareStatement("SELECT roles FROM users WHERE email= ?");
            p.setString(1, email);
            results = p.executeQuery();
            return results.getString("roles");
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    //overload to give default parameters
    public String viewPatients() {
        return viewPatients(-1);
    }

    public String viewPatients(int u_id) {
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query
            if (u_id == -1) {
                p = con.prepareStatement("SELECT * FROM patients");
            } else {
                p = con.prepareStatement("SELECT * FROM patients WHERE u_id = ?");
                p.setInt(1, u_id);
            }
            results = p.executeQuery();
            String returnString = String.format("%15s %15s %15s %15s %15s %n", "Patient ID", "Forename(s)", "Surname", "Date of Birth", "Primary Doctor ID");

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("u_id");
                String forename = results.getString("forenames");
                String surname = results.getString("surname");
                String dob = results.getString("date_of_birth");
                String primaryDoctor = results.getString("primary_doctor");
                returnString = returnString.concat(String.format("%15d %15s %15s %15s %15s %n", id, forename, surname, dob, primaryDoctor));
            }
            return returnString;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        return "No results found for this query";
    }

    //overload to give default parameters
    public String viewStaffs() {
        return viewStaffs(-1);
    }

    public String viewStaffs(int u_id) {
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query
            if (u_id == -1) {
                p = con.prepareStatement("SELECT * FROM staff");
            } else {
                p = con.prepareStatement("SELECT * FROM staff WHERE u_id = ?");
                p.setInt(1, u_id);
            }
            results = p.executeQuery();
            String returnString = String.format("%15s %15s %15s %15s %15s %n", "Patient ID", "Forename(s)", "Surname", "Date of Birth", "Job Sector");

            // Loop through each row and add to return string
            while (results.next()) {
                int id = results.getInt("u_id");
                String forename = results.getString("forenames");
                String surname = results.getString("surname");
                String dob = results.getString("date_of_birth");
                String sector = results.getString("sector");
                returnString = returnString.concat(String.format("%15d %15s %15s %15s %15s %n", id, forename, surname, dob, sector));
            }
            return returnString;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        return "No results found for this query";
    }

    public String deletePatients(int u_id) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("DELETE FROM patients WHERE u_id = ?");
            p.setInt(1, u_id);
            results = p.executeQuery();
            return "User #" + u_id + " (staff) has been successfully deleted.";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        return "Error in deleting user #" + u_id;
    }

    public String deleteStaffs(int u_id) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("DELETE FROM staff WHERE u_id = ?");
            p.setInt(1, u_id);
            results = p.executeQuery();
            return "User #" + u_id + " (staff) has been successfully deleted.";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        return "Error in deleting user #" + u_id;
    }

    public String updatePatients(int u_id, String command) {
        lock.lock();
        try {
            // Execute SQL query
            p = con.prepareStatement("UPDATE patients SET ? WHERE u_id = ?");
            p.setString(1, command);
            p.setInt(2, u_id);
            results = p.executeQuery();
            return "User #" + u_id + "has been successfully updated.";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        return "Error in updating user #" + u_id;
    }

    public String updateStaffs(int u_id, String command) {
        lock.lock();
        try {
            // Execute SQL query
            p = con.prepareStatement("UPDATE patients SET ? WHERE u_id = ?");
            p.setString(1, command);
            p.setInt(2, u_id);
            results = p.executeQuery();
            return "User #" + u_id + "has been successfully updated.";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        return "Error in updating user #" + u_id;
    }


    public DatabaseConnection() {
        try {
            // Load the JDBC driver
            Class.forName(JDBC_DRIVER);

            // Attempt to open a connection
            con = DriverManager.getConnection(DATABASE_LOCATION + dbName);

            viewPatients();
            con.setAutoCommit(true);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}