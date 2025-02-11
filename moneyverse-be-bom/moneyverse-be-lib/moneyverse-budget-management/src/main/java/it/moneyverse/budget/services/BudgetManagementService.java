package it.moneyverse.budget.services;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.budget.utils.mapper.BudgetMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.events.CategoryDeletionEvent;
import it.moneyverse.core.services.CurrencyServiceClient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetManagementService implements BudgetService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetManagementService.class);

  private final CategoryRepository categoryRepository;
  private final BudgetRepository budgetRepository;
  private final CurrencyServiceClient currencyServiceClient;
  private final ApplicationEventPublisher eventPublisher;

  public BudgetManagementService(
      CategoryRepository categoryRepository,
      BudgetRepository budgetRepository,
      CurrencyServiceClient currencyServiceClient,
      ApplicationEventPublisher eventPublisher) {
    this.categoryRepository = categoryRepository;
    this.budgetRepository = budgetRepository;
    this.currencyServiceClient = currencyServiceClient;
    this.eventPublisher = eventPublisher;
  }

  @Override
  @Transactional
  public BudgetDto createBudget(BudgetRequestDto request) {
    Category category = findCategoryById(request.categoryId());
    checkIfCurrencyExists(request.currency());
    checkIfBudgetAlreadyExists(request.categoryId(), request.startDate(), request.endDate());
    LOGGER.info("Creating budget for category '{}'", request.categoryId());
    Budget budget = BudgetMapper.toBudget(request, category);
    return BudgetMapper.toBudgetDto(budgetRepository.save(budget));
  }

  private Category findCategoryById(UUID categoryId) {
    return categoryRepository
        .findById(categoryId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Category with id %s not found".formatted(categoryId)));
  }

  private void checkIfBudgetAlreadyExists(UUID categoryId, LocalDate startDate, LocalDate endDate) {
    if (budgetRepository.existsByCategory_CategoryIdAndStartDateAndEndDate(
        categoryId, startDate, endDate)) {
      throw new ResourceAlreadyExistsException(
          "Budget with category %s, start date %s and end date %s already exists"
              .formatted(categoryId, startDate, endDate));
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<BudgetDto> getBudgetsByUserId(UUID userId, BudgetCriteria criteria) {
    return BudgetMapper.toBudgetDto(budgetRepository.filterBudgets(userId, criteria));
  }

  @Override
  @Transactional(readOnly = true)
  public BudgetDto getBudget(UUID budgetId) {
    return BudgetMapper.toBudgetDto(findBudgetById(budgetId));
  }

  @Override
  @Transactional
  public BudgetDto updateBudget(UUID budgetId, BudgetUpdateRequestDto request) {
    Budget budget = findBudgetById(budgetId);
    if (request.currency() != null) {
      checkIfCurrencyExists(request.currency());
    }
    budget = BudgetMapper.partialUpdate(budget, request);
    BudgetDto result = BudgetMapper.toBudgetDto(budgetRepository.save(budget));
    LOGGER.info(
        "Updated budget {} for user {}", result.getBudgetId(), budget.getCategory().getUserId());
    return result;
  }

  @Override
  @Transactional
  public void deleteBudget(UUID budgetId) {
    Budget budget = findBudgetById(budgetId);
    budgetRepository.delete(budget);
    eventPublisher.publishEvent(new CategoryDeletionEvent(budgetId));
    LOGGER.info("Deleted budget {} for user {}", budgetId, budget.getCategory().getUserId());
  }

  @Override
  @Transactional
  public void incrementBudgetAmount(UUID budgetId, BigDecimal amount) {
    Budget budget = findBudgetById(budgetId);
    budget.setAmount(budget.getAmount().add(amount));
    budgetRepository.save(budget);
    LOGGER.info("Incremented budget {} for user {}", budgetId, budget.getCategory().getUserId());
  }

  @Override
  @Transactional
  public void decrementBudgetAmount(UUID budgetId, BigDecimal amount) {
    Budget budget = findBudgetById(budgetId);
    budget.setAmount(budget.getAmount().subtract(amount));
    budgetRepository.save(budget);
    LOGGER.info("Decremented budget {} for user {}", budgetId, budget.getCategory().getUserId());
  }

  private Budget findBudgetById(UUID budgetId) {
    return budgetRepository
        .findById(budgetId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Budget with id %s not found".formatted(budgetId)));
  }

  private void checkIfCurrencyExists(String currency) {
    if (Boolean.FALSE.equals(currencyServiceClient.checkIfCurrencyExists(currency))) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
  }
}
