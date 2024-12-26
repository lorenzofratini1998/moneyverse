package it.moneyverse.core.boot;

import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.core.utils.properties.KafkaProperties;
import jakarta.json.stream.JsonParsingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@EnableKafka
public class KafkaAutoConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaAutoConfiguration.class);

  private final KafkaProperties kafkaProperties;

  public KafkaAutoConfiguration(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
    LOGGER.info("Starting to load beans from {}", KafkaAutoConfiguration.class);
  }

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(
        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaProperties.getAdmin().getBootstrapServers());
    return new KafkaAdmin(configs);
  }

  @Bean
  public ProducerFactory<UUID, String> producerFactory() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getAdmin().getBootstrapServers());
    configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
    configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configs.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducer().getRetry());
    configs.put(
        ProducerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaProperties.getProducer().getRetryBackoffMs());
    configs.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
    return new DefaultKafkaProducerFactory<>(configs);
  }

  @Bean
  public KafkaTemplate<UUID, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ConsumerFactory<UUID, String> consumerFactory() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getAdmin().getBootstrapServers());
    configs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
    configs.put(
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.LATEST.name().toLowerCase());
    configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
    configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(configs);
  }

  @Bean
  public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<UUID, String> kafkaTemplate) {
    var recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

    var exponentialBackOff =
        new ExponentialBackOffWithMaxRetries(
            kafkaProperties.getConsumer().getRetry().getAttempts());
    exponentialBackOff.setInitialInterval(kafkaProperties.getConsumer().getRetry().getDelay());
    exponentialBackOff.setMultiplier(kafkaProperties.getConsumer().getRetry().getMultiplier());
    exponentialBackOff.setMaxInterval(
        kafkaProperties.getConsumer().getRetry().getMaxRetryBackoffMs());

    var errorHandler = new DefaultErrorHandler(recoverer, exponentialBackOff);
    errorHandler.addNotRetryableExceptions(
        JsonParsingException.class, ResourceNotFoundException.class);

    return errorHandler;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<UUID, String> kafkaListenerContainerFactory(
      DefaultErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<UUID, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }

  @Bean
  public MessageProducer<UUID, String> messageProducer(KafkaTemplate<UUID, String> kafkaTemplate) {
    return new MessageProducer<>(kafkaTemplate);
  }
}
