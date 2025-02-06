package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryDto.Builder.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "categoryId")
public class CategoryDto implements Serializable {

  private final UUID categoryId;
  private final UUID userId;
  private final String categoryName;
  private final String description;

  public CategoryDto(Builder builder) {
    this.categoryId = builder.categoryId;
    this.userId = builder.userId;
    this.categoryName = builder.categoryName;
    this.description = builder.description;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getDescription() {
    return description;
  }

  public static class Builder {
    private UUID categoryId;
    private UUID userId;
    private String categoryName;
    private String description;

    public Builder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withCategoryName(String categoryName) {
      this.categoryName = categoryName;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public CategoryDto build() {
      return new CategoryDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
