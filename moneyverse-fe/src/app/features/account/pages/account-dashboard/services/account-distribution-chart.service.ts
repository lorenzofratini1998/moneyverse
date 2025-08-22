import {inject, Injectable} from '@angular/core';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs';
import {AccountAnalyticsDistribution} from '../models/account-analytics.model';

import {ChartFilter} from "../../../../analytics/analytics.models";

@Injectable({
  providedIn: 'root'
})
export class AccountDistributionChartService {

  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);

  data = toSignal(
    toObservable(this.dashboardStore.filter).pipe(
      switchMap(filter => this.analyticsService.calculateAccountAnalyticsDistribution(filter))
    ),
    {initialValue: []}
  )

  getPieChartValue(data: AccountAnalyticsDistribution, filter: ChartFilter): number {
    switch (filter) {
      case 'totalAmount':
        return data.totalAmount.amount;
      case 'totalIncome':
        return data.totalIncome.amount;
      case 'totalExpense':
        return data.totalExpense.amount;
      default:
        return 0
    }
  }

}
