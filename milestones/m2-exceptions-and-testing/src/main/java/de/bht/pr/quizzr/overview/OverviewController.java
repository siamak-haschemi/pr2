package de.bht.pr.quizzr.overview;

import de.bht.pr.quizzr.main.MainController;
import de.bht.pr.quizzr.model.Question;
import de.bht.pr.quizzr.model.Quiz;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class OverviewController {
  @FXML private Label titleLabel;
  @FXML private ListView<String> questionList;
  @FXML private Button startButton;

  private MainController parent;
  private Quiz quiz;

  public void setParent(MainController parent) {
    this.parent = parent;
  }

  public void setQuiz(Quiz quiz) {
    this.quiz = quiz;
    titleLabel.setText(quiz.getTitle());
    questionList.getItems().clear();
    for (Question q : quiz.getQuestions()) {
      StringBuilder sb = new StringBuilder();
      sb.append("Q: ").append(q.getText()).append("\n");
      for (int i = 0; i < q.getOptions().size(); i++) {
        String marker = (i == q.getCorrectIndex()) ? "\u2713 " : "\u2022 ";
        sb.append("  ").append(marker).append(q.getOptions().get(i)).append("\n");
      }
      questionList.getItems().add(sb.toString().trim());
    }
  }

  @FXML
  void onStartQuiz() {
    if (parent != null) parent.startQuiz();
  }
}
