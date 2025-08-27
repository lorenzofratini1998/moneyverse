package it.moneyverse.budget.runtime.messages;

import it.moneyverse.budget.services.BudgetService;
import it.moneyverse.core.model.beans.TransactionCreationTopic;
import it.moneyverse.core.model.beans.TransactionDeletionTopic;
import it.moneyverse.core.model.beans.TransactionUpdateTopic;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.model.repositories.ProcessedEventRepository;
import it.moneyverse.core.runtime.messages.AbstractConsumer;
import it.moneyverse.core.utils.JsonUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BudgetConsumer extends AbstractConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetConsumer.class);
  private final BudgetService budgetService;

  public BudgetConsumer(
      BudgetService budgetService, ProcessedEventRepository processedEventRepository) {
    super(processedEventRepository);
    this.budgetService = budgetService;
  }

  @RetryableTopic
  @KafkaListener(
      topics = TransactionCreationTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionCreation(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (eventNotProcessed(record.key()) && event.getBudgetId() != null) {
      budgetService.incrementBudgetAmount(
          event.getBudgetId(), event.getAmount().abs(), event.getCurrency(), event.getDate());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  @RetryableTopic
  @KafkaListener(
      topics = TransactionDeletionTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionDeletion(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (eventNotProcessed(record.key()) && event.getBudgetId() != null) {
      budgetService.decrementBudgetAmount(
          event.getBudgetId(),
          event.getAmount().abs().negate(),
          event.getCurrency(),
          event.getDate());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  @Transactional
  @RetryableTopic
  @KafkaListener(
      topics = TransactionUpdateTopic.TOPIC,
      autoStartup = "true",
      groupId =
          "#{environment.getProperty(T(it.moneyverse.core.utils.properties.KafkaProperties.KafkaConsumerProperties).GROUP_ID)}")
  public void onTransactionUpdate(
      ConsumerRecord<UUID, String> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    logMessage(record, topic);
    TransactionEvent event = JsonUtils.fromJson(record.value(), TransactionEvent.class);
    if (eventNotProcessed(record.key()) && event.getBudgetId() != null) {
      LOGGER.info(
          "Undoing transaction {} on budget {}",
          event.getPreviousTransaction().getTransactionId(),
          event.getPreviousTransaction().getBudgetId());
      applyTransaction(
          event.getPreviousTransaction().getBudgetId(),
          event.getPreviousTransaction().getAmount().abs().negate(),
          event.getPreviousTransaction().getCurrency(),
          event.getPreviousTransaction().getDate());
      LOGGER.info(
          "Applying transaction {} on budget {}", event.getTransactionId(), event.getBudgetId());
      applyTransaction(
          event.getBudgetId(), event.getAmount().abs(), event.getCurrency(), event.getDate());
      persistProcessedEvent(record.key(), topic, event.getEventType(), record.value());
    }
  }

  private void applyTransaction(UUID budgetId, BigDecimal amount, String currency, LocalDate date) {
    if (amount.compareTo(BigDecimal.ZERO) > 0) {
      budgetService.incrementBudgetAmount(budgetId, amount, currency, date);
    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
      budgetService.decrementBudgetAmount(budgetId, amount.abs(), currency, date);
    }
  }
}
