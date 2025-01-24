package it.moneyverse.core.model.events;

import java.util.UUID;

public record AccountDeletionEvent(UUID accountId) {}
