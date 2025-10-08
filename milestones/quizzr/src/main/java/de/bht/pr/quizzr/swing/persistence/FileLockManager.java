package de.bht.pr.quizzr.swing.persistence;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileLockManager {
  private final PathsProvider pathsProvider;
  private RandomAccessFile lockFile;
  private FileLock lock;

  public FileLockManager(PathsProvider pathsProvider) {
    this.pathsProvider = pathsProvider;
  }

  public boolean tryAcquireLock() throws IOException {
    Path lockPath = pathsProvider.getLockFilePath();
    Path appDir = pathsProvider.getAppDirectory();

    if (!Files.exists(appDir)) {
      Files.createDirectories(appDir);
    }

    lockFile = new RandomAccessFile(lockPath.toFile(), "rw");
    FileChannel channel = lockFile.getChannel();
    lock = channel.tryLock();

    return lock != null;
  }

  public void releaseLock() {
    try {
      if (lock != null) {
        lock.release();
      }
      if (lockFile != null) {
        lockFile.close();
      }
    } catch (IOException e) {
      // Log but don't throw on cleanup
      System.err.println("Error releasing lock: " + e.getMessage());
    }
  }
}
