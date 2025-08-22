import {inject, Injectable} from '@angular/core';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TransactionKpiService {
  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);

  data = toSignal(
    toObservable(this.dashboardStore.filter).pipe(
      switchMap(filter => this.analyticsService.calculateTransactionKpi(filter))
    ),
    {initialValue: null}
  )
}
