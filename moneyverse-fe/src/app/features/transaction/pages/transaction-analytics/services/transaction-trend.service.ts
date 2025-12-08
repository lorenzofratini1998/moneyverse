import {inject, Injectable} from '@angular/core';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {combineLatest, switchMap} from 'rxjs';
import {UserDateFormatPipe} from '../../../../../shared/pipes/user-date-format.pipe';
import {ExpenseIncome} from '../../../../analytics/analytics.models';
import {TranslationService} from '../../../../../shared/services/translation.service';
import {AnalyticsEventService} from '../../../../analytics/services/analytics-event.service';

@Injectable({
  providedIn: 'root'
})
export class TransactionTrendService {
  private readonly dashboardStore = inject(DashboardStore)
  private readonly analyticsService = inject(AnalyticsService)
  private readonly userDateFormatPipe = inject(UserDateFormatPipe);
  private readonly translateService = inject(TranslationService);
  private readonly analyticsEventService = inject(AnalyticsEventService);

  data = toSignal(
    combineLatest([
      toObservable(this.dashboardStore.filter),
      this.analyticsEventService.reload$
    ]).pipe(
      switchMap(([filter]) =>
        this.analyticsService.calculateTransactionTrend(filter))
    ),
    {initialValue: null}
  )

  getLabels(trend: ExpenseIncome[]): string[] {
    return trend.map(t => this.userDateFormatPipe.transform(t.period!.startDate.toString(), undefined, "year-month"));
  }

  getSeries(trend: ExpenseIncome[]): {
    name: string,
    data: number[]
  }[] {
    return [
      {
        name: this.translateService.translate("app.expense"),
        data: trend.map(t => t.expense.amount)
      },
      {
        name: this.translateService.translate("app.income"),
        data: trend.map(t => t.income.amount)
      }
    ]
  }
}
