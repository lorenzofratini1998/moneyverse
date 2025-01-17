package it.moneyverse.budget.runtime.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.grpc.lib.*;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "grpc.server.budget-service.port")
public class BudgetServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetServer.class);

  private final Integer port;
  private final Server server;

  public BudgetServer(
      @Value("${grpc.server.budget-service.port}") Integer port,
      BudgetRepository budgetRepository) {
    this.port = port;
    this.server =
        ServerBuilder.forPort(port).addService(new BudgetGrpcService(budgetRepository)).build();
  }

  @PostConstruct
  public void start() throws IOException {
    server.start();
    LOGGER.info("Server started, listening on {}", port);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.err.println("*** shutting down gRPC server since JVM is shutting down");
                  try {
                    BudgetServer.this.stop();
                  } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                  }
                  System.err.println("*** server shut down");
                }));
  }

  void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  protected static class BudgetGrpcService extends BudgetServiceGrpc.BudgetServiceImplBase {

    private final BudgetRepository budgetRepository;

    BudgetGrpcService(BudgetRepository budgetRepository) {
      this.budgetRepository = budgetRepository;
    }

    @Override
    public void checkIfBudgetExists(
        BudgetRequest request, StreamObserver<BudgetResponse> responseObserver) {
      boolean exists = budgetRepository.existsByBudgetId(UUID.fromString(request.getBudgetId()));
      BudgetResponse response = BudgetResponse.newBuilder().setExists(exists).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}
