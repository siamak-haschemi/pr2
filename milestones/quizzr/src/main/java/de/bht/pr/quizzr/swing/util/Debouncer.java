package de.bht.pr.quizzr.swing.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Debouncer {
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> pendingTask;
  private final long delayMillis;

  public Debouncer(long delayMillis) {
    this.delayMillis = delayMillis;
  }

  public synchronized void debounce(Runnable task) {
    if (pendingTask != null && !pendingTask.isDone()) {
      pendingTask.cancel(false);
    }
    pendingTask = scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
  }

  public void shutdown() {
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
