package it.moneyverse.account;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import it.moneyverse.account.model.entities.QAccount;
import it.moneyverse.core.enums.SortAttribute;

public enum AccountSortAttributeEnum implements SortAttribute {
  ACCOUNT_NAME(QAccount.account.accountName, true),
  ACCOUNT_CATEGORY(QAccount.account.accountCategory, false),
  BALANCE(QAccount.account.balance, false),
  BALANCE_TARGET(QAccount.account.balanceTarget, false),
  USERNAME(QAccount.account.username, false),
  ;

  private final ComparableExpressionBase<?> field;
  private final Boolean isDefault;

  AccountSortAttributeEnum(ComparableExpressionBase<?> field, Boolean isDefault) {
    this.field = field;
    this.isDefault = isDefault;
  }

  @Override
  public ComparableExpressionBase<?> getField() {
    return field;
  }

  @Override
  public Boolean isDefault() {
    return isDefault;
  }
}
