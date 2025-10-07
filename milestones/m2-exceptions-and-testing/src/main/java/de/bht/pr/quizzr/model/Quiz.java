package de.bht.pr.quizzr.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Quiz {
  private final String title;
  private final Instant createdAt;
  private final List<Question> questions = new ArrayList<>();

  public Quiz(String title) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("Quiz title must not be empty");
    }
    this.title = title.trim();
    this.createdAt = Instant.now();
  }

  public String getTitle() {
    return title;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public List<Question> getQuestions() {
    return Collections.unmodifiableList(questions);
  }

  public void addQuestion(Question q) {
    if (q == null) throw new IllegalArgumentException("Question must not be null");
    questions.add(q);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Quiz)) return false;
    Quiz quiz = (Quiz) o;
    return title.equals(quiz.title) && questions.equals(quiz.questions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, questions);
  }
}
