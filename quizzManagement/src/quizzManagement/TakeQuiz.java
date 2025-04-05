package quizzManagement;
//Importing necessary libraries for database interaction and handling user input

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class TakeQuiz {

    // Method to start taking a quiz, taking user ID and quiz ID as input
    public static void takeQuiz(int userId, int quizId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Retrieve questions for the quiz from the database
            ArrayList<Question> quizQuestions = getQuizQuestions(quizId, conn);

            if (quizQuestions.isEmpty()) {
                System.out.println("No questions found for quiz ID: " + quizId);
                return; // Exit if no questions are found
            }

            // Conduct the quiz if questions are available
            conductQuiz(userId, quizId, quizQuestions, conn);

        } catch (SQLException e) {
            System.out.println("Error taking quiz: " + e.getMessage()); // Handle any SQL exceptions
        }
    }

    // Method to retrieve quiz questions from the database
    private static ArrayList<Question> getQuizQuestions(int quizId, Connection conn) throws SQLException {
        ArrayList<Question> quizQuestions = new ArrayList<>();
        String query = "SELECT id, question_text, options, correct_option FROM questions WHERE quiz_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, quizId); // Set the quiz ID in the query
            ResultSet rs = pstmt.executeQuery();

            // Loop through the result set and create Question objects
            while (rs.next()) {
                int id = rs.getInt("id");
                String questionText = rs.getString("question_text");
                String[] options = (String[]) rs.getArray("options").getArray(); // Get options as an array
                int correctOption = rs.getInt("correct_option");
                quizQuestions.add(new Question(id, questionText, options, correctOption));
            }
        }
        return quizQuestions; // Return the list of quiz questions
    }

    // Method to conduct the quiz, asking questions and scoring answers
    private static void conductQuiz(int userId, int quizId, ArrayList<Question> quizQuestions, Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int score = 0;

        // Iterate through each question
        for (int i = 0; i < quizQuestions.size(); i++) {
            Question question = quizQuestions.get(i);
            System.out.println("Question " + (i + 1) + ": " + question.getQuestionText());
            String[] options = question.getOptions();
            
            // Display options for the current question
            for (int j = 0; j < options.length; j++) {
                System.out.println((j + 1) + ". " + options[j]);
            }

            int userAnswer = -1;
            // Validate user input for selecting an option
            while (true) {
                System.out.print("Enter your answer (1-" + options.length + "): ");
                try {
                    userAnswer = scanner.nextInt();
                    if (userAnswer >= 1 && userAnswer <= options.length) {
                        break; // Valid input, exit loop
                    } else {
                        System.out.println("❌ Please enter a number between 1 and " + options.length);
                    }
                } catch (Exception e) {
                    System.out.println("❌ Invalid input. Please enter a number.");
                    scanner.nextLine(); // Clear invalid input
                }
            }

            // Check if the user's answer is correct and update score
            if (userAnswer - 1 == question.getCorrectOption()) {
                score++;
                System.out.println("Correct!");
            } else {
                System.out.println("Incorrect! The correct answer was option " + (question.getCorrectOption() + 1));
            }
        }

        // Display the final score
        System.out.println("Quiz completed! Your score: " + score + "/" + quizQuestions.size());

        // Store the result in the database
        storeQuizResult(userId, quizId, score, conn);
    }

    // Method to store the quiz result in the database
    private static void storeQuizResult(int userId, int quizId, int score, Connection conn) throws SQLException {
        String query = "INSERT INTO results (user_id, quiz_id, score, taken_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, quizId);
            pstmt.setInt(3, score);
            pstmt.executeUpdate(); // Execute the query to store the result
            System.out.println("Quiz result stored successfully!");

        } catch (SQLException e) {
            System.out.println("Error storing quiz result: " + e.getMessage());
        }
    }

    // Inner class for Question to store question details
    public static class Question {
        private int id;
        private String questionText;
        private String[] options;
        private int correctOption;

        // Constructor for initializing Question object
        public Question(int id, String questionText, String[] options, int correctOption) {
            this.id = id;
            this.questionText = questionText;
            this.options = options;
            this.correctOption = correctOption;
        }

        // Getter methods for accessing Question properties
        public int getId() {
            return id;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String[] getOptions() {
            return options;
        }

        public int getCorrectOption() {
            return correctOption;
        }
    }
}
