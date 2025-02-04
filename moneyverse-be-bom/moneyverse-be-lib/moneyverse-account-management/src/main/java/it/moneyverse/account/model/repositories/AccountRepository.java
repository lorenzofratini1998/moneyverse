package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.entities.Account;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, AccountCustomRepository {

  @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.isDefault = TRUE")
  List<Account> findDefaultAccountsByUserId(UUID userId);

  Boolean existsByUserIdAndAccountName(UUID userId, String accountName);

  boolean existsByUserIdAndAccountId(UUID userId, UUID accountId);

  List<Account> findAccountByUserId(UUID userId);

  boolean existsByAccountId(UUID accountId);
}
