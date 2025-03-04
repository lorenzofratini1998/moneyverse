package it.moneyverse.transaction.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import it.moneyverse.core.model.entities.Copyable;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "TRANSFERS")
public class Transfer extends Auditable implements Serializable, Copyable<Transfer> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "TRANSFER_ID")
  private UUID transferId;

  @Column(name = "USER_ID", nullable = false)
  private UUID userId;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "TRANSACTION_FROM_ID", nullable = false, unique = true)
  private Transaction transactionFrom;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "TRANSACTION_TO_ID", nullable = false, unique = true)
  private Transaction transactionTo;

  @Column(name = "DATE", nullable = false)
  private LocalDate date;

  @Column(name = "AMOUNT", nullable = false, precision = 18, scale = 2)
  private BigDecimal amount;

  @Column(name = "CURRENCY", nullable = false, length = 3)
  private String currency;

  public Transfer() {}

  private Transfer(Transfer source) {
    super(source);
    if (source != null) {
      this.transferId = source.transferId;
      this.userId = source.userId;
      this.transactionFrom = source.transactionFrom;
      this.transactionTo = source.transactionTo;
      this.date = source.date;
      this.amount = source.amount;
      this.currency = source.currency;
    }
  }

  @Override
  public Transfer copy() {
    return new Transfer(this);
  }

  public UUID getTransferId() {
    return transferId;
  }

  public void setTransferId(UUID transferId) {
    this.transferId = transferId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public Transaction getTransactionFrom() {
    return transactionFrom;
  }

  public void setTransactionFrom(Transaction transactionFrom) {
    this.transactionFrom = transactionFrom;
  }

  public Transaction getTransactionTo() {
    return transactionTo;
  }

  public void setTransactionTo(Transaction transactionTo) {
    this.transactionTo = transactionTo;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
