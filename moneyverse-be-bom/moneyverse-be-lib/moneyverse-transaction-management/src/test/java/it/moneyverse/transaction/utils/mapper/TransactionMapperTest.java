package it.moneyverse.transaction.utils.mapper;

import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.TagTestFactory;
import it.moneyverse.transaction.model.TransactionTestFactory;
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
    assertNull(TransactionMapper.toTransaction(RandomUtils.randomUUID(), null, null));
  }

  @Test
  void testToTransaction_ValidTransactionRequest_EmptyTags() {
    TransactionRequestDto request =
        TransactionTestFactory.TransactionRequestBuilder.defaultInstance();
    TransactionRequestItemDto transactionRequestItemDto = request.transactions().getFirst();

    Transaction result =
        TransactionMapper.toTransaction(
            request.userId(),
            transactionRequestItemDto,
            Set.of(TagTestFactory.fakeTag(request.userId())));

    assertEquals(request.userId(), result.getUserId());
    assertEquals(transactionRequestItemDto.accountId(), result.getAccountId());
    assertEquals(transactionRequestItemDto.categoryId(), result.getCategoryId());
    assertEquals(transactionRequestItemDto.date(), result.getDate());
    assertEquals(transactionRequestItemDto.description(), result.getDescription());
    assertEquals(transactionRequestItemDto.currency(), result.getCurrency());
    assertEquals(transactionRequestItemDto.tags().size(), result.getTags().size());
  }

  @Test
  void testToTransactionDto_NullTransactionEntity() {
    assertNull(TransactionMapper.toTransactionDto((Transaction) null));
  }

  @Test
  void testToTransactionDto_ValidTransactionEntity() {
    Transaction transaction = TransactionTestFactory.fakeTransaction(RandomUtils.randomUUID());

    TransactionDto result = TransactionMapper.toTransactionDto(transaction);

    assertEquals(transaction.getTransactionId(), result.getTransactionId());
    assertEquals(transaction.getUserId(), result.getUserId());
    assertEquals(transaction.getAccountId(), result.getAccountId());
    assertEquals(transaction.getCategoryId(), result.getCategoryId());
    assertEquals(transaction.getBudgetId(), result.getBudgetId());
    assertEquals(transaction.getDate(), result.getDate());
    assertEquals(transaction.getDescription(), result.getDescription());
    assertEquals(transaction.getAmount(), result.getAmount());
    assertEquals(transaction.getNormalizedAmount(), result.getNormalizedAmount());
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
    int entitiesCount = RandomUtils.randomInteger(10);
    List<Transaction> transactions = new ArrayList<>(entitiesCount);
    for (int i = 0; i < entitiesCount; i++) {
      transactions.add(TransactionTestFactory.fakeTransaction(RandomUtils.randomUUID()));
    }
    List<TransactionDto> transactionDtos = TransactionMapper.toTransactionDto(transactions);

    for (int i = 0; i < entitiesCount; i++) {
      Transaction transaction = transactions.get(i);
      TransactionDto transactionDto = transactionDtos.get(i);

      assertEquals(transaction.getTransactionId(), transactionDto.getTransactionId());
      assertEquals(transaction.getUserId(), transactionDto.getUserId());
      assertEquals(transaction.getAccountId(), transactionDto.getAccountId());
      assertEquals(transaction.getCategoryId(), transactionDto.getCategoryId());
      assertEquals(transaction.getBudgetId(), transactionDto.getBudgetId());
      assertEquals(transaction.getDate(), transactionDto.getDate());
      assertEquals(transaction.getDescription(), transactionDto.getDescription());
      assertEquals(transaction.getAmount(), transactionDto.getAmount());
      assertEquals(transaction.getNormalizedAmount(), transactionDto.getNormalizedAmount());
      assertEquals(transaction.getCurrency(), transactionDto.getCurrency());
      assertEquals(transaction.getTags().size(), transactionDto.getTags().size());
    }
  }

  @Test
  void testToTransaction_PartialUpdate() {
    Transaction transaction = TransactionTestFactory.fakeTransaction(RandomUtils.randomUUID());
    Set<Tag> tags = TagTestFactory.fakeTags(RandomUtils.randomUUID());
    TransactionUpdateRequestDto request =
        TransactionTestFactory.TransactionUpdateRequestBuilder.builder()
            .withTags(tags.stream().map(Tag::getTagId).collect(Collectors.toSet()))
            .build();

    Transaction result = TransactionMapper.partialUpdate(transaction, request, tags);

    assertEquals(request.accountId(), result.getAccountId());
    assertEquals(request.categoryId(), result.getCategoryId());
    assertEquals(request.date(), result.getDate());
    assertEquals(request.description(), result.getDescription());
    assertEquals(request.amount(), result.getAmount());
    assertEquals(request.currency(), result.getCurrency());
    assertEquals(tags.size(), result.getTags().size());
  }
}
