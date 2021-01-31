package database;

import utility.PasswordHandler;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseConnection {
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String DATABASE_LOCATION = "jdbc:sqlite:";
    private static String dbName = "new-database.db";
    private static Connection con = null;
    private static PreparedStatement p = null;
    private static ResultSet results = null;
    private PasswordHandler passwordHandler = new PasswordHandler();
    private Lock lock = new ReentrantLock();

    public synchronized void createUser(User user, String plaintext) {
        //Race condition control
        lock.lock();
        try {
            // Hash and salt user password
            String salt = passwordHandler.generateSalt();
            String hashedPassword = passwordHandler.hashPassword(plaintext, salt.getBytes());

            // TODO: add roles

            // Add password and email to users table
            String statement = "INSERT INTO users(email, password_hash, password_salt) VALUES ('" +
                    user.getEmail() + "', '" + hashedPassword + "', '" + salt + "');";
            p = con.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            p.executeUpdate();

            // Return user id to add foreign key reference in staff/patient table
            int userId = -1;
            ResultSet results = p.getGeneratedKeys();
            if (results.next()) {
                userId = results.getInt(1);
            }

            // Check if user is patient or staff and add to corresponding table
            if (user instanceof Patient) {
                statement = "INSERT INTO patients(u_id, forenames, surname, date_of_birth, address) VALUES ('" +
                        userId + "', '" + user.getForenames() + "', '" + user.getSurnames() + "', '" + user.getDoB() + "', '" +
                        user.getAddress() + "'); ";
                System.out.println(statement);
            } else {
                statement = "INSERT INTO staff(u_id, forenames, surname, date_of_birth, address, job_title, phone_number) VALUES ('" +
                        userId + "', '" + user.getForenames() + "', '" + user.getSurnames() + "', '" + user.getDoB() + "', '" +
                        user.getAddress() + "', '" + ((Staff) user).getrole_title() + "', '" + ((Staff) user).getphone_number() + "'); ";
            }
            p = con.prepareStatement(statement);
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
            p = con.prepareStatement("SELECT password_hash, password_salt FROM users WHERE u_id=" + id + ";");

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

    public synchronized int getUserId(String email) {
        //Race condition control
        lock.lock();
        try {
            // Get user id that corresponds to the email
            p = con.prepareStatement("SELECT u_id FROM users WHERE email='" + email + "';");
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

    //overload to give default parameters
    public void viewPatients() {
        viewPatients(-1);
    }

    public synchronized void viewPatients(int u_id) {
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query
            if (u_id == -1)
                p = con.prepareStatement("SELECT * FROM patients");
            else
                p = con.prepareStatement("SELECT * FROM patients WHERE u_id = " + u_id);
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("u_id");
                String forename = results.getString("forenames");
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
    public void viewStaffs() {
        viewStaffs(-1);
    }

    public synchronized void viewStaffs(int u_id) {
        //Race condition control
        lock.lock();
        try {
            // Execute SQL query
            if (u_id == -1)
                p = con.prepareStatement("SELECT * FROM staff");
            else
                p = con.prepareStatement("SELECT * FROM staff WHERE u_id = " + u_id);

            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("u_id");
                String forename = results.getString("forenames");
                String surname = results.getString("surname");
                String dob = results.getString("date_of_birth");
                String job_title = results.getString("job_title");
                System.out.println(id + "\t\t" + forename + "\t\t" + surname + "\t\t" + dob + "\t\t" + job_title);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void deletePatients(int u_id) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("DELETE FROM patients WHERE u_id = " + u_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void deleteStaffs(int u_id) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("DELETE FROM staff WHERE u_id = " + u_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void updatePatients(int u_id, String command) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("UPDATE patients SET " + command + " WHERE u_id = " + u_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public synchronized void updateStaffs(int u_id, String command) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("UPDATE patients SET " + command + " WHERE u_id = " + u_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // LOGGER METHODS
    ////////////////////////////////////////////////////////////////////////////////

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