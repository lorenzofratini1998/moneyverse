package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {}
