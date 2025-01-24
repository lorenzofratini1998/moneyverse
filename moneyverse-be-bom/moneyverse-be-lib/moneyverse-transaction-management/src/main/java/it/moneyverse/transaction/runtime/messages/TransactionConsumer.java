package it.moneyverse.transaction.runtime.messages;

import it.moneyverse.core.model.beans.UserDeletionTopic;
import it.moneyverse.core.model.events.UserDeletionEvent;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.transaction.services.TransactionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static it.moneyverse.core.utils.ConsumerUtils.logMessage;

@Component
public class TransactionConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionConsumer.class);
    private final TransactionService transactionService;

    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RetryableTopic
    @KafkaListener(
            topics = UserDeletionTopic.TOPIC,
            autoStartup = "true",
            groupId =
                    "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
    public void onUserDeletionEvent(
            ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        logMessage(record, topic);
        UserDeletionEvent event = JsonUtils.fromJson(record.value(), UserDeletionEvent.class);
        transactionService.deleteAllTransactions(event.username());
    }
}
