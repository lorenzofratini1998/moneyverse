package it.moneyverse.currency.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CurrencyDto.Builder.class)
public class CurrencyDto implements Serializable {

  private final UUID currencyId;
  private final String code;
  private final String name;
  private final String country;
  private final Boolean isDefault;
  private final Boolean isEnabled;

  public CurrencyDto(Builder builder) {
    this.currencyId = builder.currencyId;
    this.code = builder.code;
    this.name = builder.name;
    this.country = builder.country;
    this.isDefault = builder.isDefault;
    this.isEnabled = builder.isEnabled;
  }

  public UUID getCurrencyId() {
    return currencyId;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getCountry() {
    return country;
  }

  public Boolean isDefault() {
    return isDefault;
  }

  public Boolean isEnabled() {
    return isEnabled;
  }

  public static class Builder {
    private UUID currencyId;
    private String code;
    private String name;
    private String country;
    private Boolean isDefault;
    private Boolean isEnabled;

    public Builder withCurrencyId(UUID currencyId) {
      this.currencyId = currencyId;
      return this;
    }

    public Builder withCode(String code) {
      this.code = code;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withCountry(String country) {
      this.country = country;
      return this;
    }

    public Builder withDefault(Boolean isDefault) {
      this.isDefault = isDefault;
      return this;
    }

    public Builder withEnabled(Boolean isEnabled) {
      this.isEnabled = isEnabled;
      return this;
    }

    public CurrencyDto build() {
      return new CurrencyDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
