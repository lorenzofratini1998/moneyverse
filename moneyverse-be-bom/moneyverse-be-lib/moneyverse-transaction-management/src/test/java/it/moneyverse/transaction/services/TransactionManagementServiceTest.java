package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import it.moneyverse.core.enums.CurrencyEnum;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.mapper.TransactionMapper;
import java.util.Collections;
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
  void givenTransactionRequest_WhenCreateTransaction_ThenReturnCreatedTransaction(
      @Mock Transaction transaction, @Mock TransactionDto transactionDto) {
    TransactionRequestDto request = createTransactionRequest();
    when(accountServiceClient.checkIfAccountExists(request.accountId())).thenReturn(true);
    when(budgetServiceClient.checkIfBudgetExists(request.budgetId())).thenReturn(true);
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
    transactionMapper.verify(
        () -> TransactionMapper.toTransaction(request, tagRepository), times(1));
    verify(transactionRepository, times(1)).save(transaction);
    transactionMapper.verify(() -> TransactionMapper.toTransactionDto(transaction), times(1));
  }

  private TransactionRequestDto createTransactionRequest() {
    Long tagId = RandomUtils.randomLong();
    return new TransactionRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomUUID(),
        RandomUtils.randomUUID(),
        RandomUtils.randomLocalDate(2024, 2025),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomEnum(CurrencyEnum.class),
        Collections.singleton(tagId));
  }
}
