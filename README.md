# 📘 Quiz Management System

A console-based Java application that allows users to register, log in, create quizzes, take quizzes, and manage quiz-related operations with role-based access (Admin/Student). Built using **Java**, **JDBC**, and **PostgreSQL**.

---

## 🔧 Features

### 👥 User Authentication
- Register new users
- Login with username and password
- Role-based access (`Admin`, `Student`)

### 🧑‍🏫 Admin Capabilities
- Create quizzes with multiple questions and options
- Edit quiz title and description
- Delete quizzes created by them
- View student results for any quiz

### 👨‍🎓 Student Capabilities
- Take quizzes using Quiz ID
- View score after completion

---

## 🧰 Technologies Used

- **Java** (Console-based application)
- **JDBC** (Java Database Connectivity)
- **PostgreSQL** (Database)

---

## 🗂️ Project Structure

```
quizzManagement/
│
├── Main.java              # Entry point of the application
├── User.java              # Model class for user
├── UserDAO.java           # Handles user-related DB operations
├── QuizCreation.java      # Admin quiz management (CRUD operations)
├── TakeQuiz.java          # Handles quiz-taking and scoring
├── DatabaseConnection.java# Utility for DB connection
```

---

## 🛠️ Setup Instructions

1. **Clone the repository**
2. **Create the PostgreSQL database** with the necessary tables:
   - `users`
   - `quizzes`
   - `questions`
   - `results`
3. **Configure your database connection** in `DatabaseConnection.java`.
4. **Compile and run** `Main.java` to start the application.

---

## 🧪 Sample Database Schema (PostgreSQL)

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE quizzes (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    created_by INT REFERENCES users(id)
);

CREATE TABLE questions (
    id SERIAL PRIMARY KEY,
    quiz_id INT REFERENCES quizzes(id),
    question_text TEXT NOT NULL,
    options TEXT[] NOT NULL,
    correct_option INT NOT NULL
);

CREATE TABLE results (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    quiz_id INT REFERENCES quizzes(id),
    score INT
);
```

---

## ✅ Status

- Core functionalities implemented
- Uses prepared statements to prevent SQL injection
- Input validation and error handling included

---

## 💡 Future Improvements

- Add password hashing
- GUI or web interface
- Timed quizzes
- Quiz categories and tags

---

## 📩 Contact

Made with ❤️ by **Opeyemi Ajayi Joseph**  
📧 ajayiopeyemi90@gmail.com  
🔗 [LinkedIn Profile](https://www.linkedin.com/in/opeyemi-joseph-ajayi-688530354)
