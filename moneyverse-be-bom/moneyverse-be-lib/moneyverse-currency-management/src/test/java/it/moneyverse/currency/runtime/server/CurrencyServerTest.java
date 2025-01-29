package it.moneyverse.currency.runtime.server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.grpc.lib.CurrencyRequest;
import it.moneyverse.grpc.lib.CurrencyResponse;
import it.moneyverse.grpc.lib.CurrencyServiceGrpc;
import it.moneyverse.test.utils.RandomUtils;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrencyServerTest {

  private CurrencyServiceGrpc.CurrencyServiceBlockingStub stub;
  private ManagedChannel channel;
  private CurrencyServer currencyServer;
  @Mock private CurrencyRepository currencyRepository;

  @BeforeEach
  void setup() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    Server server =
        InProcessServerBuilder.forName(serverName)
            .addService(new CurrencyServer.CurrencyGrpcService(currencyRepository))
            .directExecutor()
            .build()
            .start();
    stub = CurrencyServiceGrpc.newBlockingStub(channel);
    currencyServer =
        new CurrencyServer(RandomUtils.randomBigDecimal().intValue(), currencyRepository);
    currencyServer.start();
  }

  @AfterEach
  void teardown() throws InterruptedException {
    if (channel != null) {
      channel.shutdown();
    }
    currencyServer.stop();
  }

  @Test
  void checkIfCurrencyExists_shouldReturnTrueForExistingCurrency() {
    String code = RandomUtils.randomString(3).toUpperCase();
    CurrencyRequest request = CurrencyRequest.newBuilder().setCode(code).build();
    when(currencyRepository.existsByCode(code)).thenReturn(true);

    CurrencyResponse response = stub.checkIfCurrencyExists(request);

    assertTrue(response.getExists());
    verify(currencyRepository, times(1)).existsByCode(code);
  }

  @Test
  void checkIfCurrencyExists_shouldReturnFalseForExistingCurrency() {
    String code = RandomUtils.randomString(3).toUpperCase();
    CurrencyRequest request = CurrencyRequest.newBuilder().setCode(code).build();
    when(currencyRepository.existsByCode(code)).thenReturn(false);

    CurrencyResponse response = stub.checkIfCurrencyExists(request);

    assertFalse(response.getExists());
    verify(currencyRepository, times(1)).existsByCode(code);
  }
}
