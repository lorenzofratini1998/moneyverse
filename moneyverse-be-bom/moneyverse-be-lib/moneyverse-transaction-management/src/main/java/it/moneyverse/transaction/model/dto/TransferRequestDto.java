package it.moneyverse.transaction.model.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferRequestDto(
    @NotNull(message = "User ID must not be null") UUID userId,
    @NotNull(message = "Source account ID must not be null") UUID fromAccount,
    @NotNull(message = "Destination account ID must not be null") UUID toAccount,
    @NotNull(message = "Amount must not be null") BigDecimal amount,
    @NotNull(message = "Date must not be null") LocalDate date,
    @NotNull(message = "Currency must not be null") String currency) {}
