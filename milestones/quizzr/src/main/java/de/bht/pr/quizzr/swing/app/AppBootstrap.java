package de.bht.pr.quizzr.swing.app;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import de.bht.pr.quizzr.swing.autosave.AutosaveService;
import de.bht.pr.quizzr.swing.editor.EditorViewModel;
import de.bht.pr.quizzr.swing.home.HomeViewModel;
import de.bht.pr.quizzr.swing.importexport.ImportExportService;
import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.persistence.FileLockManager;
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
    FileLockManager lockManager = new FileLockManager(pathsProvider);

    // Try to acquire lock
    try {
      if (!lockManager.tryAcquireLock()) {
        JOptionPane.showMessageDialog(
            null,
            "Another instance of Quizzr is already running.",
            "Single Instance",
            JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
          null,
          "Failed to acquire application lock: " + e.getMessage(),
          "Startup Error",
          JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }

    // Set up shutdown hook to release lock
    Runtime.getRuntime().addShutdownHook(new Thread(lockManager::releaseLock));

    // Load data
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
        lockManager.releaseLock();
        System.exit(1);
        return;
      }
    }

    // Initialize services
    AutosaveService autosaveService = new AutosaveService(repository);
    ValidationService validationService = new ValidationService();
    ImportExportService importExportService = new ImportExportService(repository);

    // Initialize view models
    HomeViewModel homeViewModel = new HomeViewModel(collection, autosaveService, validationService);
    EditorViewModel editorViewModel = new EditorViewModel(autosaveService, validationService);
    editorViewModel.setCollection(collection);
    PracticeViewModel practiceViewModel = new PracticeViewModel();

    // Create and show main frame
    QuizCollection finalCollection = collection;
    SwingUtilities.invokeLater(
        () -> {
          MainView mainFrame =
              new MainView(
                  finalCollection,
                  homeViewModel,
                  editorViewModel,
                  practiceViewModel,
                  autosaveService,
                  importExportService);
          mainFrame.setVisible(true);
        });
  }
}
