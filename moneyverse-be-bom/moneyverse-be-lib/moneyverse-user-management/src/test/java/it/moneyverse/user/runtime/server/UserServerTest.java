package it.moneyverse.user.runtime.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import it.moneyverse.core.model.dto.UserDto;
import it.moneyverse.grpc.lib.UserRequest;
import it.moneyverse.grpc.lib.UserResponse;
import it.moneyverse.grpc.lib.UserServiceGrpc;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.user.model.repositories.UserPreferenceRepository;
import it.moneyverse.user.services.KeycloakService;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServerTest {

  private UserServiceGrpc.UserServiceBlockingStub stub;
  private ManagedChannel channel;
  private UserServer userServer;
  @Mock KeycloakService keycloakService;
  @Mock UserPreferenceRepository userPreferenceRepository;

  @BeforeEach
  void setup() throws IOException {
    String serverName = InProcessServerBuilder.generateName();
    Server server =
        InProcessServerBuilder.forName(serverName)
            .addService(new UserGrpcService(keycloakService, userPreferenceRepository))
            .directExecutor()
            .build()
            .start();
    channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
    stub = UserServiceGrpc.newBlockingStub(channel);
    userServer =
        new UserServer(
            RandomUtils.randomBigDecimal().intValue(), keycloakService, userPreferenceRepository);
    userServer.start();
  }

  @AfterEach
  void teardown() throws InterruptedException {
    if (channel != null) {
      channel.shutdown();
    }
    userServer.stop();
  }

  @Test
  void checkIfUserExists_ShouldReturnTrueForExistingUser(@Mock UserDto userDto) {
    UUID userId = RandomUtils.randomUUID();
    UserRequest request = UserRequest.newBuilder().setUserId(userId.toString()).build();
    when(keycloakService.getUserById(userId)).thenReturn(Optional.of(userDto));
    when(userDto.getUserId()).thenReturn(userId);
    when(userDto.getFirstName()).thenReturn(RandomUtils.randomString(10));
    when(userDto.getLastName()).thenReturn(RandomUtils.randomString(10));
    when(userDto.getEmail()).thenReturn(RandomUtils.randomString(15));

    UserResponse response = stub.getUserById(request);

    assertEquals(userId.toString(), response.getUserId());
    verify(keycloakService, times(1)).getUserById(userId);
  }

  @Test
  void checkIfUserExists_ShouldReturnFalseForNonExistingUser() {
    UUID userId = RandomUtils.randomUUID();
    UserRequest request = UserRequest.newBuilder().setUserId(userId.toString()).build();
    when(keycloakService.getUserById(userId)).thenReturn(Optional.empty());

    UserResponse response = stub.getUserById(request);

    assertEquals("", response.getUserId());
    verify(keycloakService, times(1)).getUserById(userId);
  }
}
