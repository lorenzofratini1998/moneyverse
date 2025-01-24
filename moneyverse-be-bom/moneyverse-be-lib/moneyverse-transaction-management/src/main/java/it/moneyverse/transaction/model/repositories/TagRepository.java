package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Tag;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {}
