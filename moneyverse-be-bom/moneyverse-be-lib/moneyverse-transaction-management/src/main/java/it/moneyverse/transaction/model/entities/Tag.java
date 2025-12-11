package it.moneyverse.transaction.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import it.moneyverse.core.model.entities.Style;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
    name = "TAGS",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_ID", "TAG_NAME"})})
public class Tag extends Auditable implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "TAG_ID")
  private UUID tagId;

  @Column(name = "USER_ID", nullable = false)
  private UUID userId;

  @Column(name = "TAG_NAME", nullable = false)
  private String tagName;

  @Column(name = "DESCRIPTION")
  private String description;

  @Embedded private Style style;

  @ManyToMany(mappedBy = "tags")
  private Set<Transaction> transactions = new HashSet<>();

  public UUID getTagId() {
    return tagId;
  }

  public void setTagId(UUID tagId) {
    this.tagId = tagId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getTagName() {
    return tagName;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<Transaction> getTransactions() {
    return transactions;
  }

  public Style getStyle() {
    return style;
  }

  public void setStyle(Style style) {
    this.style = style;
  }
}
