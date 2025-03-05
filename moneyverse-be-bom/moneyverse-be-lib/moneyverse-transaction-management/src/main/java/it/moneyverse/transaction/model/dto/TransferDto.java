package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = TransferDto.Builder.class)
public class TransferDto {

  private final UUID transferId;
  private final UUID userId;
  private final LocalDate date;
  private final BigDecimal amount;
  private final String currency;
  private final TransactionDto transactionFrom;
  private final TransactionDto transactionTo;

  public TransferDto(Builder builder) {
    this.transferId = builder.transferId;
    this.userId = builder.userId;
    this.date = builder.date;
    this.amount = builder.amount;
    this.currency = builder.currency;
    this.transactionFrom = builder.transactionFrom;
    this.transactionTo = builder.transactionTo;
  }

  public UUID getTransferId() {
    return transferId;
  }

  public UUID getUserId() {
    return userId;
  }

  public LocalDate getDate() {
    return date;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public TransactionDto getTransactionFrom() {
    return transactionFrom;
  }

  public TransactionDto getTransactionTo() {
    return transactionTo;
  }

  public static class Builder {
    private UUID transferId;
    private UUID userId;
    private LocalDate date;
    private BigDecimal amount;
    private String currency;
    private TransactionDto transactionFrom;
    private TransactionDto transactionTo;

    public Builder withTransferId(UUID transferId) {
      this.transferId = transferId;
      return this;
    }

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withDate(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder withAmount(BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder withCurrency(String currency) {
      this.currency = currency;
      return this;
    }

    public Builder withTransactionFrom(TransactionDto transactionFrom) {
      this.transactionFrom = transactionFrom;
      return this;
    }

    public Builder withTransactionTo(TransactionDto transactionTo) {
      this.transactionTo = transactionTo;
      return this;
    }

    public TransferDto build() {
      return new TransferDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
