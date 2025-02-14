package it.moneyverse.currency.runtime.server;

import io.grpc.ServerBuilder;
import it.moneyverse.core.runtime.server.GrpcServer;
import it.moneyverse.currency.model.repositories.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "grpc.server.currency-service.port")
public class CurrencyServer extends GrpcServer {

  public CurrencyServer(
      @Value("${grpc.server.currency-service.port}") Integer port,
      CurrencyRepository currencyRepository) {
    this.port = port;
    this.server =
        ServerBuilder.forPort(port)
            .addService(new CurrencyManagementGrpcService(currencyRepository))
            .build();
  }

  @Override
  @PostConstruct
  public void start() throws IOException {
    super.start();
  }
}
