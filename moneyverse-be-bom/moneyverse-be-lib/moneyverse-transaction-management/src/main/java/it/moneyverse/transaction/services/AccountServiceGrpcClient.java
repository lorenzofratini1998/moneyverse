package it.moneyverse.transaction.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.utils.properties.AccountServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceGrpcClient implements AccountServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceGrpcClient.class);

  private final AccountServiceGrpc.AccountServiceBlockingStub stub;

  public AccountServiceGrpcClient(AccountServiceGrpc.AccountServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Override
  @CircuitBreaker(
      name = AccountServiceGrpcCircuitBreakerProperties.ACCOUNT_SERVICE_GRPC,
      fallbackMethod = "fallbackCheckIfAccountExists")
  public Boolean checkIfAccountExists(UUID accountId) {
    final AccountResponse response =
        stub.checkIfAccountExists(
            AccountRequest.newBuilder().setAccountId(accountId.toString()).build());
    return response.getExists();
  }

  Boolean fallbackCheckIfAccountExists(UUID accountId, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the AccountService to check whether the account {} exists. Returning FALSE as fallback: {}",
        accountId,
        throwable.getMessage());
    return false;
  }
}
