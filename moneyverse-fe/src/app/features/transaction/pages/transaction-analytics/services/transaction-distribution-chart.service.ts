import {inject, Injectable} from '@angular/core';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs';
import {DistributionRange} from '../models/transaction-analytics.model';
import {PreferenceStore} from '../../../../../shared/stores/preference.store';
import {CurrencyPipe} from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class TransactionDistributionChartService {
  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly preferenceStore = inject(PreferenceStore);
  private readonly currencyPipe = inject(CurrencyPipe);

  readonly distributionRangeMap = new Map<string, DistributionRange>()

  data = toSignal(
    toObservable(this.dashboardStore.filter).pipe(
      switchMap(filter => this.analyticsService.calculateTransactionDistribution(filter))
    ),
    {initialValue: null}
  );

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
