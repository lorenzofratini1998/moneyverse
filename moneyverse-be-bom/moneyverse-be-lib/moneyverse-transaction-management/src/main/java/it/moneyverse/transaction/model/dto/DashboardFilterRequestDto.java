package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record DashboardFilterRequestDto(
    UUID userId,
    List<UUID> accounts,
    List<UUID> categories,
    @NotNull(message = "Period cannot be null or empty") PeriodDashboardDto period,
    Optional<PeriodDashboardDto> comparePeriod) {
  @AssertTrue(message = "Compare period must be before period")
  public boolean isComparePeriodValid() {
    return comparePeriod
        .map(periodDashboardDto -> periodDashboardDto.startDate().isBefore(period.endDate()))
        .orElse(true);
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
