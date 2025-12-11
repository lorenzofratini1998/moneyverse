package it.moneyverse.budget.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.budget.model.BudgetTestFactory;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.budget.runtime.messages.BudgetEventPublisher;
import it.moneyverse.budget.utils.mapper.BudgetMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.BudgetDto;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDate;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetManagementServiceTest {

  @InjectMocks private BudgetManagementService budgetManagementService;

  @Mock private CategoryRepository categoryRepository;
  @Mock private BudgetRepository budgetRepository;
  @Mock private CurrencyServiceClient currencyServiceClient;
  @Mock BudgetEventPublisher publisher;
  @Mock private SecurityService securityService;
  @Mock private SseEventService sseEventService;
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
      @Mock Category category, @Mock Budget budget, @Mock BudgetDto budgetDto) {
    BudgetRequestDto request = BudgetTestFactory.BudgetRequestBuilder.defaultInstance();
    when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(category));

    Mockito.doNothing().when(currencyServiceClient).checkIfCurrencyExists(request.currency());
    when(budgetRepository.existsByCategory_CategoryIdAndStartDateAndEndDate(
            request.categoryId(), request.startDate(), request.endDate()))
        .thenReturn(false);
    mapper.when(() -> BudgetMapper.toBudget(request, category)).thenReturn(budget);
    when(budgetRepository.save(budget)).thenReturn(budget);
    mapper.when(() -> BudgetMapper.toBudgetDto(budget)).thenReturn(budgetDto);

    BudgetDto result = budgetManagementService.createBudget(request);

    assertNotNull(result);
    verify(categoryRepository, times(1)).findById(request.categoryId());
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    verify(budgetRepository, times(1))
        .existsByCategory_CategoryIdAndStartDateAndEndDate(
            request.categoryId(), request.startDate(), request.endDate());
    verify(budgetRepository, times(1)).save(budget);
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenCategoryNotFound() {
    BudgetRequestDto request = BudgetTestFactory.BudgetRequestBuilder.defaultInstance();
    when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> budgetManagementService.createBudget(request));

    verify(categoryRepository, times(1)).findById(request.categoryId());
    verify(currencyServiceClient, never()).checkIfCurrencyExists(request.currency());
    verify(budgetRepository, never())
        .existsByCategory_CategoryIdAndStartDateAndEndDate(
            any(UUID.class), any(LocalDate.class), any(LocalDate.class));
    verify(budgetRepository, never()).save(any(Budget.class));
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenReturnCurrencyNotFound(@Mock Category category) {
    BudgetRequestDto request = BudgetTestFactory.BudgetRequestBuilder.defaultInstance();
    when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(category));
    Mockito.doThrow(ResourceNotFoundException.class)
        .when(currencyServiceClient)
        .checkIfCurrencyExists(request.currency());

    assertThrows(
        ResourceNotFoundException.class, () -> budgetManagementService.createBudget(request));

    verify(categoryRepository, times(1)).findById(request.categoryId());
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    verify(budgetRepository, never()).save(any(Budget.class));
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenReturnBudgetAlreadyExists(@Mock Category category) {
    BudgetRequestDto request = BudgetTestFactory.BudgetRequestBuilder.defaultInstance();
    when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(category));
    Mockito.doNothing().when(currencyServiceClient).checkIfCurrencyExists(request.currency());
    when(budgetRepository.existsByCategory_CategoryIdAndStartDateAndEndDate(
            request.categoryId(), request.startDate(), request.endDate()))
        .thenReturn(true);

    assertThrows(
        ResourceAlreadyExistsException.class, () -> budgetManagementService.createBudget(request));

    verify(categoryRepository, times(1)).findById(request.categoryId());
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    verify(budgetRepository, times(1))
        .existsByCategory_CategoryIdAndStartDateAndEndDate(
            request.categoryId(), request.startDate(), request.endDate());
    verify(budgetRepository, never()).save(any(Budget.class));
  }

  @Test
  void givenUserIdAndCriteria_WhenGetBudgets_ThenReturnBudgets(
      @Mock BudgetCriteria criteria, @Mock Budget budget, @Mock BudgetDto budgetDto) {
    UUID userId = RandomUtils.randomUUID();
    when(budgetRepository.filterBudgets(userId, criteria)).thenReturn(List.of(budget));
    mapper.when(() -> BudgetMapper.toBudgetDto(List.of(budget))).thenReturn(List.of(budgetDto));

    List<BudgetDto> result = budgetManagementService.getBudgetsByUserId(userId, criteria);

    assertEquals(List.of(budgetDto), result);
    verify(budgetRepository, times(1)).filterBudgets(userId, criteria);
  }

  @Test
  void givenBudgetIdAndRequest_WhenUpdateBudget_ThenReturnUpdatedBudget(
      @Mock Budget budget, @Mock Category category, @Mock BudgetDto budgetDto) {
    UUID budgetId = RandomUtils.randomUUID();
    BudgetUpdateRequestDto request = BudgetTestFactory.BudgetUpdateRequestBuilder.defaultInstance();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
    Mockito.doNothing().when(currencyServiceClient).checkIfCurrencyExists(request.currency());
    mapper.when(() -> BudgetMapper.partialUpdate(budget, request)).thenReturn(budget);
    when(budgetRepository.save(budget)).thenReturn(budget);
    mapper.when(() -> BudgetMapper.toBudgetDto(budget)).thenReturn(budgetDto);
    when(budget.getCategory()).thenReturn(category);
    when(category.getUserId()).thenReturn(RandomUtils.randomUUID());

    BudgetDto result = budgetManagementService.updateBudget(budgetId, request);

    assertEquals(budgetDto, result);
    verify(budgetRepository, times(1)).findById(budgetId);
    verify(currencyServiceClient, times(1)).checkIfCurrencyExists(request.currency());
    verify(budgetRepository, times(1)).save(budget);
  }

  @Test
  void givenBudgetId_WhenDeleteBudget_ThenDeleteBudget(
      @Mock Budget budget, @Mock Category category) {
    UUID budgetId = RandomUtils.randomUUID();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
    when(budget.getCategory()).thenReturn(category);
    when(category.getUserId()).thenReturn(RandomUtils.randomUUID());

    budgetManagementService.deleteBudget(budgetId);

    verify(budgetRepository, times(1)).findById(budgetId);
    verify(budgetRepository, times(1)).delete(budget);
  }

  @Test
  void givenBudgetId_WhenDeleteBudget_ThenBudgetNotFound() {
    UUID budgetId = RandomUtils.randomUUID();

    when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> budgetManagementService.deleteBudget(budgetId));

    verify(budgetRepository, times(1)).findById(budgetId);
    verify(budgetRepository, never()).delete(any(Budget.class));
  }
}
