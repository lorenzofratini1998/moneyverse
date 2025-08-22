import {inject, Injectable} from '@angular/core';
import {FormHandler} from '../../../shared/models/form.model';
import {AnalyticsFilterFormData, DashboardFilter, PeriodFormat} from '../analytics.models';
import {FormBuilder, FormGroup} from "@angular/forms";
import {DashboardStore} from './dashboard.store';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsFilterFormHandler implements FormHandler<DashboardFilter, AnalyticsFilterFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly dashboardStore = inject(DashboardStore);

  create(): FormGroup {
    return this.fb.group({
      periodFormat: ['year'],
      period: [null],
      comparePeriodFormat: ['none'],
      comparePeriod: [null],
      accounts: [null],
      categories: [null],
      currency: [null],
      tags: [null]
    })
  }

  patch(form: FormGroup, data: DashboardFilter): void {
    form.patchValue({
      periodFormat: data.periodFormat,
      period: [data.period.startDate, data.period.endDate],
      comparePeriodFormat: data.comparePeriodFormat,
      comparePeriod: data.comparePeriod ? [data.comparePeriod.startDate, data.comparePeriod.endDate] : null,
      accounts: data.accounts ?? [],
      categories: data.categories ?? [],
      currency: data.currency,
      tags: data.tags ?? []
    }, {emitEvent: false});
  }

  reset(form: FormGroup): void {
    this.dashboardStore.resetFilter();
    form.reset({
      periodFormat: 'year',
      period: null,
      comparePeriodFormat: 'none',
      comparePeriod: null,
      accounts: null,
      categories: null,
      currency: null,
      tags: null
    })
  }

  prepareData(form: FormGroup): AnalyticsFilterFormData {
    const formValue = form.value;
    return {
      ...formValue,
      period: {
        format: formValue.periodFormat,
        value: formValue.period ? {
          startDate: formValue.period[0],
          endDate: formValue.period[1]
        } : null
      },
      comparePeriod: {
        format: formValue.comparePeriodFormat,
        value: formValue.comparePeriod ? {
          startDate: formValue.comparePeriod[0],
          endDate: formValue.comparePeriod[1]
        } : null
      }
    } as AnalyticsFilterFormData;
  }

  defaultPeriodRange(format: PeriodFormat, comparePeriod = false): Date[] {
    const currentDate = new Date();

    switch (format) {
      case 'year':
        return this.defaultYear(currentDate, comparePeriod);
      case 'month':
        return this.defaultMonth(currentDate, comparePeriod);
      case 'custom':
      default:
        return this.defaultCustom(comparePeriod);
    }
  }

  private defaultYear(currentDate: Date, comparePeriod = false): Date[] {
    const year = currentDate.getFullYear() - (comparePeriod ? 1 : 0);
    return [
      new Date(year, 0, 1),
      new Date(year, 11, 31)
    ]
  }

  private defaultMonth(currentDate: Date, comparePeriod = false): Date[] {
    let year = currentDate.getFullYear();
    let month = currentDate.getMonth() - (comparePeriod ? 1 : 0);
    if (comparePeriod) {
      if (month < 0) {
        month = 11;
        year -= 1;
      }
    }
    const lastDayOfMonth = new Date(year, month + 1, 0).getDate();
    return [
      new Date(year, month, 1),
      new Date(year, month, lastDayOfMonth)
    ]
  }

  private defaultCustom(comparePeriod = false): Date[] {
    const startDate = new Date();
    const endDate = new Date();
    startDate.setMonth(startDate.getMonth() - 1);
    if (comparePeriod) {
      startDate.setMonth(startDate.getMonth() - 1);
      endDate.setMonth(endDate.getMonth() - 1);
    }
    return [startDate, endDate];
  }

  onSelectPeriod(form: FormGroup, event: any) {
    const dates = this.mapDates(event, form.value.periodFormat);
    form.patchValue({period: dates}, {emitEvent: false});
  }

  onSelectComparePeriod(form: FormGroup, event: any) {
    const dates = this.mapDates(event, form.value.comparePeriodFormat);
    form.patchValue({comparePeriod: dates}, {emitEvent: false});
  }

  private mapDates(event: any, format: PeriodFormat): Date[] {
    switch (format) {
      case "year":
        return this.mapYear(event);
      case "month":
        return this.mapMonth(event);
      default:
        return Array.isArray(event) ? event : [event];
    }
  }

  private mapYear(event: any): Date[] {
    const selectedYear = event.getFullYear();
    return [
      new Date(selectedYear, 0, 1),
      new Date(selectedYear, 11, 31)
    ]
  }

  private mapMonth(event: any): Date[] {
    const year = event.getFullYear();
    const month = event.getMonth();
    const lastDayOfMonth = new Date(year, month + 1, 0).getDate();
    return [
      new Date(year, month, 1),
      new Date(year, month, lastDayOfMonth)
    ]
  }

}
