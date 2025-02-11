package it.moneyverse.transaction.services;

import static it.moneyverse.transaction.utils.TransactionTestUtils.createTransactionRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionManagementServiceTest {

  @InjectMocks private TransactionManagementService transactionManagementService;

  @Mock private TransactionServiceClient transactionServiceClient;
  @Mock private TransactionRepository transactionRepository;
  @Mock private TransferRepository transferRepository;
  @Mock private TagRepository tagRepository;
  private MockedStatic<TransactionMapper> transactionMapper;

  @BeforeEach
  void setUp() {
    transactionMapper = mockStatic(TransactionMapper.class);
  }

  @AfterEach
  void tearDown() {
    transactionMapper.close();
  }

  @Test
  void givenTransactionRequest_WhenCreateTransactions_ThenAccountNotFound() {
    UUID userId = RandomUtils.randomUUID();
    TransactionRequestDto request = createTransactionRequest(userId);
    TransactionRequestItemDto item = request.transactions().getFirst();
    Mockito.doThrow(ResourceNotFoundException.class)
        .when(transactionServiceClient)
        .checkIfAccountExists(item.accountId());

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.createTransactions(request));

    verify(transactionServiceClient, times(1)).checkIfAccountExists(item.accountId());
    verify(transactionServiceClient, never()).checkIfCategoryExists(any(UUID.class));
    transactionMapper.verify(() -> TransactionMapper.toTransaction(userId, item), never());
    verify(transactionRepository, never()).saveAll(any(List.class));
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(any(List.class)), never());
  }

  @Test
  void givenTransactionRequest_WhenCreateTransactions_ThenCategoryNotFound() {
    UUID userId = RandomUtils.randomUUID();
    TransactionRequestDto request = createTransactionRequest(userId);
    TransactionRequestItemDto item = request.transactions().getFirst();
    Mockito.doNothing().when(transactionServiceClient).checkIfAccountExists(item.accountId());
    Mockito.doThrow(ResourceNotFoundException.class)
        .when(transactionServiceClient)
        .checkIfCategoryExists(item.categoryId());

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.createTransactions(request));

    verify(transactionServiceClient, times(1)).checkIfAccountExists(item.accountId());
    verify(transactionServiceClient, times(1)).checkIfCategoryExists(item.categoryId());
    transactionMapper.verify(() -> TransactionMapper.toTransaction(userId, item), never());
    verify(transactionRepository, never()).saveAll(any(List.class));
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(any(List.class)), never());
  }

  @Test
  void givenTransactionRequest_WhenCreateTransactions_ThenCurrencyNotFound() {
    UUID userId = RandomUtils.randomUUID();
    TransactionRequestDto request = createTransactionRequest(userId);
    TransactionRequestItemDto item = request.transactions().getFirst();
    Mockito.doNothing().when(transactionServiceClient).checkIfAccountExists(item.accountId());
    Mockito.doNothing().when(transactionServiceClient).checkIfCategoryExists(item.categoryId());
    Mockito.doThrow(ResourceNotFoundException.class)
        .when(transactionServiceClient)
        .checkIfCurrencyExists(item.currency());

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.createTransactions(request));

    verify(transactionServiceClient, times(1)).checkIfAccountExists(item.accountId());
    verify(transactionServiceClient, times(1)).checkIfCategoryExists(item.categoryId());
    verify(transactionServiceClient, times(1)).checkIfCurrencyExists(item.currency());
    transactionMapper.verify(() -> TransactionMapper.toTransaction(userId, item), never());
    verify(transactionRepository, never()).saveAll(any(List.class));
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(any(List.class)), never());
  }

  @Test
  void givenTransactionRequest_WhenCreateTransaction_ThenReturnCreatedTransactions(
      @Mock Transaction transaction, @Mock TransactionDto transactionDto) {
    UUID userId = RandomUtils.randomUUID();
    TransactionRequestDto request = createTransactionRequest(userId);
    TransactionRequestItemDto item = request.transactions().getFirst();
    Mockito.doNothing().when(transactionServiceClient).checkIfAccountExists(item.accountId());
    Mockito.doNothing().when(transactionServiceClient).checkIfCategoryExists(item.categoryId());
    Mockito.doNothing().when(transactionServiceClient).checkIfCurrencyExists(item.currency());

    transactionMapper
        .when(() -> TransactionMapper.toTransaction(userId, item))
        .thenReturn(transaction);
    when(transactionRepository.saveAll(List.of(transaction))).thenReturn(List.of(transaction));
    transactionMapper
        .when(() -> TransactionMapper.toTransactionDto(List.of(transaction)))
        .thenReturn(List.of(transactionDto));

    List<TransactionDto> result = transactionManagementService.createTransactions(request);

    assertNotNull(result);
    verify(transactionServiceClient, times(1)).checkIfAccountExists(item.accountId());
    verify(transactionServiceClient, times(1)).checkIfCategoryExists(item.categoryId());
    verify(transactionServiceClient, times(1)).checkIfCurrencyExists(item.currency());
    transactionMapper.verify(() -> TransactionMapper.toTransaction(userId, item), times(1));
    verify(transactionRepository, times(1)).saveAll(List.of(transaction));
    transactionMapper.verify(
        () -> TransactionMapper.toTransactionDto(List.of(transaction)), times(1));
  }

  @Test
  void givenTransactionCriteria_WhenGetTransactions_ThenReturnTransactions(
      @Mock TransactionCriteria transactionCriteria, @Mock List<Transaction> transactions) {
    UUID userId = RandomUtils.randomUUID();
    when(transactionRepository.findTransactions(userId, transactionCriteria))
        .thenReturn(transactions);

    transactionManagementService.getTransactions(userId, transactionCriteria);

    assertNotNull(transactions);
    verify(transactionRepository, times(1)).findTransactions(userId, transactionCriteria);
  }

  @Test
  void givenTransactionId_WhenGetTransaction_ThenReturnTransaction(
      @Mock Transaction transaction, @Mock TransactionDto transactionDto) {
    UUID transactionId = RandomUtils.randomUUID();

    when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
    transactionMapper
        .when(() -> TransactionMapper.toTransactionDto(transaction))
        .thenReturn(transactionDto);

    transactionDto = transactionManagementService.getTransaction(transactionId);

    assertNotNull(transactionDto);
    verify(transactionRepository, times(1)).findById(transactionId);
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(transaction), times(1));
  }

  @Test
  void givenTransactionId_WhenGetTransaction_ThenReturnNotFound() {
    UUID transactionId = RandomUtils.randomUUID();

    when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.getTransaction(transactionId));

    verify(transactionRepository, times(1)).findById(transactionId);
    transactionMapper.verify(
        () -> TransactionMapper.toTransactionDto(any(Transaction.class)), never());
  }

  @Test
  void givenTransactionId_WhenUpdateTransaction_ThenReturnTransactionDto(
      @Mock Transaction transaction, @Mock TransactionDto transactionDto, @Mock Tag tag) {
    UUID transactionId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request = createTransactionsUpdateRequest();

    when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
    Mockito.doNothing().when(transactionServiceClient).checkIfCurrencyExists(request.currency());
    when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.of(tag));
    transactionMapper
        .when(() -> TransactionMapper.partialUpdate(transaction, request, Set.of(tag)))
        .thenReturn(transaction);
    when(transactionRepository.save(transaction)).thenReturn(transaction);
    transactionMapper
        .when(() -> TransactionMapper.toTransactionDto(transaction))
        .thenReturn(transactionDto);

    transactionDto = transactionManagementService.updateTransaction(transactionId, request);

    assertNotNull(transactionDto);
    verify(transactionRepository, times(1)).findById(transactionId);
    verify(transactionServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(transaction), times(1));
    verify(transactionRepository, times(1)).save(transaction);
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(transaction), times(1));
  }

  @Test
  void givenTransactionId_WhenUpdateTransaction_ThenReturnNotFound() {
    UUID transactionId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request = createTransactionsUpdateRequest();

    when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.updateTransaction(transactionId, request));

    verify(transactionRepository, times(1)).findById(transactionId);
    transactionMapper.verify(
        () ->
            TransactionMapper.partialUpdate(
                any(Transaction.class), any(TransactionUpdateRequestDto.class)),
        never());
    verify(transactionRepository, never()).save(any(Transaction.class));
    transactionMapper.verify(
        () -> TransactionMapper.toTransactionDto(any(Transaction.class)), never());
  }

  @Test
  void givenTransactionId_WhenDeleteTransaction_ThenDeleteTransaction(
      @Mock Transaction transaction) {
    UUID transactionId = RandomUtils.randomUUID();
    when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

    transactionManagementService.deleteTransaction(transactionId);

    verify(transactionRepository, times(1)).findById(transactionId);
    verify(transactionRepository, times(1)).delete(transaction);
  }

  @Test
  void givenTransactionId_WhenDeleteTransaction_ThenTransactionNotFound() {
    UUID transactionId = RandomUtils.randomUUID();
    when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.deleteTransaction(transactionId));

    verify(transactionRepository, times(1)).findById(transactionId);
    verify(transactionRepository, never()).delete(any(Transaction.class));
  }

  private TransactionUpdateRequestDto createTransactionsUpdateRequest() {
    UUID tagId = RandomUtils.randomUUID();
    return new TransactionUpdateRequestDto(
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2024),
        RandomUtils.randomString(30),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase(),
        Collections.singleton(tagId));
  }

  @Test
  void givenUsername_WhenDeleteAllTransactions_ThenDeleteAllTransactions() {
    UUID userId = RandomUtils.randomUUID();

    Mockito.doNothing().when(transactionServiceClient).checkIfUserExists(userId);

    transactionManagementService.deleteAllTransactionsByUserId(userId);

    verify(transactionServiceClient, times(1)).checkIfUserExists(userId);
    verify(transactionRepository, times(1))
        .deleteAll(transactionRepository.findTransactionByUserId(userId));
  }

  @Test
  void givenUsername_WhenDeleteAllTransactions_ThenUserNotFound() {
    UUID userId = RandomUtils.randomUUID();

    Mockito.doThrow(ResourceNotFoundException.class)
        .when(transactionServiceClient)
        .checkIfUserExists(userId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.deleteAllTransactionsByUserId(userId));

    verify(transactionServiceClient, times(1)).checkIfUserExists(userId);
    verify(transactionRepository, times(0))
        .deleteAll(transactionRepository.findTransactionByUserId(userId));
  }

  @Test
  void givenAccountId_WhenDeleteAllTransactions_ThenDeleteAllTransactions() {
    UUID accountId = RandomUtils.randomUUID();

    Mockito.doNothing().when(transactionServiceClient).checkIfAccountExists(accountId);

    transactionManagementService.deleteAllTransactionsByAccountId(accountId);

    verify(transactionServiceClient, times(1)).checkIfAccountExists(accountId);
    verify(transactionRepository, times(1))
        .deleteAll(transactionRepository.findTransactionByAccountId(accountId));
  }

  @Test
  void givenAccountId_WhenDeleteAllTransactions_ThenAccountNotFound() {
    UUID accountId = RandomUtils.randomUUID();

    Mockito.doThrow(ResourceNotFoundException.class)
        .when(transactionServiceClient)
        .checkIfAccountExists(accountId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.deleteAllTransactionsByAccountId(accountId));

    verify(transactionServiceClient, times(1)).checkIfAccountExists(accountId);
    verify(transactionRepository, times(0))
        .deleteAll(transactionRepository.findTransactionByAccountId(accountId));
  }

  @Test
  void givenCategoryId_RemoveCategoryFromTransactions_ThenRemoveCategories(
      @Mock List<Transaction> transactions) {
    UUID categoryId = RandomUtils.randomUUID();

    Mockito.doNothing().when(transactionServiceClient).checkIfCategoryExists(categoryId);
    when(transactionRepository.findTransactionByCategoryId(categoryId)).thenReturn(transactions);

    transactionManagementService.removeCategoryFromTransactions(categoryId);

    verify(transactionServiceClient, times(1)).checkIfCategoryExists(categoryId);
    verify(transactionRepository, times(1)).saveAll(transactions);
  }

  @Test
  void givenCategoryId_RemoveCategoryFromTransactions_ThenCategoryNotFound() {
    UUID categoryId = RandomUtils.randomUUID();

    Mockito.doThrow(ResourceNotFoundException.class)
        .when(transactionServiceClient)
        .checkIfCategoryExists(categoryId);

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.removeCategoryFromTransactions(categoryId));

    verify(transactionServiceClient, times(1)).checkIfCategoryExists(categoryId);
    verify(transactionRepository, never()).saveAll(any(List.class));
  }
}
