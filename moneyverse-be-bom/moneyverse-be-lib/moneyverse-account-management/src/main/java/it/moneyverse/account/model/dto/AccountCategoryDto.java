package it.moneyverse.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.model.dto.StyleDto;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AccountCategoryDto.Builder.class)
public class AccountCategoryDto implements Serializable {

  private final Long accountCategoryId;
  private final String name;
  private final String description;
  private final StyleDto style;

  public AccountCategoryDto(Builder builder) {
    this.accountCategoryId = builder.accountCategoryId;
    this.name = builder.name;
    this.description = builder.description;
    this.style = builder.style;
  }

  public static class Builder {
    private Long accountCategoryId;
    private String name;
    private String description;
    private StyleDto style;

    public Builder withAccountCategoryId(Long accountCategoryId) {
      this.accountCategoryId = accountCategoryId;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder withStyle(StyleDto style) {
      this.style = style;
      return this;
    }

    public AccountCategoryDto build() {
      return new AccountCategoryDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public Long getAccountCategoryId() {
    return accountCategoryId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public StyleDto getStyle() {
    return style;
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
