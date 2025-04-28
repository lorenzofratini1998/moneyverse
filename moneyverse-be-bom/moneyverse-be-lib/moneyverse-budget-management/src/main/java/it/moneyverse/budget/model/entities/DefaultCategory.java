package it.moneyverse.budget.model.entities;

import it.moneyverse.core.model.entities.Style;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "DEFAULT_CATEGORIES")
public class DefaultCategory implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "DEFAULT_CATEGORY_ID")
  private UUID id;

  @Column(name = "NAME", nullable = false, unique = true)
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @Embedded private Style style;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Style getStyle() {
    return style;
  }

  public void setStyle(Style style) {
    this.style = style;
  }
}
