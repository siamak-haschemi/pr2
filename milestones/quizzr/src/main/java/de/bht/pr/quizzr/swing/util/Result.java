package de.bht.pr.quizzr.swing.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Result<T, E> {
  private final T value;
  private final E error;
  private final boolean isSuccess;

  private Result(T value, E error, boolean isSuccess) {
    this.value = value;
    this.error = error;
    this.isSuccess = isSuccess;
  }

  public static <T, E> Result<T, E> success(T value) {
    return new Result<>(value, null, true);
  }

  public static <T, E> Result<T, E> failure(E error) {
    return new Result<>(null, error, false);
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public boolean isFailure() {
    return !isSuccess;
  }

  public Optional<T> getValue() {
    return Optional.ofNullable(value);
  }

  public Optional<E> getError() {
    return Optional.ofNullable(error);
  }

  public T getOrElse(T defaultValue) {
    return isSuccess ? value : defaultValue;
  }

  public <U> Result<U, E> map(Function<T, U> mapper) {
    if (isSuccess) {
      return Result.success(mapper.apply(value));
    }
    return Result.failure(error);
  }

  public void ifSuccess(Consumer<T> consumer) {
    if (isSuccess) {
      consumer.accept(value);
    }
  }

  public void ifFailure(Consumer<E> consumer) {
    if (!isSuccess) {
      consumer.accept(error);
    }
  }
}
