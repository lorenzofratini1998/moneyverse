import {inject, Injectable} from '@angular/core';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs';
import {Category} from '../../../category.model';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {CategoryAnalyticsTrend} from '../models/category-analytics.model';
import {ChartFilter, ExpenseIncome} from '../../../../analytics/analytics.models';
import {UserDateFormatPipe} from '../../../../../shared/pipes/user-date-format.pipe';

@Injectable({
  providedIn: 'root'
})
export class CategoryTrendChartService {
  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly userDateFormatPipe = inject(UserDateFormatPipe);

  data = toSignal(
    toObservable(this.dashboardStore.filter).pipe(
      switchMap(filter => this.analyticsService.calculateCategoryTrend(filter))
    ),
    {initialValue: []}
  );

  getLabels(trend: CategoryAnalyticsTrend[], category: Category): string[] {
    const trendData = trend.find(t => t.categoryId === category.categoryId) ?? null;
    if (!trendData) {
      return [];
    }
    return trendData.data.map(t => this.userDateFormatPipe.transform(t.period!.startDate.toString()));
  }

  getSeries(trend: CategoryAnalyticsTrend[], category: Category, chartFilter: ChartFilter): {
    name: string,
    data: number[]
  }[] {
    const trendData = trend.find(t => t.categoryId === category.categoryId) ?? null;
    if (!trendData) {
      return [];
    }
    return [{
      name: category.categoryName,
      data: trendData.data.map(t => this.getData(t, chartFilter))
    }]
  }

  private getData(expenseIncome: ExpenseIncome, chartFilter: ChartFilter): number {
    switch (chartFilter) {
      case 'totalAmount':
        return expenseIncome.total.amount;
      case 'totalIncome':
        return expenseIncome.income.amount;
      case 'totalExpense':
        return expenseIncome.expense.amount;
      default:
        return 0
    }
  }
}
