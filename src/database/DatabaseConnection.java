package database;

import utility.Logger;
import utility.PasswordHandler;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseConnection {
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String DATABASE_LOCATION = "jdbc:sqlite:";
    private static String dbName = "database.db";
    private static Connection con = null;
    private static PreparedStatement p = null;
    private static ResultSet results = null;
    private PasswordHandler passwordHandler = new PasswordHandler();

    //Concurrency control
    private Lock lock = new ReentrantLock();


    public void createFakeUser() {
        try {
            // Add user to database
            String statement = "INSERT INTO patients(forename, surname, date_of_birth, address, email) VALUES ('test', 'test', '2000-01-01', 'china', 'g@gmail.com');";
            p = con.prepareStatement(statement);
            p.executeUpdate();

            results.close();
            p.close();

            Thread.sleep(1000);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void createUser(User user, String plaintext) {
        //Race condition control
        lock.lock();
        try {
            String statement = "";
            // Check if user is patient or staff
            if (user instanceof Patient) {
                statement = "INSERT INTO patients (forename, surname, date_of_birth, address, email) VALUES ('" +
                        user.getForenames() + "', '" + user.getSurnames() + "', '" + user.getDoB() + "', '" +
                        user.getAddress() + "', '" + user.getEmail() + "'); ";
            } else {
                statement = "INSERT INTO staff (forename, surname, date_of_birth, address, email, role_title, phone_number) VALUES ('" +
                        user.getForenames() + "', '" + user.getSurnames() + "', '" + user.getDoB() + "', '" +
                        user.getAddress() + "', '" + user.getEmail() + "', '" + ((Staff) user).getrole_title() + "', '" + ((Staff) user).getphone_number() + "'); ";
            }

            // Add user to statement
            p = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            p.executeUpdate();

            // Return row ID to add foreign key in passwords table
            int userId = -1;
            ResultSet results = p.getGeneratedKeys();
            if (results.next()) {
                userId = results.getInt(1);
            }

            // Hash and salt user password
            String salt = passwordHandler.generateSalt();
            String hashedPassword = passwordHandler.hashPassword(plaintext, salt.getBytes());

            // Add the user's password
            if (user instanceof Patient) {
                statement = "INSERT INTO patient_passwords(hashed_value, salt, user_id) VALUES ('" + hashedPassword + "', '" + salt + "', '" + userId + "');";
            } else {
                statement = "INSERT INTO staff_passwords(hashed_value, salt, user_id) VALUES ('" + hashedPassword + "', '" + salt + "', '" + userId + "');";
            }

            p = con.prepareStatement(statement);
            p.executeUpdate();

        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }  finally {
            //Race condition control
            lock.unlock();
        }
    }


    public synchronized void viewPatients() {
        try {
            // Execute SQL query
            p = con.prepareStatement("SELECT * FROM patients");
            results = p.executeQuery();

            // Loop through each row and print
            /*
            while (results.next()) {
                int id = results.getInt("p_id");
                String forename = results.getString("forename");
                String surname = results.getString("surname");
                String dob = results.getString("date_of_birth");
                System.out.println(id + "\t\t" + forename + "\t\t" + surname + "\t\t" + dob);
            } */

            results.close();
            p.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////
    //Logger Stuff

    /*CREATE TABLE events(
            event_id int AUTO_INCREMENT PRIMARY KEY,
            user_id VARCHAR(70),
            time_recorded DATETIME DEFAULT CURRENT_DATETIME,
            event_type varchar(100),
            description varchar(255),
            appended_by int(20),
            FOREIGN KEY(appended_by) REFERENCES staff(employee_id));
       */

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
    public boolean verifyPassword(String plaintext, String email, boolean isPatient) {
        //Race condition control
        lock.lock();
        try {
            // Get user id that corresponds to the email
            int id = getUserId(email, isPatient);

            // Prepare SQL query
            if (isPatient) {
                p = con.prepareStatement("SELECT hashed_value, salt FROM patient_passwords WHERE user_id=" + id + ";");
            } else {
                p = con.prepareStatement("SELECT hashed_value, salt FROM staff_passwords WHERE user_id=" + id + ";");
            }

            // Get correct password hash and salt for that user
            results = p.executeQuery();
            while (results.next()) {
                String passwordHash = results.getString("hashed_value");
                String salt = results.getString("salt");

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
        }  finally {
            //Race condition control
            lock.unlock();
        }
        return false;
    }


    public synchronized int getUserId(String email, boolean isPatient) {
        //Race condition control
        lock.lock();
        try {
            // Get user id that corresponds to the email
            int id;
            if (isPatient) {
                p = con.prepareStatement("SELECT p_id FROM patients WHERE email='" + email + "';");
                results = p.executeQuery();
                System.out.println("database.Patient id: " + results.getInt(1));
                id = results.getInt("p_id");
            } else {
                p = con.prepareStatement("SELECT s_id FROM staff WHERE email='" + email + "';");
                results = p.executeQuery();
                System.out.println("database.Staff id: " + results.getInt(1));
                id = results.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }  finally {
            //Race condition control
            lock.unlock();
        }
    }
    //overload to give default parameters
    //public void viewPatients()
   // {
    //    viewPatients(-1);
    //}

    public synchronized void viewPatients(int p_id) {
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query
            if(p_id==-1)
                p = con.prepareStatement("SELECT * FROM patients");
            else
                p = con.prepareStatement("SELECT * FROM patients WHERE p_id = "+ p_id);
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("p_id");
                String forename = results.getString("forename");
                String surname = results.getString("surname");
                String dob = results.getString("date_of_birth");
                System.out.println(id + "\t\t" + forename + "\t\t" + surname + "\t\t" + dob);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }
    //overload to give default parameters
    public void viewStaffs()
    {
        viewStaffs(-1);
    }
    public synchronized void viewStaffs(int s_id) {
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query
            if(s_id==-1)
                p = con.prepareStatement("SELECT * FROM staffs");
            else
                p = con.prepareStatement("SELECT * FROM staffs WHERE s_id = "+ s_id);

            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("s_id");
                String forename = results.getString("forename");
                String surname = results.getString("surname");
                String dob = results.getString("date_of_birth");
                String role_title = results.getString("role_title");
                System.out.println(id + "\t\t" + forename + "\t\t" + surname + "\t\t" + dob+"\t\t"+role_title);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void deletePatients(int p_id) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("DELETE FROM patients WHERE p_id = " + p_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void deleteStaffs(int s_id) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("DELETE FROM staffs WHERE s_id = " + s_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void updatePatients(int p_id,String command) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("UPDATE patients SET "+command+" WHERE p_id = " + p_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void updateStaffs(int s_id,String command) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("UPDATE patients SET "+command+" WHERE s_id = " + s_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////

    public synchronized void viewLogEntries( ) {
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
                System.out.println(id + "\t\t" + userId + "\t\t" + time + "\t\t" + event_type +"\t\t"+event_description +"\t\t"+ appended_by);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void viewWarningLogEntries( ) {
        //Race condition control
        lock.lock();
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
                System.out.println(id + "\t\t" + userId + "\t\t" + time + "\t\t" + event_type +"\t\t"+event_description +"\t\t"+ appended_by);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void viewErrorLogEntries( ) {
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
                System.out.println(id + "\t\t" + userId + "\t\t" + time + "\t\t" + event_type +"\t\t"+event_description +"\t\t"+ appended_by);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public String printSpecificUserResponsibility(int user_Id){
        lock.lock();
        StringBuilder builder = new StringBuilder();
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
                String a = id + "\t\t" + userId + "\t\t" + time + "\t\t" + event_type + "\t\t" + event_description + "\t\t" + appended_by;
                System.out.println(a);
                builder.append(a);
                builder.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
            return builder.toString();
        }
    }

    // Returns all the suspicious activity of the system (Warnings , errors)
    // If print boolen is true, it prints out all the warnings and errors
    // If print boolean is false, it calculates responisbility and prints out the table
    public  LinkedHashMap<String,LinkedList<Integer>> viewErrorAndWarningLogEntries(boolean print) {
        //Race condition control
        lock.lock();
        LinkedHashMap<String, LinkedList<Integer>> userResponsibilityMap = new LinkedHashMap<>();
        try {
            // Execute SQL query

            p = con.prepareStatement("SELECT * FROM event_logs WHERE event_type IN ('WARNING','ERROR');");
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
                if(print) {
                    System.out.println(id + "\t\t" + userId + "\t\t" + time + "\t\t" + event_type + "\t\t" + event_description + "\t\t" + appended_by);
                }
                // Populate responsibilities map
                else {
                    System.out.println("Trying to meow..;");
                    //If User Already Exists in the Map
                    if (userResponsibilityMap.containsKey(userId)) {
                        System.out.println("User Exists in map");
                        if (event_type.contains("WARNING")) {
                            newList.set(0,userResponsibilityMap.get(userId).get(0) + 1);
                            newList.set(1,userResponsibilityMap.get(userId).get(1));
                            userResponsibilityMap.put(userId,new LinkedList<>(newList));
                        }
                        else if (event_type.contains("ERROR")) {
                            System.out.println("\t Trying to update EROR");
                            newList.set(0,userResponsibilityMap.get(userId).get(0));
                            newList.set(1,userResponsibilityMap.get(userId).get(1) + 1);
                            userResponsibilityMap.put(userId,new LinkedList<>(newList));
                        }
                    }
                    // First time seeing this User Id
                    else{
                        values.clear();
                        System.out.println("User doesn't exist in map");
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
                        System.out.println(newValue);
                        Logger.printMap(userResponsibilityMap);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
        if(!print)
            System.out.println("Returning map");
        return userResponsibilityMap;
    }
}