package it.moneyverse.user.runtime.server;

import io.grpc.ServerBuilder;
import it.moneyverse.core.runtime.server.GrpcServer;
import it.moneyverse.user.services.KeycloakService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "grpc.server.user-service.port")
public class UserServer extends GrpcServer {

  public UserServer(
      @Value("${grpc.server.user-service.port}") Integer port, KeycloakService keycloakService) {
    this.port = port;
    this.server =
        ServerBuilder.forPort(port).addService(new UserGrpcService(keycloakService)).build();
  }

  @PostConstruct
  public void start() throws IOException {
    super.start();
  }
}
