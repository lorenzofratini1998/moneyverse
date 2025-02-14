package it.moneyverse.transaction.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TagRequestDto(
    @NotNull(message = "User ID must not be null") UUID userId,
    @NotNull(message = "Tag name must not be null") String tagName,
    String description) {}
