package de.bht.pr.quizzr.swing.home;

import de.bht.pr.quizzr.swing.autosave.AutosaveService;
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

public class HomeViewModel {
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private final QuizCollection collection;
  private final AutosaveService autosaveService;
  private final ValidationService validationService;

  private List<Quiz> filteredQuizzes;
  private String searchQuery = "";
  private Quiz selectedQuiz;

  public HomeViewModel(
      QuizCollection collection,
      AutosaveService autosaveService,
      ValidationService validationService) {
    this.collection = collection;
    this.autosaveService = autosaveService;
    this.validationService = validationService;
    this.filteredQuizzes = new ArrayList<>(collection.getQuizzes());
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  public List<Quiz> getFilteredQuizzes() {
    return filteredQuizzes;
  }

  public String getSearchQuery() {
    return searchQuery;
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
    autosaveService.scheduleAutosave(collection);
    return Result.success(quiz);
  }

  public Result<Void, String> renameQuiz(Quiz quiz, String newName) {
    Result<Void, String> validation = validationService.validateQuizName(newName, collection, quiz);
    if (validation.isFailure()) {
      return validation;
    }

    quiz.setName(newName);
    updateFilter();
    autosaveService.scheduleAutosave(collection);
    return Result.success(null);
  }

  public Quiz duplicateQuiz(Quiz original) {
    Quiz duplicate = new Quiz(original.getName() + " (copy)");
    duplicate.setQuestions(new ArrayList<>(original.getQuestions()));
    collection.getQuizzes().add(duplicate);
    updateFilter();
    autosaveService.scheduleAutosave(collection);
    return duplicate;
  }

  public void deleteQuiz(Quiz quiz) {
    collection.getQuizzes().remove(quiz);
    if (selectedQuiz != null && selectedQuiz.getId().equals(quiz.getId())) {
      setSelectedQuiz(null);
    }
    updateFilter();
    autosaveService.scheduleAutosave(collection);
  }

  public void saveNow() throws IOException {
    autosaveService.saveNow(collection);
  }

  public void refresh() {
    updateFilter();
  }
}
