import {computed, inject, Injectable, signal} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {AuthService} from '../../../core/auth/auth.service';
import {AnalyticsService} from '../../../shared/services/analytics.service';
import {OverviewAnalytics} from '../models/overview-analytics.model';
import {UserDateFormatPipe} from '../../../shared/pipes/user-date-format.pipe';
import {ChartType} from '../../../shared/models/chart.model';
import {PeriodFormat} from '../../analytics/analytics.models';
import {TranslationService} from '../../../shared/services/translation.service';
import {AnalyticsEventService} from '../../analytics/services/analytics-event.service';
import {combineLatest, of, switchMap} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OverviewChartService {

  private readonly authService = inject(AuthService);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly userDateFormatPipe = inject(UserDateFormatPipe);
  private readonly translateService = inject(TranslationService);
  private readonly analyticsEventService = inject(AnalyticsEventService);

  private readonly viewOption = signal<PeriodFormat>('year');

  data = toSignal(
    combineLatest([
      of(this.authService.user().userId),
      this.analyticsEventService.reload$
    ]).pipe(
      switchMap(([userId]) =>
        this.analyticsService.calculateOverview(userId)
      )
    ),
    { initialValue: [] }
  );

  chartViewOptions = computed(() => {
    this.translateService.lang();
    return [
      {label: this.translateService.translate('app.chart.year'), value: 'year'},
      {label: this.translateService.translate('app.chart.month'), value: 'month'}
    ];
  });

  series = computed(() => {
    this.translateService.lang();
    const data = this.data();
    const view = this.viewOption();

    if (!data || data.length === 0) {
      return [];
    }

    const {expenses, incomes, totals} = view === 'year'
      ? this.extractYearData(data)
      : this.extractMonthData(data);

    return [
      {
        type: 'bar' as ChartType,
        name: this.translateService.translate('app.expense'),
        data: expenses
      },
      {
        type: 'bar' as ChartType,
        name: this.translateService.translate('app.income'),
        data: incomes
      },
      {
        type: 'line' as ChartType,
        name: this.translateService.translate('app.chart.total'),
        data: totals
      }
    ];
  });

  setViewOption(option: PeriodFormat): void {
    this.viewOption.set(option);
  }

  getLabels(data: OverviewAnalytics[], viewOption: PeriodFormat): string[] {
    switch (viewOption) {
      case "year":
        return this.getYearLabels(data);
      case "month":
        return this.getMonthLabels(data);
      default:
        return [];
    }
  }

  private getYearLabels(data: OverviewAnalytics[]): string[] {
    return data.map(item => {
      const startDate = item.period?.startDate;
      if (!startDate) return "";
      return this.userDateFormatPipe.transform(startDate, "yyyy", "year");
    });
  }

  private getMonthLabels(data: OverviewAnalytics[]): string[] {
    const labels: string[] = [];
    for (const year of data) {
      if (!year.data) continue;
      for (const month of year.data) {
        const startDate = month.period?.startDate;
        if (!startDate) continue;
        labels.push(
          this.userDateFormatPipe.transform(startDate, "MM-yyyy", "year-month")
        );
      }
    }
    return labels;
  }

  private extractYearData(data: OverviewAnalytics[]) {
    return {
      expenses: data.map(y => y.total?.expense?.amount ?? 0),
      incomes: data.map(y => y.total?.income?.amount ?? 0),
      totals: data.map(y => y.total?.total?.amount ?? 0)
    };
  }

  private extractMonthData(data: OverviewAnalytics[]) {
    const expenses: number[] = [];
    const incomes: number[] = [];
    const totals: number[] = [];

    for (const year of data) {
      if (!year.data) continue;
      for (const month of year.data) {
        expenses.push(month.expense?.amount ?? 0);
        incomes.push(month.income?.amount ?? 0);
        totals.push(month.total?.amount ?? 0);
      }
    }

    return {expenses, incomes, totals};
  }

}
