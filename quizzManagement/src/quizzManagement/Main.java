package quizzManagement;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO(); // DAO class to handle user-related DB operations

        System.out.println("Welcome to Quiz Management System");
        System.out.println("1. Register\n2. Login"); // Prompt user to register or log in

        int choice = 0;

        // Loop to validate user input for registration or login
        while (true) {
            System.out.print("Enter your choice (1 or 2): ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (choice == 1 || choice == 2) {
                    break; // Exit loop if valid input
                } else {
                    System.out.println("❌ Invalid choice. Please enter 1 or 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }

        // Registration flow
        if (choice == 1) {
            boolean registered = false;
            while (!registered) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                // Create a new user with default role "Student"
                User newUser = new User(0, username, password, "Student");
                int userId = userDAO.userRegister(newUser); // Register the user

                if (userId > 0) {
                    System.out.println("✅ User registered successfully with User ID: " + userId);
                    registered = true; // Exit registration loop
                } else {
                    System.out.println("❌ Registration failed. Please try again.");
                }
            }
        }

        // Login flow
        else if (choice == 2) {
            User loggedInUser = null;

            // Loop until valid credentials are provided
            while (loggedInUser == null) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                loggedInUser = userDAO.loginUser(username, password); // Authenticate user

                if (loggedInUser == null) {
                    System.out.println("❌ Invalid credentials. Please try again.");
                }
            }

            System.out.println("✅ Login successful.");
            System.out.println("Welcome, " + loggedInUser.getUsername() + "!");

            // Admin-specific functionality
            if (loggedInUser.getRole().equalsIgnoreCase("Admin")) {
                System.out.println("Admin access granted.");

                boolean exit = false;
                while (!exit) {
                    // Display admin options
                    System.out.println("\nChoose an action:");
                    System.out.println("1. Create Quiz");
                    System.out.println("2. Edit Quiz");
                    System.out.println("3. Delete Quiz");
                    System.out.println("4. Take Quiz");
                    System.out.println("5. View Student Results");
                    System.out.println("6. Logout");

                    System.out.print("Enter your choice: ");
                    int adminChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    // Perform admin action based on selection
                    switch (adminChoice) {
                        case 1:
                            QuizCreation.createQuiz(loggedInUser.getId());
                            break;
                        case 2:
                            QuizCreation.editQuiz(loggedInUser.getId());
                            break;
                        case 3:
                            QuizCreation.deleteQuiz(loggedInUser.getId());
                            break;
                        case 4:
                            int quizId;
                            while (true) {
                                System.out.print("Enter Quiz ID: ");
                                try {
                                    quizId = scanner.nextInt();
                                    scanner.nextLine(); // Clear buffer
                                    break;
                                } catch (InputMismatchException e) {
                                    System.out.println("❌ Invalid input. Please enter a valid Quiz ID.");
                                    scanner.nextLine(); // Clear buffer
                                }
                            }
                            TakeQuiz.takeQuiz(loggedInUser.getId(), quizId);
                            break;
                        case 5:
                            QuizCreation.viewStudentResults();
                            break;
                        case 6:
                            exit = true;
                            System.out.println("👋 Logged out successfully.");
                            break;
                        default:
                            System.out.println("❌ Invalid choice. Try again.");
                            break;
                    }
                }

            }
            // Student-specific functionality: only taking a quiz
            else {
                int quizId = 0;
                while (true) {
                    System.out.print("Enter Quiz ID: ");
                    try {
                        quizId = scanner.nextInt();
                        scanner.nextLine(); // Clear buffer
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("❌ Invalid input. Please enter a valid Quiz ID.");
                        scanner.nextLine(); // Clear buffer
                    }
                }
                TakeQuiz.takeQuiz(loggedInUser.getId(), quizId); // Student takes the quiz
            }
        }

        scanner.close(); // Close the scanner to prevent resource leak
    }
}
