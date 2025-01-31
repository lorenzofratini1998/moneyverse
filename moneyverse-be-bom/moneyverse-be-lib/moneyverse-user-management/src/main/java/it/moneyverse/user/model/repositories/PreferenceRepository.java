package it.moneyverse.user.model.repositories;

import it.moneyverse.user.model.entities.Preference;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PreferenceRepository extends JpaRepository<Preference, UUID> {

  @Query("SELECT p FROM Preference p WHERE p.mandatory = :mandatory")
  List<Preference> findAllByMandatory(Boolean mandatory);
}
