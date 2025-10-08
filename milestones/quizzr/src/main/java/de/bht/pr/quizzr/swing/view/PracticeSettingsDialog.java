package de.bht.pr.quizzr.swing.view;

import de.bht.pr.quizzr.swing.model.Quiz;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;

public class PracticeSettingsDialog extends JDialog {
  private final Quiz quiz;

  private JCheckBox shuffleCheckBox;
  private JCheckBox limitCheckBox;
  private JSpinner limitSpinner;
  private boolean confirmed = false;

  public PracticeSettingsDialog(JFrame parent, Quiz quiz) {
    super(parent, "Practice Settings", true);
    this.quiz = quiz;

    initializeUI();
    setSize(400, 200);
    setLocationRelativeTo(parent);
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));

    JPanel settingsPanel = new JPanel(new GridLayout(2, 1, 5, 10));
    settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Shuffle option
    shuffleCheckBox = new JCheckBox("Shuffle questions");
    shuffleCheckBox.setSelected(false);
    settingsPanel.add(shuffleCheckBox);

    // Limit option
    JPanel limitPanel = new JPanel(new BorderLayout(5, 5));
    limitCheckBox = new JCheckBox("Limit number of questions:");
    limitCheckBox.setSelected(false);
    SpinnerNumberModel spinnerModel =
        new SpinnerNumberModel(
            Math.min(10, quiz.getQuestions().size()), 1, quiz.getQuestions().size(), 1);
    limitSpinner = new JSpinner(spinnerModel);
    limitSpinner.setEnabled(false);

    limitCheckBox.addActionListener(e -> limitSpinner.setEnabled(limitCheckBox.isSelected()));

    limitPanel.add(limitCheckBox, BorderLayout.WEST);
    limitPanel.add(limitSpinner, BorderLayout.CENTER);
    settingsPanel.add(limitPanel);

    // Info label
    JLabel infoLabel =
        new JLabel(
            "Quiz: " + quiz.getName() + " (" + quiz.getQuestions().size() + " questions)",
            SwingConstants.CENTER);
    infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    // Buttons
    JPanel buttonPanel = new JPanel();
    JButton startButton = new JButton("Start Practice");
    startButton.addActionListener(
        e -> {
          confirmed = true;
          dispose();
        });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());

    buttonPanel.add(startButton);
    buttonPanel.add(cancelButton);

    add(infoLabel, BorderLayout.NORTH);
    add(settingsPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public boolean isShuffle() {
    return shuffleCheckBox.isSelected();
  }

  public Integer getLimit() {
    if (limitCheckBox.isSelected()) {
      return (Integer) limitSpinner.getValue();
    }
    return null;
  }
}
