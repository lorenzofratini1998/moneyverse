import {computed, inject, Injectable} from '@angular/core';
import {DashboardFilter} from '../../analytics/analytics.models';
import {AuthService} from '../../../core/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class OverviewService {

  private readonly authService = inject(AuthService);

  readonly filter = computed<DashboardFilter>(() => ({
    periodFormat: 'year',
    period: {
      startDate: new Date(new Date().getFullYear(), 0, 1),
      endDate: new Date(new Date().getFullYear(), 11, 31)
    },
    comparePeriodFormat: 'none',
    userId: this.authService.user().userId
  }))
}
