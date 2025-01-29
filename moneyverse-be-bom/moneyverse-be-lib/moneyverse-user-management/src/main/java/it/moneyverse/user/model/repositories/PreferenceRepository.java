package it.moneyverse.user.model.repositories;

import it.moneyverse.user.model.entities.Preference;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceRepository extends JpaRepository<Preference, UUID> {}
