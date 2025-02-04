package it.moneyverse.core.model.events;

import java.util.UUID;

public record UserCreationEvent(UUID userId, String currency) {}
