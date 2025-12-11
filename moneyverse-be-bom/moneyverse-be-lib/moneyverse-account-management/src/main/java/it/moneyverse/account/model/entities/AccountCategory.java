package it.moneyverse.account.model.entities;

import it.moneyverse.core.model.entities.Style;
import it.moneyverse.core.model.entities.Translatable;
import it.moneyverse.core.model.entities.TranslationEntityListener;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ACCOUNT_CATEGORIES")
@EntityListeners(TranslationEntityListener.class)
public class AccountCategory implements Serializable, Translatable {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ACCOUNT_CATEGORY_ID")
  private Long accountCategoryId;

  @Column(name = "NAME", nullable = false, unique = true)
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @Embedded private Style style;

  @OneToMany(mappedBy = "accountCategory", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Account> accounts = new ArrayList<>();

  @OneToMany(mappedBy = "accountCategory", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AccountCategoryTranslation> translations = new ArrayList<>();

  public Long getAccountCategoryId() {
    return accountCategoryId;
  }

  public void setAccountCategoryId(Long accountCategoryId) {
    this.accountCategoryId = accountCategoryId;
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
