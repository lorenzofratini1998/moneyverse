package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.model.dto.PreferenceDto;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = UserPreferenceItemDto.Builder.class)
public class UserPreferenceItemDto implements Serializable {

  private final UUID userPreferenceId;
  private final PreferenceDto preference;
  private final String value;

  public UserPreferenceItemDto(Builder builder) {
    this.userPreferenceId = builder.userPreferenceId;
    this.preference = builder.preference;
    this.value = builder.value;
  }

  public static class Builder {
    private UUID userPreferenceId;
    private PreferenceDto preference;
    private String value;

    public Builder withUserPreferenceId(UUID userPreferenceId) {
      this.userPreferenceId = userPreferenceId;
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

    public UserPreferenceItemDto build() {
      return new UserPreferenceItemDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public UUID getUserPreferenceId() {
    return userPreferenceId;
  }

  public PreferenceDto getPreference() {
    return preference;
  }

  public String getValue() {
    return value;
  }
}
