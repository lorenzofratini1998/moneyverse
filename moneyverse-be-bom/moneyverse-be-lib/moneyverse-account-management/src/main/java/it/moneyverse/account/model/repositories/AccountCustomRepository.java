package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import java.util.List;

public interface AccountCustomRepository {
  List<Account> findAccounts(AccountCriteria param);
}
