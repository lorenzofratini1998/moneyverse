package it.moneyverse.core.boot;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import it.moneyverse.core.utils.constants.GrpcClientPropertiesConstants;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import it.moneyverse.grpc.lib.UserServiceGrpc.UserServiceBlockingStub;
import jakarta.annotation.PostConstruct;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = GrpcClientPropertiesConstants.USER_SERVICE_CLIENT_PREFIX)
public class UserServiceGrpcClientAutoConfiguration {

  private String host;
  private Integer port;

  @PostConstruct
  public void init() {
    Objects.requireNonNull(
        host, GrpcClientPropertiesConstants.USER_SERVICE_CLIENT_HOST + " is required");
    Objects.requireNonNull(
        port, GrpcClientPropertiesConstants.USER_SERVICE_CLIENT_PORT + " is required");
  }

  @Bean
  public ManagedChannel managedChannelUserService() {
    return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
  }

  @Bean
  public UserServiceBlockingStub userServiceBlockingStub(ManagedChannel managedChannelUserService) {
    return UserServiceGrpc.newBlockingStub(managedChannelUserService);
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
