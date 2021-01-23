import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;

public class DatabaseConnection {
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String DATABASE_LOCATION = "jdbc:sqlite:";
    private static String dbName = "database.db";
    private static Connection con = null;
    private static PreparedStatement p = null;
    private static ResultSet results = null;

    public synchronized void createUser(User user) {
        try {
            // Execute SQL query
            String statement = "INSERT INTO patients (forename, surname, date_of_birth, address, email) VALUES ('" +
                    user.getForenames() + "', '" + user.getSurnames() + "', '" + user.getDoB() + "', '" +
                    user.getAddress() + "', '" + user.getEmail() + "');";
            p = con.prepareStatement(statement);
            p.executeUpdate();

            // TODO: why does this cause a "database file locked" error?
            // viewPatients();
        } catch (SQLException e) {
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
        } catch (SQLException e) {
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
            con.setAutoCommit(false);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}