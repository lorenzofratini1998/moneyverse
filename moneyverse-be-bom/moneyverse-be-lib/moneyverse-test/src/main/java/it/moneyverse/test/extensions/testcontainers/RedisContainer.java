package it.moneyverse.test.extensions.testcontainers;

import org.testcontainers.containers.wait.strategy.Wait;

public class RedisContainer extends com.redis.testcontainers.RedisContainer {

  private static final String REDIS = "redis";
  private static final String VERSION = "7.4.2-alpine";

  public RedisContainer() {
    this(REDIS + ":" + VERSION);
  }

  public RedisContainer(String dockerImageName) {
    super(dockerImageName);
    super.withCommand("redis-server", "--requirepass", REDIS);
    super.waitingFor(Wait.forListeningPort());
  }

  public String getUsername() {
    return REDIS;
  }

  public String getPassword() {
    return REDIS;
  }
}
