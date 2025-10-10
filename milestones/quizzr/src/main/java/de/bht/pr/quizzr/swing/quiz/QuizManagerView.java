package de.bht.pr.quizzr.swing.quiz;

import de.bht.pr.quizzr.swing.app.AppView;
import de.bht.pr.quizzr.swing.question.QuizEditorViewModel;
import de.bht.pr.quizzr.swing.model.Quiz;
import de.bht.pr.quizzr.swing.util.Result;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class QuizManagerView extends JPanel implements PropertyChangeListener {
  private final QuizManagerViewModel viewModel;
  private final QuizEditorViewModel editorViewModel;

  private JTextField searchField;
  private JList<Quiz> quizList;
  private DefaultListModel<Quiz> listModel;
  private JButton createButton;
  private JButton renameButton;
  private JButton duplicateButton;
  private JButton deleteButton;
  private JButton editButton;
  private JButton practiceButton;

  public QuizManagerView(QuizManagerViewModel viewModel, QuizEditorViewModel editorViewModel) {
    this.viewModel = viewModel;
    this.editorViewModel = editorViewModel;
    this.viewModel.addPropertyChangeListener(this);

    initializeUI();
    refreshList();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));

    // Search panel
    JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
    searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
    searchField = new JTextField();
    searchField
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              public void changedUpdate(DocumentEvent e) {
                updateSearch();
              }

              public void removeUpdate(DocumentEvent e) {
                updateSearch();
              }

              public void insertUpdate(DocumentEvent e) {
                updateSearch();
              }
            });
    searchPanel.add(searchField, BorderLayout.CENTER);

    // Quiz list
    listModel = new DefaultListModel<>();
    quizList = new JList<>(listModel);
    quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    quizList.setCellRenderer(new QuizListCellRenderer());
    quizList.addListSelectionListener(
        e -> {
          if (!e.getValueIsAdjusting()) {
            viewModel.setSelectedQuiz(quizList.getSelectedValue());
          }
        });
    quizList.addMouseListener(
        new java.awt.event.MouseAdapter() {
          @Override
          public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2) {
              int index = quizList.locationToIndex(e.getPoint());
              if (index >= 0) {
                editQuiz();
              }
            }
          }
        });
    JScrollPane scrollPane = new JScrollPane(quizList);

    // Button panel
    JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
    createButton = new JButton("Create Quiz");
    createButton.addActionListener(e -> createQuiz());

    renameButton = new JButton("Rename Quiz");
    renameButton.addActionListener(e -> renameQuiz());

    duplicateButton = new JButton("Duplicate Quiz");
    duplicateButton.addActionListener(e -> duplicateQuiz());

    deleteButton = new JButton("Delete Quiz");
    deleteButton.addActionListener(e -> deleteQuiz());

    editButton = new JButton("Edit Quiz");
    editButton.addActionListener(e -> editQuiz());

    practiceButton = new JButton("Start Practice");
    practiceButton.addActionListener(e -> startPractice());

    buttonPanel.add(createButton);
    buttonPanel.add(renameButton);
    buttonPanel.add(duplicateButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(editButton);
    buttonPanel.add(practiceButton);

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(buttonPanel, BorderLayout.NORTH);

    add(searchPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(rightPanel, BorderLayout.EAST);

    updateButtonStates();
  }

  private void updateSearch() {
    viewModel.setSearchQuery(searchField.getText());
  }

  private void createQuiz() {
    String name =
        JOptionPane.showInputDialog(
            this, "Enter quiz name:", "Create Quiz", JOptionPane.PLAIN_MESSAGE);
    if (name != null && !name.trim().isEmpty()) {
      Result<Quiz, String> result = viewModel.createQuiz(name.trim());
      if (result.isFailure()) {
        JOptionPane.showMessageDialog(
            this,
            result.getError().orElse("Failed to create quiz"),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      } else {
        refreshList();
      }
    }
  }

  private void renameQuiz() {
    Quiz selected = viewModel.getSelectedQuiz();
    if (selected == null) {
      return;
    }
    String newName = JOptionPane.showInputDialog(this, "Enter new name:", selected.getName());
    if (newName != null && !newName.trim().isEmpty()) {
      Result<Void, String> result = viewModel.renameQuiz(selected, newName.trim());
      if (result.isFailure()) {
        JOptionPane.showMessageDialog(
            this,
            result.getError().orElse("Failed to rename quiz"),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      } else {
        refreshList();
      }
    }
  }

  private void duplicateQuiz() {
    Quiz selected = viewModel.getSelectedQuiz();
    if (selected == null) {
      return;
    }
    Result<Quiz, String> result = viewModel.duplicateQuiz(selected);
    if (result.isFailure()) {
      JOptionPane.showMessageDialog(
          this,
          result.getError().orElse("Failed to duplicate quiz"),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
    refreshList();
  }

  private void deleteQuiz() {
    Quiz selected = viewModel.getSelectedQuiz();
    if (selected == null) {
      return;
    }
    int choice =
        JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete quiz '" + selected.getName() + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
    if (choice == JOptionPane.YES_OPTION) {
      viewModel.deleteQuiz(selected);
      refreshList();
    }
  }

  private void editQuiz() {
    Quiz selected = viewModel.getSelectedQuiz();
    if (selected == null) {
      return;
    }
    editorViewModel.setCurrentQuiz(selected);
    SwingUtilities.invokeLater(
        () -> {
          AppView frame = (AppView) SwingUtilities.getWindowAncestor(this);
          if (frame != null) {
            frame.switchToEditorTab();
          }
        });
  }

  private void refreshList() {
    listModel.clear();
    for (Quiz quiz : viewModel.getFilteredQuizzes()) {
      listModel.addElement(quiz);
    }
    updateButtonStates();
  }

  private void startPractice() {
    Quiz selected = viewModel.getSelectedQuiz();
    if (selected == null) {
      return;
    }
    if (selected.getQuestions().isEmpty()) {
      JOptionPane.showMessageDialog(
          this,
          "This quiz has no questions. Please add questions before practicing.",
          "No Questions",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    SwingUtilities.invokeLater(
        () -> {
          AppView frame = (AppView) SwingUtilities.getWindowAncestor(this);
          if (frame != null) {
            frame.startPracticeWithQuiz(selected);
          }
        });
  }

  private void updateButtonStates() {
    boolean hasSelection = viewModel.getSelectedQuiz() != null;
    renameButton.setEnabled(hasSelection);
    duplicateButton.setEnabled(hasSelection);
    deleteButton.setEnabled(hasSelection);
    editButton.setEnabled(hasSelection);
    practiceButton.setEnabled(hasSelection);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(
        () -> {
          if ("filteredQuizzes".equals(evt.getPropertyName())) {
            refreshList();
          } else if ("selectedQuiz".equals(evt.getPropertyName())) {
            updateButtonStates();
          }
        });
  }

  public void createQuizAction() {
    createQuiz();
  }

  public void deleteQuizAction() {
    deleteQuiz();
  }

  private static class QuizListCellRenderer extends DefaultListCellRenderer {
    @Override
    public java.awt.Component getListCellRendererComponent(
        JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof Quiz) {
        Quiz quiz = (Quiz) value;
        setText(quiz.getName() + " (" + quiz.getQuestions().size() + " questions)");
      }
      return this;
    }
  }
}
