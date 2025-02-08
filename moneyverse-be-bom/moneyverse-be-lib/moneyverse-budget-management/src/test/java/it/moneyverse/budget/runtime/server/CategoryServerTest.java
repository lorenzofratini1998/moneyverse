package it.moneyverse.budget.runtime.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.grpc.lib.BudgetServiceGrpc;
import it.moneyverse.grpc.lib.CategoryRequest;
import it.moneyverse.grpc.lib.CategoryResponse;
import it.moneyverse.test.utils.RandomUtils;
import java.io.IOException;
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

  @BeforeEach
  void setup() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    Server server =
        InProcessServerBuilder.forName(serverName)
            .addService(new BudgetManagementGrpcService(categoryRepository))
            .directExecutor()
            .build()
            .start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    stub = BudgetServiceGrpc.newBlockingStub(channel);
    budgetServer = new BudgetServer(RandomUtils.randomBigDecimal().intValue(), categoryRepository);
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
  void checkIfCategoryExists_shouldReturnTrueForExistingCategory() {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryRequest request =
        CategoryRequest.newBuilder().setCategoryId(categoryId.toString()).build();
    when(categoryRepository.existsByCategoryId(categoryId)).thenReturn(true);

    CategoryResponse response = stub.checkIfCategoryExists(request);

    assertTrue(response.getExists());
    verify(categoryRepository, times(1)).existsByCategoryId(categoryId);
  }

  @Test
  void checkIfCategoryExists_shouldReturnFalseForNonExistingCategory() {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryRequest request =
        CategoryRequest.newBuilder().setCategoryId(categoryId.toString()).build();
    when(categoryRepository.existsByCategoryId(categoryId)).thenReturn(false);

    CategoryResponse response = stub.checkIfCategoryExists(request);

    assertFalse(response.getExists());
    verify(categoryRepository, times(1)).existsByCategoryId(categoryId);
  }
}
