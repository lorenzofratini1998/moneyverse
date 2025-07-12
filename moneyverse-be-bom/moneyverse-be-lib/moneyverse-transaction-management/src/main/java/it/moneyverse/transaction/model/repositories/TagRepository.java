package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.entities.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {
  boolean existsByTagNameAndUserId(String tagName, UUID userId);

  boolean existsByTagNameAndUserIdAndTagIdNot(String tagName, UUID userId, UUID excludedTagId);

  boolean existsByTagIdAndUserId(UUID tagId, UUID userId);

  List<Tag> findByUserId(UUID userId);
}
