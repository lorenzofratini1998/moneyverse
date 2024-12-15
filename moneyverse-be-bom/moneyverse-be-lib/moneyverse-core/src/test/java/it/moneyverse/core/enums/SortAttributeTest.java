package it.moneyverse.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

enum ExampleSortAttribute implements SortAttribute {
  NAME("name", true),
  AGE("age", false);

  private final String field;
  private final Boolean isDefault;

  ExampleSortAttribute(String field, Boolean isDefault) {
    this.field = field;
    this.isDefault = isDefault;
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public Boolean isDefault() {
    return isDefault;
  }
}

class SortAttributeTest {

  @Test
  void testGetDefaultReturnsDefaultValue() {
    ExampleSortAttribute defaultAttribute = SortAttribute.getDefault(ExampleSortAttribute.class);
    assertEquals(ExampleSortAttribute.NAME, defaultAttribute);
  }

  @Test
  void testGetDefaultThrowsExceptionWhenNoDefaultExists() {
    enum NoDefaultSortAttribute implements SortAttribute {
      SIZE("size", false);

      private final String field;
      private final Boolean isDefault;

      NoDefaultSortAttribute(String field, Boolean isDefault) {
        this.field = field;
        this.isDefault = isDefault;
      }

      @Override
      public String getField() {
        return field;
      }

      @Override
      public Boolean isDefault() {
        return isDefault;
      }
    }

    assertThrows(
        IllegalArgumentException.class,
        () -> SortAttribute.getDefault(NoDefaultSortAttribute.class),
        "No default sorting attribute found");
  }

  @Test
  void testGetDefaultWithEmptyEnum() {
    enum EmptySortAttribute implements SortAttribute {
      ;

      @Override
      public String getField() {
        return null;
      }

      @Override
      public Boolean isDefault() {
        return false;
      }
    }

    assertThrows(
        IllegalArgumentException.class,
        () -> SortAttribute.getDefault(EmptySortAttribute.class),
        "No default sorting attribute found");
  }
}
