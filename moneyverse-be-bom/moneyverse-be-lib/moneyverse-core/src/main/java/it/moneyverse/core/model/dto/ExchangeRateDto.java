package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ExchangeRateDto.Builder.class)
public class ExchangeRateDto implements Serializable {

  private final String currencyFrom;
  private final String currencyTo;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private final LocalDate date;

  private final BigDecimal rate;

  public ExchangeRateDto(Builder builder) {
    this.currencyFrom = builder.currencyFrom;
    this.currencyTo = builder.currencyTo;
    this.date = builder.date;
    this.rate = builder.rate;
  }

  public String getCurrencyFrom() {
    return currencyFrom;
  }

  public String getCurrencyTo() {
    return currencyTo;
  }

  public LocalDate getDate() {
    return date;
  }

  public BigDecimal getRate() {
    return rate;
  }

  public static class Builder {
    private String currencyFrom;
    private String currencyTo;
    private LocalDate date;
    private BigDecimal rate;

    public Builder withCurrencyFrom(String currencyFrom) {
      this.currencyFrom = currencyFrom;
      return this;
    }

    public Builder withCurrencyTo(String currencyTo) {
      this.currencyTo = currencyTo;
      return this;
    }

    public Builder withDate(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder withRate(BigDecimal rate) {
      this.rate = rate;
      return this;
    }

    public ExchangeRateDto build() {
      return new ExchangeRateDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
