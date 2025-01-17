package it.moneyverse.budget.runtime.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.grpc.lib.BudgetRequest;
import it.moneyverse.grpc.lib.BudgetResponse;
import it.moneyverse.grpc.lib.BudgetServiceGrpc;
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
public class BudgetServerTest {

  private BudgetServiceGrpc.BudgetServiceBlockingStub stub;
  private ManagedChannel channel;
  private BudgetServer budgetServer;
  @Mock private BudgetRepository budgetRepository;

  @BeforeEach
  void setup() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    Server server =
        InProcessServerBuilder.forName(serverName)
            .addService(new BudgetServer.BudgetGrpcService(budgetRepository))
            .directExecutor()
            .build()
            .start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    stub = BudgetServiceGrpc.newBlockingStub(channel);
    budgetServer = new BudgetServer(RandomUtils.randomBigDecimal().intValue(), budgetRepository);
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
  void checkIfBudgetExists_shouldReturnTrueForExistingBudget() {
    UUID budgetId = RandomUtils.randomUUID();
    BudgetRequest request = BudgetRequest.newBuilder().setBudgetId(budgetId.toString()).build();
    when(budgetRepository.existsByBudgetId(budgetId)).thenReturn(true);

    BudgetResponse response = stub.checkIfBudgetExists(request);

    assertTrue(response.getExists());
    verify(budgetRepository, times(1)).existsByBudgetId(budgetId);
  }

  @Test
  void checkIfBudgetExists_shouldReturnFalseForNonExistingBudget() {
    UUID budgetId = RandomUtils.randomUUID();
    BudgetRequest request = BudgetRequest.newBuilder().setBudgetId(budgetId.toString()).build();
    when(budgetRepository.existsByBudgetId(budgetId)).thenReturn(false);

    BudgetResponse response = stub.checkIfBudgetExists(request);

    assertFalse(response.getExists());
    verify(budgetRepository, times(1)).existsByBudgetId(budgetId);
  }
}
