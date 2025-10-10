package de.bht.pr.quizzr.swing.editor;

import de.bht.pr.quizzr.swing.model.Answer;
import de.bht.pr.quizzr.swing.model.Question;
import de.bht.pr.quizzr.swing.model.Quiz;
import de.bht.pr.quizzr.swing.util.Result;
import de.bht.pr.quizzr.swing.validation.ValidationService;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;

public class EditorViewModel {
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private final ValidationService validationService;

  private Quiz currentQuiz;

  public EditorViewModel(ValidationService validationService) {
    this.validationService = validationService;
  }

  public ValidationService getValidationService() {
    return validationService;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  public Quiz getCurrentQuiz() {
    return currentQuiz;
  }

  public void setCurrentQuiz(Quiz quiz) {
    Quiz old = this.currentQuiz;
    this.currentQuiz = quiz;
    pcs.firePropertyChange("currentQuiz", old, quiz);
  }

  public Question addQuestion() {
    if (currentQuiz == null) {
      return null;
    }
    Question question = new Question();
    question.setText("");
    currentQuiz.getQuestions().add(question);
    pcs.firePropertyChange("questions", null, currentQuiz.getQuestions());
    return question;
  }

  public void removeQuestion(Question question) {
    if (currentQuiz == null) {
      return;
    }
    currentQuiz.getQuestions().remove(question);
    pcs.firePropertyChange("questions", null, currentQuiz.getQuestions());
  }

  public void moveQuestionUp(Question question) {
    if (currentQuiz == null) {
      return;
    }
    int index = currentQuiz.getQuestions().indexOf(question);
    if (index > 0) {
      Collections.swap(currentQuiz.getQuestions(), index, index - 1);
      pcs.firePropertyChange("questions", null, currentQuiz.getQuestions());
    }
  }

  public void moveQuestionDown(Question question) {
    if (currentQuiz == null) {
      return;
    }
    int index = currentQuiz.getQuestions().indexOf(question);
    if (index >= 0 && index < currentQuiz.getQuestions().size() - 1) {
      Collections.swap(currentQuiz.getQuestions(), index, index + 1);
      pcs.firePropertyChange("questions", null, currentQuiz.getQuestions());
    }
  }

  public Result<Void, String> validateAndSaveQuestion(Question question) {
    Result<Void, String> validation = validationService.validateQuestion(question);
    if (validation.isFailure()) {
      return validation;
    }
    pcs.firePropertyChange("questions", null, currentQuiz.getQuestions());
    return Result.success(null);
  }

  public Answer addAnswer(Question question) {
    Answer answer = new Answer("");
    question.getAnswers().add(answer);
    return answer;
  }

  public void removeAnswer(Question question, Answer answer) {
    question.getAnswers().remove(answer);
    question.getCorrectAnswerIds().remove(answer.getId());
  }
}
