package it.moneyverse.transaction.utils;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionRequestItemDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TransactionTestUtils {

  public static TransactionRequestDto createTransactionRequest(UUID userId) {
    return new TransactionRequestDto(userId, List.of(createTransactionRequestItem()));
  }

  public static TransactionRequestDto createTransactionRequest(UUID userId, Set<Tag> tags) {
    return new TransactionRequestDto(userId, List.of(createTransactionRequestItem(tags)));
  }

  private static TransactionRequestItemDto createTransactionRequestItem() {
    return new TransactionRequestItemDto(
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase(),
        null);
  }

  private static TransactionRequestItemDto createTransactionRequestItem(Set<Tag> tags) {
    return new TransactionRequestItemDto(
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase(),
        null);
  }

  public static Tag createTag(UUID userId) {
    Tag tag = new Tag();
    tag.setTagId(RandomUtils.randomUUID());
    tag.setUserId(userId);
    tag.setTagName(RandomUtils.randomString(15));
    tag.setDescription(RandomUtils.randomString(15));
    return tag;
  }

  public static Transaction createTransaction(UUID userId) {
    Transaction transaction = new Transaction();
    transaction.setTransactionId(RandomUtils.randomUUID());
    transaction.setUserId(userId);
    transaction.setAccountId(RandomUtils.randomUUID());
    transaction.setCategoryId(RandomUtils.randomUUID());
    transaction.setDate(RandomUtils.randomLocalDate(2024, 2025));
    transaction.setDescription(RandomUtils.randomString(15));
    transaction.setAmount(RandomUtils.randomBigDecimal());
    transaction.setCurrency(RandomUtils.randomString(3).toUpperCase());
    return transaction;
  }

  public static Set<Tag> createTags(UUID userId) {
    int tagsCount = RandomUtils.randomInteger(1, 3);
    Set<Tag> tags = new HashSet<>();
    for (int i = 0; i < tagsCount; i++) {
      tags.add(createTag(userId));
    }
    return tags;
  }

  public static TransferRequestDto createTransferRequest(UUID userId) {
    return new TransferRequestDto(
        userId,
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(3).toUpperCase());
  }

  public static TransferUpdateRequestDto createTransferUpdateRequest() {
    return new TransferUpdateRequestDto(
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomString(3).toUpperCase());
  }

  public static Transfer createTransfer(UUID userId) {
    Transfer transfer = new Transfer();
    transfer.setTransferId(RandomUtils.randomUUID());
    transfer.setTransactionFrom(createTransaction(userId));
    transfer.setTransactionTo(createTransaction(userId));
    transfer.setDate(RandomUtils.randomLocalDate(2024, 2025));
    transfer.setAmount(RandomUtils.randomBigDecimal());
    transfer.setCurrency(RandomUtils.randomString(3).toUpperCase());
    return transfer;
  }

  private TransactionTestUtils() {}
}
