package it.moneyverse.core.model.entities;

import it.moneyverse.core.enums.AccountCategoryEnum;
import java.math.BigDecimal;
import java.util.UUID;

public interface AccountModel extends AuditableModel {

  UUID getAccountId();
  String getUsername();
  String getAccountName();
  BigDecimal getBalance();
  BigDecimal getBalanceTarget();
  AccountCategoryEnum getAccountCategory();
  String getAccountDescription();
  Boolean isDefault();

}
