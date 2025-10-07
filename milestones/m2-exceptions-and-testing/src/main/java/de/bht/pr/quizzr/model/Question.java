package de.bht.pr.quizzr.model;

import de.bht.pr.quizzr.model.exception.InvalidQuestionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Question {
  private final String text;
  private final List<String> options;
  private final int correctIndex; // 0-based

  public Question(String text, List<String> options, int correctIndex) {
    if (text == null || text.isBlank()) {
      throw new InvalidQuestionException("Question text must not be empty");
    }
    if (options == null || options.isEmpty()) {
      throw new InvalidQuestionException("Question must have at least one option");
    }
    if (correctIndex < 0 || correctIndex >= options.size()) {
      throw new InvalidQuestionException("Correct index out of bounds");
    }
    this.text = text.trim();
    this.options = new ArrayList<>(options);
    this.correctIndex = correctIndex;
  }

  public String getText() {
    return text;
  }

  public List<String> getOptions() {
    return Collections.unmodifiableList(options);
  }

  public int getCorrectIndex() {
    return correctIndex;
  }

  public String getCorrectAnswer() {
    return options.get(correctIndex);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Question)) return false;
    Question q = (Question) o;
    return correctIndex == q.correctIndex && text.equals(q.text) && options.equals(q.options);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, options, correctIndex);
  }
}
