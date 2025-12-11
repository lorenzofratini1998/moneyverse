package it.moneyverse.account.model.converter;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.core.enums.SortAttribute;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortAttributeConverter implements Converter<String, SortAttribute> {

  @Override
  public SortAttribute convert(String source) {
    try {
      return AccountSortAttributeEnum.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid sort attribute: " + source, e);
    }
  }
}
