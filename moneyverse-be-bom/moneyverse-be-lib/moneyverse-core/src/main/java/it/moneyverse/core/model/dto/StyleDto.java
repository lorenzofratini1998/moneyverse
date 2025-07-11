package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = StyleDto.Builder.class)
public class StyleDto {

  private final String color;
  private final String icon;

  public StyleDto(Builder builder) {
    this.color = builder.color;
    this.icon = builder.icon;
  }

  public static class Builder {
    private String color;
    private String icon;

    public Builder withColor(String color) {
      this.color = color;
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

  public String getColor() {
    return color;
  }

  public String getIcon() {
    return icon;
  }
}
