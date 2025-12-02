package it.moneyverse.budget.model.entities;

import it.moneyverse.core.model.entities.Style;
import it.moneyverse.core.model.entities.Translatable;
import it.moneyverse.core.model.entities.TranslationEntityListener;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "DEFAULT_CATEGORIES")
@EntityListeners(TranslationEntityListener.class)
public class DefaultCategory implements Serializable, Translatable {

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

  @OneToMany(mappedBy = "defaultCategory", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DefaultCategoryTranslation> translations = new ArrayList<>();

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

  public List<DefaultCategoryTranslation> getTranslations() {
    return translations;
  }

  public void setTranslations(List<DefaultCategoryTranslation> translations) {
    this.translations = translations;
  }

  @Override
  public void applyTranslations(String locale) {
    if (locale == null || translations.isEmpty()) {
      return;
    }

    translations.stream()
        .filter(translation -> translation.getLocale().equals(locale))
        .findFirst()
        .ifPresent(
            translation -> {
              this.name = translation.getName();
              if (translation.getDescription() != null) {
                this.description = translation.getDescription();
              }
            });
  }
}
