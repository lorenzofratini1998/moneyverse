package it.moneyverse.analytics.model.repositories;

import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.OverviewAnalyticsProjection;
import it.moneyverse.analytics.model.queries.OverviewAnalyticsQuery;
import it.moneyverse.analytics.services.QueryExecutor;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class OverviewAnalyticsClickhouseDataAccess implements OverviewAnalyticsDataAccess {
    private final QueryExecutor queryExecutor;
    private final OverviewAnalyticsQuery overviewQuery;

    public OverviewAnalyticsClickhouseDataAccess(QueryExecutor queryExecutor, OverviewAnalyticsQuery overviewQuery) {
        this.queryExecutor = queryExecutor;
        this.overviewQuery = overviewQuery;
    }

    @Override
    public List<OverviewAnalyticsProjection> getOverviewAnalytics(FilterDto parameters) {
        return queryExecutor.execute(overviewQuery, parameters);
    }
}
