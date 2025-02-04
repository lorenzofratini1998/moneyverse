package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PreferenceDto.Builder.class)
public class PreferenceDto implements Serializable {

  private final UUID preferenceId;
  private final String name;
  private final Boolean mandatory;
  private final Boolean updatable;
  private final String defaultValue;

  public PreferenceDto(Builder builder) {
    this.preferenceId = builder.preferenceId;
    this.name = builder.name;
    this.mandatory = builder.mandatory;
    this.updatable = builder.updatable;
    this.defaultValue = builder.defaultValue;
  }

  public static class Builder {
    private UUID preferenceId;
    private String name;
    private Boolean mandatory;
    private Boolean updatable;
    private String defaultValue;

    public Builder withPreferenceId(UUID preferenceId) {
      this.preferenceId = preferenceId;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withMandatory(Boolean mandatory) {
      this.mandatory = mandatory;
      return this;
    }

    public Builder withUpdatable(Boolean updatable) {
      this.updatable = updatable;
      return this;
    }

    public Builder withDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public PreferenceDto build() {
      return new PreferenceDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public UUID getPreferenceId() {
    return preferenceId;
  }

  public String getName() {
    return name;
  }

  public Boolean getMandatory() {
    return mandatory;
  }

  public Boolean getUpdatable() {
    return updatable;
  }

  public String getDefaultValue() {
    return defaultValue;
  }
}
