package it.moneyverse.budget.services;

import static it.moneyverse.core.utils.constants.CommonConstants.BACKEND;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.event.BudgetDeletionEvent;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.DefaultBudgetTemplateRepository;
import it.moneyverse.budget.utils.mapper.BudgetMapper;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.BudgetDeletionTopic;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.core.services.UserServiceClient;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetManagementService implements BudgetService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetManagementService.class);

  private final BudgetRepository budgetRepository;
  private final DefaultBudgetTemplateRepository defaultBudgetTemplateRepository;
  private final UserServiceClient userServiceClient;
  private final CurrencyServiceClient currencyServiceClient;
  private final MessageProducer<UUID, String> messageProducer;

  public BudgetManagementService(
      BudgetRepository budgetRepository,
      DefaultBudgetTemplateRepository defaultBudgetTemplateRepository,
      UserServiceClient userServiceClient,
      CurrencyServiceClient currencyServiceClient,
      MessageProducer<UUID, String> messageProducer) {
    this.budgetRepository = budgetRepository;
    this.defaultBudgetTemplateRepository = defaultBudgetTemplateRepository;
    this.userServiceClient = userServiceClient;
    this.currencyServiceClient = currencyServiceClient;
    this.messageProducer = messageProducer;
  }

  @Override
  @Transactional
  public BudgetDto createBudget(BudgetRequestDto request) {
    checkIfUserExists(request.userId());
    checkIfCurrencyExists(request.currency());
    checkIfBudgetAlreadyExists(request.userId(), request.budgetName());
    LOGGER.info("Creating budget {} for user {}", request.budgetName(), request.userId());
    Budget budget = BudgetMapper.toBudget(request);
    BudgetDto result = BudgetMapper.toBudgetDto(budgetRepository.save(budget));
    LOGGER.info("Created budget {} for user {}", result, request.userId());
    return result;
  }

  @Override
  @Transactional
  public void createDefaultBudgets(UUID userId, String currency) {
    checkIfUserExists(userId);
    checkIfCurrencyExists(currency);
    LOGGER.info("Creating default budgets for user {}", userId);
    List<Budget> defaultBudgets =
        defaultBudgetTemplateRepository.findAll().stream()
            .map(
                defaultBudgetTemplate -> {
                  Budget budget = new Budget();
                  budget.setUserId(userId);
                  budget.setBudgetName(defaultBudgetTemplate.getName());
                  budget.setDescription(defaultBudgetTemplate.getDescription());
                  budget.setCurrency(currency);
                  budget.setCreatedBy(BACKEND);
                  budget.setUpdatedBy(BACKEND);
                  return budget;
                })
            .toList();
    budgetRepository.saveAll(defaultBudgets);
  }

  private void checkIfBudgetAlreadyExists(UUID userId, String budgetName) {
    if (Boolean.TRUE.equals(budgetRepository.existsByUserIdAndBudgetName(userId, budgetName))) {
      throw new ResourceAlreadyExistsException(
          "Budget with name %s already exists for user %s".formatted(budgetName, userId));
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<BudgetDto> getBudgets(UUID userId, BudgetCriteria criteria) {
    if (criteria.getPage() == null) {
      criteria.setPage(new PageCriteria());
    }
    if (criteria.getSort() == null) {
      criteria.setSort(
          new SortCriteria<>(
              SortAttribute.getDefault(BudgetSortAttributeEnum.class), Sort.Direction.ASC));
    }
    LOGGER.info("Finding budgets with filters: {}", criteria);
    return BudgetMapper.toBudgetDto(budgetRepository.findBudgets(userId, criteria));
  }

  @Override
  @Transactional(readOnly = true)
  public BudgetDto getBudget(UUID budgetId) {
    return BudgetMapper.toBudgetDto(findBudgetById(budgetId));
  }

  @Override
  @Transactional
  public BudgetDto updateBudget(UUID budgetId, BudgetUpdateRequestDto budgetDto) {
    Budget budget = findBudgetById(budgetId);
    if (budgetDto.currency() != null) {
      checkIfCurrencyExists(budgetDto.currency());
    }
    budget = BudgetMapper.partialUpdate(budget, budgetDto);
    BudgetDto result = BudgetMapper.toBudgetDto(budgetRepository.save(budget));
    LOGGER.info("Updated budget {} for user {}", result, budget.getUserId());
    return result;
  }

  @Override
  @Transactional
  public void deleteBudget(UUID budgetId) {
    Budget budget = findBudgetById(budgetId);
    budgetRepository.delete(budget);
    messageProducer.send(
        new BudgetDeletionEvent(budgetId, budget.getUserId()), BudgetDeletionTopic.TOPIC);
    LOGGER.info("Deleted budget {} for user {}", budget, budget.getUserId());
  }

  @Override
  @Transactional
  public void deleteAllBudgets(UUID userId) {
    checkIfUserExists(userId);
    LOGGER.info("Deleting accounts by username {}", userId);
    budgetRepository.deleteAll(budgetRepository.findBudgetByUserId(userId));
  }

  private void checkIfUserExists(UUID userId) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(userId))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(userId));
    }
  }

  private void checkIfCurrencyExists(String currency) {
    if (Boolean.FALSE.equals(currencyServiceClient.checkIfCurrencyExists(currency))) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
  }

  private Budget findBudgetById(UUID budgetId) {
    return budgetRepository
        .findById(budgetId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Budget with id %s not found".formatted(budgetId)));
  }
}
