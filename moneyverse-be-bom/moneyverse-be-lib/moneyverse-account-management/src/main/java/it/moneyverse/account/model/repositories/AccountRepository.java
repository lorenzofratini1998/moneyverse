package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.entities.Account;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, AccountCustomRepository {

  @Query("SELECT a FROM Account a WHERE a.username = :username AND a.isDefault = TRUE")
  Optional<Account> findDefaultAccountByUser(String username);

  Boolean existsByUsernameAndAccountName(String username, String accountName);

  Optional<Object> findByAccountId(UUID accountId);

  boolean existsByUsernameAndAccountId(String username, UUID accountId);
}
