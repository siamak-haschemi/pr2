package de.bht.pr.quizzr.swing;

import de.bht.pr.quizzr.swing.app.AppBootstrap;
import java.awt.EventQueue;

public class SwingApp {
  public static void main(String[] args) {
    EventQueue.invokeLater(AppBootstrap::run);
  }
}
