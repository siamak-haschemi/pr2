package de.bht.pr.quizzr.swing.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathsProvider {
  private static final String APP_DIR = ".quizzr";
  private static final String DATA_FILE = "quizzes.json";

  public Path getAppDirectory() {
    return Paths.get(System.getProperty("user.home"), APP_DIR);
  }

  public Path getDataFilePath() {
    return getAppDirectory().resolve(DATA_FILE);
  }
}
