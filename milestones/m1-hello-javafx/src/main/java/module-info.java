module de.bht.pr_two.quizzr {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires eu.hansolo.tilesfx;
  requires com.almasb.fxgl.all;

  opens de.bht.pr_two.quizzr to
      javafx.fxml;

  exports de.bht.pr_two.quizzr;
}
