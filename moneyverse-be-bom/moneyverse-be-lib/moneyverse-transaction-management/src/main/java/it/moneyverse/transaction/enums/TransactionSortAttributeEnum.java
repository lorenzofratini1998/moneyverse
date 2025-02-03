package it.moneyverse.transaction.enums;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.transaction.model.entities.Transaction_;

public enum TransactionSortAttributeEnum implements SortAttribute {
  USER_ID {
    @Override
    public String getField() {
      return Transaction_.USER_ID;
    }

    @Override
    public Boolean isDefault() {
      return false;
    }
  },
  DATE {
    @Override
    public String getField() {
      return Transaction_.DATE;
    }

    @Override
    public Boolean isDefault() {
      return true;
    }
  },
  AMOUNT {
    @Override
    public String getField() {
      return Transaction_.AMOUNT;
    }

    @Override
    public Boolean isDefault() {
      return false;
    }
  }
}
