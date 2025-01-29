package it.moneyverse.account.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "ACCOUNTS")
public class Account extends Auditable implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ACCOUNT_ID")
  private UUID accountId;

  @Column(name = "USERNAME", nullable = false, length = 64)
  private String username;

  @Column(name = "ACCOUNT_NAME", nullable = false)
  private String accountName;

  @Column(name = "BALANCE")
  @ColumnDefault(value = "0.0")
  private BigDecimal balance;

  @Column(name = "BALANCE_TARGET")
  private BigDecimal balanceTarget;

  @Column(name = "ACCOUNT_DESCRIPTION")
  private String accountDescription;

  @Column(name = "IS_DEFAULT")
  @ColumnDefault(value = "FALSE")
  private Boolean isDefault;

  @Column(name = "CURRENCY", nullable = false, length = 3)
  private String currency;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ACCOUNT_CATEGORY", nullable = false)
  private AccountCategory accountCategory;

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public BigDecimal getBalanceTarget() {
    return balanceTarget;
  }

  public void setBalanceTarget(BigDecimal balanceTarget) {
    this.balanceTarget = balanceTarget;
  }

  public String getAccountDescription() {
    return accountDescription;
  }

  public void setAccountDescription(String accountDescription) {
    this.accountDescription = accountDescription;
  }

  public Boolean isDefault() {
    return isDefault;
  }

  public void setDefault(Boolean isDefault) {
    this.isDefault = isDefault;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public AccountCategory getAccountCategory() {
    return accountCategory;
  }

  public void setAccountCategory(AccountCategory accountCategory) {
    this.accountCategory = accountCategory;
  }
}
