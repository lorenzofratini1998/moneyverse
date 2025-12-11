package it.moneyverse.account.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.moneyverse.account.model.entities.Account_;
import org.junit.jupiter.api.Test;

class AccountSortAttributeEnumTest {

  @Test
  void fieldTest() {
    assertEquals(Account_.ACCOUNT_NAME, AccountSortAttributeEnum.ACCOUNT_NAME.getField());
    assertEquals(Account_.ACCOUNT_CATEGORY, AccountSortAttributeEnum.ACCOUNT_CATEGORY.getField());
    assertEquals(Account_.BALANCE, AccountSortAttributeEnum.BALANCE.getField());
    assertEquals(Account_.BALANCE_TARGET, AccountSortAttributeEnum.BALANCE_TARGET.getField());
  }

  @Test
  void isDefaultTest() {
    assertTrue(AccountSortAttributeEnum.ACCOUNT_NAME.isDefault());
    assertFalse(AccountSortAttributeEnum.ACCOUNT_CATEGORY.isDefault());
    assertFalse(AccountSortAttributeEnum.BALANCE.isDefault());
    assertFalse(AccountSortAttributeEnum.BALANCE_TARGET.isDefault());
  }
}
