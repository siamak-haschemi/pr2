package de.bht.pr.quizzr.swing.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Question {
  private UUID id;
  private String text;
  private QuestionType type;
  private List<Answer> answers;
  private Set<UUID> correctAnswerIds;

  public Question() {
    this.id = UUID.randomUUID();
    this.answers = new ArrayList<>();
    this.correctAnswerIds = new HashSet<>();
    this.type = QuestionType.SINGLE;
  }

  public Question(String text, QuestionType type) {
    this.id = UUID.randomUUID();
    this.text = text;
    this.type = type;
    this.answers = new ArrayList<>();
    this.correctAnswerIds = new HashSet<>();
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

  public QuestionType getType() {
    return type;
  }

  public void setType(QuestionType type) {
    this.type = type;
  }

  public List<Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(List<Answer> answers) {
    this.answers = answers;
  }

  public Set<UUID> getCorrectAnswerIds() {
    return correctAnswerIds;
  }

  public void setCorrectAnswerIds(Set<UUID> correctAnswerIds) {
    this.correctAnswerIds = correctAnswerIds;
  }

  public Question deepCopy() {
    Question copy = new Question();
    copy.setText(this.text);
    copy.setType(this.type);

    // Deep copy answers and build mapping of old ID to new ID
    java.util.Map<UUID, UUID> idMapping = new java.util.HashMap<>();
    for (Answer answer : this.answers) {
      Answer answerCopy = answer.deepCopy();
      copy.getAnswers().add(answerCopy);
      idMapping.put(answer.getId(), answerCopy.getId());
    }

    // Map correct answer IDs from old to new
    for (UUID oldId : this.correctAnswerIds) {
      UUID newId = idMapping.get(oldId);
      if (newId != null) {
        copy.getCorrectAnswerIds().add(newId);
      }
    }

    return copy;
  }
}
