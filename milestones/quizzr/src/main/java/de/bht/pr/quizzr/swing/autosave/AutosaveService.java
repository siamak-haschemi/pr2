package de.bht.pr.quizzr.swing.autosave;

import de.bht.pr.quizzr.swing.model.QuizCollection;
import de.bht.pr.quizzr.swing.persistence.JsonRepository;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AutosaveService {
  private static final long AUTOSAVE_DELAY_MS = 5000;

  private final JsonRepository repository;
  private final Debouncer debouncer;
  private final ExecutorService executor;

  public AutosaveService(JsonRepository repository) {
    this.repository = repository;
    this.debouncer = new Debouncer(AUTOSAVE_DELAY_MS);
    this.executor = Executors.newSingleThreadExecutor();
  }

  public void scheduleAutosave(QuizCollection collection) {
    debouncer.debounce(
        () -> {
          executor.submit(
              () -> {
                try {
                  repository.save(collection);
                } catch (IOException e) {
                  System.err.println("Autosave failed: " + e.getMessage());
                }
              });
        });
  }

  public void saveNow(QuizCollection collection) throws IOException {
    repository.save(collection);
  }

  public void shutdown() {
    debouncer.shutdown();
    executor.shutdown();
  }
}
