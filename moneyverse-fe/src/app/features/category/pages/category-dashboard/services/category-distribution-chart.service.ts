import {inject, Injectable} from '@angular/core';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {combineLatest, switchMap} from 'rxjs';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {CategoryStore} from '../../../services/category.store';
import {CategoryAnalyticsDistribution} from '../models/category-analytics.model';
import {ChartFilter} from '../../../../analytics/analytics.models';
import {BarLineChartOptions} from '../../../../../shared/models/chart.model';
import {TranslationService} from '../../../../../shared/services/translation.service';
import {AnalyticsEventService} from '../../../../analytics/services/analytics-event.service';

@Injectable({
  providedIn: 'root'
})
export class CategoryDistributionChartService {
  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);
  protected readonly categoryStore = inject(CategoryStore);
  private readonly translateService = inject(TranslationService);
  private readonly analyticsEventService = inject(AnalyticsEventService);

  data = toSignal(
    combineLatest([
      toObservable(this.dashboardStore.filter),
      this.analyticsEventService.reload$
    ]).pipe(
      switchMap(([filter]) =>
        this.analyticsService.calculateCategoryDistribution(filter))
    ),
    {initialValue: []}
  )

  getChartOptions(data: CategoryAnalyticsDistribution[], filter: ChartFilter): BarLineChartOptions {
    switch (filter) {
      case "totalExpense":
        return this.buildSeries(data, "totalExpense", true);
      case "totalIncome":
        return this.buildSeries(data, "totalIncome", true);
      case "totalAmount":
        return this.buildSeries(data, "totalAmount", false);
      default:
        return {
          labels: [],
          series: [{name: this.translateService.translate('app.chart.empty'), data: []}],
        };
    }
  }

  private buildSeries(
    data: CategoryAnalyticsDistribution[],
    key: "totalExpense" | "totalIncome" | "totalAmount",
    filterPositive: boolean
  ): BarLineChartOptions {
    let _filteredData = data;
    if (filterPositive) {
      _filteredData = _filteredData.filter(c => c[key].amount > 0);
    }
    _filteredData = _filteredData.sort((a, b) => a[key].amount - b[key].amount);

    const series = [{
      name: this.translateService.translate('app.chart.current'),
      data: _filteredData.map(c => c[key].amount),
    }];

    const hasCompareData = _filteredData.some(c => c.compare);
    if (hasCompareData) {
      series.push({
        name: this.translateService.translate('app.chart.compare'),
        data: _filteredData.map(c => c.compare?.[key].amount || 0),
      });
    }

    return {
      labels: _filteredData.map(c =>
        this.categoryStore.categoriesMap().get(c.categoryId)!.categoryName
      ),
      series,
    };
  }
}
