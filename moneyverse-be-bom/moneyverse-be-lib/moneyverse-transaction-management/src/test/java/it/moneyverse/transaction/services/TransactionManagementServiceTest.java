package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.exceptions.ResourceStillExistsException;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.TransactionTestFactory;
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.model.validator.TransactionValidator;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
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

  @Mock private CurrencyServiceClient currencyServiceClient;
  @Mock private AccountServiceClient accountServiceClient;
  @Mock private UserServiceClient userServiceClient;
  @Mock private BudgetServiceClient budgetServiceClient;
  @Mock private TransactionRepository transactionRepository;
  @Mock private TransferRepository transferRepository;
  @Mock private TagRepository tagRepository;
  @Mock private TransactionEventPublisher transactionEventPublisher;
  @Mock private TransferService transferService;
  @Mock private SubscriptionService subscriptionService;
  @Mock private TagService tagService;
  @Mock private TransactionValidator transactionValidator;
  @Mock private TransactionFactoryService transactionFactoryService;
  @Mock private SecurityService securityService;
  @Mock private SseEventService eventService;
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
  void givenTransactionRequest_WhenCreateTransaction_ThenReturnCreatedTransactions(
      @Mock Transaction transaction, @Mock TransactionDto transactionDto) {
    TransactionRequestDto request =
        TransactionTestFactory.TransactionRequestBuilder.builder().withEmptyTags().build();
    TransactionRequestItemDto item = request.transactions().getFirst();
    Mockito.doNothing().when(transactionValidator).validate(item);
    when(transactionFactoryService.createTransaction(request.userId(), item))
        .thenReturn(transaction);
    when(transactionRepository.saveAll(List.of(transaction))).thenReturn(List.of(transaction));
    transactionMapper
        .when(() -> TransactionMapper.toTransactionDto(List.of(transaction)))
        .thenReturn(List.of(transactionDto));
    when(securityService.getAuthenticatedUserId()).thenReturn(request.userId());

    List<TransactionDto> result = transactionManagementService.createTransactions(request);

    assertNotNull(result);
    verify(transactionValidator, times(1)).validate(item);
    verify(transactionFactoryService, times(1)).createTransaction(request.userId(), item);
    verify(transactionRepository, times(1)).saveAll(List.of(transaction));
  }

  @Test
  void givenTransactionCriteria_WhenGetTransactions_ThenReturnTransactions(
      @Mock TransactionCriteria transactionCriteria,
      @Mock List<Transaction> transactions,
      @Mock PageCriteria page) {
    UUID userId = RandomUtils.randomUUID();
    when(transactionRepository.findTransactions(userId, transactionCriteria))
        .thenReturn(transactions);
    when(transactionCriteria.getPage()).thenReturn(page);
    when(page.getOffset()).thenReturn(0);
    when(page.getLimit()).thenReturn(25);

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
    verify(budgetServiceClient, never()).getBudgetId(any(), any());
    verify(currencyServiceClient, never())
        .convertCurrencyAmountByUserPreference(any(), any(), any(), any());
    verify(transactionRepository, never()).saveAll(any());
  }

  @Test
  void givenTransactionId_WhenUpdateTransaction_ThenReturnTransactionDto(
      @Mock Transaction transaction, @Mock TransactionDto transactionDto, @Mock Tag tag) {
    UUID transactionId = RandomUtils.randomUUID();
    UUID userId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request =
        TransactionTestFactory.TransactionUpdateRequestBuilder.defaultInstance();

    when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
    Mockito.doNothing().when(transactionValidator).validate(request);
    when(budgetServiceClient.getBudgetId(request.categoryId(), request.date())).thenReturn(null);
    transactionMapper
        .when(() -> TransactionMapper.partialUpdate(transaction, request, Set.of(tag)))
        .thenReturn(transaction);
    when(transaction.getUserId()).thenReturn(userId);
    when(transaction.getCurrency()).thenReturn(request.currency());
    when(transaction.getAmount()).thenReturn(request.amount());
    when(transaction.getDate()).thenReturn(request.date());
    when(currencyServiceClient.convertCurrencyAmountByUserPreference(
            userId, transaction.getAmount(), transaction.getCurrency(), transaction.getDate()))
        .thenReturn(RandomUtils.randomBigDecimal());
    when(transactionRepository.save(transaction)).thenReturn(transaction);
    transactionMapper
        .when(() -> TransactionMapper.toTransactionDto(transaction))
        .thenReturn(transactionDto);
    when(securityService.getAuthenticatedUserId()).thenReturn(userId);

    transactionDto = transactionManagementService.updateTransaction(transactionId, request);

    assertNotNull(transactionDto);
    verify(transactionValidator, times(1)).validate(request);
    verify(transactionRepository, times(1)).findById(transactionId);
    verify(budgetServiceClient, times(1)).getBudgetId(request.categoryId(), request.date());
    verify(currencyServiceClient, times(1))
        .convertCurrencyAmountByUserPreference(
            userId, transaction.getAmount(), transaction.getCurrency(), transaction.getDate());
    verify(transactionRepository, times(1)).save(transaction);
  }

  @Test
  void givenTransactionId_WhenUpdateTransaction_ThenReturnNotFound() {
    UUID transactionId = RandomUtils.randomUUID();
    TransactionUpdateRequestDto request =
        TransactionTestFactory.TransactionUpdateRequestBuilder.defaultInstance();

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
    when(securityService.getAuthenticatedUserId()).thenReturn(RandomUtils.randomUUID());

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

  @Test
  void givenUsername_WhenDeleteAllTransactions_ThenDeleteAllTransactions() {
    UUID userId = RandomUtils.randomUUID();

    Mockito.doNothing().when(userServiceClient).checkIfUserStillExist(userId);

    transactionManagementService.deleteAllTransactionsByUserId(userId);

    verify(userServiceClient, times(1)).checkIfUserStillExist(userId);
    verify(transactionRepository, times(1))
        .deleteAll(transactionRepository.findTransactionByUserId(userId));
  }

  @Test
  void givenUsername_WhenDeleteAllTransactions_ThenUserStillExist() {
    UUID userId = RandomUtils.randomUUID();

    doThrow(ResourceStillExistsException.class)
        .when(userServiceClient)
        .checkIfUserStillExist(userId);

    assertThrows(
        ResourceStillExistsException.class,
        () -> transactionManagementService.deleteAllTransactionsByUserId(userId));

    verify(userServiceClient, times(1)).checkIfUserStillExist(userId);
    verify(transactionRepository, times(0))
        .deleteAll(transactionRepository.findTransactionByUserId(userId));
  }

  @Test
  void givenAccountId_WhenDeleteAllTransactions_ThenDeleteAllTransactions() {
    UUID accountId = RandomUtils.randomUUID();

    Mockito.doNothing().when(accountServiceClient).checkIfAccountStillExists(accountId);
    when(securityService.getAuthenticatedUserId()).thenReturn(RandomUtils.randomUUID());

    transactionManagementService.deleteAllTransactionsByAccountId(accountId);

    verify(accountServiceClient, times(1)).checkIfAccountStillExists(accountId);
    verify(transactionRepository, times(1))
        .deleteAll(transactionRepository.findTransactionByAccountId(accountId));
  }

  @Test
  void givenAccountId_WhenDeleteAllTransactions_ThenAccountStillExists() {
    UUID accountId = RandomUtils.randomUUID();

    doThrow(ResourceStillExistsException.class)
        .when(accountServiceClient)
        .checkIfAccountStillExists(accountId);

    assertThrows(
        ResourceStillExistsException.class,
        () -> transactionManagementService.deleteAllTransactionsByAccountId(accountId));

    verify(accountServiceClient, times(1)).checkIfAccountStillExists(accountId);
    verify(transactionRepository, times(0))
        .deleteAll(transactionRepository.findTransactionByAccountId(accountId));
  }

  @Test
  void givenCategoryId_RemoveCategoryFromTransactions_ThenRemoveCategories(
      @Mock Transaction transaction) {
    UUID categoryId = RandomUtils.randomUUID();

    Mockito.doNothing().when(budgetServiceClient).checkIfCategoryStillExists(categoryId);
    when(transactionRepository.findTransactionByCategoryId(categoryId))
        .thenReturn(List.of(transaction));
    when(securityService.getAuthenticatedUserId()).thenReturn(RandomUtils.randomUUID());

    transactionManagementService.removeCategoryFromTransactions(categoryId);

    verify(budgetServiceClient, times(1)).checkIfCategoryStillExists(categoryId);
    verify(transactionRepository, times(1)).saveAll(List.of(transaction));
  }

  @Test
  void givenCategoryId_RemoveCategoryFromTransactions_ThenCategoryNotFound() {
    UUID categoryId = RandomUtils.randomUUID();

    doThrow(ResourceStillExistsException.class)
        .when(budgetServiceClient)
        .checkIfCategoryStillExists(categoryId);

    assertThrows(
        ResourceStillExistsException.class,
        () -> transactionManagementService.removeCategoryFromTransactions(categoryId));

    verify(budgetServiceClient, times(1)).checkIfCategoryStillExists(categoryId);
    verify(transactionRepository, never()).saveAll(any(List.class));
  }

  @Test
  void givenBudgetId_RemoveBudgetFromTransactions_ThenRemoveBudgets(@Mock Transaction transaction) {
    UUID budgetId = RandomUtils.randomUUID();

    Mockito.doNothing().when(budgetServiceClient).checkIfBudgetStillExists(budgetId);
    when(transactionRepository.findTransactionByBudgetId(budgetId))
        .thenReturn(List.of(transaction));
    when(securityService.getAuthenticatedUserId()).thenReturn(RandomUtils.randomUUID());

    transactionManagementService.removeBudgetFromTransactions(budgetId);

    verify(budgetServiceClient, times(1)).checkIfBudgetStillExists(budgetId);
    verify(transactionRepository, times(1)).saveAll(List.of(transaction));
  }
}
