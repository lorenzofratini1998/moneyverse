package it.moneyverse.core.model.events;

import java.util.UUID;

public class CategoryDeletionEvent implements MessageEvent<UUID, String> {
  private UUID categoryId;

  public CategoryDeletionEvent() {}

  public CategoryDeletionEvent(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  @Override
  public UUID key() {
    return categoryId;
  }

  @Override
  public String value() {
    return categoryId.toString();
  }
}
