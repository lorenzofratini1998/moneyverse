package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.entities.Account;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, AccountCustomRepository {

  @Query("SELECT a FROM Account a WHERE a.username = :username AND a.isDefault = TRUE")
  List<Account> findDefaultAccountsByUser(String username);

  Boolean existsByUsernameAndAccountName(String username, String accountName);

  boolean existsByUsernameAndAccountId(String username, UUID accountId);

  List<Account> findAccountByUsername(String username);
}
