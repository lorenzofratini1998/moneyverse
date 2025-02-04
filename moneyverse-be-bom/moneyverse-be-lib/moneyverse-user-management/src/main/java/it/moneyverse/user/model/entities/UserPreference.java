package it.moneyverse.user.model.entities;

import it.moneyverse.core.model.entities.Auditable;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(
    name = "USER_PREFERENCES",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"USER_ID", "PREFERENCE_ID"})})
public class UserPreference extends Auditable implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "USER_PREFERENCE_ID")
  private UUID userPreferenceId;

  @Column(name = "USER_ID", nullable = false)
  private UUID userId;

  @ManyToOne
  @JoinColumn(name = "PREFERENCE_ID", nullable = false)
  private Preference preference;

  @Column(name = "PREFERENCE_VALUE", nullable = false)
  private String value;

  public UUID getUserPreferenceId() {
    return userPreferenceId;
  }

  public void setUserPreferenceId(UUID userPreferenceId) {
    this.userPreferenceId = userPreferenceId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public Preference getPreference() {
    return preference;
  }

  public void setPreference(Preference preference) {
    this.preference = preference;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
