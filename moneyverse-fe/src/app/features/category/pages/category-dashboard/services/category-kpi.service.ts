import {inject, Injectable} from '@angular/core';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs';
import {DashboardStore} from '../../../../analytics/services/dashboard.store';
import {AnalyticsService} from '../../../../../shared/services/analytics.service';

@Injectable({
  providedIn: 'root'
})
export class CategoryKpiService {
  private readonly dashboardStore = inject(DashboardStore);
  private readonly analyticsService = inject(AnalyticsService);

  data = toSignal(
    toObservable(this.dashboardStore.filter).pipe(
      switchMap(filter => this.analyticsService.calculateCategoryKpi(filter))
    ),
    {initialValue: null}
  )
}
