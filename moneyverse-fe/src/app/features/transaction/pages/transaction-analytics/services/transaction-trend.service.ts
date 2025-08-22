import {inject, Injectable} from '@angular/core';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs';
import {UserDateFormatPipe} from '../../../../../shared/pipes/user-date-format.pipe';
import {ExpenseIncome} from '../../../../analytics/analytics.models';

@Injectable({
  providedIn: 'root'
})
export class TransactionTrendService {
  private readonly dashboardStore = inject(DashboardStore)
  private readonly analyticsService = inject(AnalyticsService)
  private readonly userDateFormatPipe = inject(UserDateFormatPipe);


  data = toSignal(
    toObservable(this.dashboardStore.filter).pipe(
      switchMap(filter => this.analyticsService.calculateTransactionTrend(filter))
    ),
    {initialValue: null}
  )

  getLabels(trend: ExpenseIncome[]): string[] {
    return trend.map(t => this.userDateFormatPipe.transform(t.period!.startDate.toString()));
  }

  getSeries(trend: ExpenseIncome[]): {
    name: string,
    data: number[]
  }[] {
    return [
      {
        name: 'Expense',
        data: trend.map(t => t.expense.amount)
      },
      {
        name: 'Income',
        data: trend.map(t => t.income.amount)
      }
    ]
  }
}
