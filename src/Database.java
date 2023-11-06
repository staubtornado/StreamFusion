import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class to interact with the database
 */
public class Database {
    private final Connection connection;

    /**
     * Create a new database connection
     * @param host The host of the database
     * @param port The port of the database
     * @param username The username to connect with
     * @param password The password to connect with
     * @throws SQLException If there is an error connecting to the database
     */
    public Database(String host, int port, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%d/", host, port),
                username,
                password
        );
    }

    /**
     * Execute a query
     * @param query The query to execute
     * @return The result of the query
     * @throws SQLException If there is an error executing the query
     */
    private ResultSet executeQuery(String query) throws SQLException {
        return this.connection.createStatement().executeQuery(query);
    }

    /**
     * Get a user by their ID
     * @param id The ID of the user
     * @return The user, or null if they don't exist
     * @throws SQLException If there is an error executing the query
     */
    public User getUser(long id) throws SQLException {
        try (
            ResultSet resultSet = this.executeQuery(String.format("SELECT * FROM users WHERE id = %d", id));
        ) {
            // If the user doesn't exist, return null
            if (!resultSet.next()) {
                return null;
            }
            return new User(
                resultSet.getString("username"),
                resultSet.getLong("id"),
                resultSet.getString("password"),
                resultSet.getString("email")
            );
        }
    }
}
