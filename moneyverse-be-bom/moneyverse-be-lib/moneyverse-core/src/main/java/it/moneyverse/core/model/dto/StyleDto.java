package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = StyleDto.Builder.class)
public class StyleDto {

  private final String backgroundColor;
  private final String textColor;
  private final String icon;

  public StyleDto(Builder builder) {
    this.backgroundColor = builder.backgroundColor;
    this.textColor = builder.textColor;
    this.icon = builder.icon;
  }

  public static class Builder {
    private String backgroundColor;
    private String textColor;
    private String icon;

    public Builder withBackgroundColor(String backgroundColor) {
      this.backgroundColor = backgroundColor;
      return this;
    }

    public Builder withTextColor(String textColor) {
      this.textColor = textColor;
      return this;
    }

    public Builder withIcon(String icon) {
      this.icon = icon;
      return this;
    }

    public StyleDto build() {
      return new StyleDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public String getTextColor() {
    return textColor;
  }

  public String getIcon() {
    return icon;
  }
}
