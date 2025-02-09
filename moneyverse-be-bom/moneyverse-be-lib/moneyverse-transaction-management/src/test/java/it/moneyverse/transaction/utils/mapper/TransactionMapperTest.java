package it.moneyverse.transaction.utils.mapper;

import static it.moneyverse.transaction.utils.TransactionTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionRequestItemDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class TransactionMapperTest {

  @Test
  void testToTransaction_NullTransactionRequest() {
    assertNull(TransactionMapper.toTransaction(RandomUtils.randomUUID(), null));
  }

  @Test
  void testToTransaction_ValidTransactionRequest_EmptyTags() {
    UUID userId = RandomUtils.randomUUID();
    TransactionRequestDto request = createTransactionRequest(userId);
    TransactionRequestItemDto transactionRequestItemDto = request.transactions().getFirst();

    Transaction result = TransactionMapper.toTransaction(userId, transactionRequestItemDto);

    assertEquals(request.userId(), result.getUserId());
    assertEquals(transactionRequestItemDto.accountId(), result.getAccountId());
    assertEquals(transactionRequestItemDto.categoryId(), result.getBudgetId());
    assertEquals(transactionRequestItemDto.date(), result.getDate());
    assertEquals(transactionRequestItemDto.description(), result.getDescription());
    assertEquals(transactionRequestItemDto.currency(), result.getCurrency());
    assertEquals(Collections.emptySet(), result.getTags());
  }

  @Test
  void testToTransaction_ValidTransactionRequest_Tags() {
    UUID userId = RandomUtils.randomUUID();
    Set<Tag> tags = Set.of(createTag(userId));
    TransactionRequestDto request = createTransactionRequest(userId, tags);
    TransactionRequestItemDto transactionRequestItemDto = request.transactions().getFirst();

    Transaction result = TransactionMapper.toTransaction(userId, transactionRequestItemDto, tags);

    assertEquals(request.userId(), result.getUserId());
    assertEquals(transactionRequestItemDto.accountId(), result.getAccountId());
    assertEquals(transactionRequestItemDto.categoryId(), result.getBudgetId());
    assertEquals(transactionRequestItemDto.date(), result.getDate());
    assertEquals(transactionRequestItemDto.description(), result.getDescription());
    assertEquals(transactionRequestItemDto.currency(), result.getCurrency());
    assertEquals(tags, result.getTags());
  }

  @Test
  void testToTransactionDto_NullTransactionEntity() {
    assertNull(TransactionMapper.toTransactionDto((Transaction) null));
  }

  @Test
  void testToTransactionDto_ValidTransactionEntity() {
    Transaction transaction = createTransaction(RandomUtils.randomUUID());

    TransactionDto result = TransactionMapper.toTransactionDto(transaction);

    assertEquals(transaction.getTransactionId(), result.getTransactionId());
    assertEquals(transaction.getAccountId(), result.getAccountId());
    assertEquals(transaction.getBudgetId(), result.getBudgetId());
    assertEquals(transaction.getDate(), result.getDate());
    assertEquals(transaction.getDescription(), result.getDescription());
    assertEquals(transaction.getAmount(), result.getAmount());
    assertEquals(transaction.getCurrency(), result.getCurrency());
    assertEquals(transaction.getTags().size(), result.getTags().size());
  }

  @Test
  void testToTransactionDto_EmptyTransactionList() {
    assertEquals(
        Collections.emptyList(), TransactionMapper.toTransactionDto(Collections.emptyList()));
  }

  @Test
  void testToTransactionDto_NonEmptyTransactionList() {
    int entitiesCount = RandomUtils.randomInteger(0, 10);
    List<Transaction> transactions = new ArrayList<>(entitiesCount);
    for (int i = 0; i < entitiesCount; i++) {
      transactions.add(createTransaction(RandomUtils.randomUUID()));
    }
    List<TransactionDto> transactionDtos = TransactionMapper.toTransactionDto(transactions);

    for (int i = 0; i < entitiesCount; i++) {
      Transaction transaction = transactions.get(i);
      TransactionDto transactionDto = transactionDtos.get(i);

      assertEquals(transaction.getTransactionId(), transactionDto.getTransactionId());
      assertEquals(transaction.getUserId(), transactionDto.getUserId());
      assertEquals(transaction.getAccountId(), transactionDto.getAccountId());
      assertEquals(transaction.getBudgetId(), transactionDto.getBudgetId());
      assertEquals(transaction.getDate(), transactionDto.getDate());
      assertEquals(transaction.getDescription(), transactionDto.getDescription());
      assertEquals(transaction.getAmount(), transactionDto.getAmount());
      assertEquals(transaction.getCurrency(), transactionDto.getCurrency());
      assertEquals(transaction.getTags().size(), transactionDto.getTags().size());
    }
  }

  @Test
  void testToTransaction_PartialUpdate() {
    UUID userId = RandomUtils.randomUUID();
    Transaction transaction = createTransaction(userId);
    Set<Tag> tags = createTags(userId);
    TransactionUpdateRequestDto request =
        new TransactionUpdateRequestDto(
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomLocalDate(2024, 2024),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(3).toUpperCase(),
            tags.stream().map(Tag::getTagId).collect(Collectors.toSet()));

    Transaction result = TransactionMapper.partialUpdate(transaction, request, tags);

    assertEquals(request.accountId(), result.getAccountId());
    assertEquals(request.budgetId(), result.getBudgetId());
    assertEquals(request.date(), result.getDate());
    assertEquals(request.description(), result.getDescription());
    assertEquals(request.currency(), result.getCurrency());
    assertEquals(tags.size(), result.getTags().size());
  }

}
