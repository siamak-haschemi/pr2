package de.bht.pr.quizzr.swing.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quiz {
  private UUID id;
  private String name;
  private List<Question> questions;

  public Quiz() {
    this.id = UUID.randomUUID();
    this.questions = new ArrayList<>();
  }

  public Quiz(String name) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.questions = new ArrayList<>();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }
}
