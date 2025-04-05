package quizzManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class handles the creation, editing, and deletion of quizzes,
 * as well as managing quiz-related operations like adding questions.
 */
public class QuizCreation {

    /**
     * Creates a quiz and stores it in the database.
     * @param userId The ID of the user creating the quiz.
     */
    public static void createQuiz(int userId) {
        Scanner scanner = new Scanner(System.in);

        // Input loop for quiz title
        String quizTitle = "";
        while (quizTitle.isBlank()) {
            System.out.print("Enter quiz title: ");
            quizTitle = scanner.nextLine();
            if (quizTitle.isBlank()) {
                System.out.println("❌ Quiz title cannot be empty.");
            }
        }

        // Input loop for quiz description
        String quizDescription = "";
        while (quizDescription.isBlank()) {
            System.out.print("Enter quiz description: ");
            quizDescription = scanner.nextLine();
            if (quizDescription.isBlank()) {
                System.out.println("❌ Quiz description cannot be empty.");
            }
        }

        // Save the quiz to the database and create associated questions
        int quizId = saveQuizToDatabase(userId, quizTitle, quizDescription);
        if (quizId > 0) {
            System.out.println("✅ Quiz created successfully with ID: " + quizId);
            createQuestionsForQuiz(quizId);
        } else {
            System.out.println("❌ Quiz creation failed.");
        }
        
    }
    

    /**
     * Saves a quiz to the database.
     * @param userId The ID of the user creating the quiz.
     * @param quizTitle The title of the quiz.
     * @param quizDescription The description of the quiz.
     * @return The ID of the created quiz.
     */
    private static int saveQuizToDatabase(int userId, String quizTitle, String quizDescription) {
        String query = "INSERT INTO quizzes (title, description, created_by) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, quizTitle);
            pstmt.setString(2, quizDescription);
            pstmt.setInt(3, userId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Returns the quiz ID if successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Returns 0 if the quiz creation fails
    }

    /**
     * Handles creating questions for the quiz.
     * @param quizId The ID of the quiz for which questions are being created.
     */
    private static void createQuestionsForQuiz(int quizId) {
        Scanner scanner = new Scanner(System.in);
        int numQuestions = 0;

        // Input loop for the number of questions
        while (true) {
            System.out.print("Enter the number of questions for the quiz: ");
            try {
                numQuestions = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                if (numQuestions <= 0) {
                    System.out.println("❌ Please enter a positive number.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a valid number.");
                scanner.nextLine();  // Discard invalid input
            }
        }

        // Loop through each question and input its details
        for (int i = 0; i < numQuestions; i++) {
            System.out.println("\nCreating question " + (i + 1) + ":");

            // Input for question text
            String questionText = "";
            while (questionText.isBlank()) {
                System.out.print("Enter question text: ");
                questionText = scanner.nextLine();
                if (questionText.isBlank()) {
                    System.out.println("❌ Question text cannot be empty.");
                }
            }

            // Input loop for options
            String[] optionsArray = null;
            while (true) {
                System.out.print("Enter options (comma-separated): ");
                String optionsInput = scanner.nextLine();
                optionsArray = optionsInput.split(",");

                // Ensure there are at least two options
                if (optionsArray.length < 2) {
                    System.out.println("❌ Please enter at least two options.");
                    continue;
                }

                // Ensure options are not all blank
                boolean allBlank = true;
                for (String option : optionsArray) {
                    if (!option.strip().isEmpty()) {
                        allBlank = false;
                        break;
                    }
                }

                if (allBlank) {
                    System.out.println("❌ Options cannot all be empty.");
                } else {
                    break;
                }
            }

            // Input loop for correct option index
            int correctOption = -1;
            while (true) {
                System.out.print("Enter the index of the correct option (0-" + (optionsArray.length - 1) + "): ");
                try {
                    correctOption = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    if (correctOption >= 0 && correctOption < optionsArray.length) {
                        break;
                    } else {
                        System.out.println("❌ Index out of range. Try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("❌ Invalid input. Please enter a number.");
                    scanner.nextLine();  // Discard invalid input
                }
            }

            // Save the question to the database
            saveQuestionToDatabase(quizId, questionText, optionsArray, correctOption);
        }

        System.out.println("✅ All questions created for the quiz.");
    }

    /**
     * Saves a question to the database.
     * @param quizId The ID of the quiz the question belongs to.
     * @param questionText The text of the question.
     * @param options The options for the question.
     * @param correctOption The index of the correct option.
     */
    private static void saveQuestionToDatabase(int quizId, String questionText, String[] options, int correctOption) {
        String query = "INSERT INTO questions (quiz_id, question_text, options, correct_option) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, quizId);
            pstmt.setString(2, questionText);
            pstmt.setArray(3, conn.createArrayOf("TEXT", options));
            pstmt.setInt(4, correctOption);

            pstmt.executeUpdate();
            System.out.println("✅ Question saved to database.");
        } catch (SQLException e) {
            System.out.println("❌ Failed to save question.");
            e.printStackTrace();
        }
    }

    // Methods for editing, deleting quizzes, and viewing student results...
    public static void editQuiz(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter quiz ID to edit: ");
        int quizId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter new quiz title: ");
        String newTitle = scanner.nextLine();

        System.out.print("Enter new quiz description: ");
        String newDescription = scanner.nextLine();

        // Update quiz title and description in the database
        String query = "UPDATE quizzes SET title = ?, description = ? WHERE id = ? AND created_by = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newTitle);
            pstmt.setString(2, newDescription);
            pstmt.setInt(3, quizId);
            pstmt.setInt(4, userId);

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                System.out.println("✅ Quiz updated successfully.");
            } else {
                System.out.println("❌ Quiz not found or you are not the creator.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error updating quiz.");
            e.printStackTrace();
        }
    }

    // Method to delete a quiz created by the admin
    public static void deleteQuiz(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter quiz ID to delete: ");
        int quizId = scanner.nextInt();
        scanner.nextLine();

        String query = "DELETE FROM quizzes WHERE id = ? AND created_by = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, quizId);
            pstmt.setInt(2, userId);

            int deleted = pstmt.executeUpdate();
            if (deleted > 0) {
                System.out.println("✅ Quiz deleted successfully.");
            } else {
                System.out.println("❌ Quiz not found or you are not the creator.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error deleting quiz.");
            e.printStackTrace();
        }
    }

    // Method to view all student quiz results
    public static void viewStudentResults() {
        String query = "SELECT u.username, q.title, r.score FROM results r " +
                       "JOIN users u ON r.user_id = u.id " +
                       "JOIN quizzes q ON r.quiz_id = q.id ORDER BY q.title";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n📊 Student Results:");
            
            // Loop through and print results
            while (rs.next()) {
                String username = rs.getString("username");
                String quizTitle = rs.getString("title");
                int score = rs.getInt("score");

                System.out.printf("👤 %s | 📘 %s | 🧮 Score: %d%n", username, quizTitle, score);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching results.");
            e.printStackTrace();
        }
    }
}
