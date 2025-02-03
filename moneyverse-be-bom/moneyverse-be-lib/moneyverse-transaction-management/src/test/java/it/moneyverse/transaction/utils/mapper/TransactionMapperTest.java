package it.moneyverse.transaction.utils.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionMapperTest {

  @Mock private TagRepository tagRepository;

  @Test
  void testToTransaction_NullTransactionRequest() {
    assertNull(TransactionMapper.toTransaction(null, tagRepository));
  }

  @Test
  void testToTransaction_ValidTransactionRequest_EmptyTags() {
    TransactionRequestDto request =
        new TransactionRequestDto(
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomLocalDate(2024, 2025),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(3).toUpperCase(),
            null);

    Transaction result = TransactionMapper.toTransaction(request, tagRepository);

    assertEquals(request.userId(), result.getUserId());
    assertEquals(request.accountId(), result.getAccountId());
    assertEquals(request.budgetId(), result.getBudgetId());
    assertEquals(request.date(), result.getDate());
    assertEquals(request.description(), result.getDescription());
    assertEquals(request.currency(), result.getCurrency());
    assertEquals(Collections.emptySet(), result.getTags());
  }

  @Test
  void testToTransaction_ValidTransactionRequest_Tags(@Mock Tag tag) {
    UUID tagId = RandomUtils.randomUUID();
    TransactionRequestDto request =
        new TransactionRequestDto(
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomLocalDate(2024, 2025),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(3).toUpperCase(),
            Collections.singleton(tagId));
    when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

    Transaction result = TransactionMapper.toTransaction(request, tagRepository);

    assertEquals(request.userId(), result.getUserId());
    assertEquals(request.accountId(), result.getAccountId());
    assertEquals(request.budgetId(), result.getBudgetId());
    assertEquals(request.date(), result.getDate());
    assertEquals(request.description(), result.getDescription());
    assertEquals(request.currency(), result.getCurrency());
    assertEquals(Collections.singleton(tag), result.getTags());
  }

  @Test
  void testToTransaction_ValidTransactionRequest_TagNotFound() {
    UUID tagId = RandomUtils.randomUUID();
    TransactionRequestDto request =
        new TransactionRequestDto(
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomLocalDate(2024, 2025),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(3).toUpperCase(),
            Collections.singleton(tagId));
    when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> TransactionMapper.toTransaction(request, tagRepository));
  }

  @Test
  void testToTransactionDto_NullTransactionEntity() {
    assertNull(TransactionMapper.toTransactionDto((Transaction) null));
  }

  @Test
  void testToTransactionDto_ValidTransactionEntity() {
    Transaction transaction = createTransaction();

    TransactionDto result = TransactionMapper.toTransactionDto(transaction);

    assertEquals(transaction.getTransactionId(), result.getTransactionId());
    assertEquals(transaction.getUserId(), result.getUserId());
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
      transactions.add(createTransaction());
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
  void testToTransaction_PartialUpdate(@Mock Tag tag) {
    Transaction transaction = createTransaction();
    TransactionUpdateRequestDto request =
        new TransactionUpdateRequestDto(
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomLocalDate(2024, 2024),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(3).toUpperCase(),
            createTags().stream().map(Tag::getTagId).collect(Collectors.toSet()));
    when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.of(tag));

    Transaction result = TransactionMapper.partialUpdate(transaction, request, tagRepository);

    assertEquals(request.accountId(), result.getAccountId());
    assertEquals(request.budgetId(), result.getBudgetId());
    assertEquals(request.date(), result.getDate());
    assertEquals(request.description(), result.getDescription());
    assertEquals(request.currency(), result.getCurrency());
    assertEquals(1, result.getTags().size());
  }

  @Test
  void testToTransaction_PartialUpdate_TagNotFound() {
    Transaction transaction = createTransaction();
    TransactionUpdateRequestDto request =
        new TransactionUpdateRequestDto(
            RandomUtils.randomUUID(),
            RandomUtils.randomUUID(),
            RandomUtils.randomLocalDate(2024, 2024),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(3).toUpperCase(),
            createTags().stream().map(Tag::getTagId).collect(Collectors.toSet()));
    when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> TransactionMapper.partialUpdate(transaction, request, tagRepository));
  }

  private Transaction createTransaction() {
    Transaction transaction = new Transaction();
    transaction.setTransactionId(RandomUtils.randomUUID());
    transaction.setUserId(RandomUtils.randomUUID());
    transaction.setAccountId(RandomUtils.randomUUID());
    transaction.setBudgetId(RandomUtils.randomUUID());
    transaction.setDate(RandomUtils.randomLocalDate(2024, 2025));
    transaction.setDescription(RandomUtils.randomString(15));
    transaction.setAmount(RandomUtils.randomBigDecimal());
    transaction.setCurrency(RandomUtils.randomString(3).toUpperCase());
    transaction.setTags(createTags());
    return transaction;
  }

  private Set<Tag> createTags() {
    int tagsCount = RandomUtils.randomInteger(1, 3);
    Set<Tag> tags = new HashSet<>();
    for (int i = 0; i < tagsCount; i++) {
      tags.add(TagMapperTest.createTag());
    }
    return tags;
  }
}
