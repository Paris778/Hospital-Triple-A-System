import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.*;

public class DatabaseConnection {
    private static final String JDBC_DRIVER = "org.sqlite.JDBC";
    private static final String DATABASE_LOCATION = "jdbc:sqlite:";
    private static String dbName = "patients.db";
    private static Connection con = null;
    private static PreparedStatement p = null;
    private static ResultSet results = null;

    private static void establishConnection() {
        try {
            // Load the JDBC driver
            Class.forName(JDBC_DRIVER);

            // Attempt to open a connection
            con = DriverManager.getConnection(DATABASE_LOCATION + dbName);

            // Execute SQL query
            p = con.prepareStatement("SELECT * FROM patients");
            results = p.executeQuery();

            // Loop through each row and print
            while (results.next()) {
                int id = results.getInt("patient_id");
                String forename = results.getString("forename");
                String surname = results.getString("surname");
                System.out.println(id + "\t\t" + forename + "\t\t" + surname);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            establishConnection();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}