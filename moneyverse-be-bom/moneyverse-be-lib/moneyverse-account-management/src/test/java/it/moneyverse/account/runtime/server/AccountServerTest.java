package it.moneyverse.account.runtime.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
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
public class AccountServerTest {

  private AccountServiceGrpc.AccountServiceBlockingStub stub;
  private ManagedChannel channel;
  private AccountServer accountServer;
  @Mock private AccountRepository accountRepository;

  @BeforeEach
  void setup() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    Server server =
        InProcessServerBuilder.forName(serverName)
            .addService(new AccountServer.AccountGrpcService(accountRepository))
            .directExecutor()
            .build()
            .start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    stub = AccountServiceGrpc.newBlockingStub(channel);
    accountServer = new AccountServer(RandomUtils.randomBigDecimal().intValue(), accountRepository);
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
  void checkIfAccountExists_shouldReturnTrueForExistingAccount() {
    UUID accountId = RandomUtils.randomUUID();
    AccountRequest request = AccountRequest.newBuilder().setAccountId(accountId.toString()).build();
    when(accountRepository.existsByAccountId(accountId)).thenReturn(true);

    AccountResponse response = stub.checkIfAccountExists(request);

    assertTrue(response.getExists());
    verify(accountRepository, times(1)).existsByAccountId(accountId);
  }

  @Test
  void checkIfAccountExists_shouldReturnFalseForNonExistingAccount() {
    UUID accountId = RandomUtils.randomUUID();
    AccountRequest request = AccountRequest.newBuilder().setAccountId(accountId.toString()).build();
    when(accountRepository.existsByAccountId(accountId)).thenReturn(false);

    AccountResponse response = stub.checkIfAccountExists(request);

    assertFalse(response.getExists());
    verify(accountRepository, times(1)).existsByAccountId(accountId);
  }
}
