package it.moneyverse.currency.runtime.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import it.moneyverse.grpc.lib.CurrencyRequest;
import it.moneyverse.grpc.lib.CurrencyResponse;
import it.moneyverse.grpc.lib.CurrencyServiceGrpc;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "grpc.server.currency-service.port")
public class CurrencyServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyServer.class);

  private final Integer port;
  private final Server server;

  public CurrencyServer(
      @Value("${grpc.server.currency-service.port}") Integer port,
      CurrencyRepository currencyRepository) {
    this.port = port;
    this.server =
        ServerBuilder.forPort(port).addService(new CurrencyGrpcService(currencyRepository)).build();
  }

  @PostConstruct
  public void start() throws IOException {
    server.start();
    LOGGER.info("Server started, listening on {}", port);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  LOGGER.warn("*** shutting down gRPC server since JVM is shutting down");
                  try {
                    CurrencyServer.this.stop();
                  } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                  }
                  LOGGER.warn("*** server shut down");
                }));
  }

  void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  protected static class CurrencyGrpcService extends CurrencyServiceGrpc.CurrencyServiceImplBase {

    private final CurrencyRepository currencyRepository;

    public CurrencyGrpcService(CurrencyRepository currencyRepository) {
      this.currencyRepository = currencyRepository;
    }

    @Override
    public void checkIfCurrencyExists(
        CurrencyRequest request, StreamObserver<CurrencyResponse> responseObserver) {
      boolean exists = currencyRepository.existsByCode(request.getCode());
      CurrencyResponse response = CurrencyResponse.newBuilder().setExists(exists).build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}
