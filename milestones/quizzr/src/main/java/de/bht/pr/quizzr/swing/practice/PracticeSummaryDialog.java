package de.bht.pr.quizzr.swing.practice;

import de.bht.pr.quizzr.swing.app.AppView;
import de.bht.pr.quizzr.swing.model.Question;
import de.bht.pr.quizzr.swing.model.Quiz;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class PracticeSummaryDialog extends JDialog {
  private final int score;
  private final int total;
  private final List<Question> questions;
  private final Map<UUID, Boolean> results;
  private final PracticeViewModel practiceViewModel;

  public PracticeSummaryDialog(
      JFrame parent,
      int score,
      int total,
      List<Question> questions,
      Map<UUID, Boolean> results,
      PracticeViewModel practiceViewModel) {
    super(parent, "Practice Summary", true);
    this.score = score;
    this.total = total;
    this.questions = questions;
    this.results = results;
    this.practiceViewModel = practiceViewModel;

    initializeUI();
    setSize(700, 500);
    setLocationRelativeTo(parent);
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));

    // Score panel
    JPanel scorePanel = new JPanel(new BorderLayout());
    scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    double percentage = total > 0 ? (score * 100.0 / total) : 0.0;
    JLabel scoreLabel =
        new JLabel(
            String.format("Score: %d / %d (%.1f%%)", score, total, percentage),
            SwingConstants.CENTER);
    scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 18f));

    if (percentage >= 70) {
      scoreLabel.setForeground(new Color(0, 150, 0));
    } else if (percentage >= 50) {
      scoreLabel.setForeground(new Color(200, 150, 0));
    } else {
      scoreLabel.setForeground(new Color(200, 0, 0));
    }

    scorePanel.add(scoreLabel, BorderLayout.CENTER);

    // Results table
    ResultsTableModel tableModel = new ResultsTableModel();
    JTable resultsTable = new JTable(tableModel);
    resultsTable.setDefaultRenderer(Object.class, new ResultsCellRenderer());
    resultsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    resultsTable.getColumnModel().getColumn(0).setMaxWidth(50);
    resultsTable.getColumnModel().getColumn(1).setPreferredWidth(400);
    resultsTable.getColumnModel().getColumn(2).setPreferredWidth(150);

    JScrollPane scrollPane = new JScrollPane(resultsTable);
    scrollPane.setBorder(BorderFactory.createTitledBorder("Question Results"));

    // Buttons
    JPanel buttonPanel = new JPanel();

    JButton retryIncorrectButton = new JButton("Retry Incorrect Questions");
    retryIncorrectButton.addActionListener(e -> retryIncorrect());
    retryIncorrectButton.setEnabled(score < total);

    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dispose());

    buttonPanel.add(retryIncorrectButton);
    buttonPanel.add(closeButton);

    add(scorePanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void retryIncorrect() {
    List<Question> incorrectQuestions = new ArrayList<>();
    for (Question q : questions) {
      if (Boolean.FALSE.equals(results.get(q.getId()))) {
        incorrectQuestions.add(q);
      }
    }

    if (incorrectQuestions.isEmpty()) {
      JOptionPane.showMessageDialog(
          this,
          "All questions were answered correctly!",
          "No Incorrect Questions",
          JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    // Create a temporary quiz with incorrect questions
    Quiz retryQuiz = new Quiz("Retry - Incorrect Questions");
    retryQuiz.getQuestions().addAll(incorrectQuestions);

    dispose();

    // Start new practice session
    AppView frame = (AppView) SwingUtilities.getWindowAncestor(getParent());
    if (frame != null) {
      frame.startPracticeWithQuiz(retryQuiz);
    }
  }

  private class ResultsTableModel extends AbstractTableModel {
    @Override
    public int getRowCount() {
      return questions.size();
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public String getColumnName(int column) {
      return switch (column) {
        case 0 -> "#";
        case 1 -> "Question";
        case 2 -> "Result";
        default -> "";
      };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      Question question = questions.get(rowIndex);
      return switch (columnIndex) {
        case 0 -> rowIndex + 1;
        case 1 -> question.getText();
        case 2 -> Boolean.TRUE.equals(results.get(question.getId())) ? "Correct" : "Incorrect";
        default -> null;
      };
    }
  }

  private class ResultsCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c =
          super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if (!isSelected && column == 2) {
        if ("Correct".equals(value)) {
          c.setForeground(new Color(0, 150, 0));
        } else {
          c.setForeground(new Color(200, 0, 0));
        }
      }

      return c;
    }
  }
}
