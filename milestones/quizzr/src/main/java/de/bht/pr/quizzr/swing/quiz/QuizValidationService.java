package de.bht.pr.quizzr.swing.quiz;

import de.bht.pr.quizzr.swing.model.Quiz;
import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.util.Result;
import java.util.Locale;

public class QuizValidationService {

  public Result<Void, String> validateQuizName(
      String name, QuizCollection collection, Quiz currentQuiz) {
    if (name == null || name.trim().isEmpty()) {
      return Result.failure("Quiz name cannot be empty");
    }

    String normalizedName = name.trim().toLowerCase(Locale.ROOT);
    for (Quiz quiz : collection.getQuizzes()) {
      if (currentQuiz != null && quiz.getId().equals(currentQuiz.getId())) {
        continue;
      }
      if (quiz.getName() != null
          && quiz.getName().trim().toLowerCase(Locale.ROOT).equals(normalizedName)) {
        return Result.failure("Quiz name already exists (case-insensitive)");
      }
    }

    return Result.success(null);
  }
}
