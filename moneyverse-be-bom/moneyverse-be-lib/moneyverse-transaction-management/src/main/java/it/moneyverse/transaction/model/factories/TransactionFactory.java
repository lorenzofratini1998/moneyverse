package it.moneyverse.transaction.model.factories;

import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.entities.Subscription;
import it.moneyverse.transaction.model.entities.Transaction;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionFactory {

  public static Transaction createDebitTransaction(
      TransferRequestDto request,
      BigDecimal normalizedAmount,
      AccountDto fromAccount,
      AccountDto toAccount) {
    Transaction transaction = new Transaction();
    transaction.setUserId(request.userId());
    transaction.setAccountId(request.fromAccount());
    transaction.setDate(request.date());
    transaction.setDescription(fromAccount.getAccountName() + " ⇌ " + toAccount.getAccountName());
    transaction.setCurrency(request.currency());
    transaction.setAmount(request.amount().negate());
    transaction.setNormalizedAmount(normalizedAmount.negate());
    return transaction;
  }

  public static Transaction createCreditTransaction(
      TransferRequestDto request,
      BigDecimal normalizedAmount,
      AccountDto fromAccount,
      AccountDto toAccount) {
    Transaction transaction = new Transaction();
    transaction.setUserId(request.userId());
    transaction.setAccountId(request.toAccount());
    transaction.setDate(request.date());
    transaction.setDescription(fromAccount.getAccountName() + " ⇌ " + toAccount.getAccountName());
    transaction.setCurrency(request.currency());
    transaction.setAmount(request.amount());
    transaction.setNormalizedAmount(normalizedAmount);
    return transaction;
  }

  public static Transaction createTransaction(
      Subscription subscription, LocalDate date, UUID budgetId, BigDecimal normalizedAmount) {
    Transaction transaction = new Transaction();
    transaction.setUserId(subscription.getUserId());
    return setTransaction(transaction, subscription, date, budgetId, normalizedAmount);
  }

  public static void updateTransaction(
      Transaction transaction,
      Subscription subscription,
      LocalDate date,
      UUID budgetId,
      BigDecimal normalizedAmount) {
    setTransaction(transaction, subscription, date, budgetId, normalizedAmount);
  }

  @NotNull
  private static Transaction setTransaction(
      Transaction transaction,
      Subscription subscription,
      LocalDate date,
      UUID budgetId,
      BigDecimal normalizedAmount) {
    transaction.setAccountId(subscription.getAccountId());
    transaction.setCategoryId(subscription.getCategoryId());
    transaction.setBudgetId(budgetId);
    transaction.setAmount(subscription.getAmount().negate());
    transaction.setNormalizedAmount(normalizedAmount.negate());
    transaction.setCurrency(subscription.getCurrency());
    transaction.setDescription(subscription.getSubscriptionName());
    transaction.setDate(date);
    return transaction;
  }

  private TransactionFactory() {}
}
