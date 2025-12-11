package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.*;
import it.moneyverse.analytics.model.projections.OverviewAnalyticsProjection;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OverviewAnalyticsStrategy implements AnalyticsStrategy<List<OverviewAnalyticsDto>, List<OverviewAnalyticsProjection>> {

    @Override
    public List<OverviewAnalyticsDto> calculate(List<OverviewAnalyticsProjection> currentData, List<OverviewAnalyticsProjection> compareData, FilterDto parameters) {
        if (currentData == null || currentData.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, List<OverviewAnalyticsProjection>> byYear =
                currentData.stream().collect(Collectors.groupingBy(p -> p.startDate().getYear()));

        return byYear.entrySet().stream()
                .map(entry -> buildYearOverview(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(o -> o.getPeriod().startDate()))
                .toList();
    }

    private OverviewAnalyticsDto buildYearOverview(int year,
                                                   List<OverviewAnalyticsProjection> projections) {

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        BigDecimal totalAmount = sum(projections, OverviewAnalyticsProjection::totalAmount);
        BigDecimal totalIncome = sum(projections, OverviewAnalyticsProjection::totalIncome);
        BigDecimal totalExpense = sum(projections, OverviewAnalyticsProjection::totalExpense);

        List<ExpenseIncomeDto> months = projections.stream()
                .sorted(Comparator.comparing(OverviewAnalyticsProjection::startDate))
                .map(this::buildMonthlyExpenseIncome)
                .toList();

        return OverviewAnalyticsDto.builder()
                .withPeriod(new PeriodDto(start, end))
                .withTotal(ExpenseIncomeDto.builder()
                        .withPeriod(new PeriodDto(start, end))
                        .withIncome(buildAmount(start, end, totalIncome))
                        .withExpense(buildAmount(start, end, totalExpense))
                        .withTotal(buildAmount(start, end, totalAmount))
                        .build())
                .withData(months)
                .build();
    }


    private ExpenseIncomeDto buildMonthlyExpenseIncome(OverviewAnalyticsProjection p) {
        return ExpenseIncomeDto.builder()
                .withPeriod(new PeriodDto(p.startDate(), p.endDate()))
                .withIncome(buildAmount(p.startDate(), p.endDate(), p.totalIncome()))
                .withExpense(buildAmount(p.startDate(), p.endDate(), p.totalExpense()))
                .withTotal(buildAmount(p.startDate(), p.endDate(), p.totalAmount()))
                .build();
    }


    private AmountDto buildAmount(LocalDate start, LocalDate end, BigDecimal amount) {
        return AmountDto.builder()
                .withPeriod(new PeriodDto(start, end))
                .withAmount(amount)
                .build();
    }


    private BigDecimal sum(List<OverviewAnalyticsProjection> list,
                           Function<OverviewAnalyticsProjection, BigDecimal> extractor) {
        return list.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
