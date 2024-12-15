package it.moneyverse.budget.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.utils.mapper.BudgetMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit test for {@link BudgetManagementService} */
@ExtendWith(MockitoExtension.class)
public class BudgetManagementServiceTest {

  @InjectMocks private BudgetManagementService budgetManagementService;

  @Mock private BudgetRepository budgetRepository;
  @Mock private UserServiceClient userServiceClient;
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
    final String username = RandomUtils.randomString(15);
    BudgetRequestDto request =
        new BudgetRequestDto(
            username,
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal());
    when(userServiceClient.checkIfUserExists(username)).thenReturn(true);
    when(budgetRepository.existsByUsernameAndBudgetName(username, request.budgetName()))
        .thenReturn(false);
    mapper.when(() -> BudgetMapper.toBudget(request)).thenReturn(budget);
    when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
    mapper.when(() -> BudgetMapper.toBudgetDto(budget)).thenReturn(budgetDto);

    budgetDto = budgetManagementService.createBudget(request);

    assertNotNull(budgetDto);
    verify(userServiceClient, times(1)).checkIfUserExists(username);
    verify(budgetRepository, times(1))
        .existsByUsernameAndBudgetName(username, request.budgetName());
    mapper.verify(() -> BudgetMapper.toBudget(request), times(1));
    verify(budgetRepository, times(1)).save(any(Budget.class));
    mapper.verify(() -> BudgetMapper.toBudgetDto(budget), times(1));
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenUserNotFound() {
    final String username = RandomUtils.randomString(15);
    BudgetRequestDto request =
        new BudgetRequestDto(
            username,
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal());

    when(userServiceClient.checkIfUserExists(username)).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class, () -> budgetManagementService.createBudget(request));

    verify(budgetRepository, never()).save(any(Budget.class));
    verify(budgetRepository, never()).existsByUsernameAndBudgetName(username, request.budgetName());
    verify(userServiceClient, times(1)).checkIfUserExists(username);
  }

  @Test
  void givenBudgetRequest_WhenCreateBudget_ThenBudgetAlreadyExists() {
    final String username = RandomUtils.randomString(15);
    BudgetRequestDto request =
        new BudgetRequestDto(
            username,
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal());

    when(userServiceClient.checkIfUserExists(username)).thenReturn(true);
    when(budgetRepository.existsByUsernameAndBudgetName(username, request.budgetName()))
        .thenReturn(true);

    assertThrows(
        ResourceAlreadyExistsException.class, () -> budgetManagementService.createBudget(request));

    verify(budgetRepository, never()).save(any(Budget.class));
    verify(userServiceClient, times(1)).checkIfUserExists(username);
    verify(budgetRepository, times(1))
        .existsByUsernameAndBudgetName(username, request.budgetName());
  }
}
