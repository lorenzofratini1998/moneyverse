package it.moneyverse.test.model;

import it.moneyverse.model.entities.AuditableModel;
import java.time.LocalDateTime;

public class FakeAuditable implements AuditableModel {

  private static final String FAKE_USER = "FAKE_USER";

  @Override
  public String getCreatedBy() {
    return FAKE_USER;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return LocalDateTime.now();
  }

  @Override
  public String getUpdatedBy() {
    return FAKE_USER;
  }

  @Override
  public LocalDateTime getUpdatedAt() {
    return LocalDateTime.now();
  }
}
