package it.moneyverse.core.utils.properties;

import jakarta.annotation.PostConstruct;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = KafkaProperties.PREFIX)
public class KafkaProperties {
  public static final String PREFIX = "spring.kafka";
  public static final String BOOTSTRAP_SERVERS = PREFIX + ".bootstrap-servers";
  public static final String GROUP_ID = PREFIX + ".group-id";

  private final String bootstrapServers;
  private final String groupId;
  private final Integer retry;
  private final Integer retryBackoffMs;
  private final String acks;

  @ConstructorBinding
  public KafkaProperties(
      String bootstrapServers,
      String topic,
      String groupId,
      Integer retry,
      Integer retryBackoffMs,
      String acks) {
    this.bootstrapServers = bootstrapServers;
    this.groupId = groupId;
    this.retry = retry != null ? retry : 5;
    this.retryBackoffMs = retryBackoffMs != null ? retryBackoffMs : 1000;
    this.acks = acks != null ? acks : "all";
  }

  @PostConstruct
  public void init() {
    Objects.requireNonNull(bootstrapServers, BOOTSTRAP_SERVERS + " cannot be null");
    Objects.requireNonNull(groupId, GROUP_ID + " cannot be null");
  }

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public String getGroupId() {
    return groupId;
  }

  public Integer getRetry() {
    return retry;
  }

  public Integer getRetryBackoffMs() {
    return retryBackoffMs;
  }

  public String getAcks() {
    return acks;
  }
}
