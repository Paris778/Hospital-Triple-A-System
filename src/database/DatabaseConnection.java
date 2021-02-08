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

    public String lockAccount(int id) {
        lock.lock();
        try {
            p = con.prepareStatement("UPDATE users SET account_locked=1 WHERE u_id= ?");
            p.setInt(1, id);
            p.executeUpdate();
            return "User #" + id + "'s account has been successfully locked.";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "Error in locking account of User #" + id;
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

    public int getUserId(String email) {
        //Race condition control
        lock.lock();
        try {
            // Get user id that corresponds to the email
            p = con.prepareStatement("SELECT u_id FROM users WHERE email= ?");
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

    public void deletePatients(int u_id) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("DELETE FROM patients WHERE u_id = ?");
            p.setInt(1, u_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public void deleteStaffs(int u_id) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("DELETE FROM staff WHERE u_id = ?");
            p.setInt(1, u_id);
            results = p.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public void updatePatients(int u_id, String command) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("UPDATE patients SET ? WHERE u_id = ?");
            p.setString(1, command);
            p.setInt(2, u_id);
            results = p.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //Race condition control
            lock.unlock();
        }
    }

    public void updateStaffs(int u_id, String command) {
        lock.lock();

        try {
            // Execute SQL query
            p = con.prepareStatement("UPDATE patients SET ? WHERE u_id = ?");
            p.setString(1, command);
            p.setInt(2, u_id);
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

    public void appendLog(int userId, String eventType, String eventDescription, int appendedBy) {
        //Race Condition Control
        lock.lock();
        try {
            String statement = "INSERT INTO event_logs (u_id, event_type, event_description, appended_by) VALUES (?, ?, ?, ?)";
            p = con.prepareStatement(statement);

            // Insert values
            p.setInt(1, userId);
            p.setString(2, eventType);
            p.setString(3, eventDescription);
            p.setInt(4, appendedBy);

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