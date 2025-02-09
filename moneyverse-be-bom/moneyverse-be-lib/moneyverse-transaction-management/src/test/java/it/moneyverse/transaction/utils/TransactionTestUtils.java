package it.moneyverse.transaction.utils;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionRequestItemDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
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
    transaction.setBudgetId(RandomUtils.randomUUID());
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

  private TransactionTestUtils() {}
}
