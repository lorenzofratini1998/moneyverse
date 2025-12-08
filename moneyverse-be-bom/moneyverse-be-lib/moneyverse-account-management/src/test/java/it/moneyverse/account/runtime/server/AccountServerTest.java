package it.moneyverse.account.runtime.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
import it.moneyverse.test.utils.RandomUtils;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
class AccountServerTest {

  private AccountServiceGrpc.AccountServiceBlockingStub stub;
  private ManagedChannel channel;
  private AccountServer accountServer;
  @Mock private AccountRepository accountRepository;
  @Mock private PlatformTransactionManager transactionManager;

  @BeforeEach
  void setup() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    Server server =
        InProcessServerBuilder.forName(serverName)
            .addService(new AccountManagementGrpcService(accountRepository, transactionManager))
            .directExecutor()
            .build()
            .start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    stub = AccountServiceGrpc.newBlockingStub(channel);
    accountServer =
        new AccountServer(
            RandomUtils.randomBigDecimal().intValue(), accountRepository, transactionManager);
    accountServer.start();
  }

  @AfterEach
  void teardown() throws InterruptedException {
    if (channel != null) {
      channel.shutdown();
    }
    accountServer.stop();
  }

  @Test
  void getAccountById_thenReturnAccountResponse(
      @Mock Account account, @Mock AccountCategory accountCategory) {
    UUID accountId = RandomUtils.randomUUID();
    AccountRequest request = AccountRequest.newBuilder().setAccountId(accountId.toString()).build();
    when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
    when(account.getAccountId()).thenReturn(accountId);
    when(account.getUserId()).thenReturn(RandomUtils.randomUUID());
    when(account.getAccountName()).thenReturn(RandomUtils.randomString(15));
    when(account.getBalance()).thenReturn(RandomUtils.randomBigDecimal());
    when(account.getBalanceTarget()).thenReturn(RandomUtils.randomBigDecimal());
    when(account.getAccountCategory()).thenReturn(accountCategory);
    when(accountCategory.getAccountCategoryId()).thenReturn(RandomUtils.randomLong());
    when(account.getAccountDescription()).thenReturn(RandomUtils.randomString(15));
    when(account.getCurrency()).thenReturn(RandomUtils.randomString(15));
    when(account.isDefault()).thenReturn(true);

    AccountResponse response = stub.getAccountById(request);

    assertEquals(accountId.toString(), response.getAccountId());
    verify(accountRepository, times(1)).findById(accountId);
  }

  @Test
  void getAccountById_thenReturnDefaultAccountResponse() {
    UUID accountId = RandomUtils.randomUUID();
    AccountRequest request = AccountRequest.newBuilder().setAccountId(accountId.toString()).build();
    when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

    AccountResponse response = stub.getAccountById(request);

    assertNotEquals(accountId.toString(), response.getAccountId());
    verify(accountRepository, times(1)).findById(accountId);
  }
}
