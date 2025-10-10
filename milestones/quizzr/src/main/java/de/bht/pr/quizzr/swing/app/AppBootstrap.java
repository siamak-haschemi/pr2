package de.bht.pr.quizzr.swing.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import de.bht.pr.quizzr.swing.importexport.ImportExportViewModel;
import de.bht.pr.quizzr.swing.practice.PracticeViewModel;
import de.bht.pr.quizzr.swing.question.QuestionValidationService;
import de.bht.pr.quizzr.swing.question.QuizEditorViewModel;
import de.bht.pr.quizzr.swing.quiz.QuizManagerViewModel;
import de.bht.pr.quizzr.swing.quiz.QuizValidationService;
import de.bht.pr.quizzr.swing.quiz.QuizzRepository;
import de.bht.pr.quizzr.swing.util.PathsProvider;
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

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    QuizzRepository repository = new QuizzRepository(objectMapper, pathsProvider);
    repository.loadFromFile();

    // Initialize services
    QuestionValidationService questionValidationService = new QuestionValidationService();
    QuizValidationService quizValidationService = new QuizValidationService();

    // Initialize view models
    QuizManagerViewModel homeViewModel =
        new QuizManagerViewModel(repository, quizValidationService);
    QuizEditorViewModel editorViewModel = new QuizEditorViewModel(questionValidationService);
    PracticeViewModel practiceViewModel = new PracticeViewModel();
    ImportExportViewModel importExportViewModel = new ImportExportViewModel(repository);

    // Create and show main frame
    SwingUtilities.invokeLater(
        () -> {
          AppView mainFrame =
              new AppView(
                  repository,
                  homeViewModel,
                  editorViewModel,
                  practiceViewModel,
                  importExportViewModel);
          mainFrame.setVisible(true);
        });
  }
}
