package it.moneyverse.currency.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyDto implements Serializable {

  private final UUID currencyId;
  private final String code;
  private final String name;
  private final String country;

  public CurrencyDto(Builder builder) {
    this.currencyId = builder.currencyId;
    this.code = builder.code;
    this.name = builder.name;
    this.country = builder.country;
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

  public static class Builder {
    private UUID currencyId;
    private String code;
    private String name;
    private String country;

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

    public CurrencyDto build() {
      return new CurrencyDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
