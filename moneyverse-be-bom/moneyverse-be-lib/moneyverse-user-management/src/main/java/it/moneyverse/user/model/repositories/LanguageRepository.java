package it.moneyverse.user.model.repositories;

import it.moneyverse.user.model.entities.Language;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, UUID> {}
