package de.bht.pr.quizzr.quiz;

import de.bht.pr.quizzr.main.MainController;
import de.bht.pr.quizzr.model.Question;
import de.bht.pr.quizzr.model.Quiz;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class QuizExecutionController {
  @FXML private Label execTitleLabel;
  @FXML private Label questionText;
  @FXML private VBox answersBox;
  @FXML private Button continueButton;
  @FXML private Button nextButton;

  private MainController parent;
  private Quiz quiz;
  private int currentIndex = -1;
  private ToggleGroup answersGroup;

  public void setParent(MainController parent) {
    this.parent = parent;
  }

  public void start(Quiz quiz) {
    this.quiz = quiz;
    currentIndex = 0;
    loadCurrentQuestion();
  }

  private void loadCurrentQuestion() {
    if (quiz == null || currentIndex < 0 || currentIndex >= quiz.getQuestions().size()) {
      if (parent != null) parent.showOverview();
      return;
    }
    Question q = quiz.getQuestions().get(currentIndex);
    execTitleLabel.setText(
        String.format("Question %d/%d", currentIndex + 1, quiz.getQuestions().size()));
    questionText.setText(q.getText());

    answersBox.getChildren().clear();
    answersGroup = new ToggleGroup();
    List<String> opts = q.getOptions();
    for (int i = 0; i < opts.size(); i++) {
      RadioButton rb = new RadioButton(opts.get(i));
      rb.setUserData(i);
      rb.setToggleGroup(answersGroup);
      rb.setWrapText(true);
      rb.setMaxWidth(Double.MAX_VALUE);
      answersBox.getChildren().add(rb);
    }
    continueButton.setDisable(false);
    nextButton.setDisable(true);
  }

  @FXML
  void onContinue() {
    if (answersGroup.getSelectedToggle() == null) return;
    int selected = (int) answersGroup.getSelectedToggle().getUserData();
    Question q = quiz.getQuestions().get(currentIndex);
    for (int i = 0; i < answersBox.getChildren().size(); i++) {
      RadioButton rb = (RadioButton) answersBox.getChildren().get(i);
      if (i == q.getCorrectIndex()) {
        rb.setStyle("-fx-background-color: rgba(76,175,80,0.25);");
      } else if (i == selected) {
        rb.setStyle("-fx-background-color: rgba(244,67,54,0.25);");
      } else {
        rb.setStyle("");
      }
      rb.setDisable(true);
    }
    continueButton.setDisable(true);
    nextButton.setDisable(false);
  }

  @FXML
  void onNext() {
    currentIndex++;
    if (currentIndex >= quiz.getQuestions().size()) {
      if (parent != null) parent.showOverview();
    } else {
      loadCurrentQuestion();
    }
  }

  @FXML
  void onCancelQuiz() {
    if (parent != null) parent.showOverview();
  }
}
