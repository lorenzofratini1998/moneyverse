package it.moneyverse.budget.services;

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
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.core.services.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BudgetManagementService implements BudgetService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetManagementService.class);

  private final BudgetRepository budgetRepository;
  private final DefaultBudgetTemplateRepository defaultBudgetTemplateRepository;
  private final UserServiceClient userServiceClient;
  private final MessageProducer<UUID, String> messageProducer;

  public BudgetManagementService(
          BudgetRepository budgetRepository, DefaultBudgetTemplateRepository defaultBudgetTemplateRepository, UserServiceClient userServiceClient, MessageProducer<UUID, String> messageProducer) {
    this.budgetRepository = budgetRepository;
      this.defaultBudgetTemplateRepository = defaultBudgetTemplateRepository;
      this.userServiceClient = userServiceClient;
      this.messageProducer = messageProducer;
  }

  @Override
  @Transactional
  public BudgetDto createBudget(BudgetRequestDto request) {
    checkIfUserExists(request.username());
    checkIfBudgetAlreadyExists(request.username(), request.budgetName());
    LOGGER.info("Creating budget {} for user {}", request.budgetName(), request.username());
    Budget budget = BudgetMapper.toBudget(request);
    BudgetDto result = BudgetMapper.toBudgetDto(budgetRepository.save(budget));
    LOGGER.info("Created budget {} for user {}", result, request.username());
    return result;
  }

  @Override
  @Transactional
  public void createDefaultBudgets(String username) {
    checkIfUserExists(username);
    LOGGER.info("Creating default budgets for user {}", username);
    List<Budget> defaultBudgets = defaultBudgetTemplateRepository.findAll()
            .stream()
            .map(defaultBudgetTemplate -> {
              Budget budget = new Budget();
              budget.setUsername(username);
              budget.setBudgetName(defaultBudgetTemplate.getName());
              budget.setDescription(defaultBudgetTemplate.getDescription());
              return budget;
            })
            .toList();
    budgetRepository.saveAll(defaultBudgets);
  }

  private void checkIfBudgetAlreadyExists(String username, String budgetName) {
    if (Boolean.TRUE.equals(budgetRepository.existsByUsernameAndBudgetName(username, budgetName))) {
      throw new ResourceAlreadyExistsException(
          "Budget with name %s already exists for user %s".formatted(budgetName, username));
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<BudgetDto> getBudgets(BudgetCriteria criteria) {
    if (criteria.getPage() == null) {
      criteria.setPage(new PageCriteria());
    }
    if (criteria.getSort() == null) {
      criteria.setSort(
              new SortCriteria<>(
                      SortAttribute.getDefault(BudgetSortAttributeEnum.class), Sort.Direction.ASC));
    }
    LOGGER.info("Finding budgets with filters: {}", criteria);
    return BudgetMapper.toBudgetDto(budgetRepository.findBudgets(criteria));
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
    budget = BudgetMapper.partialUpdate(budget, budgetDto);
    BudgetDto result = BudgetMapper.toBudgetDto(budgetRepository.save(budget));
    LOGGER.info("Updated budget {} for user {}", result, budget.getUsername());
    return result;
  }

  @Override
  @Transactional
  public void deleteBudget(UUID budgetId) {
    Budget budget = findBudgetById(budgetId);
    budgetRepository.delete(budget);
    messageProducer.send(
        new BudgetDeletionEvent(budgetId, budget.getUsername()), BudgetDeletionTopic.TOPIC);
    LOGGER.info("Deleted budget {} for user {}", budget, budget.getUsername());
  }

  @Override
  @Transactional
  public void deleteAllBudgets(String username) {
    checkIfUserExists(username);
  }

  private void checkIfUserExists(String username) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(username))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(username));
    }
    LOGGER.info("Deleting accounts by username {}", username);
    budgetRepository.deleteAll(budgetRepository.findBudgetByUsername(username));
  }

  private Budget findBudgetById(UUID budgetId) {
    return budgetRepository
        .findById(budgetId)
        .orElseThrow(() -> new ResourceNotFoundException("Budget with id %s not found".formatted(budgetId)));
  }
}
