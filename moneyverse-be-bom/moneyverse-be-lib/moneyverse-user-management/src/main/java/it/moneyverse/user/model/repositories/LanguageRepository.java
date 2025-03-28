package it.moneyverse.user.model.repositories;

import it.moneyverse.user.model.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LanguageRepository extends JpaRepository<Language, UUID> {}
