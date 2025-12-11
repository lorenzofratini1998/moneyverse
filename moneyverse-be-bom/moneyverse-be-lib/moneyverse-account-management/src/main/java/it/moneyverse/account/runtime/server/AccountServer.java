package it.moneyverse.account.runtime.server;

import io.grpc.ServerBuilder;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.core.runtime.server.GrpcServer;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@ConditionalOnProperty(name = "grpc.server.account-service.port")
public class AccountServer extends GrpcServer {

  public AccountServer(
      @Value("${grpc.server.account-service.port}") Integer port,
      AccountRepository accountRepository,
      PlatformTransactionManager platformTransactionManager) {
    this.port = port;
    this.server =
        ServerBuilder.forPort(port)
            .addService(
                new AccountManagementGrpcService(accountRepository, platformTransactionManager))
            .build();
  }

  @Override
  @PostConstruct
  public void start() throws IOException {
    super.start();
  }
}
