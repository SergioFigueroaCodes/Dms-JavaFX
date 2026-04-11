import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Provides a utility method for creating SQLite database connections.
 * <p>
 * This class centralizes connection setup so other parts of the system can
 * connect to a database file using a consistent JDBC URL format.
 */
public class DatabaseConnection {

    /**
     * Creates a connection to the SQLite database at the specified file path.
     *
     * @param dbPath the file path to the SQLite database
     * @return an active JDBC connection to the database
     * @throws Exception if the database connection cannot be established
     */
    public static Connection connect(String dbPath) throws Exception {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }
}