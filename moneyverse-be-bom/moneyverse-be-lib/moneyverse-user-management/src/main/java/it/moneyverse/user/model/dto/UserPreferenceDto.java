package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = UserPreferenceDto.Builder.class)
public class UserPreferenceDto implements Serializable {

  private final UUID userId;
  private final List<UserPreferenceItemDto> preferences;

  public UserPreferenceDto(Builder builder) {
    this.userId = builder.userId;
    this.preferences = builder.preferences;
  }

  public static class Builder {
    private UUID userId;
    private List<UserPreferenceItemDto> preferences;

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withPreferences(List<UserPreferenceItemDto> preferences) {
      this.preferences = preferences;
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

  public List<UserPreferenceItemDto> getPreferences() {
    return preferences;
  }
}
