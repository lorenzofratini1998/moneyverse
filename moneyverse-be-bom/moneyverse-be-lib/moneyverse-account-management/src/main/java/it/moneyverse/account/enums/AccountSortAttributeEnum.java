package it.moneyverse.account.enums;

import it.moneyverse.account.model.entities.Account_;
import it.moneyverse.core.enums.SortAttribute;

public enum AccountSortAttributeEnum implements SortAttribute {
  ACCOUNT_NAME {
    @Override
    public String getField() {
      return Account_.ACCOUNT_NAME;
    }

    @Override
    public Boolean isDefault() {
      return true;
    }
  },
  ACCOUNT_CATEGORY {
    @Override
    public String getField() {
      return Account_.ACCOUNT_CATEGORY;
    }

    @Override
    public Boolean isDefault() {
      return false;
    }
  },
  BALANCE {
    @Override
    public String getField() {
      return Account_.BALANCE;
    }

    @Override
    public Boolean isDefault() {
      return false;
    }
  },
  BALANCE_TARGET {
    @Override
    public String getField() {
      return Account_.BALANCE_TARGET;
    }

    @Override
    public Boolean isDefault() {
      return false;
    }
  },
  USERNAME {
    @Override
    public String getField() {
      return Account_.USERNAME;
    }

    @Override
    public Boolean isDefault() {
      return false;
    }
  }
}
