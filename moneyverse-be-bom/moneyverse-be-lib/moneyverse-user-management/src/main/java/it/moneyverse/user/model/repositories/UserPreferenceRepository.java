package it.moneyverse.user.model.repositories;

import it.moneyverse.user.model.entities.UserPreference;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

  @Query(
      "SELECT up FROM UserPreference up WHERE up.userId = :userId AND up.preference.mandatory = TRUE")
  List<UserPreference> findMandatoryPreferencesByUserId(UUID userId);

  List<UserPreference> findByUserId(UUID userId);

  Optional<UserPreference> findUserPreferenceByUserIdAndPreference_Name(
      UUID userId, String preferenceName);
}
