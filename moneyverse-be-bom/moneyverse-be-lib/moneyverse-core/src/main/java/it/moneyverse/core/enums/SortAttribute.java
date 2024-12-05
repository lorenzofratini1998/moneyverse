package it.moneyverse.core.enums;

import java.util.Arrays;

public interface SortAttribute {
  static <T extends Enum<T> & SortAttribute> T getDefault(Class<T> enumType) {
    return Arrays.stream(enumType.getEnumConstants())
        .filter(SortAttribute::isDefault)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No default sorting attribute found"));
  }

  String getField();

  Boolean isDefault();
}
