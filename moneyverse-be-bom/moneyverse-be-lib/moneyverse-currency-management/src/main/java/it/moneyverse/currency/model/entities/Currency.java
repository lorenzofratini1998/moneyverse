package it.moneyverse.currency.model.entities;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "CURRENCIES")
public class Currency implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "CURRENCY_ID")
  private UUID currencyId;

  @Column(name = "ISO_CODE", length = 3, nullable = false, unique = true)
  private String code;

  @Column(name = "CURRENCY_NAME", length = 50)
  private String name;

  @Column(name = "COUNTRY", length = 50)
  private String country;

  @Column(name = "IS_DEFAULT", nullable = false)
  @ColumnDefault("FALSE")
  private Boolean isDefault;

  @Column(name = "IS_ENABLED", nullable = false)
  @ColumnDefault("TRUE")
  private Boolean isEnabled;

  public UUID getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(UUID currencyId) {
    this.currencyId = currencyId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
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
