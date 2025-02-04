package it.moneyverse.budget.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.event.BudgetDeletionEvent;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.utils.mapper.BudgetMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;

/** Unit test for {@link BudgetManagementService} */
@ExtendWith(MockitoExtension.class)
class BudgetManagementServiceTest {

  @InjectMocks private BudgetManagementService budgetManagementService;

  @Mock private BudgetRepository budgetRepository;
  @Mock private UserServiceClient userServiceClient;
  @Mock private CurrencyServiceClient currencyServiceClient;
  @Mock private MessageProducer<UUID, String> messageProducer;
  private MockedStatic<BudgetMapper> mapper;

  @BeforeEach
  public void setup() {
    mapper = mockStatic(BudgetMapper.class);
  }

  @AfterEach
  public void tearDown() {
    mapper.close();
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenReturnCreatedBudget(
      @Mock Budget budget, @Mock BudgetDto budgetDto) {
    final UUID userId = RandomUtils.randomUUID();
    BudgetRequestDto request = createBudgetRequest(userId);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    when(budgetRepository.existsByUserIdAndBudgetName(userId, request.budgetName()))
        .thenReturn(false);
    mapper.when(() -> BudgetMapper.toBudget(request)).thenReturn(budget);
    when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
    mapper.when(() -> BudgetMapper.toBudgetDto(budget)).thenReturn(budgetDto);

    budgetDto = budgetManagementService.createBudget(request);

    assertNotNull(budgetDto);
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
    verify(budgetRepository, times(1)).existsByUserIdAndBudgetName(userId, request.budgetName());
    mapper.verify(() -> BudgetMapper.toBudget(request), times(1));
    verify(budgetRepository, times(1)).save(any(Budget.class));
    mapper.verify(() -> BudgetMapper.toBudgetDto(budget), times(1));
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenUserNotFound() {
    final UUID userId = RandomUtils.randomUUID();
    BudgetRequestDto request = createBudgetRequest(userId);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class, () -> budgetManagementService.createBudget(request));

    verify(budgetRepository, never()).save(any(Budget.class));
    verify(budgetRepository, never()).existsByUserIdAndBudgetName(userId, request.budgetName());
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenCurrencyNotFound() {
    final UUID userId = RandomUtils.randomUUID();
    BudgetRequestDto request = createBudgetRequest(userId);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class, () -> budgetManagementService.createBudget(request));

    verify(budgetRepository, never()).save(any(Budget.class));
    verify(budgetRepository, never()).existsByUserIdAndBudgetName(userId, request.budgetName());
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenBudgetAlreadyExists() {
    final UUID userId = RandomUtils.randomUUID();
    BudgetRequestDto request = createBudgetRequest(userId);

    when(userServiceClient.checkIfUserExists(userId)).thenReturn(true);
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    when(budgetRepository.existsByUserIdAndBudgetName(userId, request.budgetName()))
        .thenReturn(true);

    assertThrows(
        ResourceAlreadyExistsException.class, () -> budgetManagementService.createBudget(request));

    verify(budgetRepository, never()).save(any(Budget.class));
    verify(userServiceClient, times(1)).checkIfUserExists(userId);
    verify(budgetRepository, times(1)).existsByUserIdAndBudgetName(userId, request.budgetName());
  }

  private BudgetRequestDto createBudgetRequest(UUID userId) {
    return new BudgetRequestDto(
        userId,
        RandomUtils.randomString(15),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase());
  }

  @Test
  void givenBudgetId_WhenGetBudget_ThenReturnBudget(
      @Mock Budget budget, @Mock BudgetDto budgetDto) {
    UUID budgetId = RandomUtils.randomUUID();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.ofNullable(budget));
    mapper.when(() -> BudgetMapper.toBudgetDto(budget)).thenReturn(budgetDto);

    budgetDto = budgetManagementService.getBudget(budgetId);

    assertNotNull(budgetDto);
    verify(budgetRepository, times(1)).findById(budgetId);
    mapper.verify(() -> BudgetMapper.toBudgetDto(budget), times(1));
  }

  @Test
  void givenBudgetId_WhenGetBudget_ThenBudgetNotFound() {
    UUID budgetId = RandomUtils.randomUUID();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> budgetManagementService.getBudget(budgetId));

    verify(budgetRepository, times(1)).findById(budgetId);
    mapper.verify(() -> BudgetMapper.toBudgetDto(any(Budget.class)), never());
  }

  @Test
  void givenBudgetId_WhenUpdateBudget_ThenReturnBudgetDto(
      @Mock Budget budget, @Mock BudgetDto budgetDto) {
    UUID budgetId = RandomUtils.randomUUID();
    BudgetUpdateRequestDto request = createBudgetUpdateRequest();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
    when(currencyServiceClient.checkIfCurrencyExists(request.currency())).thenReturn(true);
    mapper.when(() -> BudgetMapper.partialUpdate(budget, request)).thenReturn(budget);
    when(budgetRepository.save(budget)).thenReturn(budget);
    mapper.when(() -> BudgetMapper.toBudgetDto(budget)).thenReturn(budgetDto);

    budgetDto = budgetManagementService.updateBudget(budgetId, request);

    assertNotNull(budgetDto);
    verify(budgetRepository, times(1)).findById(budgetId);
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    mapper.verify(() -> BudgetMapper.partialUpdate(budget, request), times(1));
    verify(budgetRepository, times(1)).save(budget);
    mapper.verify(() -> BudgetMapper.toBudgetDto(budget), times(1));
  }

  @Test
  void givenBudgetId_WhenUpdateBudget_ThenUserNotFound() {
    UUID budgetId = RandomUtils.randomUUID();
    BudgetUpdateRequestDto request = createBudgetUpdateRequest();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> budgetManagementService.updateBudget(budgetId, request));

    verify(budgetRepository, times(1)).findById(budgetId);
    mapper.verify(
        () -> BudgetMapper.partialUpdate(any(Budget.class), any(BudgetUpdateRequestDto.class)),
        never());
    verify(budgetRepository, never()).save(any(Budget.class));
    mapper.verify(() -> BudgetMapper.toBudgetDto(any(Budget.class)), never());
  }

  private BudgetUpdateRequestDto createBudgetUpdateRequest() {
    return new BudgetUpdateRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase());
  }

  @Test
  void givenBudgetId_WhenDeleteBudget_ThenDeleteAccount(
      @Mock Budget budget, @Mock CompletableFuture<SendResult<UUID, String>> future) {
    UUID budgetId = RandomUtils.randomUUID();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
    when(messageProducer.send(any(BudgetDeletionEvent.class), any(String.class)))
        .thenReturn(future);

    budgetManagementService.deleteBudget(budgetId);

    verify(budgetRepository, times(1)).findById(budgetId);
    verify(messageProducer, times(1)).send(any(BudgetDeletionEvent.class), any(String.class));
  }

  @Test
  void givenBudgetId_WhenDeleteBudget_ThenBudgetNotFound() {
    UUID budgetId = RandomUtils.randomUUID();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> budgetManagementService.deleteBudget(budgetId));

    verify(budgetRepository, times(1)).findById(budgetId);
    verify(messageProducer, never()).send(any(BudgetDeletionEvent.class), any(String.class));
  }
}
