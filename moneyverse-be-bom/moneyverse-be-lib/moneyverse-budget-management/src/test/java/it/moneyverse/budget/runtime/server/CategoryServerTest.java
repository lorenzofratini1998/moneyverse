package it.moneyverse.budget.runtime.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.grpc.lib.*;
import it.moneyverse.test.utils.RandomUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServerTest {

  private BudgetServiceGrpc.BudgetServiceBlockingStub stub;
  private ManagedChannel channel;
  private BudgetServer budgetServer;
  @Mock private CategoryRepository categoryRepository;
  @Mock private BudgetRepository budgetRepository;

  @BeforeEach
  void setup() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    Server server =
        InProcessServerBuilder.forName(serverName)
            .addService(new BudgetManagementGrpcService(categoryRepository, budgetRepository))
            .directExecutor()
            .build()
            .start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    stub = BudgetServiceGrpc.newBlockingStub(channel);
    budgetServer =
        new BudgetServer(
            RandomUtils.randomBigDecimal().intValue(), categoryRepository, budgetRepository);
    budgetServer.start();
  }

  @AfterEach
  void teardown() throws InterruptedException {
    if (channel != null) {
      channel.shutdown();
    }
    budgetServer.stop();
  }

  @Test
  void getCategoryById_thenReturnCategoryResponse(@Mock Category category) {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryRequest request =
        CategoryRequest.newBuilder().setCategoryId(categoryId.toString()).build();
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(category.getCategoryId()).thenReturn(categoryId);
    when(category.getUserId()).thenReturn(RandomUtils.randomUUID());
    when(category.getDescription()).thenReturn(RandomUtils.randomString(15));
    when(category.getCategoryName()).thenReturn(RandomUtils.randomString(15));

    CategoryResponse response = stub.getCategoryById(request);

    assertEquals(categoryId.toString(), response.getCategoryId());
    verify(categoryRepository, times(1)).findById(categoryId);
  }

  @Test
  void getCategoryById_thenReturnDefaultCategoryResponse() {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryRequest request =
        CategoryRequest.newBuilder().setCategoryId(categoryId.toString()).build();
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    CategoryResponse response = stub.getCategoryById(request);

    assertNotEquals(categoryId.toString(), response.getCategoryId());
    verify(categoryRepository, times(1)).findById(categoryId);
  }

  @Test
  void getBudgetByCategoryAndDate_ThenReturnBudgetResponse(
      @Mock Budget budget, @Mock Category category) {
    UUID categoryId = RandomUtils.randomUUID();
    LocalDate date = RandomUtils.randomDate();
    BudgetRequest request =
        BudgetRequest.newBuilder()
            .setCategoryId(categoryId.toString())
            .setDate(date.toString())
            .build();
    when(budgetRepository.findBudgetByCategoryAndDate(categoryId, date))
        .thenReturn(Optional.of(budget));
    when(budget.getBudgetId()).thenReturn(RandomUtils.randomUUID());
    when(budget.getCategory()).thenReturn(category);
    when(budget.getCategory().getCategoryId()).thenReturn(categoryId);
    when(budget.getStartDate()).thenReturn(RandomUtils.randomDate());
    when(budget.getEndDate()).thenReturn(RandomUtils.randomDate());

    BudgetResponse response = stub.getBudget(request);

    assertEquals(categoryId.toString(), response.getCategoryId());
    verify(budgetRepository, times(1)).findBudgetByCategoryAndDate(categoryId, date);
  }

  @Test
  void getBudgetByCategoryAndDate_ThenReturnDefaultBudgetResponse() {
    UUID categoryId = RandomUtils.randomUUID();
    LocalDate date = RandomUtils.randomDate();
    BudgetRequest request =
        BudgetRequest.newBuilder()
            .setCategoryId(categoryId.toString())
            .setDate(date.toString())
            .build();
    when(budgetRepository.findBudgetByCategoryAndDate(categoryId, date))
        .thenReturn(Optional.empty());

    BudgetResponse response = stub.getBudget(request);

    assertNotEquals(categoryId.toString(), response.getCategoryId());
    verify(budgetRepository, times(1)).findBudgetByCategoryAndDate(categoryId, date);
  }
}
