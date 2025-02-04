package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import java.util.List;
import java.util.UUID;

public interface AccountCustomRepository {
  List<Account> findAccounts(UUID userId, AccountCriteria param);
}
