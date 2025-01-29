package it.moneyverse.currency.model.entities;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(
    name = "EXCHANGE_RATES",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"DATE", "CURRENCY_TO"})})
public class ExchangeRate implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "EXCHANGE_RATE_ID")
  private UUID exchangeRateId;

  @Column(name = "DATE", nullable = false)
  private LocalDate date;

  @Column(name = "CURRENCY_FROM", nullable = false, length = 3)
  @ColumnDefault(value = "'EUR'")
  private String currencyFrom;

  @Column(name = "CURRENCY_TO", nullable = false, length = 3)
  private String currencyTo;

  @Column(name = "RATE", nullable = false)
  private BigDecimal rate;

  public UUID getExchangeRateId() {
    return exchangeRateId;
  }

  public void setExchangeRateId(UUID exchangeRateId) {
    this.exchangeRateId = exchangeRateId;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getCurrencyFrom() {
    return currencyFrom;
  }

  public void setCurrencyFrom(String currencyFrom) {
    this.currencyFrom = currencyFrom;
  }

  public String getCurrencyTo() {
    return currencyTo;
  }

  public void setCurrencyTo(String currencyTo) {
    this.currencyTo = currencyTo;
  }

  public BigDecimal getRate() {
    return rate;
  }

  public void setRate(BigDecimal rate) {
    this.rate = rate;
  }
}
