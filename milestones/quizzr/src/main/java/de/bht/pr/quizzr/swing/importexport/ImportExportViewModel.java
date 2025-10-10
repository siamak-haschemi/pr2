package de.bht.pr.quizzr.swing.importexport;

import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.quiz.QuizzRepository;
import java.io.IOException;
import java.nio.file.Path;

public class ImportExportViewModel {
  private final QuizzRepository repository;

  public ImportExportViewModel(QuizzRepository repository) {
    this.repository = repository;
  }

  public QuizCollection importFromPath(Path path) throws IOException {
    QuizCollection fullCollection = repository.get();
    QuizCollection result = repository.importFromFile(path);

    // Update the collection
    fullCollection.getQuizzes().clear();
    fullCollection.getQuizzes().addAll(result.getQuizzes());

    repository.save(fullCollection);
    return result;
  }

  public void exportToPath(Path path) throws IOException {
    repository.exportToFile(repository.get(), path);
  }
}
