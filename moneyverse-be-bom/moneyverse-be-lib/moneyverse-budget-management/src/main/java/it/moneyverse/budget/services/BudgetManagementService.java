package it.moneyverse.budget.services;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.utils.mapper.BudgetMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.UserServiceClient;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BudgetManagementService implements BudgetService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetManagementService.class);

  private final BudgetRepository budgetRepository;
  private final UserServiceClient userServiceClient;

  public BudgetManagementService(
      BudgetRepository budgetRepository, UserServiceClient userServiceClient) {
    this.budgetRepository = budgetRepository;
    this.userServiceClient = userServiceClient;
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

  private void checkIfUserExists(String username) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(username))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(username));
    }
  }

  private void checkIfBudgetAlreadyExists(String username, String budgetName) {
    if (Boolean.TRUE.equals(budgetRepository.existsByUsernameAndBudgetName(username, budgetName))) {
      throw new ResourceAlreadyExistsException(
          "Budget with name %s already exists for user %s".formatted(budgetName, username));
    }
  }
}