package it.moneyverse.transaction.model.converter;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortAttributeConverter implements Converter<String, SortAttribute> {

  @Override
  public SortAttribute convert(String source) {
    try {
      return TransactionSortAttributeEnum.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid sort attribute: " + source, e);
    }
  }
}
