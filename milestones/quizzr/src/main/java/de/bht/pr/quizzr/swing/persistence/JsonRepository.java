package de.bht.pr.quizzr.swing.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.bht.pr.quizzr.swing.model.QuizCollection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonRepository {
  private final PathsProvider pathsProvider;
  private final ObjectMapper objectMapper;

  public JsonRepository(PathsProvider pathsProvider) {
    this.pathsProvider = pathsProvider;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public QuizCollection load() throws IOException {
    Path dataPath = pathsProvider.getDataFilePath();
    Path appDir = pathsProvider.getAppDirectory();

    if (!Files.exists(appDir)) {
      Files.createDirectories(appDir);
    }

    if (!Files.exists(dataPath)) {
      return new QuizCollection();
    }

    try {
      return objectMapper.readValue(dataPath.toFile(), QuizCollection.class);
    } catch (IOException e) {
      throw new IOException("Failed to parse quiz data: " + e.getMessage(), e);
    }
  }

  public void save(QuizCollection collection) throws IOException {
    Path dataPath = pathsProvider.getDataFilePath();
    Path appDir = pathsProvider.getAppDirectory();

    if (!Files.exists(appDir)) {
      Files.createDirectories(appDir);
    }

    objectMapper.writeValue(dataPath.toFile(), collection);
  }

  public void exportToFile(QuizCollection collection, Path targetPath) throws IOException {
    objectMapper.writeValue(targetPath.toFile(), collection);
  }

  public QuizCollection importFromFile(Path sourcePath) throws IOException {
    return objectMapper.readValue(sourcePath.toFile(), QuizCollection.class);
  }
}
