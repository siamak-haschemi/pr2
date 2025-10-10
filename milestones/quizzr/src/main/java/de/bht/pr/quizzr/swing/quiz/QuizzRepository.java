package de.bht.pr.quizzr.swing.quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.util.PathsProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class QuizzRepository {
  private final PathsProvider pathsProvider;
  private final ObjectMapper objectMapper;
  private QuizCollection cachedCollection;

  public QuizzRepository(ObjectMapper objectMapper, PathsProvider pathsProvider) {
    this.pathsProvider = pathsProvider;
    this.objectMapper = objectMapper;
  }

  public QuizCollection get() {
    return cachedCollection;
  }

  public QuizCollection loadFromFile() throws RuntimeException {
    Path dataPath = pathsProvider.getDataFilePath();
    Path appDir = pathsProvider.getAppDirectory();

    try {
      if (!Files.exists(appDir)) {
        Files.createDirectories(appDir);
      }

      if (!Files.exists(dataPath)) {
        return new QuizCollection();
      }

      cachedCollection = objectMapper.readValue(dataPath.toFile(), QuizCollection.class);
      return cachedCollection;
    } catch (IOException e) {
      throw new RuntimeException("Failed to parse quiz data: " + e.getMessage(), e);
    }
  }

  public QuizCollection save(QuizCollection collection) throws IOException {
    Path dataPath = pathsProvider.getDataFilePath();
    Path appDir = pathsProvider.getAppDirectory();

    if (!Files.exists(appDir)) {
      Files.createDirectories(appDir);
    }

    objectMapper.writeValue(dataPath.toFile(), collection);
    return loadFromFile();
  }

  public void exportToFile(QuizCollection collection, Path targetPath) throws IOException {
    objectMapper.writeValue(targetPath.toFile(), collection);
  }

  public QuizCollection importFromFile(Path sourcePath) throws IOException {
    return objectMapper.readValue(sourcePath.toFile(), QuizCollection.class);
  }
}
