package it.moneyverse.transaction.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction extends Auditable implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "TRANSACTION_ID")
  private UUID transactionId;

  @Column(name = "USER_ID", nullable = false)
  private UUID userId;

  @Column(name = "ACCOUNT_ID", nullable = false)
  private UUID accountId;

  @Column(name = "CATEGORY_ID")
  private UUID categoryId;

  @Column(name = "DATE", nullable = false)
  private LocalDate date;

  @Column(name = "DESCRIPTION", nullable = false)
  private String description;

  @Column(name = "AMOUNT", nullable = false)
  private BigDecimal amount;

  @Column(name = "NORMALIZED_AMOUNT", nullable = false)
  private BigDecimal normalizedAmount = amount;

  @Column(name = "CURRENCY", nullable = false, length = 3)
  private String currency;

  @ManyToOne
  @JoinColumn(name = "TRANSFER_ID")
  private Transfer transfer;

  @ManyToOne
  @JoinColumn(name = "SUBSCRIPTION_ID")
  private Subscription subscription;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "TRANSACTIONS_TAGS",
      joinColumns = @JoinColumn(name = "TRANSACTION_ID"),
      inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
  private Set<Tag> tags = new HashSet<>();

  public void addTag(Tag tag) {
    tags.add(tag);
    tag.getTransactions().add(this);
  }

  public void removeTag(Tag tag) {
    tags.remove(tag);
    tag.getTransactions().remove(this);
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(UUID transactionId) {
    this.transactionId = transactionId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public BigDecimal getNormalizedAmount() {
    return normalizedAmount;
  }

  public void setNormalizedAmount(BigDecimal normalizedAmount) {
    this.normalizedAmount = normalizedAmount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Transfer getTransfer() {
    return transfer;
  }

  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

  public Subscription getSubscription() {
    return subscription;
  }

  public void setSubscription(Subscription subscription) {
    this.subscription = subscription;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }
}
