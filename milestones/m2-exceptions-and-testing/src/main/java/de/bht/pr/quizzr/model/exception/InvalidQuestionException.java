package de.bht.pr.quizzr.model.exception;

public class InvalidQuestionException extends RuntimeException {
  public InvalidQuestionException(String message) {
    super(message);
  }
}
