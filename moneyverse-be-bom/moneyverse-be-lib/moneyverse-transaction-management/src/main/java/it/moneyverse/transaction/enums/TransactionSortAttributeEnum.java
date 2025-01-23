package it.moneyverse.transaction.enums;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.transaction.model.entities.Transaction_;

public enum TransactionSortAttributeEnum implements SortAttribute {
  USERNAME {
    @Override
    public String getField() {
      return Transaction_.USERNAME;
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
