package it.moneyverse.transaction.model;

import static it.moneyverse.test.model.TestFactory.FAKE_USER;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TransferTestFactory {

  private static final Supplier<UUID> FAKE_USER_ID_SUPPLIER = RandomUtils::randomUUID;
  private static final Supplier<BigDecimal> FAKE_AMOUNT_SUPPLIER = RandomUtils::randomBigDecimal;
  private static final Supplier<LocalDate> FAKE_DATE_SUPPLIER = RandomUtils::randomDate;
  private static final Supplier<String> FAKE_CURRENCY_SUPPLIER = RandomUtils::randomCurrency;
  private static final Supplier<UUID> FAKE_ACCOUNT_FROM_SUPPLIER = RandomUtils::randomUUID;
  private static final Supplier<UUID> FAKE_ACCOUNT_TO_SUPPLIER = RandomUtils::randomUUID;

  public static List<Transfer> createTransfers(
      List<UserModel> users, List<Transaction> transactions) {
    List<Transfer> transfers =
        users.stream().map(user -> createUserTransfer(user, transactions)).toList();
    transactions.addAll(transfers.stream().map(Transfer::getTransactionFrom).toList());
    transactions.addAll(transfers.stream().map(Transfer::getTransactionTo).toList());
    return transfers;
  }

  private static Transfer createUserTransfer(UserModel user, List<Transaction> transactions) {
    List<UUID> userAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(user.getUserId()))
            .map(Transaction::getAccountId)
            .distinct()
            .collect(Collectors.toList());
    Collections.shuffle(userAccounts);
    TransferRequestDto request =
        TransferRequestBuilder.builder()
            .withUserId(user.getUserId())
            .withFromAccount(userAccounts.getFirst())
            .withToAccount(userAccounts.get(1))
            .build();
    Transaction transactionFrom = createFromTransaction(request);
    Transaction transactionTo = createToTransaction(request);
    Transfer transfer = createFakeTransfer(transactionFrom, transactionTo, request);
    transactionFrom.setTransfer(transfer);
    transactionTo.setTransfer(transfer);
    return transfer;
  }

  private static Transaction createFromTransaction(TransferRequestDto request) {
    Transaction transaction = createTransaction(request);
    transaction.setAccountId(request.fromAccount());
    transaction.setAmount(request.amount().multiply(BigDecimal.valueOf(-1)));
    transaction.setNormalizedAmount(transaction.getAmount());
    transaction.setDescription("Transfer to %s".formatted(request.toAccount()));
    return transaction;
  }

  private static Transaction createToTransaction(TransferRequestDto request) {
    Transaction transaction = createTransaction(request);
    transaction.setAccountId(request.toAccount());
    transaction.setAmount(request.amount());
    transaction.setNormalizedAmount(transaction.getAmount());
    transaction.setDescription("Transfer from %s".formatted(request.fromAccount()));
    return transaction;
  }

  private static Transaction createTransaction(TransferRequestDto request) {
    Transaction transaction = new Transaction();
    transaction.setTransactionId(RandomUtils.randomUUID());
    transaction.setUserId(request.userId());
    transaction.setDate(request.date());
    transaction.setCurrency(request.currency());
    transaction.setCreatedBy(FAKE_USER);
    transaction.setCreatedAt(LocalDateTime.now());
    transaction.setUpdatedBy(FAKE_USER);
    transaction.setUpdatedAt(LocalDateTime.now());
    return transaction;
  }

  private static Transfer createFakeTransfer(
      Transaction transactionFrom, Transaction transactionTo, TransferRequestDto request) {
    Transfer transfer = new Transfer();
    transfer.setTransferId(RandomUtils.randomUUID());
    transfer.setUserId(request.userId());
    transfer.setTransactionFrom(transactionFrom);
    transfer.setTransactionTo(transactionTo);
    transfer.setAmount(request.amount());
    transfer.setDate(request.date());
    transfer.setCurrency(request.currency());
    transfer.setCreatedBy(FAKE_USER);
    transfer.setCreatedAt(LocalDateTime.now());
    transfer.setUpdatedBy(FAKE_USER);
    transfer.setUpdatedAt(LocalDateTime.now());
    return transfer;
  }

  public static Transfer fakeTransfer(UUID userId) {
    Transfer transfer = new Transfer();
    transfer.setTransferId(RandomUtils.randomUUID());
    transfer.setUserId(userId);
    transfer.setTransactionFrom(TransactionTestFactory.fakeTransaction(userId));
    transfer.setTransactionTo(TransactionTestFactory.fakeTransaction(userId));
    transfer.setAmount(FAKE_AMOUNT_SUPPLIER.get());
    transfer.setDate(FAKE_DATE_SUPPLIER.get());
    transfer.setCurrency(FAKE_CURRENCY_SUPPLIER.get());
    transfer.setCreatedBy(FAKE_USER);
    transfer.setCreatedAt(LocalDateTime.now());
    transfer.setUpdatedBy(FAKE_USER);
    transfer.setUpdatedAt(LocalDateTime.now());
    return transfer;
  }

  public static class TransferRequestBuilder {
    private UUID userId = FAKE_USER_ID_SUPPLIER.get();
    private UUID fromAccount = FAKE_ACCOUNT_FROM_SUPPLIER.get();
    private UUID toAccount = FAKE_ACCOUNT_TO_SUPPLIER.get();
    private final BigDecimal amount = FAKE_AMOUNT_SUPPLIER.get();
    private final LocalDate date = FAKE_DATE_SUPPLIER.get();
    private final String currency = FAKE_CURRENCY_SUPPLIER.get();

    public TransferRequestBuilder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public TransferRequestBuilder withFromAccount(UUID fromAccount) {
      this.fromAccount = fromAccount;
      return this;
    }

    public TransferRequestBuilder withToAccount(UUID toAccount) {
      this.toAccount = toAccount;
      return this;
    }

    public static TransferRequestDto defaultInstance() {
      return builder().build();
    }

    public static TransferRequestBuilder builder() {
      return new TransferRequestBuilder();
    }

    public TransferRequestDto build() {
      return new TransferRequestDto(userId, fromAccount, toAccount, amount, date, currency);
    }
  }

  public static class TransferUpdateRequestBuilder {
    private UUID fromAccount = FAKE_ACCOUNT_FROM_SUPPLIER.get();
    private UUID toAccount = FAKE_ACCOUNT_TO_SUPPLIER.get();
    private final BigDecimal amount = FAKE_AMOUNT_SUPPLIER.get();
    private final LocalDate date = FAKE_DATE_SUPPLIER.get();
    private final String currency = FAKE_CURRENCY_SUPPLIER.get();

    public TransferUpdateRequestBuilder withFromAccount(UUID fromAccount) {
      this.fromAccount = fromAccount;
      return this;
    }

    public TransferUpdateRequestBuilder withToAccount(UUID toAccount) {
      this.toAccount = toAccount;
      return this;
    }

    public static TransferUpdateRequestDto defaultInstance() {
      return builder().build();
    }

    public static TransferUpdateRequestBuilder builder() {
      return new TransferUpdateRequestBuilder();
    }

    public TransferUpdateRequestDto build() {
      return new TransferUpdateRequestDto(fromAccount, toAccount, amount, date, currency);
    }
  }
}
