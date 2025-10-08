package de.bht.pr.quizzr.swing.model;

import java.util.UUID;

public class Answer {
  private UUID id;
  private String text;

  public Answer() {
    this.id = UUID.randomUUID();
  }

  public Answer(UUID id, String text) {
    this.id = id;
    this.text = text;
  }

  public Answer(String text) {
    this.id = UUID.randomUUID();
    this.text = text;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
