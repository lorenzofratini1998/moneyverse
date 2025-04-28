package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryDto.Builder.class)
public class CategoryDto implements Serializable {

  private final UUID categoryId;
  private final UUID userId;
  private final String categoryName;
  private final String description;
  private final UUID parentCategory;
  private final List<CategoryDto> children;
  private final StyleDto style;

  public CategoryDto(Builder builder) {
    this.categoryId = builder.categoryId;
    this.userId = builder.userId;
    this.categoryName = builder.categoryName;
    this.description = builder.description;
    this.parentCategory = builder.parentCategory;
    this.children = builder.children;
    this.style = builder.style;
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

  public UUID getParentCategory() {
    return parentCategory;
  }

  public List<CategoryDto> getChildren() {
    return children;
  }

  public StyleDto getStyle() {
    return style;
  }

  public static class Builder {
    private UUID categoryId;
    private UUID userId;
    private String categoryName;
    private String description;
    private UUID parentCategory;
    private List<CategoryDto> children;
    private StyleDto style;

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

    public Builder withParentCategory(UUID parentCategory) {
      this.parentCategory = parentCategory;
      return this;
    }

    public Builder withChildren(List<CategoryDto> children) {
      this.children = children;
      return this;
    }

    public Builder withStyle(StyleDto style) {
      this.style = style;
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
