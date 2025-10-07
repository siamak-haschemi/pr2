package de.bht.pr.quizzr.model;

import java.util.List;

public final class QuizGenerator {
  private QuizGenerator() {}

  public static Quiz generateQuiz() {
    Quiz quiz = new Quiz("JUnit and Exceptions in Java");

    quiz.addQuestion(
        new Question(
            "Which annotation marks a JUnit 5 test method?",
            List.of("@TestCase", "@Test", "@Run", "@Spec"),
            1));

    quiz.addQuestion(
        new Question(
            "Which JUnit 5 annotation runs once before all tests in a class?",
            List.of("@BeforeEach", "@BeforeAll", "@Setup", "@Init"),
            1));

    quiz.addQuestion(
        new Question(
            "How do you assert that a method throws an exception in JUnit 5?",
            List.of(
                "assertThrows(Exception.class, () -> method())",
                "assertException(() -> method())",
                "expectThrows(Exception.class, method)",
                "assert(method).throws(Exception.class)"),
            0));

    quiz.addQuestion(
        new Question(
            "Which statement about checked vs unchecked exceptions is correct?",
            List.of(
                "Checked exceptions extend RuntimeException",
                "Unchecked exceptions must be declared with throws",
                "Checked exceptions must be handled or declared",
                "Unchecked exceptions cannot be caught"),
            2));

    quiz.addQuestion(
        new Question(
            "What does try-with-resources guarantee for AutoCloseable resources?",
            List.of(
                "Resources are closed only on success",
                "Resources are closed automatically even on exceptions",
                "Resources are closed only in finally block",
                "Resources are garbage collected immediately"),
            1));

    quiz.addQuestion(
        new Question(
            "What is the purpose of exception chaining?",
            List.of(
                "To silence original exceptions",
                "To attach a cause to provide context",
                "To log without throwing",
                "To convert checked to unchecked implicitly"),
            1));

    quiz.addQuestion(
        new Question(
            "Which JUnit 5 annotation disables a test?",
            List.of("@Ignore", "@Disabled", "@Skip", "@Off"),
            1));

    quiz.addQuestion(
        new Question(
            "Which block executes regardless of whether an exception is thrown?",
            List.of("catch", "finally", "try", "throws"),
            1));

    quiz.addQuestion(
        new Question(
            "Which is a good practice for custom exceptions?",
            List.of(
                "Always extend Throwable directly",
                "Provide multiple constructors including cause",
                "Avoid messages to keep them short",
                "Use checked exceptions for all errors"),
            1));

    quiz.addQuestion(
        new Question(
            "Which JUnit 5 lifecycle annotations pair correctly?",
            List.of(
                "@BeforeEach / @AfterEach",
                "@BeforeEach / @AfterAll",
                "@BeforeAll / @AfterEach",
                "@Setup / @Teardown"),
            0));

    return quiz;
  }
}
