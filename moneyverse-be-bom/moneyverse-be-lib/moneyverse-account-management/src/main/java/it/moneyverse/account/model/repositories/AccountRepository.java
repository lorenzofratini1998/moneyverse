package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.entities.Account;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.isDefault = TRUE")
  Optional<Account> findDefaultAccount(UUID userId);

}
