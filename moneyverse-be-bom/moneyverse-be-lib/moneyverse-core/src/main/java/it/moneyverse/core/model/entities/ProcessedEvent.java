package it.moneyverse.core.model.entities;

import it.moneyverse.core.enums.EventTypeEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PROCESSED_EVENTS")
public class ProcessedEvent {

  @Id
  @Column(name = "EVENT_ID")
  private UUID eventId;

  @Column(name = "TOPIC", nullable = false)
  private String topic;

  @Column(name = "PAYLOAD", nullable = false, columnDefinition = "json")
  @JdbcTypeCode(SqlTypes.JSON)
  private String payload;

  @Column(name = "EVENT_TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  private EventTypeEnum eventType;

  @Column(name = "PROCESSED_AT", nullable = false, updatable = false)
  private LocalDateTime processedAt = LocalDateTime.now();

  public UUID getEventId() {
    return eventId;
  }

  public void setEventId(UUID eventId) {
    this.eventId = eventId;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public EventTypeEnum getEventType() {
    return eventType;
  }

  public void setEventType(EventTypeEnum eventType) {
    this.eventType = eventType;
  }

  public LocalDateTime getProcessedAt() {
    return processedAt;
  }

  public void setProcessedAt(LocalDateTime processedAt) {
    this.processedAt = processedAt;
  }
}
