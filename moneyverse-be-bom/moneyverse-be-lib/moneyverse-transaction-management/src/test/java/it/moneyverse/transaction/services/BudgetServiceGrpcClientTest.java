package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.exceptions.ResourceStillExistsException;
import it.moneyverse.core.model.dto.BudgetDto;
import it.moneyverse.core.model.dto.CategoryDto;
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
class BudgetServiceGrpcClientTest {

  @InjectMocks private BudgetServiceGrpcClient budgetServiceGrpcClient;
  @Mock private BudgetGrpcService budgetGrpcService;

  @Test
  void testGetCategoryById(@Mock CategoryDto categoryDto) {
    UUID categoryId = UUID.randomUUID();
    when(budgetGrpcService.getCategoryById(categoryId)).thenReturn(Optional.of(categoryDto));

    Optional<CategoryDto> response = budgetServiceGrpcClient.getCategoryById(categoryId);
    assertTrue(response.isPresent());
    verify(budgetGrpcService, times(1)).getCategoryById(categoryId);
  }

  @Test
  void testCheckIfCategoryExists(@Mock CategoryDto categoryDto) {
    UUID categoryId = RandomUtils.randomUUID();
    when(budgetGrpcService.getCategoryById(categoryId)).thenReturn(Optional.of(categoryDto));

    assertDoesNotThrow(() -> budgetServiceGrpcClient.checkIfCategoryExists(categoryId));
    verify(budgetGrpcService, times(1)).getCategoryById(categoryId);
  }

  @Test
  void testCheckIfCategoryExists_Exception() {
    UUID categoryId = RandomUtils.randomUUID();
    when(budgetGrpcService.getCategoryById(categoryId)).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> budgetServiceGrpcClient.checkIfCategoryExists(categoryId));
    verify(budgetGrpcService, times(1)).getCategoryById(categoryId);
  }

  @Test
  void testCheckIfCategoryStillExists() {
    UUID categoryId = RandomUtils.randomUUID();
    when(budgetGrpcService.getCategoryById(categoryId)).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> budgetServiceGrpcClient.checkIfCategoryStillExists(categoryId));
    verify(budgetGrpcService, times(1)).getCategoryById(categoryId);
  }

  @Test
  void testCheckIfCategoryStillExists_Exception(@Mock CategoryDto categoryDto) {
    UUID categoryId = RandomUtils.randomUUID();
    when(budgetGrpcService.getCategoryById(categoryId)).thenReturn(Optional.of(categoryDto));
    assertThrows(
        ResourceStillExistsException.class,
        () -> budgetServiceGrpcClient.checkIfCategoryStillExists(categoryId));
    verify(budgetGrpcService, times(1)).getCategoryById(categoryId);
  }

  @Test
  void testGetBudgetByCategoryIdAndDate(@Mock BudgetDto budgetDto) {
    UUID categoryId = RandomUtils.randomUUID();
    LocalDate date = RandomUtils.randomDate();
    UUID budgetId = RandomUtils.randomUUID();
    when(budgetGrpcService.getBudgetByCategoryIdAndDate(categoryId, date))
        .thenReturn(Optional.of(budgetDto));
    when(budgetDto.getBudgetId()).thenReturn(budgetId);

    UUID response = budgetServiceGrpcClient.getBudgetId(categoryId, date);

    assertEquals(budgetId, response);
    verify(budgetGrpcService, times(1)).getBudgetByCategoryIdAndDate(categoryId, date);
  }

  @Test
  void testCheckIfBudgetStillExists() {
    UUID budgetId = RandomUtils.randomUUID();
    when(budgetGrpcService.getBudgetByBudgetId(budgetId)).thenReturn(Optional.empty());

    assertDoesNotThrow(() -> budgetServiceGrpcClient.checkIfBudgetStillExists(budgetId));
    verify(budgetGrpcService, times(1)).getBudgetByBudgetId(budgetId);
  }

  @Test
  void testCheckIfBudgetStillExists_Exception(@Mock BudgetDto budgetDto) {
    UUID budgetId = RandomUtils.randomUUID();
    when(budgetGrpcService.getBudgetByBudgetId(budgetId)).thenReturn(Optional.of(budgetDto));
    assertThrows(
        ResourceStillExistsException.class,
        () -> budgetServiceGrpcClient.checkIfBudgetStillExists(budgetId));
    verify(budgetGrpcService, times(1)).getBudgetByBudgetId(budgetId);
  }
}
