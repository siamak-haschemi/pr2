package de.bht.pr.quizzr.swing.viewmodel;

import de.bht.pr.quizzr.swing.model.Question;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SummaryViewModel {
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  private int score;
  private int total;
  private List<Question> allQuestions;
  private Map<UUID, Boolean> results;

  public SummaryViewModel() {}

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  public void setResults(
      int score, int total, List<Question> questions, Map<UUID, Boolean> results) {
    this.score = score;
    this.total = total;
    this.allQuestions = questions;
    this.results = results;
    pcs.firePropertyChange("results", null, results);
  }

  public int getScore() {
    return score;
  }

  public int getTotal() {
    return total;
  }

  public double getPercentage() {
    if (total == 0) {
      return 0.0;
    }
    return (score * 100.0) / total;
  }

  public List<Question> getAllQuestions() {
    return allQuestions;
  }

  public Map<UUID, Boolean> getResults() {
    return results;
  }

  public List<Question> getIncorrectQuestions() {
    List<Question> incorrect = new ArrayList<>();
    if (allQuestions == null || results == null) {
      return incorrect;
    }
    for (Question q : allQuestions) {
      if (Boolean.FALSE.equals(results.get(q.getId()))) {
        incorrect.add(q);
      }
    }
    return incorrect;
  }
}
