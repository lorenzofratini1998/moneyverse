import {inject, Injectable} from '@angular/core';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {combineLatest, switchMap} from 'rxjs';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {AccountAnalyticsTrend} from '../models/account-analytics.model';
import {Account} from '../../../account.model';
import {UserDateFormatPipe} from '../../../../../shared/pipes/user-date-format.pipe';
import {ChartFilter, ExpenseIncome} from "../../../../analytics/analytics.models";
import {AnalyticsEventService} from '../../../../analytics/services/analytics-event.service';

@Injectable({
  providedIn: 'root'
})
export class AccountTrendChartService {

  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly userDateFormatPipe = inject(UserDateFormatPipe);
  private readonly analyticsEventService = inject(AnalyticsEventService);

  data = toSignal(
    combineLatest([
      toObservable(this.dashboardStore.filter),
      this.analyticsEventService.reload$
    ]).pipe(
      switchMap(([filter]) =>
        this.analyticsService.calculateAccountAnalyticsTrend(filter)
      )
    ),
    { initialValue: [] }
  );

  getLabels(trend: AccountAnalyticsTrend[], account: Account): string[] {
    const trendData = trend.find(t => t.accountId === account.accountId) ?? null
    if (!trendData) {
      return [];
    }
    return trendData.data.map(t => this.userDateFormatPipe.transform(t.period!.startDate.toString(), undefined, "year-month"));
  }

  getSeries(trend: AccountAnalyticsTrend[], account: Account, chartFilter: ChartFilter): {
    name: string,
    data: number[]
  }[] {
    const trendData = trend.find(t => t.accountId === account.accountId) ?? null;
    if (!trendData) {
      return [];
    }
    return [{
      name: account.accountName,
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
