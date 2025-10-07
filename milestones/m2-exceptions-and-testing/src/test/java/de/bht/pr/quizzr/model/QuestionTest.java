package de.bht.pr.quizzr.model;

import static org.junit.jupiter.api.Assertions.*;

import de.bht.pr.quizzr.model.exception.InvalidQuestionException;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuestionTest {
  @Test
  void validQuestionConstructs() {
    Question q = new Question("Q?", List.of("A", "B"), 1);
    assertEquals("Q?", q.getText());
    assertEquals(2, q.getOptions().size());
    assertEquals("B", q.getCorrectAnswer());
  }

  @Test
  void emptyTextThrows() {
    assertThrows(InvalidQuestionException.class, () -> new Question(" ", List.of("A"), 0));
  }

  @Test
  void noOptionsThrow() {
    assertThrows(InvalidQuestionException.class, () -> new Question("Q", List.of(), 0));
  }

  @Test
  void wrongIndexThrows() {
    assertThrows(InvalidQuestionException.class, () -> new Question("Q", List.of("A"), -1));
    assertThrows(InvalidQuestionException.class, () -> new Question("Q", List.of("A"), 1));
  }
}
