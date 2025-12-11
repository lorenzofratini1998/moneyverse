package it.moneyverse.test.model.entities;

import it.moneyverse.core.model.entities.AuditableModel;
import java.time.LocalDateTime;

public class FakeAuditable implements AuditableModel {

  private static final String FAKE_USER = "FAKE_USER";
  private final String createdBy;
  private final LocalDateTime createdAt;
  private final String updatedBy;
  private final LocalDateTime updatedAt;

  public FakeAuditable() {
    this.createdBy = FAKE_USER;
    this.createdAt = LocalDateTime.now();
    this.updatedBy = FAKE_USER;
    this.updatedAt = LocalDateTime.now();
  }

  @Override
  public String getCreatedBy() {
    return createdBy;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public String getUpdatedBy() {
    return updatedBy;
  }

  @Override
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
