package it.moneyverse.core.model.entities;

import java.time.LocalDateTime;

public interface AuditableModel {

  String getCreatedBy();
  LocalDateTime getCreatedAt();
  String getUpdatedBy();
  LocalDateTime getUpdatedAt();

}
