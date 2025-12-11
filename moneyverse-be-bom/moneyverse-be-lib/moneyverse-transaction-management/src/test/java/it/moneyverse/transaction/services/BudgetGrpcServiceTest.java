package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import it.moneyverse.core.model.dto.BudgetDto;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.grpc.lib.*;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetGrpcServiceTest {

  @Mock private BudgetServiceGrpc.BudgetServiceBlockingStub stub;
  @InjectMocks private BudgetGrpcService budgetGrpcService;

  @Test
  void givenCategoryId_WhenGetCategoryById_ThenReturnCategoryDto() {
    UUID categoryId = UUID.randomUUID();
    CategoryResponse response =
        CategoryResponse.newBuilder()
            .setCategoryId(categoryId.toString())
            .setUserId(RandomUtils.randomUUID().toString())
            .setDescription(RandomUtils.randomString(30))
            .setCategoryName(RandomUtils.randomString(30))
            .build();
    when(stub.getCategoryById(any(CategoryRequest.class))).thenReturn(response);

    Optional<CategoryDto> responseDto = budgetGrpcService.getCategoryById(categoryId);

    assertTrue(responseDto.isPresent());
    verify(stub, times(1)).getCategoryById(any(CategoryRequest.class));
  }

  @Test
  void givenCategoryId_WhenGetCategoryById_ThenReturnEmptyResponse() {
    UUID categoryId = UUID.randomUUID();
    CategoryResponse response = CategoryResponse.getDefaultInstance();
    when(stub.getCategoryById(any(CategoryRequest.class))).thenReturn(response);

    Optional<CategoryDto> responseDto = budgetGrpcService.getCategoryById(categoryId);

    assertTrue(responseDto.isEmpty());
    verify(stub, times(1)).getCategoryById(any(CategoryRequest.class));
  }

  @Test
  void givenCircuitBreaker_WhenGetCategoryById_ThenFallbackMethodIsTriggered() {
    UUID categoryId = UUID.randomUUID();
    Throwable throwable = mock(CallNotPermittedException.class);

    Optional<CategoryDto> responseDto =
        budgetGrpcService.fallbackGetCategoryById(categoryId, throwable);

    assertTrue(responseDto.isEmpty());
  }

  @Test
  void givenCategoryAndDate_WhenGetBudgetByCategoryIdAndDate_ThenReturnBudgetDto() {
    UUID categoryId = UUID.randomUUID();
    LocalDate date = LocalDate.now();
    BudgetResponse response =
        BudgetResponse.newBuilder()
            .setBudgetId(RandomUtils.randomUUID().toString())
            .setCategoryId(categoryId.toString())
            .setStartDate(date.minusDays(RandomUtils.randomInteger(10)).toString())
            .setEndDate(date.minusDays(RandomUtils.randomInteger(10)).toString())
            .build();
    when(stub.getBudget(any(BudgetRequest.class))).thenReturn(response);

    Optional<BudgetDto> responseDto =
        budgetGrpcService.getBudgetByCategoryIdAndDate(categoryId, date);

    assertTrue(responseDto.isPresent());
    verify(stub, times(1)).getBudget(any(BudgetRequest.class));
  }

  @Test
  void givenCategoryAndDate_WhenGetBudgetByCategoryIdAndDate_ThenReturnEmptyResponse() {
    UUID categoryId = UUID.randomUUID();
    LocalDate date = LocalDate.now();
    BudgetResponse response = BudgetResponse.getDefaultInstance();
    when(stub.getBudget(any(BudgetRequest.class))).thenReturn(response);

    Optional<BudgetDto> responseDto =
        budgetGrpcService.getBudgetByCategoryIdAndDate(categoryId, date);

    assertTrue(responseDto.isEmpty());
    verify(stub, times(1)).getBudget(any(BudgetRequest.class));
  }

  @Test
  void givenCircuitBreaker_WhenGetBudgetByCategoryIdAndDate_ThenFallbackMethodIsTriggered() {
    UUID categoryId = UUID.randomUUID();
    LocalDate date = LocalDate.now();
    Throwable throwable = mock(CallNotPermittedException.class);

    Optional<BudgetDto> responseDto =
        budgetGrpcService.fallbackGetBudgetByCategoryIdAndDate(categoryId, date, throwable);

    assertTrue(responseDto.isEmpty());
  }

  @Test
  void givenBudgetId_WhenGetBudgetById_ThenReturnBudgetDto() {
    UUID budgetId = UUID.randomUUID();
    BudgetResponse response =
        BudgetResponse.newBuilder()
            .setBudgetId(budgetId.toString())
            .setCategoryId(RandomUtils.randomUUID().toString())
            .setStartDate(LocalDate.now().minusDays(RandomUtils.randomInteger(10)).toString())
            .setEndDate(LocalDate.now().minusDays(RandomUtils.randomInteger(10)).toString())
            .build();

    when(stub.getBudget(any(BudgetRequest.class))).thenReturn(response);

    Optional<BudgetDto> responseDto = budgetGrpcService.getBudgetByBudgetId(budgetId);

    assertTrue(responseDto.isPresent());
    verify(stub, times(1)).getBudget(any(BudgetRequest.class));
  }

  @Test
  void givenBudgetId_WhenGetBudgetById_ThenReturnEmptyResponse() {
    UUID budgetId = UUID.randomUUID();
    BudgetResponse response = BudgetResponse.getDefaultInstance();
    when(stub.getBudget(any(BudgetRequest.class))).thenReturn(response);

    Optional<BudgetDto> responseDto = budgetGrpcService.getBudgetByBudgetId(budgetId);

    assertTrue(responseDto.isEmpty());
    verify(stub, times(1)).getBudget(any(BudgetRequest.class));
  }

  @Test
  void givenCircuitBreaker_WhenGetBudgetById_ThenFallbackMethodIsTriggered() {
    UUID budgetId = UUID.randomUUID();
    Throwable throwable = mock(CallNotPermittedException.class);

    Optional<BudgetDto> responseDto =
        budgetGrpcService.fallbackGetBudgetByBudgetId(budgetId, throwable);

    assertTrue(responseDto.isEmpty());
  }
}
