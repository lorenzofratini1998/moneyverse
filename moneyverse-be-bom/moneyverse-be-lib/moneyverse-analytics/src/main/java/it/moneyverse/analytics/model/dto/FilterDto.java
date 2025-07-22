package it.moneyverse.analytics.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record FilterDto(
    @NotNull(message = "User id is required") UUID userId,
    List<UUID> accounts,
    List<UUID> categories,
    List<UUID> tags,
    String currency,
    @Valid @NotNull(message = "Period is required") PeriodDto period,
    PeriodDto comparePeriod)
    implements Serializable {}
