package de.bht.pr_two.quizzr;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
  @FXML private Label welcomeText;

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }
}
