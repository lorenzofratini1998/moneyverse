package it.moneyverse.user.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import it.moneyverse.user.enums.PreferenceKeyEnum;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(
    name = "PREFERENCES",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_ID", "PREFERENCE_KEY"})})
public class Preference extends Auditable implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "PREFERENCE_ID")
  private UUID preferenceId;

  @Column(name = "USER_ID", nullable = false)
  private UUID userId;

  @Column(name = "PREFERENCE_KEY", nullable = false)
  @Enumerated(EnumType.STRING)
  private PreferenceKeyEnum key;

  @Column(name = "PREFERENCE_VALUE", nullable = false)
  private String value;

  @Column(name = "UPDATABLE")
  @ColumnDefault("TRUE")
  private Boolean updatable;

  public UUID getPreferenceId() {
    return preferenceId;
  }

  public void setPreferenceId(UUID preferenceId) {
    this.preferenceId = preferenceId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public PreferenceKeyEnum getKey() {
    return key;
  }

  public void setKey(PreferenceKeyEnum key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Boolean isUpdatable() {
    return updatable;
  }

  public void setUpdatable(Boolean updatable) {
    this.updatable = updatable;
  }
}
