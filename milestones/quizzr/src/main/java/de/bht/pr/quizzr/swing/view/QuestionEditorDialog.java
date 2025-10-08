package de.bht.pr.quizzr.swing.view;

import de.bht.pr.quizzr.swing.model.Answer;
import de.bht.pr.quizzr.swing.model.Question;
import de.bht.pr.quizzr.swing.model.QuestionType;
import de.bht.pr.quizzr.swing.service.ValidationService;
import de.bht.pr.quizzr.swing.util.Result;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public class QuestionEditorDialog extends JDialog {
  private final Question question;
  private final ValidationService validationService;

  private JTextArea questionTextArea;
  private JComboBox<QuestionType> typeComboBox;
  private JTable answersTable;
  private AnswersTableModel answersModel;
  private JButton addAnswerButton;
  private JButton removeAnswerButton;
  private JButton moveUpButton;
  private JButton moveDownButton;
  private JLabel validationLabel;

  private boolean confirmed = false;

  public QuestionEditorDialog(
      JFrame parent, Question question, ValidationService validationService) {
    super(parent, "Edit Question", true);
    this.question = question;
    this.validationService = validationService;

    initializeUI();
    loadQuestion();
    setSize(700, 500);
    setLocationRelativeTo(parent);
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));

    // Question text panel
    JPanel questionPanel = new JPanel(new BorderLayout(5, 5));
    questionPanel.setBorder(BorderFactory.createTitledBorder("Question Text"));
    questionTextArea = new JTextArea(3, 40);
    questionTextArea.setLineWrap(true);
    questionTextArea.setWrapStyleWord(true);
    JScrollPane textScroll = new JScrollPane(questionTextArea);
    questionPanel.add(textScroll, BorderLayout.CENTER);

    // Type panel
    JPanel typePanel = new JPanel(new BorderLayout(5, 5));
    typePanel.add(new JLabel("Question Type:"), BorderLayout.WEST);
    typeComboBox = new JComboBox<>(QuestionType.values());
    typeComboBox.addActionListener(e -> updateCorrectAnswerColumn());
    typePanel.add(typeComboBox, BorderLayout.CENTER);

    JPanel topPanel = new JPanel(new BorderLayout(5, 5));
    topPanel.add(questionPanel, BorderLayout.CENTER);
    topPanel.add(typePanel, BorderLayout.SOUTH);

    // Answers table
    answersModel = new AnswersTableModel();
    answersTable = new JTable(answersModel);
    answersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    answersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    answersTable.getColumnModel().getColumn(0).setMaxWidth(50);
    answersTable.getColumnModel().getColumn(1).setPreferredWidth(400);

    // Custom renderer for correct column
    answersTable
        .getColumnModel()
        .getColumn(0)
        .setCellRenderer(
            new TableCellRenderer() {
              private final JCheckBox checkBox = new JCheckBox();

              @Override
              public Component getTableCellRendererComponent(
                  JTable table,
                  Object value,
                  boolean isSelected,
                  boolean hasFocus,
                  int row,
                  int column) {
                checkBox.setSelected(Boolean.TRUE.equals(value));
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                  checkBox.setBackground(table.getSelectionBackground());
                } else {
                  checkBox.setBackground(table.getBackground());
                }
                return checkBox;
              }
            });

    JScrollPane answersScroll = new JScrollPane(answersTable);
    answersScroll.setBorder(BorderFactory.createTitledBorder("Answers"));

    // Answer buttons
    JPanel answerButtonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
    addAnswerButton = new JButton("Add Answer");
    addAnswerButton.addActionListener(e -> addAnswer());

    removeAnswerButton = new JButton("Remove Answer");
    removeAnswerButton.addActionListener(e -> removeAnswer());

    moveUpButton = new JButton("Move Up");
    moveUpButton.addActionListener(e -> moveUp());

    moveDownButton = new JButton("Move Down");
    moveDownButton.addActionListener(e -> moveDown());

    answerButtonPanel.add(addAnswerButton);
    answerButtonPanel.add(removeAnswerButton);
    answerButtonPanel.add(moveUpButton);
    answerButtonPanel.add(moveDownButton);

    JPanel answersPanel = new JPanel(new BorderLayout(5, 5));
    answersPanel.add(answersScroll, BorderLayout.CENTER);
    answersPanel.add(answerButtonPanel, BorderLayout.EAST);

    // Validation label
    validationLabel = new JLabel(" ");
    validationLabel.setForeground(java.awt.Color.RED);

    // Bottom buttons
    JPanel buttonPanel = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.addActionListener(
        e -> {
          if (validateAndSave()) {
            confirmed = true;
            dispose();
          }
        });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());

    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(validationLabel, BorderLayout.NORTH);
    bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

    add(topPanel, BorderLayout.NORTH);
    add(answersPanel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  private void loadQuestion() {
    questionTextArea.setText(question.getText());
    typeComboBox.setSelectedItem(question.getType());
    answersModel.loadAnswers();
  }

  private void addAnswer() {
    Answer answer = new Answer("New answer");
    question.getAnswers().add(answer);
    answersModel.fireTableDataChanged();
  }

  private void removeAnswer() {
    int row = answersTable.getSelectedRow();
    if (row >= 0) {
      Answer answer = question.getAnswers().get(row);
      question.getAnswers().remove(row);
      question.getCorrectAnswerIds().remove(answer.getId());
      answersModel.fireTableDataChanged();
    }
  }

  private void moveUp() {
    int row = answersTable.getSelectedRow();
    if (row > 0) {
      Answer temp = question.getAnswers().get(row);
      question.getAnswers().set(row, question.getAnswers().get(row - 1));
      question.getAnswers().set(row - 1, temp);
      answersModel.fireTableDataChanged();
      answersTable.setRowSelectionInterval(row - 1, row - 1);
    }
  }

  private void moveDown() {
    int row = answersTable.getSelectedRow();
    if (row >= 0 && row < question.getAnswers().size() - 1) {
      Answer temp = question.getAnswers().get(row);
      question.getAnswers().set(row, question.getAnswers().get(row + 1));
      question.getAnswers().set(row + 1, temp);
      answersModel.fireTableDataChanged();
      answersTable.setRowSelectionInterval(row + 1, row + 1);
    }
  }

  private void updateCorrectAnswerColumn() {
    answersModel.fireTableStructureChanged();
    answersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
    answersTable.getColumnModel().getColumn(0).setMaxWidth(50);
  }

  private boolean validateAndSave() {
    question.setText(questionTextArea.getText());
    question.setType((QuestionType) typeComboBox.getSelectedItem());

    Result<Void, String> result = validationService.validateQuestion(question);
    if (result.isFailure()) {
      validationLabel.setText(result.getError().orElse("Validation failed"));
      return false;
    }

    validationLabel.setText(" ");
    return true;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  private class AnswersTableModel extends AbstractTableModel {

    @Override
    public int getRowCount() {
      return question.getAnswers().size();
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

    @Override
    public String getColumnName(int column) {
      return switch (column) {
        case 0 -> "✓";
        case 1 -> "Answer Text";
        default -> "";
      };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return switch (columnIndex) {
        case 0 -> Boolean.class;
        case 1 -> String.class;
        default -> Object.class;
      };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      Answer answer = question.getAnswers().get(rowIndex);
      return switch (columnIndex) {
        case 0 -> question.getCorrectAnswerIds().contains(answer.getId());
        case 1 -> answer.getText();
        default -> null;
      };
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
      Answer answer = question.getAnswers().get(rowIndex);
      switch (columnIndex) {
        case 0:
          boolean isCorrect = Boolean.TRUE.equals(value);
          QuestionType type = (QuestionType) typeComboBox.getSelectedItem();
          if (isCorrect) {
            if (type == QuestionType.SINGLE) {
              question.getCorrectAnswerIds().clear();
            }
            question.getCorrectAnswerIds().add(answer.getId());
          } else {
            question.getCorrectAnswerIds().remove(answer.getId());
          }
          fireTableDataChanged();
          break;
        case 1:
          answer.setText((String) value);
          break;
      }
    }

    public void loadAnswers() {
      fireTableDataChanged();
    }
  }
}
