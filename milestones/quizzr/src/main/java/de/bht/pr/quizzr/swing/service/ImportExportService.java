package de.bht.pr.quizzr.swing.service;

import de.bht.pr.quizzr.swing.model.Quiz;
import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.persistence.JsonRepository;
import de.bht.pr.quizzr.swing.util.Result;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImportExportService {
  private final JsonRepository repository;

  public ImportExportService(JsonRepository repository) {
    this.repository = repository;
  }

  public void exportToFile(QuizCollection collection, Path targetPath) throws IOException {
    repository.exportToFile(collection, targetPath);
  }

  public Result<QuizCollection, String> importFromFile(
      Path sourcePath, QuizCollection currentCollection) {
    try {
      QuizCollection imported = repository.importFromFile(sourcePath);

      List<String> conflicts = findNameConflicts(imported, currentCollection);
      if (!conflicts.isEmpty()) {
        return Result.failure("Name conflicts found: " + String.join(", ", conflicts));
      }

      return Result.success(imported);
    } catch (IOException e) {
      return Result.failure("Failed to import: " + e.getMessage());
    }
  }

  private List<String> findNameConflicts(QuizCollection imported, QuizCollection current) {
    List<String> conflicts = new ArrayList<>();
    for (Quiz importedQuiz : imported.getQuizzes()) {
      String importedName = importedQuiz.getName().trim().toLowerCase(Locale.ROOT);
      for (Quiz currentQuiz : current.getQuizzes()) {
        if (currentQuiz.getName().trim().toLowerCase(Locale.ROOT).equals(importedName)) {
          conflicts.add(importedQuiz.getName());
          break;
        }
      }
    }
    return conflicts;
  }

  public QuizCollection mergeCollections(QuizCollection current, QuizCollection imported) {
    QuizCollection merged = new QuizCollection();
    merged.getQuizzes().addAll(current.getQuizzes());
    merged.getQuizzes().addAll(imported.getQuizzes());
    return merged;
  }
}
