package de.bht.pr.quizzr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class QuizzrApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader loader =
        new FXMLLoader(QuizzrApplication.class.getResource("/de/bht/pr/quizzr/main/main.fxml"));
    Scene scene = new Scene(loader.load(), 600, 400);
    stage.setTitle("Quizzr - M2");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
