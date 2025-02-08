package it.moneyverse.budget.runtime.server;

import io.grpc.ServerBuilder;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.core.runtime.server.GrpcServer;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "grpc.server.budget-service.port")
public class BudgetServer extends GrpcServer {

  public BudgetServer(
      @Value("${grpc.server.budget-service.port}") Integer port,
      CategoryRepository categoryRepository) {
    this.port = port;
    this.server =
        ServerBuilder.forPort(port)
            .addService(new BudgetManagementGrpcService(categoryRepository))
            .build();
  }

  @PostConstruct
  public void start() throws IOException {
    super.start();
  }
}
