package it.moneyverse.core.model.events;

import it.moneyverse.core.enums.CurrencyEnum;

public record UserCreationEvent(String username, CurrencyEnum currency) {}
