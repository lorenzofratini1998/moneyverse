package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionManagementServiceTest {

  @InjectMocks private TransactionManagementService transactionManagementService;

  @Mock private AccountServiceGrpcClient accountServiceClient;
  @Mock private BudgetServiceGrpcClient budgetServiceClient;
  @Mock private UserServiceClient userServiceClient;
  @Mock private CurrencyServiceClient currencyServiceClient;
  @Mock private TransactionRepository transactionRepository;
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
  void givenTransactionRequest_WhenCreateTransaction_ThenAccountNotFound() {
    TransactionRequestDto request = createTransactionRequest();
    when(accountServiceClient.checkIfAccountExists(request.accountId())).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.createTransaction(request));

    verify(accountServiceClient, times(1)).checkIfAccountExists(request.accountId());
    verify(budgetServiceClient, never()).checkIfBudgetExists(any(UUID.class));
    transactionMapper.verify(
        () -> TransactionMapper.toTransaction(request, tagRepository), never());
    verify(transactionRepository, never()).save(any(Transaction.class));
    transactionMapper.verify(
        () -> TransactionMapper.toTransactionDto(any(Transaction.class)), never());
  }

  @Test
  void givenTransactionRequest_WhenCreateTransaction_ThenBudgetNotFound() {
    TransactionRequestDto request = createTransactionRequest();
    when(accountServiceClient.checkIfAccountExists(request.accountId())).thenReturn(true);
    when(budgetServiceClient.checkIfBudgetExists(request.budgetId())).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.createTransaction(request));

    verify(accountServiceClient, times(1)).checkIfAccountExists(request.accountId());
    verify(budgetServiceClient, times(1)).checkIfBudgetExists(request.budgetId());
    transactionMapper.verify(
        () -> TransactionMapper.toTransaction(request, tagRepository), never());
    verify(transactionRepository, never()).save(any(Transaction.class));
    transactionMapper.verify(
        () -> TransactionMapper.toTransactionDto(any(Transaction.class)), never());
  }

  @Test
  void givenTransactionRequest_WhenCreateTransaction_ThenCurrencyNotFound() {
    TransactionRequestDto request = createTransactionRequest();
    when(accountServiceClient.checkIfAccountExists(request.accountId())).thenReturn(true);
    when(budgetServiceClient.checkIfBudgetExists(request.budgetId())).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.createTransaction(request));

    verify(accountServiceClient, times(1)).checkIfAccountExists(request.accountId());
    verify(budgetServiceClient, times(1)).checkIfBudgetExists(request.budgetId());
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    transactionMapper.verify(
        () -> TransactionMapper.toTransaction(request, tagRepository), never());
    verify(transactionRepository, never()).save(any(Transaction.class));
    transactionMapper.verify(
        () -> TransactionMapper.toTransactionDto(any(Transaction.class)), never());
  }

  @Test
  void givenTransactionRequest_WhenCreateTransaction_ThenReturnCreatedTransaction(
      @Mock Transaction transaction, @Mock TransactionDto transactionDto) {
    TransactionRequestDto request = createTransactionRequest();
    when(accountServiceClient.checkIfAccountExists(request.accountId())).thenReturn(true);
    when(budgetServiceClient.checkIfBudgetExists(request.budgetId())).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    transactionMapper
        .when(() -> TransactionMapper.toTransaction(request, tagRepository))
        .thenReturn(transaction);
    when(transactionRepository.save(transaction)).thenReturn(transaction);
    transactionMapper
        .when(() -> TransactionMapper.toTransactionDto(transaction))
        .thenReturn(transactionDto);

    TransactionDto result = transactionManagementService.createTransaction(request);

    assertNotNull(result);
    verify(accountServiceClient, times(1)).checkIfAccountExists(request.accountId());
    verify(budgetServiceClient, times(1)).checkIfBudgetExists(request.budgetId());
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    transactionMapper.verify(
        () -> TransactionMapper.toTransaction(request, tagRepository), times(1));
    verify(transactionRepository, times(1)).save(transaction);
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(transaction), times(1));
  }

  @Test
  void givenTransactionCriteria_WhenGetTransactions_ThenReturnTransactions(
      @Mock TransactionCriteria transactionCriteria, @Mock List<Transaction> transactions) {
    when(transactionRepository.findTransactions(transactionCriteria)).thenReturn(transactions);

    transactionManagementService.getTransactions(transactionCriteria);

    assertNotNull(transactions);
    verify(transactionRepository, times(1)).findTransactions(transactionCriteria);
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
      @Mock Transaction transaction, @Mock TransactionDto transactionDto) {
    UUID transactionId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request = createTransactionUpdateRequest();

    when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    transactionMapper
        .when(() -> TransactionMapper.partialUpdate(transaction, request, tagRepository))
        .thenReturn(transaction);
    when(transactionRepository.save(transaction)).thenReturn(transaction);
    transactionMapper
        .when(() -> TransactionMapper.toTransactionDto(transaction))
        .thenReturn(transactionDto);

    transactionDto = transactionManagementService.updateTransaction(transactionId, request);

    assertNotNull(transactionDto);
    verify(transactionRepository, times(1)).findById(transactionId);
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(transaction), times(1));
    verify(transactionRepository, times(1)).save(transaction);
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(transaction), times(1));
  }

  @Test
  void givenTransactionId_WhenUpdateTransaction_ThenReturnNotFound() {
    UUID transactionId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request = createTransactionUpdateRequest();

    when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionManagementService.updateTransaction(transactionId, request));

    verify(transactionRepository, times(1)).findById(transactionId);
    transactionMapper.verify(
        () ->
            TransactionMapper.partialUpdate(
                any(Transaction.class),
                any(TransactionUpdateRequestDto.class),
                any(TagRepository.class)),
        never());
    verify(transactionRepository, never()).save(any(Transaction.class));
    transactionMapper.verify(
        () -> TransactionMapper.toTransactionDto(any(Transaction.class)), never());
  }

  @Test
  void givenTransactionId_WhenDeleteTransaction_ThenDeleteTransaction(
          @Mock Transaction transaction
  ) {
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
        () -> transactionManagementService.deleteTransaction(transactionId)
    );

    verify(transactionRepository, times(1)).findById(transactionId);
    verify(transactionRepository, never()).delete(any(Transaction.class));
  }

  private TransactionUpdateRequestDto createTransactionUpdateRequest() {
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

  private TransactionRequestDto createTransactionRequest() {
    UUID tagId = RandomUtils.randomUUID();
    return new TransactionRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase(),
        Collections.singleton(tagId));
  }

  @Test
  void givenUsername_WhenDeleteAllTransactions_ThenDeleteAllTransactions() {
      String username = RandomUtils.randomString(15);

      when(userServiceClient.checkIfUserExists(username)).thenReturn(true);

      transactionManagementService.deleteAllTransactionsByUsername(username);

      verify(userServiceClient, times(1)).checkIfUserExists(username);
      verify(transactionRepository, times(1)).deleteAll(transactionRepository.findTransactionByUsername(username));
  }

  @Test
  void givenUsername_WhenDeleteAllTransactions_ThenUserNotFound() {
    String username = RandomUtils.randomString(15);

    when(userServiceClient.checkIfUserExists(username)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> transactionManagementService.deleteAllTransactionsByUsername(username));

    verify(userServiceClient, times(1)).checkIfUserExists(username);
    verify(transactionRepository, times(0)).deleteAll(transactionRepository.findTransactionByUsername(username));
  }

  @Test
  void givenAccountId_WhenDeleteAllTransactions_ThenDeleteAllTransactions() {
    UUID accountId = RandomUtils.randomUUID();

    when(accountServiceClient.checkIfAccountExists(accountId)).thenReturn(true);

    transactionManagementService.deleteAllTransactionsByAccountId(accountId);

    verify(accountServiceClient, times(1)).checkIfAccountExists(accountId);
    verify(transactionRepository, times(1)).deleteAll(transactionRepository.findTransactionByAccountId(accountId));
  }

  @Test
  void givenAccountId_WhenDeleteAllTransactions_ThenUserNotFound() {
    UUID accountId = RandomUtils.randomUUID();

    when(accountServiceClient.checkIfAccountExists(accountId)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> transactionManagementService.deleteAllTransactionsByAccountId(accountId));

    verify(accountServiceClient, times(1)).checkIfAccountExists(accountId);
    verify(transactionRepository, times(0)).deleteAll(transactionRepository.findTransactionByAccountId(accountId));
  }

  @Test
  void givenBudgetId_RemoveBudgetFromTransactions_ThenRemoveBudgets(@Mock List<Transaction> transactions) {
    UUID budgetId = RandomUtils.randomUUID();

    when(budgetServiceClient.checkIfBudgetExists(budgetId)).thenReturn(true);
    when(transactionRepository.findTransactionByBudgetId(budgetId)).thenReturn(transactions);

    transactionManagementService.removeBudgetFromTransactions(budgetId);

    verify(budgetServiceClient, times(1)).checkIfBudgetExists(budgetId);
    verify(transactionRepository, times(1)).saveAll(transactions);
  }

  @Test
  void givenBudgetId_RemoveBudgetFromTransactions_ThenBudgetNotFound() {
    UUID budgetId = RandomUtils.randomUUID();

    when(budgetServiceClient.checkIfBudgetExists(budgetId)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> transactionManagementService.removeBudgetFromTransactions(budgetId));

    verify(budgetServiceClient, times(1)).checkIfBudgetExists(budgetId);
    verify(transactionRepository, never()).saveAll(any(List.class));
  }
}
