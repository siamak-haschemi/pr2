package de.bht.pr.quizzr.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class QuizTest {
  @Test
  void quizRequiresTitle() {
    assertThrows(IllegalArgumentException.class, () -> new Quiz(" "));
  }

  @Test
  void addQuestion() {
    Quiz quiz = new Quiz("Title");
    Question q = new Question("Q?", List.of("A", "B"), 0);
    quiz.addQuestion(q);
    assertEquals(1, quiz.getQuestions().size());
  }
}
