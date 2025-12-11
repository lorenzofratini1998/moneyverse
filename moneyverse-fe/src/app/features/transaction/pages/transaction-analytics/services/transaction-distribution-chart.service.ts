import {inject, Injectable} from '@angular/core';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {combineLatest, switchMap} from 'rxjs';
import {DistributionRange} from '../models/transaction-analytics.model';
import {PreferenceStore} from '../../../../../shared/stores/preference.store';
import {CurrencyPipe} from '@angular/common';
import {AnalyticsEventService} from '../../../../analytics/services/analytics-event.service';

@Injectable({
  providedIn: 'root',
})
export class TransactionDistributionChartService {
  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly preferenceStore = inject(PreferenceStore);
  private readonly currencyPipe = inject(CurrencyPipe);
  private readonly analyticsEventService = inject(AnalyticsEventService);

  readonly distributionRangeMap = new Map<string, DistributionRange>()

  data = toSignal(
    combineLatest([
      toObservable(this.dashboardStore.filter),
      this.analyticsEventService.reload$
    ]).pipe(
      switchMap(([filter]) =>
        this.analyticsService.calculateTransactionDistribution(filter))
    ),
    { initialValue: null}
  )

  getLabels(data: DistributionRange[]): string[] {
    return data.map(t => {
      if (!t.range.lower) {
        const label = `< ${this.currencyPipe.transform(t.range.upper, this.preferenceStore.userCurrency())}`;
        this.distributionRangeMap.set(label, t);
        return label;
      }
      if (!t.range.upper) {
        const label = `> ${this.currencyPipe.transform(t.range.lower, this.preferenceStore.userCurrency())}`;
        this.distributionRangeMap.set(label, t);
        return label;
      }
      const label = `${this.currencyPipe.transform(t.range.lower, this.preferenceStore.userCurrency())} - ${this.currencyPipe.transform(t.range.upper, this.preferenceStore.userCurrency())}`;
      this.distributionRangeMap.set(label, t);
      return label;
    })
  }

}
