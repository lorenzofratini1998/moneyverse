package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.exceptions.ResourceStillExistsException;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.test.utils.RandomUtils;
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
}
