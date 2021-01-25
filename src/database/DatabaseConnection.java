package database;

import utility.Constants;
import utility.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;
import java.util.Calendar;

public class DatabaseConnection {
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String DATABASE_LOCATION = "jdbc:sqlite:";
    private static String dbName = "database.db";
    private static Connection con = null;
    private static PreparedStatement p = null;
    private static ResultSet results = null;


    public void createFakeUser() {
        try {
            // Add user to database
            String statement = "INSERT INTO patients(forename, surname, date_of_birth, address, email) VALUES ('test', 'test', '2000-01-01', 'china', 'g@gmail.com');";
            p = con.prepareStatement(statement);
            p.executeUpdate();

            results.close();
            p.close();

            // TODO: replace this with actual race condition prevention lol
            Thread.sleep(1000);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void createUser(User user, byte[] hashedPassword) {
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

            // Add the user's password
            if (user instanceof Patient) {
                statement = "INSERT INTO patient_passwords(hashed_value, user_id) VALUES ('" + hashedPassword + "', '" + userId + "');";
            } else {
                statement = "INSERT INTO staff_passwords(hashed_value, user_id) VALUES ('" + hashedPassword + "', '" + userId + "');";
            }

            p = con.prepareStatement(statement);
            p.executeUpdate();

            results.close();
            p.close();

            // TODO: replace this with actual race condition prevention lol
            Thread.sleep(1000);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void viewPatients() {
        try {
            // Execute SQL query
            p = con.prepareStatement("SELECT * FROM patients");
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("p_id");
                String forename = results.getString("forename");
                String surname = results.getString("surname");
                String dob = results.getString("date_of_birth");
                System.out.println(id + "\t\t" + forename + "\t\t" + surname + "\t\t" + dob);
            }

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

    public synchronized void appendLog(int userId, String eventType, String eventDescription) {
        try {
            String statement = "";

            statement =("INSERT INTO events (user_id, event_type, description) VALUES ('"
                    + userId + "', '"
                    + eventType + "', '"
                    + eventDescription + "'); "
            );

            // Add to statement
            p = con.prepareStatement(statement);
            p.executeUpdate();
            results.close();
            p.close();

            // TODO: replace this with actual race condition prevention lol
            Thread.sleep(1000);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
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
}