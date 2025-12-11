package it.moneyverse.transaction.services;

import static it.moneyverse.core.utils.constants.CacheConstants.ACCOUNTS_CACHE;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.core.utils.properties.AccountServiceGrpcCircuitBreakerProperties;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AccountGrpcService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountGrpcService.class);
  private final AccountServiceGrpc.AccountServiceBlockingStub stub;

  public AccountGrpcService(AccountServiceGrpc.AccountServiceBlockingStub stub) {
    this.stub = stub;
  }

  @Cacheable(value = ACCOUNTS_CACHE, key = "#accountId", unless = "#result == null")
  @CircuitBreaker(
      name = AccountServiceGrpcCircuitBreakerProperties.ACCOUNT_SERVICE_GRPC,
      fallbackMethod = "fallbackGetAccountById")
  public Optional<AccountDto> getAccountById(UUID accountId) {
    final AccountResponse response =
        stub.getAccountById(AccountRequest.newBuilder().setAccountId(accountId.toString()).build());
    if (response == null || isEmptyResponse(response)) {
      return Optional.empty();
    }
    return Optional.of(
        AccountDto.builder()
            .withAccountId(UUID.fromString(response.getAccountId()))
            .withUserId(UUID.fromString(response.getUserId()))
            .withAccountName(response.getAccountName())
            .withBalance(BigDecimal.valueOf(response.getBalance()))
            .withBalanceTarget(BigDecimal.valueOf(response.getBalanceTarget()))
            .withAccountCategory(response.getAccountCategory())
            .withAccountDescription(response.getAccountDescription())
            .withCurrency(response.getCurrency())
            .build());
  }

  private boolean isEmptyResponse(AccountResponse response) {
    return response.getAccountId().isEmpty()
        && response.getUserId().isEmpty()
        && response.getAccountName().isEmpty()
        && response.getAccountCategory().isEmpty()
        && response.getAccountDescription().isEmpty()
        && response.getCurrency().isEmpty();
  }

  Optional<AccountDto> fallbackGetAccountById(UUID accountId, Throwable throwable) {
    LOGGER.error(
        "Impossible to contact the AccountService to check whether the account {} exists. Returning FALSE as fallback: {}",
        accountId,
        throwable.getMessage());
    return Optional.empty();
  }
}
