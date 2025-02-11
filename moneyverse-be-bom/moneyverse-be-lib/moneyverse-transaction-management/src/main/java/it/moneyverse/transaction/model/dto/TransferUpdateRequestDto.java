package it.moneyverse.transaction.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferUpdateRequestDto(
    UUID fromAccount, UUID toAccount, BigDecimal amount, LocalDate date, String currency) {}
