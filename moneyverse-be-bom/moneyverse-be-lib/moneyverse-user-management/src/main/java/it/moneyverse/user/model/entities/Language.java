package it.moneyverse.user.model.entities;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "LANGUAGES")
public class Language implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "LANGUAGE_ID")
  private UUID languageId;

  @Column(name = "ISO_CODE", length = 2, nullable = false)
  private String isoCode;

  @Column(name = "LOCALE", length = 5, nullable = false)
  private String locale;

  @Column(name = "COUNTRY", length = 50, nullable = false)
  private String country;

  @Column(name = "ICON", length = 10, nullable = false)
  private String icon;

  @Column(name = "IS_DEFAULT", nullable = false)
  @ColumnDefault("FALSE")
  private Boolean isDefault = false;

  @Column(name = "ENABLED", nullable = false)
  @ColumnDefault("TRUE")
  private Boolean isEnabled = true;

  public UUID getLanguageId() {
    return languageId;
  }

  public void setLanguageId(UUID languageId) {
    this.languageId = languageId;
  }

  public String getIsoCode() {
    return isoCode;
  }

  public void setIsoCode(String isoCode) {
    this.isoCode = isoCode;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public Boolean isDefault() {
    return isDefault;
  }

  public void setDefault(Boolean aDefault) {
    isDefault = aDefault;
  }

  public Boolean isEnabled() {
    return isEnabled;
  }

  public void setEnabled(Boolean enabled) {
    isEnabled = enabled;
  }
}
