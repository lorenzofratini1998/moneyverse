package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = LanguageDto.Builder.class)
public class LanguageDto implements Serializable {

  private final UUID languageId;
  private final String isoCode;
  private final String locale;
  private final String country;
  private final String icon;
  private final Boolean isDefault;
  private final Boolean isEnabled;

  public LanguageDto(Builder builder) {
    this.languageId = builder.languageId;
    this.isoCode = builder.isoCode;
    this.locale = builder.locale;
    this.country = builder.country;
    this.icon = builder.icon;
    this.isDefault = builder.isDefault;
    this.isEnabled = builder.isEnabled;
  }

  public UUID getLanguageId() {
    return languageId;
  }

  public String getIsoCode() {
    return isoCode;
  }

  public String getLocale() {
    return locale;
  }

  public String getCountry() {
    return country;
  }

  public String getIcon() {
    return icon;
  }

  public Boolean isDefault() {
    return isDefault;
  }

  public Boolean isEnabled() {
    return isEnabled;
  }

  public static class Builder {
    private UUID languageId;
    private String isoCode;
    private String locale;
    private String country;
    private String icon;
    private Boolean isDefault;
    private Boolean isEnabled;

    public Builder withLanguageId(UUID languageId) {
      this.languageId = languageId;
      return this;
    }

    public Builder withIsoCode(String isoCode) {
      this.isoCode = isoCode;
      return this;
    }

    public Builder withLocale(String locale) {
      this.locale = locale;
      return this;
    }

    public Builder withCountry(String country) {
      this.country = country;
      return this;
    }

    public Builder withIcon(String icon) {
      this.icon = icon;
      return this;
    }

    public Builder withDefault(Boolean isDefault) {
      this.isDefault = isDefault;
      return this;
    }

    public Builder withEnabled(Boolean isEnabled) {
      this.isEnabled = isEnabled;
      return this;
    }

    public LanguageDto build() {
      return new LanguageDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
