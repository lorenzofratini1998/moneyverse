package it.moneyverse.transaction.enums;

import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.transaction.model.entities.Transaction_;
import org.junit.jupiter.api.Test;

class TransactionSortAttributeEnumTest {

  @Test
  void fieldTest() {
    assertEquals(Transaction_.DATE, TransactionSortAttributeEnum.DATE.getField());
    assertEquals(Transaction_.AMOUNT, TransactionSortAttributeEnum.AMOUNT.getField());
  }

  @Test
  void isDefaultTest() {
    assertTrue(TransactionSortAttributeEnum.DATE.isDefault());
    assertFalse(TransactionSortAttributeEnum.AMOUNT.isDefault());
  }
}
