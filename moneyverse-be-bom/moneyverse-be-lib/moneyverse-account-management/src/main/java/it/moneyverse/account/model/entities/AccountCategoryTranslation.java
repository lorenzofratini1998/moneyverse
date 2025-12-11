package it.moneyverse.account.model.entities;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(
    name = "ACCOUNT_CATEGORY_TRANSLATIONS",
    uniqueConstraints = @UniqueConstraint(columnNames = {"ACCOUNT_CATEGORY_ID", "LOCALE"}))
public class AccountCategoryTranslation implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "TRANSLATION_ID")
  private Long translationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ACCOUNT_CATEGORY_ID", nullable = false)
  private AccountCategory accountCategory;

  @Column(name = "LOCALE", nullable = false, length = 10)
  private String locale;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  public Long getTranslationId() {
    return translationId;
  }

  public void setTranslationId(Long translationId) {
    this.translationId = translationId;
  }

  public AccountCategory getAccountCategory() {
    return accountCategory;
  }

  public void setAccountCategory(AccountCategory accountCategory) {
    this.accountCategory = accountCategory;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
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
}
