package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.user.enums.PreferenceKeyEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PreferenceItemDto.Builder.class)
public class PreferenceItemDto {

  private final PreferenceKeyEnum key;
  private final String value;
  private final Boolean updatable;

  public PreferenceItemDto(Builder builder) {
    this.key = builder.key;
    this.value = builder.value;
    this.updatable = builder.updatable;
  }

  public static class Builder {
    private PreferenceKeyEnum key;
    private String value;
    private Boolean updatable;

    public Builder withKey(PreferenceKeyEnum key) {
      this.key = key;
      return this;
    }

    public Builder withValue(String value) {
      this.value = value;
      return this;
    }

    public Builder withUpdatable(Boolean updatable) {
      this.updatable = updatable;
      return this;
    }

    public PreferenceItemDto build() {
      return new PreferenceItemDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public PreferenceKeyEnum getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public Boolean getUpdatable() {
    return updatable;
  }
}
