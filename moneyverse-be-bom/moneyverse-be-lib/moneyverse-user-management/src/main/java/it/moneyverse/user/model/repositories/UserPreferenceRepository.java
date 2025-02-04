package it.moneyverse.user.model.repositories;

import it.moneyverse.user.model.entities.UserPreference;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

  @Query(
      "SELECT up FROM UserPreference up JOIN Preference p ON up.userId = :userId AND p.mandatory = TRUE")
  List<UserPreference> findMandatoryPreferencesByUserId(UUID userId);

  List<UserPreference> findByUserId(UUID userId);
}
