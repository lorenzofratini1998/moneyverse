package it.moneyverse.transaction.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
import it.moneyverse.test.utils.RandomUtils;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountGrpcServiceTest {

  @Mock private AccountServiceGrpc.AccountServiceBlockingStub stub;
  @InjectMocks private AccountGrpcService accountGrpcService;

  @Test
  void givenAccountId_WhenGetAccountById_ThenReturnAccountDto() {
    UUID accountId = UUID.randomUUID();
    AccountResponse response =
        AccountResponse.newBuilder()
            .setAccountId(accountId.toString())
            .setUserId(RandomUtils.randomUUID().toString())
            .setAccountName(RandomUtils.randomString(30))
            .setBalance(RandomUtils.randomBigDecimal().doubleValue())
            .setBalanceTarget(RandomUtils.randomBigDecimal().doubleValue())
            .setAccountCategory(RandomUtils.randomString(15))
            .setAccountDescription(RandomUtils.randomString(30))
            .setCurrency(RandomUtils.randomString(3).toUpperCase())
            .build();
    when(stub.getAccountById(any(AccountRequest.class))).thenReturn(response);

    Optional<AccountDto> responseDto = accountGrpcService.getAccountById(accountId);

    assertTrue(responseDto.isPresent());
    verify(stub, times(1)).getAccountById(any(AccountRequest.class));
  }

  @Test
  void givenAccountId_WhenGetAccountById_ThenReturnEmptyResponse() {
    UUID accountId = UUID.randomUUID();
    AccountResponse response = AccountResponse.getDefaultInstance();
    when(stub.getAccountById(any(AccountRequest.class))).thenReturn(response);

    Optional<AccountDto> responseDto = accountGrpcService.getAccountById(accountId);

    assertTrue(responseDto.isEmpty());
    verify(stub, times(1)).getAccountById(any(AccountRequest.class));
  }

  @Test
  void givenCircuitBreaker_WhenGetAccountById_ThenFallbackMethodIsTriggered() {
    UUID accountId = UUID.randomUUID();
    Throwable throwable = mock(CallNotPermittedException.class);

    Optional<AccountDto> responseDto =
        accountGrpcService.fallbackGetAccountById(accountId, throwable);

    assertTrue(responseDto.isEmpty());
  }
}
