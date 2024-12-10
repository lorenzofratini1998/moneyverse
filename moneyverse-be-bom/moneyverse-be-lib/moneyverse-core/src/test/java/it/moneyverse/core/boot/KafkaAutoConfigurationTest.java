package it.moneyverse.core.boot;

import static org.assertj.core.api.Assertions.assertThat;

import it.moneyverse.core.utils.properties.KafkaProperties;
import it.moneyverse.core.utils.properties.KafkaProperties.KafkaAdminProperties;
import it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

public class KafkaAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withUserConfiguration(KafkaAutoConfiguration.class)
          .withPropertyValues(
              "%s=%s".formatted(KafkaAdminProperties.BOOTSTRAP_SERVERS, "localhost:9092"),
              "%s=%s".formatted(KafkaConsumerProperties.GROUP_ID, "group-id"));

  @Test
  void testKafkaBeansCreation() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(KafkaProperties.class);
          assertThat(context).hasSingleBean(KafkaAdmin.class);
          assertThat(context).hasSingleBean(ProducerFactory.class);
          assertThat(context).hasSingleBean(KafkaTemplate.class);
          assertThat(context).hasSingleBean(ConsumerFactory.class);
          assertThat(context).hasSingleBean(ConcurrentKafkaListenerContainerFactory.class);
        });
  }
}
