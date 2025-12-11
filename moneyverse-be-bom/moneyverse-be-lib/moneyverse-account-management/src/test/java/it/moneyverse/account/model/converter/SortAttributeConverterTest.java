package it.moneyverse.account.model.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.test.utils.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit Tests for {@link SortAttributeConverter} */
@ExtendWith(MockitoExtension.class)
class SortAttributeConverterTest {

  @InjectMocks SortAttributeConverter sortAttributeConverter;

  @Test
  void givenSource_ThenReturnSortAttribute() {
    AccountSortAttributeEnum source = RandomUtils.randomEnum(AccountSortAttributeEnum.class);
    AccountSortAttributeEnum result =
        (AccountSortAttributeEnum) sortAttributeConverter.convert(source.name());
    assertEquals(source, result);
  }

  @Test
  void givenSource_ThrowIllegalArgumentException() {
    String source = RandomUtils.randomString(10);
    assertThrows(IllegalArgumentException.class, () -> sortAttributeConverter.convert(source));
  }
}
