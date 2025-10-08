package de.bht.pr.quizzr.swing.editor;

import de.bht.pr.quizzr.swing.home.HomeViewModel;
import de.bht.pr.quizzr.swing.model.Quiz;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class QuizEditorPanel extends JPanel implements PropertyChangeListener {
  private final EditorViewModel viewModel;
  private final HomeViewModel homeViewModel;

  private JLabel titleLabel;
  private JTextField nameField;
  private JLabel questionCountLabel;
  private JButton saveButton;
  private JButton cancelButton;

  public QuizEditorPanel(EditorViewModel viewModel, HomeViewModel homeViewModel) {
    this.viewModel = viewModel;
    this.homeViewModel = homeViewModel;
    this.viewModel.addPropertyChangeListener(this);

    initializeUI();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Title
    titleLabel = new JLabel("Quiz Properties");
    titleLabel.setFont(titleLabel.getFont().deriveFont(18f));

    // Form panel
    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;

    // Quiz name
    gbc.gridx = 0;
    gbc.gridy = 0;
    formPanel.add(new JLabel("Quiz Name:"), gbc);

    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    nameField = new JTextField(30);
    formPanel.add(nameField, gbc);

    // Question count (read-only)
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    formPanel.add(new JLabel("Number of Questions:"), gbc);

    gbc.gridx = 1;
    questionCountLabel = new JLabel("0");
    formPanel.add(questionCountLabel, gbc);

    // Info label
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    JLabel infoLabel =
        new JLabel("<html><i>Switch to the Questions tab to add and edit questions</i></html>");
    infoLabel.setForeground(java.awt.Color.GRAY);
    formPanel.add(infoLabel, gbc);

    // Buttons
    JPanel buttonPanel = new JPanel();
    saveButton = new JButton("Save Changes");
    saveButton.addActionListener(e -> saveChanges());

    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> cancelChanges());

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    // Layout
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(titleLabel, BorderLayout.WEST);

    add(topPanel, BorderLayout.NORTH);
    add(formPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);

    updateUI();
  }

  private void saveChanges() {
    Quiz quiz = viewModel.getCurrentQuiz();
    if (quiz == null) {
      return;
    }

    String newName = nameField.getText().trim();
    if (newName.isEmpty()) {
      JOptionPane.showMessageDialog(
          this, "Quiz name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    de.bht.pr.quizzr.swing.util.Result<Void, String> result =
        homeViewModel.renameQuiz(quiz, newName);
    if (result.isFailure()) {
      JOptionPane.showMessageDialog(
          this,
          result.getError().orElse("Failed to save changes"),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(
          this, "Quiz updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
      updateQuizUI();
    }
  }

  private void cancelChanges() {
    updateUI();
  }

  private void updateQuizUI() {
    Quiz quiz = viewModel.getCurrentQuiz();
    if (quiz != null) {
      titleLabel.setText("Quiz Properties: " + quiz.getName());
      nameField.setText(quiz.getName());
      questionCountLabel.setText(String.valueOf(quiz.getQuestions().size()));
      saveButton.setEnabled(true);
      cancelButton.setEnabled(true);
    } else {
      titleLabel.setText("Quiz Properties");
      nameField.setText("");
      questionCountLabel.setText("0");
      saveButton.setEnabled(false);
      cancelButton.setEnabled(false);
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(
        () -> {
          if ("currentQuiz".equals(evt.getPropertyName())
              || "questions".equals(evt.getPropertyName())) {
            updateQuizUI();
          }
        });
  }
}
