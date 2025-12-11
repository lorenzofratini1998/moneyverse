package it.moneyverse.core.model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = CategoryEvent.Builder.class)
public class CategoryEvent extends AbstractEvent {
  private final UUID categoryId;
  private final UUID parentId;
  private final UUID userId;
  private final String categoryName;

  public CategoryEvent(Builder builder) {
    super(builder);
    this.categoryId = builder.categoryId;
    this.parentId = builder.parentId;
    this.userId = builder.userId;
    this.categoryName = builder.categoryName;
  }

  public static class Builder extends AbstractBuilder<CategoryEvent, Builder> {
    private UUID categoryId;
    private UUID parentId;
    private UUID userId;
    private String categoryName;

    public Builder withCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public Builder withParentId(UUID parentId) {
      this.parentId = parentId;
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

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public CategoryEvent build() {
      return new CategoryEvent(this);
    }
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public UUID getParentId() {
    return parentId;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public UUID key() {
    return categoryId;
  }
}
