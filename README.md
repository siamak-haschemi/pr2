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

### **Milestone 1 – Project Bootstrap & Git Workflow**
**Topics:** GitHub workflow · Branching · CI basics

**Goal:** Start from a fully functioning, instructor-provided JavaFX app and establish your Git/GitHub workflow.

**Tasks:**
- Create a GitHub account (if not already available)
- Create a new private repository for your team
- Enable branch protection on `main` and add the instructor as collaborator/reviewer
- Create a feature branch (e.g., `milestone/1-bootstrap`)
- Commit and push the provided Milestone 1 code (fully functioning app)
- Open a Pull Request and add the instructor as reviewer
- Trigger CI and verify it builds/tests and produces the expected artifacts (macOS: zipped `.app`; Windows: `.exe`)

💡 *Focus:* Team collaboration, PR workflow, CI visibility

------

### **Milestone 2 – Exceptions, Testing, and Quiz UI**
**Topics:** Exceptions · Testing · JavaFX Views/Controllers

**Goal:** Add a validated core model with tests and a simple quiz UI using a fixed set of questions.

**Tasks:**
- Model and exceptions:
  - Implement `Question` (text, options, single correct index) and `Quiz` (title + list of questions)
  - Add `InvalidQuestionException` and input validation (non-empty text, options present, index in range)
  - Write JUnit 5 tests to cover valid and invalid scenarios
- Fixed quiz content:
  - Provide a 10-question quiz on “JUnit and Exceptions in Java” via a fixture
- JavaFX UI (separate views/controllers by feature):
  - `main.MainController` hosts a `StackPane`
  - `overview.OverviewController` + `overview.fxml` show quiz title and list of all questions with the correct answer marked
  - `quiz.QuizExecutionController` + `quiz_execution.fxml` run a quiz flow:
    - Start Quiz → show first question with radio-button answers
    - Continue → immediate feedback (highlights correct and selection)
    - Next → next question, then return to overview after the last
- Keep module-info simple (use `open module`) to avoid module boilerplate for students

💡 *Focus:* Unit tests + exception design, small but well-structured UI with feature-based packages

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
