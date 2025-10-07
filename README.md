# 🧠 Quiz & Flashcard Trainer

## 🎯 Project Overview
In this semester project, you will work in teams of **three students** to develop a **Java desktop application** that helps users create, manage, and play quizzes or flashcards.
The project is part of the course **“Programming 2 with Java”** and is designed to deepen your understanding of Java programming concepts, software structure, and development tools.

The application will include both:
- A **graphical user interface (JavaFX)**
- A **backend logic** with data persistence and testing

You will host your code on **GitHub**, work collaboratively using **Git branches**, and submit **Pull Requests** for each milestone. Each milestone will be presented and discussed with the instructor.

------

## 🧩 Learning Objectives
By completing this project, you will:
- Apply Java fundamentals in a larger software context
- Use **Git and GitHub** for collaborative development
- Write and test code with **JUnit**
- Handle **exceptions** and design robust applications
- Use **Java Collections** for data management and sorting
- Implement **(de-)serialization** for persistent storage
- Build a **JavaFX GUI** with event handling and data binding
- Package your application using **Gradle** and **jpackage**

------

## 🪜 Project Milestones

### **Milestone 1 – Hello World with Gradle and JavaFX**
**Topics:** Build process with Gradle · JavaFX Basics

**Goal:** Create the project setup and a minimal working UI.

**Tasks:**
- Initialize a Gradle project with JavaFX support
- Create a main window that displays a “Hello Quiz Trainer!” message
- Add a simple “Start” button (no functionality yet)
- Push the setup to GitHub and create your first Pull Request

💡 *Focus:* Gradle build, JavaFX setup, Git workflow

------

### **Milestone 2 – Exceptions and Testing with JUnit**
**Topics:** Exceptions · Testing · Error Handling

**Goal:** Implement the core logic layer with proper exception handling and unit tests.

**Tasks:**
- Create model classes:
  - `Question` (fields: questionText, correctAnswer, possibleAnswers)
  - `Quiz` (a list of `Question` objects)
- Validate input (e.g., non-empty questions, at least one correct answer)
- Implement custom exceptions such as `InvalidQuestionException`
- Write **JUnit tests** for question and quiz validation

💡 *Focus:* Unit testing, exception handling, clean code

------

### **Milestone 3 – Collections and Sorting**
**Topics:** Java Collection Framework · Sorting

**Goal:** Manage and organize multiple quizzes using Java Collections.

**Tasks:**
- Create a `QuizManager` class to manage multiple quizzes
- Implement sorting (e.g., by title, creation date, number of questions)
- Add searching or filtering using `Comparator` or `Collections.sort()`
- Display the results in the console or a basic JavaFX list view
- Write JUnit tests for sorting and searching

💡 *Focus:* Collections, sorting, reusable data structures

------

### **Milestone 4 – (De-)Serialization of Objects**
**Topics:** Object Serialization · JSON · XML

**Goal:** Implement data persistence for quizzes and questions.

**Tasks:**
- Add save/load functionality for quiz data
  - Option 1: Native Java serialization
  - Option 2: JSON or XML serialization (student choice)
- Handle file I/O errors with exceptions
- Load quizzes on startup and save on exit
- Write unit tests for serialization

💡 *Focus:* File handling, persistent storage, error handling

------

### **Milestone 5 – GUI Development with JavaFX**
**Topics:** GUI Design · Event Handling · Data Binding

**Goal:** Build a complete JavaFX GUI that connects to your backend logic.

**Tasks:**
- Create user interfaces for:
  - Viewing and selecting quizzes
  - Creating new quizzes and questions
  - Playing quizzes (multiple-choice questions)
  - Displaying final scores
- Connect GUI controls to backend logic (Controller pattern)
- Add basic input validation and error messages
- Package the app using **Badass Runtime Plugin** and **jpackage** for Windows/Mac

💡 *Focus:* MVC architecture, event-driven programming, deployment

------

## 🧠 Expected Outcome
By the end of the semester, you will have developed a **fully functional Quiz & Flashcard Trainer** that:
- Uses professional tools and best practices
- Is tested, version-controlled, and packaged for distribution
- Demonstrates key Java programming concepts from the course

------

## 🧰 Tools & Frameworks
- **Java 21+**
- **JavaFX**
- **Gradle**
- **JUnit 5**
- **Git & GitHub**
- **Badass Runtime Plugin** / **jpackage**

------

## 📦 Submission & Presentation
Each milestone will be:
- Submitted as a **GitHub Pull Request**
- **Presented and discussed orally** with the instructor

Final grading considers:
- Code quality and structure
- Functionality and completeness
- Use of Git branches and commits
- Presentation and teamwork

------

**Quiz & Flashcard Trainer** – Learn by coding – your JavaFX app that teaches while you build!
