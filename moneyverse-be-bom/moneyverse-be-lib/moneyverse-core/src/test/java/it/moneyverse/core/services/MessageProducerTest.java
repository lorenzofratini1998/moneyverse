package it.moneyverse.core.services;

import it.moneyverse.core.model.events.MessageEvent;
import it.moneyverse.core.utils.JsonUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@EmbeddedKafka(
        partitions = 1,
        topics = {"test-topic"})
class MessageProducerTest {

    private static final String TOPIC = "test-topic";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<UUID, String> container;
    private BlockingQueue<ConsumerRecord<UUID, String>> consumerRecords;

    @BeforeEach
    void setup() {
        consumerRecords = new LinkedBlockingQueue<>();

        Map<String, Object> consumerProps =
                new HashMap<>(KafkaTestUtils.consumerProps("test-group-id", "false", embeddedKafkaBroker));
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        DefaultKafkaConsumerFactory<UUID, String> consumer =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps, new UUIDDeserializer(), new StringDeserializer());

        ContainerProperties containerProperties = new ContainerProperties(TOPIC);
        container = new KafkaMessageListenerContainer<>(consumer, containerProperties);
        container.setupMessageListener(
                (MessageListener<UUID, String>) record -> consumerRecords.add(record));
        container.start();

        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    void after() {
        container.stop();
    }

    @Test
    void testSend_Success() throws ExecutionException, InterruptedException {
        MessageProducer<UUID, String> messageProducer =
                createProducer(
                        new KafkaTemplate<>(
                                new DefaultKafkaProducerFactory<>(
                                        new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker)),
                                        new UUIDSerializer(),
                                        new StringSerializer())));

        UUID key = UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        MessageEvent<UUID, String> event = new TestMessageEvent(key, value);

        messageProducer.send(event, TOPIC).get();

        ConsumerRecord<UUID, String> received = consumerRecords.poll(1, TimeUnit.SECONDS);
        assertNotNull(received);
        assertEquals(key, received.key());
        assertEquals(event.value(), received.value());
    }

    @Test
    void testSend_Failure(@Mock KafkaTemplate<UUID, String> kafkaTemplate) {
        MessageProducer<UUID, String> messageProducer = createProducer(kafkaTemplate);
        UUID key = UUID.randomUUID();
        String value = UUID.randomUUID().toString();
        MessageEvent<UUID, String> event = new TestMessageEvent(key, value);
        ProducerRecord<UUID, String> record = new ProducerRecord<>(TOPIC, key, event.value());

        CompletableFuture<SendResult<UUID, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new KafkaException("Simulated failure"));
        when(kafkaTemplate.send(record)).thenReturn(future);

        assertThrows(ExecutionException.class, () -> messageProducer.send(event, TOPIC).get());
        assertEquals(0, consumerRecords.size());
    }

    private MessageProducer<UUID, String> createProducer(KafkaTemplate<UUID, String> kafkaTemplate) {
        return new MessageProducer<>(kafkaTemplate);
    }

    private record TestMessageEvent(UUID key, String value) implements MessageEvent<UUID, String> {

        @Override
            public String value() {
                Map<String, Object> payload = new HashMap<>();
                for (Field field : this.getClass().getDeclaredFields()) {
                    ReflectionUtils.makeAccessible(field);
                    payload.put(field.getName(), ReflectionUtils.getField(field, this));
                }
                return JsonUtils.toJson(payload);
            }

        }
}
