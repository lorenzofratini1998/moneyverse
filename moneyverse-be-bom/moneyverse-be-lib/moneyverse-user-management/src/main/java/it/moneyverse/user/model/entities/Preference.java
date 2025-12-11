package it.moneyverse.user.model.entities;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "PREFERENCES")
public class Preference implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "PREFERENCE_ID")
  private UUID preferenceId;

  @Column(name = "NAME", nullable = false, unique = true)
  private String name;

  @Column(name = "MANDATORY", nullable = false)
  @ColumnDefault("FALSE")
  private Boolean mandatory = false;

  @Column(name = "UPDATABLE", nullable = false)
  @ColumnDefault("TRUE")
  private Boolean updatable = true;

  @Column(name = "DEFAULT_VALUE")
  private String defaultValue;

  @OneToMany(mappedBy = "preference", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserPreference> userPreferences = new ArrayList<>();

  public UUID getPreferenceId() {
    return preferenceId;
  }

  public void setPreferenceId(UUID preferenceId) {
    this.preferenceId = preferenceId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getMandatory() {
    return mandatory;
  }

  public void setMandatory(Boolean mandatory) {
    this.mandatory = mandatory;
  }

  public Boolean getUpdatable() {
    return updatable;
  }

  public void setUpdatable(Boolean updatable) {
    this.updatable = updatable;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public List<UserPreference> getUserPreferences() {
    return userPreferences;
  }

  public void setUserPreferences(List<UserPreference> userPreferences) {
    this.userPreferences = userPreferences;
  }
}
