package it.moneyverse.core.boot;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import it.moneyverse.core.utils.constants.UserServiceGrpcClientProperties;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import it.moneyverse.grpc.lib.UserServiceGrpc.UserServiceBlockingStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UserServiceGrpcClientProperties.class)
public class UserServiceGrpcClientAutoConfiguration {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UserServiceGrpcClientAutoConfiguration.class);

  private final UserServiceGrpcClientProperties properties;

  public UserServiceGrpcClientAutoConfiguration(UserServiceGrpcClientProperties properties) {
    this.properties = properties;
    LOGGER.info("Starting to load beans from {}", UserServiceGrpcClientAutoConfiguration.class);
  }

  @Bean
  public ManagedChannel managedChannelUserService() {
    return ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
        .usePlaintext()
        .build();
  }

  @Bean
  public UserServiceBlockingStub userServiceBlockingStub(ManagedChannel managedChannelUserService) {
    return UserServiceGrpc.newBlockingStub(managedChannelUserService);
  }
}
