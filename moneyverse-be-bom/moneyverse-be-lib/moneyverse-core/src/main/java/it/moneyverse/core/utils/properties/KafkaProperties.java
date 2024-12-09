package it.moneyverse.core.utils.properties;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = KafkaProperties.PREFIX)
public class KafkaProperties {
  public static final String PREFIX = "spring.kafka";

  private final KafkaAdminProperties admin = new KafkaAdminProperties();
  private final KafkaProducerProperties producer = new KafkaProducerProperties();
  private final KafkaConsumerProperties consumer = new KafkaConsumerProperties();

  public KafkaAdminProperties getAdmin() {
    return admin;
  }

  public KafkaProducerProperties getProducer() {
    return producer;
  }

  public KafkaConsumerProperties getConsumer() {
    return consumer;
  }

  public static class KafkaAdminProperties {

    private static final String ADMIN_PREFIX = PREFIX + ".admin";
    public static final String BOOTSTRAP_SERVERS = ADMIN_PREFIX + ".bootstrap-servers";

    private String bootstrapServers;

    @PostConstruct
    public void init() {
      Objects.requireNonNull(bootstrapServers, BOOTSTRAP_SERVERS + " cannot be null");
    }

    public String getBootstrapServers() {
      return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
      this.bootstrapServers = bootstrapServers;
    }
  }

  public static class KafkaProducerProperties {

    private Integer retry = 5;
    private Integer retryBackoffMs = 1000;
    private String acks = "all";

    public Integer getRetry() {
      return retry;
    }

    public void setRetry(Integer retry) {
      this.retry = retry;
    }

    public Integer getRetryBackoffMs() {
      return retryBackoffMs;
    }

    public void setRetryBackoffMs(Integer retryBackoffMs) {
      this.retryBackoffMs = retryBackoffMs;
    }

    public String getAcks() {
      return acks;
    }

    public void setAcks(String acks) {
      this.acks = acks;
    }
  }

  public static class KafkaConsumerProperties {

    private static final String CONSUMER_PREFIX = PREFIX + ".consumer";
    public static final String GROUP_ID = CONSUMER_PREFIX + ".group-id";

    private String groupId;
    private Boolean autoStartup = true;
    private final KafkaConsumerRetryProperties retry = new KafkaConsumerRetryProperties();

    @PostConstruct
    public void init() {
      Objects.requireNonNull(groupId, GROUP_ID + " cannot be null");
    }

    public String getGroupId() {
      return groupId;
    }

    public void setGroupId(String groupId) {
      this.groupId = groupId;
    }

    public Boolean getAutoStartup() {
      return autoStartup;
    }

    public void setAutoStartup(Boolean autoStartup) {
      this.autoStartup = autoStartup;
    }

    public KafkaConsumerRetryProperties getRetry() {
      return retry;
    }

    public static class KafkaConsumerRetryProperties {

      private Integer attempts = 3;
      private Long delay = 1500L;
      private Double multiplier = 1.5;
      private Long maxRetryBackoffMs = 15000L;

      public Integer getAttempts() {
        return attempts;
      }

      public void setAttempts(Integer attempts) {
        this.attempts = attempts;
      }

      public Long getDelay() {
        return delay;
      }

      public void setDelay(Long delay) {
        this.delay = delay;
      }

      public Double getMultiplier() {
        return multiplier;
      }

      public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
      }

      public Long getMaxRetryBackoffMs() {
        return maxRetryBackoffMs;
      }

      public void setMaxRetryBackoffMs(Long maxRetryBackoffMs) {
        this.maxRetryBackoffMs = maxRetryBackoffMs;
      }
    }
  }
}
