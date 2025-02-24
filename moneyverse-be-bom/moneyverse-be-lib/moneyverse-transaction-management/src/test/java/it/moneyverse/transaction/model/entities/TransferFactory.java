package it.moneyverse.transaction.model.entities;

import static it.moneyverse.test.utils.FakeUtils.FAKE_USER;

import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransferFactory {

  public static List<Transfer> createTransfers(
      List<UserModel> users, List<Transaction> transactions) {
    List<Transfer> transfers =
        users.stream().map(user -> createTransfer(user, transactions)).toList();
    transactions.addAll(transfers.stream().map(Transfer::getTransactionFrom).toList());
    transactions.addAll(transfers.stream().map(Transfer::getTransactionTo).toList());
    return transfers;
  }

  private static Transfer createTransfer(UserModel user, List<Transaction> transactions) {
    List<UUID> userAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(user.getUserId()))
            .map(Transaction::getAccountId)
            .distinct()
            .collect(Collectors.toList());
    Collections.shuffle(userAccounts);
    TransferRequestDto request =
        new TransferRequestDto(
            user.getUserId(),
            userAccounts.get(0),
            userAccounts.get(1),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomLocalDate(2025, 2025),
            RandomUtils.randomString(3).toUpperCase());
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
}
