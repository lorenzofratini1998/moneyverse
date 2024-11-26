package it.moneyverse.model.entities;

import it.moneyverse.enums.AccountCategoryEnum;
import java.math.BigDecimal;
import java.util.UUID;

public interface AccountModel extends AuditableModel {

  UUID getAccountId();
  UUID getUserId();
  String getAccountName();
  BigDecimal getBalance();
  BigDecimal getBalanceTarget();
  AccountCategoryEnum getAccountCategory();
  String getAccountDescription();
  Boolean isDefault();

}
