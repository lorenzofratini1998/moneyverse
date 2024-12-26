package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.entities.AccountCategory;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountCategoryRepository extends JpaRepository<AccountCategory, UUID> {
  Optional<AccountCategory> findByName(String name);
}
