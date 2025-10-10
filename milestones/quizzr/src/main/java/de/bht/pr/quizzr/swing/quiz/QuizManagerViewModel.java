package de.bht.pr.quizzr.swing.quiz;

import de.bht.pr.quizzr.swing.model.Quiz;
import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.util.Result;
import de.bht.pr.quizzr.swing.validation.ValidationService;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuizManagerViewModel {
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private final QuizCollection collection;
  private final QuizzRepository repository;
  private final ValidationService validationService;

  private List<Quiz> filteredQuizzes;
  private String searchQuery = "";
  private Quiz selectedQuiz;

  public QuizManagerViewModel(QuizzRepository repository, ValidationService validationService) {
    this.repository = repository;
    this.validationService = validationService;
    this.collection = repository.get();
    this.filteredQuizzes = new ArrayList<>(collection.getQuizzes());
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  public List<Quiz> getFilteredQuizzes() {
    return filteredQuizzes;
  }

  public void setSearchQuery(String query) {
    String old = this.searchQuery;
    this.searchQuery = query;
    updateFilter();
    pcs.firePropertyChange("searchQuery", old, query);
  }

  public Quiz getSelectedQuiz() {
    return selectedQuiz;
  }

  public void setSelectedQuiz(Quiz quiz) {
    Quiz old = this.selectedQuiz;
    this.selectedQuiz = quiz;
    pcs.firePropertyChange("selectedQuiz", old, quiz);
  }

  private void updateFilter() {
    List<Quiz> old = this.filteredQuizzes;
    if (searchQuery == null || searchQuery.trim().isEmpty()) {
      this.filteredQuizzes = new ArrayList<>(collection.getQuizzes());
    } else {
      String query = searchQuery.toLowerCase();
      this.filteredQuizzes =
          collection.getQuizzes().stream()
              .filter(q -> q.getName() != null && q.getName().toLowerCase().contains(query))
              .collect(Collectors.toList());
    }
    pcs.firePropertyChange("filteredQuizzes", old, filteredQuizzes);
  }

  public Result<Quiz, String> createQuiz(String name) {
    Result<Void, String> validation = validationService.validateQuizName(name, collection, null);
    if (validation.isFailure()) {
      return Result.failure(validation.getError().orElse("Invalid quiz name"));
    }

    Quiz quiz = new Quiz(name);
    collection.getQuizzes().add(quiz);
    updateFilter();
    return Result.success(quiz);
  }

  public Result<Void, String> renameQuiz(Quiz quiz, String newName) {
    Result<Void, String> validation = validationService.validateQuizName(newName, collection, quiz);
    if (validation.isFailure()) {
      return validation;
    }

    quiz.setName(newName);
    updateFilter();
    return Result.success(null);
  }

  public Result<Quiz, String> duplicateQuiz(Quiz original) {
    String baseName = original.getName() + " (copy)";
    String newName = baseName;
    int counter = 1;

    // Find unique name
    while (validationService.validateQuizName(newName, collection, null).isFailure()) {
      newName = baseName + " " + counter;
      counter++;
    }

    Quiz duplicate = new Quiz(newName);
    // Deep copy all questions
    for (de.bht.pr.quizzr.swing.model.Question question : original.getQuestions()) {
      duplicate.getQuestions().add(question.deepCopy());
    }

    collection.getQuizzes().add(duplicate);
    updateFilter();
    return Result.success(duplicate);
  }

  public void deleteQuiz(Quiz quiz) {
    collection.getQuizzes().remove(quiz);
    if (selectedQuiz != null && selectedQuiz.getId().equals(quiz.getId())) {
      setSelectedQuiz(null);
    }
    updateFilter();
  }

  public void saveNow() throws IOException {
    repository.save(collection);
  }

  public void refresh() {
    updateFilter();
  }
}
