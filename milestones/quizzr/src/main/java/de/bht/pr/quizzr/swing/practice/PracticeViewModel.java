package de.bht.pr.quizzr.swing.practice;

import de.bht.pr.quizzr.swing.model.Question;
import de.bht.pr.quizzr.swing.model.Quiz;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

public class PracticeViewModel {
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  private Quiz quiz;
  private List<Question> practiceQuestions;
  private int currentIndex;
  private Map<UUID, Set<UUID>> userAnswers;
  private Map<UUID, Boolean> results;
  private boolean sessionActive;
  private boolean currentQuestionSubmitted;

  // Settings
  private boolean shuffleQuestions;
  private Integer questionLimit;

  public PracticeViewModel() {
    this.userAnswers = new HashMap<>();
    this.results = new HashMap<>();
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  public void startSession(Quiz quiz, boolean shuffle, Integer limit) {
    this.quiz = quiz;
    this.shuffleQuestions = shuffle;
    this.questionLimit = limit;
    this.currentIndex = 0;
    this.sessionActive = true;
    this.currentQuestionSubmitted = false;
    this.userAnswers.clear();
    this.results.clear();

    this.practiceQuestions = new ArrayList<>(quiz.getQuestions());
    if (shuffle) {
      Collections.shuffle(practiceQuestions);
    }
    if (limit != null && limit < practiceQuestions.size()) {
      practiceQuestions = practiceQuestions.subList(0, limit);
    }

    pcs.firePropertyChange("sessionStarted", false, true);
    pcs.firePropertyChange("currentQuestion", null, getCurrentQuestion());
  }

  public Question getCurrentQuestion() {
    if (!sessionActive || practiceQuestions == null || currentIndex >= practiceQuestions.size()) {
      return null;
    }
    return practiceQuestions.get(currentIndex);
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public int getTotalQuestions() {
    return practiceQuestions != null ? practiceQuestions.size() : 0;
  }

  public void setUserAnswer(UUID questionId, Set<UUID> answerIds) {
    userAnswers.put(questionId, new HashSet<>(answerIds));
  }

  public Set<UUID> getUserAnswer(UUID questionId) {
    return userAnswers.getOrDefault(questionId, new HashSet<>());
  }

  public void submitCurrentAnswer() {
    if (!sessionActive || currentQuestionSubmitted) {
      return;
    }

    Question current = getCurrentQuestion();
    if (current == null) {
      return;
    }

    Set<UUID> userAnswer = userAnswers.getOrDefault(current.getId(), new HashSet<>());
    boolean correct = userAnswer.equals(current.getCorrectAnswerIds());
    results.put(current.getId(), correct);
    currentQuestionSubmitted = true;

    pcs.firePropertyChange("answerSubmitted", false, true);
  }

  public boolean isCurrentQuestionSubmitted() {
    return currentQuestionSubmitted;
  }

  public Boolean isCurrentAnswerCorrect() {
    Question current = getCurrentQuestion();
    if (current == null) {
      return null;
    }
    return results.get(current.getId());
  }

  public void nextQuestion() {
    if (!sessionActive || currentIndex >= practiceQuestions.size() - 1) {
      return;
    }
    currentIndex++;
    currentQuestionSubmitted = false;
    pcs.firePropertyChange("currentQuestion", null, getCurrentQuestion());
  }

  public void previousQuestion() {
    if (!sessionActive || currentIndex <= 0) {
      return;
    }
    currentIndex--;
    currentQuestionSubmitted = results.containsKey(getCurrentQuestion().getId());
    pcs.firePropertyChange("currentQuestion", null, getCurrentQuestion());
  }

  public boolean canGoNext() {
    return sessionActive && currentIndex < practiceQuestions.size() - 1;
  }

  public boolean canGoPrevious() {
    return sessionActive && currentIndex > 0;
  }

  public void finishSession() {
    sessionActive = false;
    pcs.firePropertyChange("sessionFinished", false, true);
  }

  public int getScore() {
    return (int) results.values().stream().filter(b -> b).count();
  }

  public Map<UUID, Boolean> getResults() {
    return new HashMap<>(results);
  }

  public List<Question> getPracticeQuestions() {
    return practiceQuestions;
  }

  public boolean isSessionActive() {
    return sessionActive;
  }
}
