package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.grpc.lib.*;
import it.moneyverse.test.utils.RandomUtils;
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
}
