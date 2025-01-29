package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PreferenceDto.Builder.class)
public class PreferenceDto implements Serializable {

  private final UUID userId;
  private final List<PreferenceItemDto> preferences;

  public PreferenceDto(Builder builder) {
    this.userId = builder.userId;
    this.preferences = builder.preferences;
  }

  public static class Builder {
    private UUID userId;
    private List<PreferenceItemDto> preferences;

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withPreferences(List<PreferenceItemDto> preferences) {
      this.preferences = preferences;
      return this;
    }

    public PreferenceDto build() {
      return new PreferenceDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public UUID getUserId() {
    return userId;
  }

  public List<PreferenceItemDto> getPreferences() {
    return preferences;
  }
}
