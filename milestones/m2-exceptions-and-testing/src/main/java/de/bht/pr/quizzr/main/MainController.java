package de.bht.pr.quizzr.main;

import de.bht.pr.quizzr.model.Quiz;
import de.bht.pr.quizzr.model.QuizFixtures;
import de.bht.pr.quizzr.overview.OverviewController;
import de.bht.pr.quizzr.quiz.QuizExecutionController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
  @FXML private StackPane rootStack;

  private Node overviewNode;
  private Node executionNode;
  private OverviewController overviewController;
  private QuizExecutionController executionController;
  private Quiz quiz;

  @FXML
  void initialize() throws IOException {
    quiz = QuizFixtures.fixedJUnitAndExceptionsQuiz();

    FXMLLoader ovLoader =
        new FXMLLoader(getClass().getResource("/de/bht/pr/quizzr/overview/overview.fxml"));
    overviewNode = ovLoader.load();
    overviewController = ovLoader.getController();
    overviewController.setParent(this);
    overviewController.setQuiz(quiz);

    FXMLLoader exLoader =
        new FXMLLoader(getClass().getResource("/de/bht/pr/quizzr/quiz/quiz_execution.fxml"));
    executionNode = exLoader.load();
    executionController = exLoader.getController();
    executionController.setParent(this);

    rootStack.getChildren().setAll(overviewNode);
  }

  public void startQuiz() {
    executionController.start(quiz);
    rootStack.getChildren().setAll(executionNode);
  }

  public void showOverview() {
    rootStack.getChildren().setAll(overviewNode);
  }
}
