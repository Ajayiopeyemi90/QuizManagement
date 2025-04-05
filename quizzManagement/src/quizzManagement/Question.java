package quizzManagement;

import java.util.ArrayList;

/**
 * This class represents a Question in the quiz system, containing the question text,
 * a list of options, and the index of the correct option.
 */
public class Question {

    public String QuestionText;  // The text of the question
    public ArrayList<String> Options;  // The options for the question
    public int CorrectOption;  // The index of the correct option

    /**
     * Constructor to initialize a new Question object with the given text, options, and correct option index.
     *
     * @param questionText The text of the question
     * @param options A list of options for the question
     * @param correctOption The index of the correct option
     */
    public Question(String questionText, ArrayList<String> options, int correctOption) {
        this.QuestionText = questionText;
        this.Options = options;
        this.CorrectOption = correctOption;
    }

    /**
     * Getter method for the question text.
     *
     * @return The text of the question
     */
    public String getQuestionText() {
        return QuestionText;
    }

    /**
     * Getter method for the options.
     *
     * @return The list of options
     */
    public ArrayList<String> getOptions() {
        return Options;
    }

    /**
     * Getter method for the correct option index.
     *
     * @return The index of the correct option
     */
    public int getCorrectOption() {
        return CorrectOption;
    }
}
