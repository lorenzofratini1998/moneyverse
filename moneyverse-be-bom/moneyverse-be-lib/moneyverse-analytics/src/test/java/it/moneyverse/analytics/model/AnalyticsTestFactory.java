package it.moneyverse.analytics.model;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.PeriodDto;
import it.moneyverse.analytics.model.entities.TransactionEvent;
import it.moneyverse.test.utils.RandomUtils;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalyticsTestFactory {

  public static FilterDto createFilter(UUID userId, List<TransactionEvent> transactionEvents) {
    List<TransactionEvent> userEvents =
        transactionEvents.stream().filter(e -> e.getUserId().equals(userId)).toList();

    List<UUID> accounts =
        extractRandomSubset(
            userEvents.stream()
                .map(TransactionEvent::getAccountId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList()));

    List<UUID> categories =
        extractRandomSubset(
            userEvents.stream()
                .map(TransactionEvent::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList()));

    List<UUID> tags =
        extractRandomSubset(
            userEvents.stream()
                .flatMap(e -> e.getTags() != null ? e.getTags().stream() : Stream.empty())
                .distinct()
                .collect(Collectors.toList()));

    PeriodDto period = new PeriodDto(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

    PeriodDto comparePeriod = null;
    comparePeriod = new PeriodDto(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
    /*if (RandomUtils.flipCoin()) {
      comparePeriod = new PeriodDto(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
    }*/

    return new FilterDto(
        userId,
        accounts.isEmpty() ? null : accounts,
        categories.isEmpty() ? null : categories,
        tags.isEmpty() ? null : tags,
        null,
        period,
        comparePeriod);
  }

  private static List<UUID> extractRandomSubset(List<UUID> fullList) {
    if (fullList.isEmpty() || !RandomUtils.flipCoin()) {
      return Collections.emptyList();
    }
    return RandomUtils.randomSubList(fullList);
  }

  private AnalyticsTestFactory() {}
}
