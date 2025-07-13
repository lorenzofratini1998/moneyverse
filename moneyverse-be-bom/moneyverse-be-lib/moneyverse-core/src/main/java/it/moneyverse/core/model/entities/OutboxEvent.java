package it.moneyverse.core.model.entities;

import it.moneyverse.core.enums.AggregateTypeEnum;
import it.moneyverse.core.enums.EventTypeEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "OUTBOX_EVENTS")
public class OutboxEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "EVENT_ID")
  private UUID eventId;

  @Enumerated(EnumType.STRING)
  private AggregateTypeEnum aggregateType;

  @Column(name = "TOPIC", nullable = false)
  private String topic;

  @Column(name = "AGGREGATE_ID", nullable = false)
  private UUID aggregateId;

  @Enumerated(EnumType.STRING)
  private EventTypeEnum eventType;

  @Column(name = "PAYLOAD", nullable = false, columnDefinition = "json")
  @JdbcTypeCode(SqlTypes.JSON)
  private String payload;

  @Column(name = "CREATED_AT", nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "PROCESSED")
  private Boolean processed = false;

  public UUID getEventId() {
    return eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
  }

  public UUID getAggregateId() {
    return aggregateId;
  }

  public void setAggregateId(UUID aggregateId) {
    this.aggregateId = aggregateId;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public AggregateTypeEnum getAggregateType() {
    return aggregateType;
  }

  public void setAggregateType(AggregateTypeEnum aggregateType) {
    this.aggregateType = aggregateType;
  }

  public EventTypeEnum getEventType() {
    return eventType;
  }

  public void setEventType(EventTypeEnum eventType) {
    this.eventType = eventType;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Boolean getProcessed() {
    return processed;
  }

  public void setProcessed(Boolean processed) {
    this.processed = processed;
  }
}
