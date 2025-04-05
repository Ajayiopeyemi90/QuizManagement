package quizzManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class handles all database operations related to the User entity,
 * such as user registration and login.
 */
public class UserDAO {

    /**
     * Authenticates a user by checking if the given username and password match any record in the database.
     *
     * @param username The username entered by the user
     * @param password The password entered by the user
     * @return A User object if credentials are valid, otherwise null
     */
    public User loginUser(String username, String password) {
        String query = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the username and password in the SQL query
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            // If a matching user is found, return a User object
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Print error if something goes wrong with the database
        }
        return null;  // Return null if login fails
    }

    /**
     * Registers a new user by inserting their details into the database.
     *
     * @param user The User object containing username, password, and role
     * @return The generated user ID if registration is successful, or 0 if it fails
     */
    public int userRegister(User user) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set user values into the insert query
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);  // Return newly created user ID
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Print error if something goes wrong
        }
        return 0;  // Return 0 if registration fails
    }

    }
