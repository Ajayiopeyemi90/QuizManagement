package quizzManagement;
//This class represents a User in the Quiz Management system

public class User {
    private int id; // User's unique identifier
    private String username; // User's username
    private String password; // User's password 
    private String role; // User's role (e.g., admin, student)

    // Constructor to initialize a User object with id, username, password, and role
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter method to retrieve the user's ID
    public int getId() {
        return id;
    }

    // Getter method to retrieve the user's username
    public String getUsername() {
        return username;
    }

    // Getter method to retrieve the user's password
    public String getPassword() {
        return password;
    }

    // Getter method to retrieve the user's role
    public String getRole() {
        return role;
    }
}
