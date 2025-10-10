package de.bht.pr.quizzr.swing.question;

import de.bht.pr.quizzr.swing.model.Question;
import de.bht.pr.quizzr.swing.model.QuestionType;
import de.bht.pr.quizzr.swing.util.Result;

public class QuestionValidationService {

  public Result<Void, String> validateQuestion(Question question) {
    if (question.getText() == null || question.getText().trim().isEmpty()) {
      return Result.failure("Question text cannot be empty");
    }

    if (question.getAnswers().size() < 2) {
      return Result.failure("Question must have at least 2 answers");
    }

    if (question.getType() == QuestionType.SINGLE) {
      if (question.getCorrectAnswerIds().size() != 1) {
        return Result.failure("Single-answer question must have exactly one correct answer");
      }
    } else if (question.getType() == QuestionType.MULTIPLE) {
      if (question.getCorrectAnswerIds().isEmpty()) {
        return Result.failure("Multiple-answer question must have at least one correct answer");
      }
    }

    return Result.success(null);
  }

  public Result<Void, String> validateAnswerText(String text) {
    if (text == null || text.trim().isEmpty()) {
      return Result.failure("Answer text cannot be empty");
    }
    return Result.success(null);
  }
}
