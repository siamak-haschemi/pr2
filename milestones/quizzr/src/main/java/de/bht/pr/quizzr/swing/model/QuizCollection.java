package de.bht.pr.quizzr.swing.model;

import java.util.ArrayList;
import java.util.List;

public class QuizCollection {
  private List<Quiz> quizzes;

  public QuizCollection() {
    this.quizzes = new ArrayList<>();
  }

  public List<Quiz> getQuizzes() {
    return quizzes;
  }

  public void setQuizzes(List<Quiz> quizzes) {
    this.quizzes = quizzes;
  }
}
