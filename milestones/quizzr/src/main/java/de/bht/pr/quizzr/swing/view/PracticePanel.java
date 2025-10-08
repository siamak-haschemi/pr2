package de.bht.pr.quizzr.swing.view;

import de.bht.pr.quizzr.swing.model.Quiz;
import de.bht.pr.quizzr.swing.viewmodel.PracticeViewModel;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class PracticePanel extends JPanel implements PropertyChangeListener {
  private final PracticeViewModel viewModel;

  private JLabel statusLabel;

  public PracticePanel(PracticeViewModel viewModel) {
    this.viewModel = viewModel;
    this.viewModel.addPropertyChangeListener(this);

    initializeUI();
  }

  private void initializeUI() {
    setLayout(new BorderLayout(10, 10));

    statusLabel =
        new JLabel(
            "Select a quiz from the Home tab and click 'Start Practice'", SwingConstants.CENTER);
    statusLabel.setFont(statusLabel.getFont().deriveFont(14f));

    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(statusLabel, BorderLayout.CENTER);

    add(centerPanel, BorderLayout.CENTER);
  }

  public void startPracticeSession(Quiz quiz) {
    if (quiz == null || quiz.getQuestions().isEmpty()) {
      statusLabel.setText("Cannot start practice: Quiz has no questions");
      return;
    }

    statusLabel.setText("Starting practice for: " + quiz.getName());
    JOptionPane.showMessageDialog(
        this,
        "Practice mode will be fully implemented in Milestone 3.\n\n"
            + "Quiz: "
            + quiz.getName()
            + "\n"
            + "Questions: "
            + quiz.getQuestions().size(),
        "Practice Session",
        JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    SwingUtilities.invokeLater(
        () -> {
          // Practice functionality will be fully implemented in later milestones
        });
  }
}
