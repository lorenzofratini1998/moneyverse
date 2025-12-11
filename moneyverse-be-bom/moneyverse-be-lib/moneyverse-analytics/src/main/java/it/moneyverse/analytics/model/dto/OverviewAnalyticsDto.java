package it.moneyverse.analytics.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = OverviewAnalyticsDto.Builder.class)
public class OverviewAnalyticsDto implements Serializable {
    private final PeriodDto period;
    private final ExpenseIncomeDto total;
    private final List<ExpenseIncomeDto> data;

    public static class Builder {
        private PeriodDto period;
        private ExpenseIncomeDto total;
        private List<ExpenseIncomeDto> data;

        public Builder withPeriod(PeriodDto period) {
            this.period = period;
            return this;
        }

        public Builder withTotal(ExpenseIncomeDto total) {
            this.total = total;
            return this;
        }

        public Builder withData(List<ExpenseIncomeDto> data) {
            this.data = data;
            return this;
        }

        public OverviewAnalyticsDto build() {
            return new OverviewAnalyticsDto(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public OverviewAnalyticsDto(Builder builder) {
        this.period = builder.period;
        this.total = builder.total;
        this.data = builder.data;
    }

    public PeriodDto getPeriod() {
        return period;
    }

    public ExpenseIncomeDto getTotal() {
        return total;
    }

    public List<ExpenseIncomeDto> getData() {
        return data;
    }
}
