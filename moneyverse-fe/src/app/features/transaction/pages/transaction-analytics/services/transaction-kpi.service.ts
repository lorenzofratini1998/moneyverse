import {inject, Injectable} from '@angular/core';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {combineLatest, switchMap} from "rxjs";
import {AnalyticsEventService} from '../../../../analytics/services/analytics-event.service';

@Injectable({
  providedIn: 'root'
})
export class TransactionKpiService {
  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly analyticsEventService = inject(AnalyticsEventService);

  data = toSignal(
    combineLatest([
      toObservable(this.dashboardStore.filter),
      this.analyticsEventService.reload$
    ]).pipe(
      switchMap(([filter]) =>
        this.analyticsService.calculateTransactionKpi(filter))
    ),
    {initialValue: null}
  )
}
