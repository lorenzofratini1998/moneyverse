package it.moneyverse.account.runtime.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
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
@ConditionalOnProperty(name = "grpc.server.account-service.port")
public class AccountServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountServer.class);

  private final Integer port;
  private final Server server;

  public AccountServer(
      @Value("${grpc.server.account-service.port}") Integer port,
      AccountRepository accountRepository) {
    this.port = port;
    this.server =
        ServerBuilder.forPort(port).addService(new AccountGrpcService(accountRepository)).build();
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
                    AccountServer.this.stop();
                  } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                  }
                  System.err.println("*** server shut down");
                }));
  }

  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  protected static class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    private final AccountRepository accountRepository;

    AccountGrpcService(AccountRepository accountRepository) {
      this.accountRepository = accountRepository;
    }

    @Override
    public void checkIfAccountExists(
        AccountRequest request, StreamObserver<AccountResponse> responseObserver) {
      boolean exists = accountRepository.existsByAccountId(UUID.fromString(request.getAccountId()));
      AccountResponse response = AccountResponse.newBuilder().setExists(exists).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }

  public Integer getPort() {
    return port;
  }
}
