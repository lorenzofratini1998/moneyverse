package it.moneyverse.core.enums;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import java.util.Arrays;

public interface SortAttribute {
  static <T extends Enum<T> & SortAttribute> T getDefault(Class<T> enumType) {
    return Arrays.stream(enumType.getEnumConstants())
        .filter(SortAttribute::isDefault)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No default sorting attribute found"));
  }

  ComparableExpressionBase<?> getField();

  Boolean isDefault();
}
