package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record TransactionRequestDto(
    @NotNull(message = "'User ID' must not be null") UUID userId,
    @Valid List<TransactionRequestItemDto> transactions) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
