package it.moneyverse.user.runtime.controllers;

import it.moneyverse.user.model.dto.PreferenceDto;
import it.moneyverse.user.model.dto.PreferenceRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface UserOperations {

  PreferenceDto createPreferences(UUID userId, List<@Valid PreferenceRequest> request);
}
