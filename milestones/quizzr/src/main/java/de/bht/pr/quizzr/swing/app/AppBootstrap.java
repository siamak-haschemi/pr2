package de.bht.pr.quizzr.swing.app;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import de.bht.pr.quizzr.swing.editor.EditorViewModel;
import de.bht.pr.quizzr.swing.home.HomeViewModel;
import de.bht.pr.quizzr.swing.importexport.ImportExportService;
import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.persistence.JsonRepository;
import de.bht.pr.quizzr.swing.persistence.PathsProvider;
import de.bht.pr.quizzr.swing.practice.PracticeViewModel;
import de.bht.pr.quizzr.swing.validation.ValidationService;
import java.io.IOException;
import javax.swing.*;

public class AppBootstrap {

  public static void run() {
    // Set up theme
    try {
      FlatDraculaIJTheme.setup();
    } catch (Exception e) {
      System.err.println("Failed to set theme: " + e.getMessage());
    }

    // Initialize persistence
    PathsProvider pathsProvider = new PathsProvider();
    JsonRepository repository = new JsonRepository(pathsProvider);
    QuizCollection collection;
    try {
      collection = repository.load();
    } catch (IOException e) {
      int choice =
          JOptionPane.showConfirmDialog(
              null,
              "Failed to load quiz data: " + e.getMessage() + "\n\nStart with empty collection?",
              "Load Error",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.ERROR_MESSAGE);
      if (choice == JOptionPane.YES_OPTION) {
        collection = new QuizCollection();
        try {
          repository.save(collection);
        } catch (IOException ex) {
          System.err.println("Failed to save empty collection: " + ex.getMessage());
        }
      } else {
        System.exit(1);
        return;
      }
    }

    // Initialize services
    ValidationService validationService = new ValidationService();
    ImportExportService importExportService = new ImportExportService(repository);

    // Initialize view models
    HomeViewModel homeViewModel = new HomeViewModel(collection, repository, validationService);
    EditorViewModel editorViewModel = new EditorViewModel(validationService);
    PracticeViewModel practiceViewModel = new PracticeViewModel();

    // Create and show main frame
    QuizCollection finalCollection = collection;
    JsonRepository finalRepository = repository;
    SwingUtilities.invokeLater(
        () -> {
          MainView mainFrame =
              new MainView(
                  finalCollection,
                  homeViewModel,
                  editorViewModel,
                  practiceViewModel,
                  finalRepository,
                  importExportService);
          mainFrame.setVisible(true);
        });
  }
}
