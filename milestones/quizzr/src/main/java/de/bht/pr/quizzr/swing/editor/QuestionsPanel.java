package de.bht.pr.quizzr.swing.editor;

import de.bht.pr.quizzr.swing.app.AppView;
import de.bht.pr.quizzr.swing.model.Question;
import de.bht.pr.quizzr.swing.model.Quiz;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class QuestionsPanel extends JPanel implements PropertyChangeListener {
  private final QuizEditorViewModel viewModel;

  private JLabel quizNameLabel;
  private JList<Question> questionList;
  private DefaultListModel<Question> listModel;
  private JButton addQuestionButton;
  private JButton removeQuestionButton;
  private JButton moveUpButton;
  private JButton moveDownButton;
  private JButton editQuestionButton;

  public QuestionsPanel(QuizEditorViewModel viewModel) {
    this.viewModel = viewModel;
    this.viewModel.addPropertyChangeListener(this);

    initializeUI();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));

    // Quiz name label
    JPanel topPanel = new JPanel(new BorderLayout());
    quizNameLabel = new JLabel("No quiz selected");
    quizNameLabel.setFont(quizNameLabel.getFont().deriveFont(16f));
    topPanel.add(quizNameLabel, BorderLayout.WEST);

    // Question list
    listModel = new DefaultListModel<>();
    questionList = new JList<>(listModel);
    questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    questionList.setCellRenderer(new QuestionListCellRenderer());
    questionList.addListSelectionListener(
        e -> {
          if (!e.getValueIsAdjusting()) {
            updateButtonStates();
          }
        });
    questionList.addMouseListener(
        new java.awt.event.MouseAdapter() {
          @Override
          public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2) {
              int index = questionList.locationToIndex(e.getPoint());
              if (index >= 0) {
                editQuestion();
              }
            }
          }
        });
    JScrollPane scrollPane = new JScrollPane(questionList);

    // Button panel
    JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
    addQuestionButton = new JButton("Add Question");
    addQuestionButton.addActionListener(e -> addQuestion());

    editQuestionButton = new JButton("Edit Question");
    editQuestionButton.addActionListener(e -> editQuestion());

    removeQuestionButton = new JButton("Remove Question");
    removeQuestionButton.addActionListener(e -> removeQuestion());

    moveUpButton = new JButton("Move Up");
    moveUpButton.addActionListener(e -> moveUp());

    moveDownButton = new JButton("Move Down");
    moveDownButton.addActionListener(e -> moveDown());

    buttonPanel.add(addQuestionButton);
    buttonPanel.add(editQuestionButton);
    buttonPanel.add(removeQuestionButton);
    buttonPanel.add(moveUpButton);
    buttonPanel.add(moveDownButton);

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(buttonPanel, BorderLayout.NORTH);

    add(topPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(rightPanel, BorderLayout.EAST);

    updateEditorUI();
  }

  private void addQuestion() {
    if (viewModel.getCurrentQuiz() == null) {
      JOptionPane.showMessageDialog(
          this, "Please select a quiz first", "No Quiz Selected", JOptionPane.WARNING_MESSAGE);
      return;
    }
    Question question = viewModel.addQuestion();
    refreshList();
    // Auto-open editor for new question
    if (question != null) {
      questionList.setSelectedValue(question, true);
      editQuestion();
    }
  }

  private void editQuestion() {
    Question selected = questionList.getSelectedValue();
    if (selected == null) {
      JOptionPane.showMessageDialog(
          this,
          "Please select a question first",
          "No Question Selected",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    AppView frame = (AppView) SwingUtilities.getWindowAncestor(this);
    QuestionEditorDialog dialog = new QuestionEditorDialog(frame, selected, viewModel);
    dialog.setVisible(true);

    if (dialog.isConfirmed()) {
      refreshList();
    }
  }

  private void removeQuestion() {
    Question selected = questionList.getSelectedValue();
    if (selected == null) {
      return;
    }
    int choice =
        JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this question?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
    if (choice == JOptionPane.YES_OPTION) {
      viewModel.removeQuestion(selected);
      refreshList();
    }
  }

  private void moveUp() {
    Question selected = questionList.getSelectedValue();
    if (selected != null) {
      viewModel.moveQuestionUp(selected);
      refreshList();
      questionList.setSelectedValue(selected, true);
    }
  }

  private void moveDown() {
    Question selected = questionList.getSelectedValue();
    if (selected != null) {
      viewModel.moveQuestionDown(selected);
      refreshList();
      questionList.setSelectedValue(selected, true);
    }
  }

  private void refreshList() {
    listModel.clear();
    Quiz quiz = viewModel.getCurrentQuiz();
    if (quiz != null) {
      for (Question question : quiz.getQuestions()) {
        listModel.addElement(question);
      }
    }
    updateButtonStates();
  }

  private void updateEditorUI() {
    Quiz quiz = viewModel.getCurrentQuiz();
    if (quiz != null) {
      quizNameLabel.setText("Questions for: " + quiz.getName());
    } else {
      quizNameLabel.setText("No quiz selected");
    }
    refreshList();
  }

  private void updateButtonStates() {
    Quiz quiz = viewModel.getCurrentQuiz();
    boolean hasQuiz = quiz != null;
    boolean hasSelection = questionList.getSelectedValue() != null;

    addQuestionButton.setEnabled(hasQuiz);
    editQuestionButton.setEnabled(hasSelection);
    removeQuestionButton.setEnabled(hasSelection);
    moveUpButton.setEnabled(hasSelection);
    moveDownButton.setEnabled(hasSelection);
  }

  public void deleteQuestionAction() {
    removeQuestion();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(
        () -> {
          if ("currentQuiz".equals(evt.getPropertyName())) {
            updateEditorUI();
          } else if ("questions".equals(evt.getPropertyName())) {
            refreshList();
          }
        });
  }

  private static class QuestionListCellRenderer extends DefaultListCellRenderer {
    @Override
    public java.awt.Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof Question) {
        Question question = (Question) value;
        String text = question.getText();
        if (text == null || text.isEmpty()) {
          text = "(empty question)";
        }
        setText(
            (index + 1)
                + ". "
                + text
                + " ["
                + question.getType()
                + ", "
                + question.getAnswers().size()
                + " answers]");
      }
      return this;
    }
  }
}
