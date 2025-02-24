package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = UserPreferenceDto.Builder.class)
public class UserPreferenceDto implements Serializable {

  private final UUID userId;
  private final PreferenceDto preference;
  private final String value;

  public UserPreferenceDto(Builder builder) {
    this.userId = builder.userId;
    this.preference = builder.preference;
    this.value = builder.value;
  }

  public static class Builder {
    private UUID userId;
    private PreferenceDto preference;
    private String value;

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withPreference(PreferenceDto preference) {
      this.preference = preference;
      return this;
    }

    public Builder withValue(String value) {
      this.value = value;
      return this;
    }

    public UserPreferenceDto build() {
      return new UserPreferenceDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public UUID getUserId() {
    return userId;
  }

  public PreferenceDto getPreference() {
    return preference;
  }

  public String getValue() {
    return value;
  }
}
