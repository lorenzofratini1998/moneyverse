package it.moneyverse.budget.model.converter;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.core.enums.SortAttribute;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortAttributeConverter implements Converter<String, SortAttribute> {

  @Override
  public SortAttribute convert(String source) {
    try {
      return BudgetSortAttributeEnum.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid sort attribute: " + source, e);
    }
  }
}
