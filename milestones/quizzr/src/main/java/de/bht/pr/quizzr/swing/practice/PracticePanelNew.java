package de.bht.pr.quizzr.swing.practice;

import de.bht.pr.quizzr.swing.app.MainView;
import de.bht.pr.quizzr.swing.model.Answer;
import de.bht.pr.quizzr.swing.model.Question;
import de.bht.pr.quizzr.swing.model.QuestionType;
import de.bht.pr.quizzr.swing.model.Quiz;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.swing.*;

public class PracticePanelNew extends JPanel implements PropertyChangeListener {
  private final PracticeViewModel viewModel;

  private JLabel welcomeLabel;
  private JPanel contentPanel;
  private CardLayout cardLayout;

  // Question view components
  private JLabel questionNumberLabel;
  private JTextArea questionTextArea;
  private JPanel answersPanel;
  private Map<UUID, JCheckBox> answerCheckBoxes;
  private Map<UUID, JRadioButton> answerRadioButtons;
  private ButtonGroup radioButtonGroup;
  private JButton submitButton;
  private JButton nextButton;
  private JButton previousButton;
  private JButton finishButton;
  private JPanel feedbackPanel;
  private JLabel feedbackLabel;

  public PracticePanelNew(PracticeViewModel viewModel) {
    this.viewModel = viewModel;
    this.viewModel.addPropertyChangeListener(this);
    this.answerCheckBoxes = new HashMap<>();
    this.answerRadioButtons = new HashMap<>();

    initializeUI();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));

    // Card layout for welcome vs practice views
    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);

    // Welcome panel
    JPanel welcomePanel = new JPanel(new BorderLayout());
    welcomeLabel =
        new JLabel(
            "Select a quiz from the Home tab and click 'Start Practice'", SwingConstants.CENTER);
    welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(14f));
    welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

    // Practice panel
    JPanel practicePanel = createPracticePanel();

    contentPanel.add(welcomePanel, "WELCOME");
    contentPanel.add(practicePanel, "PRACTICE");

    add(contentPanel, BorderLayout.CENTER);
    cardLayout.show(contentPanel, "WELCOME");
  }

  private JPanel createPracticePanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Top panel with question number
    questionNumberLabel = new JLabel("Question 1 of 10", SwingConstants.CENTER);
    questionNumberLabel.setFont(questionNumberLabel.getFont().deriveFont(Font.BOLD, 14f));

    // Question text
    questionTextArea = new JTextArea(3, 40);
    questionTextArea.setLineWrap(true);
    questionTextArea.setWrapStyleWord(true);
    questionTextArea.setEditable(false);
    questionTextArea.setFont(questionTextArea.getFont().deriveFont(16f));
    questionTextArea.setBackground(getBackground());
    JScrollPane questionScroll = new JScrollPane(questionTextArea);
    questionScroll.setBorder(BorderFactory.createTitledBorder("Question"));

    // Answers panel
    answersPanel = new JPanel();
    answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
    JScrollPane answersScroll = new JScrollPane(answersPanel);
    answersScroll.setBorder(BorderFactory.createTitledBorder("Answers"));

    // Feedback panel
    feedbackPanel = new JPanel(new BorderLayout());
    feedbackPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    feedbackLabel = new JLabel("", SwingConstants.CENTER);
    feedbackLabel.setFont(feedbackLabel.getFont().deriveFont(Font.BOLD, 14f));
    feedbackPanel.add(feedbackLabel, BorderLayout.CENTER);
    feedbackPanel.setVisible(false);

    // Center panel
    JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
    centerPanel.add(questionScroll, BorderLayout.NORTH);
    centerPanel.add(answersScroll, BorderLayout.CENTER);
    centerPanel.add(feedbackPanel, BorderLayout.SOUTH);

    // Navigation buttons
    JPanel navPanel = new JPanel(new BorderLayout());
    JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    previousButton = new JButton("← Previous");
    previousButton.addActionListener(e -> previousQuestion());

    nextButton = new JButton("Next →");
    nextButton.addActionListener(e -> nextQuestion());

    submitButton = new JButton("Submit Answer");
    submitButton.addActionListener(e -> submitAnswer());

    finishButton = new JButton("Finish Practice");
    finishButton.addActionListener(e -> finishPractice());

    leftButtons.add(previousButton);
    rightButtons.add(submitButton);
    rightButtons.add(nextButton);
    rightButtons.add(finishButton);

    navPanel.add(leftButtons, BorderLayout.WEST);
    navPanel.add(rightButtons, BorderLayout.EAST);

    panel.add(questionNumberLabel, BorderLayout.NORTH);
    panel.add(centerPanel, BorderLayout.CENTER);
    panel.add(navPanel, BorderLayout.SOUTH);

    return panel;
  }

  public void startPracticeSession(Quiz quiz) {
    if (quiz == null || quiz.getQuestions().isEmpty()) {
      welcomeLabel.setText("Cannot start practice: Quiz has no questions");
      return;
    }

    // Show settings dialog
    MainView frame = (MainView) SwingUtilities.getWindowAncestor(this);
    PracticeSettingsDialog dialog = new PracticeSettingsDialog(frame, quiz);
    dialog.setVisible(true);

    if (!dialog.isConfirmed()) {
      return;
    }

    // Start session
    viewModel.startSession(quiz, dialog.isShuffle(), dialog.getLimit());
    cardLayout.show(contentPanel, "PRACTICE");
    updateQuestionDisplay();
  }

  private void updateQuestionDisplay() {
    Question question = viewModel.getCurrentQuestion();
    if (question == null) {
      return;
    }

    // Update question number
    int current = viewModel.getCurrentIndex() + 1;
    int total = viewModel.getTotalQuestions();
    questionNumberLabel.setText("Question " + current + " of " + total);

    // Update question text
    questionTextArea.setText(question.getText());

    // Update answers
    answersPanel.removeAll();
    answerCheckBoxes.clear();
    answerRadioButtons.clear();

    Set<UUID> userAnswers = viewModel.getUserAnswer(question.getId());

    if (question.getType() == QuestionType.SINGLE) {
      radioButtonGroup = new ButtonGroup();
      for (Answer answer : question.getAnswers()) {
        JRadioButton radio = new JRadioButton(answer.getText());
        radio.setSelected(userAnswers.contains(answer.getId()));
        radio.setEnabled(!viewModel.isCurrentQuestionSubmitted());
        answerRadioButtons.put(answer.getId(), radio);
        radioButtonGroup.add(radio);
        answersPanel.add(radio);
      }
    } else {
      for (Answer answer : question.getAnswers()) {
        JCheckBox checkBox = new JCheckBox(answer.getText());
        checkBox.setSelected(userAnswers.contains(answer.getId()));
        checkBox.setEnabled(!viewModel.isCurrentQuestionSubmitted());
        answerCheckBoxes.put(answer.getId(), checkBox);
        answersPanel.add(checkBox);
      }
    }

    // Update feedback
    if (viewModel.isCurrentQuestionSubmitted()) {
      Boolean correct = viewModel.isCurrentAnswerCorrect();
      if (correct != null) {
        showFeedback(correct, question);
      }
    } else {
      feedbackPanel.setVisible(false);
    }

    // Update button states
    updateButtonStates();

    answersPanel.revalidate();
    answersPanel.repaint();
  }

  private void submitAnswer() {
    Question question = viewModel.getCurrentQuestion();
    if (question == null) {
      return;
    }

    // Collect user's answer
    Set<UUID> userAnswer = new HashSet<>();
    if (question.getType() == QuestionType.SINGLE) {
      for (Map.Entry<UUID, JRadioButton> entry : answerRadioButtons.entrySet()) {
        if (entry.getValue().isSelected()) {
          userAnswer.add(entry.getKey());
        }
      }
    } else {
      for (Map.Entry<UUID, JCheckBox> entry : answerCheckBoxes.entrySet()) {
        if (entry.getValue().isSelected()) {
          userAnswer.add(entry.getKey());
        }
      }
    }

    viewModel.setUserAnswer(question.getId(), userAnswer);
    viewModel.submitCurrentAnswer();

    // Disable answer controls
    for (JRadioButton radio : answerRadioButtons.values()) {
      radio.setEnabled(false);
    }
    for (JCheckBox checkBox : answerCheckBoxes.values()) {
      checkBox.setEnabled(false);
    }

    Boolean correct = viewModel.isCurrentAnswerCorrect();
    if (correct != null) {
      showFeedback(correct, question);
    }

    updateButtonStates();
  }

  private void showFeedback(boolean correct, Question question) {
    feedbackPanel.setVisible(true);
    if (correct) {
      feedbackLabel.setText("✓ Correct!");
      feedbackLabel.setForeground(new Color(0, 150, 0));
    } else {
      StringBuilder correctAnswers = new StringBuilder("✗ Incorrect. Correct answer(s): ");
      boolean first = true;
      for (Answer answer : question.getAnswers()) {
        if (question.getCorrectAnswerIds().contains(answer.getId())) {
          if (!first) correctAnswers.append(", ");
          correctAnswers.append(answer.getText());
          first = false;
        }
      }
      feedbackLabel.setText(correctAnswers.toString());
      feedbackLabel.setForeground(new Color(200, 0, 0));
    }
  }

  private void previousQuestion() {
    saveCurrentAnswer();
    viewModel.previousQuestion();
    updateQuestionDisplay();
  }

  private void nextQuestion() {
    saveCurrentAnswer();
    viewModel.nextQuestion();
    updateQuestionDisplay();
  }

  private void saveCurrentAnswer() {
    Question question = viewModel.getCurrentQuestion();
    if (question == null || viewModel.isCurrentQuestionSubmitted()) {
      return;
    }

    Set<UUID> userAnswer = new HashSet<>();
    if (question.getType() == QuestionType.SINGLE) {
      for (Map.Entry<UUID, JRadioButton> entry : answerRadioButtons.entrySet()) {
        if (entry.getValue().isSelected()) {
          userAnswer.add(entry.getKey());
        }
      }
    } else {
      for (Map.Entry<UUID, JCheckBox> entry : answerCheckBoxes.entrySet()) {
        if (entry.getValue().isSelected()) {
          userAnswer.add(entry.getKey());
        }
      }
    }

    viewModel.setUserAnswer(question.getId(), userAnswer);
  }

  private void finishPractice() {
    int choice =
        JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to finish this practice session?",
            "Finish Practice",
            JOptionPane.YES_NO_OPTION);

    if (choice == JOptionPane.YES_OPTION) {
      viewModel.finishSession();
      showSummary();
    }
  }

  private void showSummary() {
    MainView frame = (MainView) SwingUtilities.getWindowAncestor(this);
    if (frame != null) {
      frame.showPracticeSummary(
          viewModel.getScore(),
          viewModel.getTotalQuestions(),
          viewModel.getPracticeQuestions(),
          viewModel.getResults());
    }
    cardLayout.show(contentPanel, "WELCOME");
  }

  private void updateButtonStates() {
    previousButton.setEnabled(viewModel.canGoPrevious());
    nextButton.setEnabled(viewModel.canGoNext());
    submitButton.setEnabled(viewModel.isSessionActive() && !viewModel.isCurrentQuestionSubmitted());
    finishButton.setEnabled(viewModel.isSessionActive());
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(
        () -> {
          if ("sessionStarted".equals(evt.getPropertyName())) {
            updateQuestionDisplay();
          } else if ("currentQuestion".equals(evt.getPropertyName())) {
            updateQuestionDisplay();
          } else if ("answerSubmitted".equals(evt.getPropertyName())) {
            updateButtonStates();
          }
        });
  }
}
