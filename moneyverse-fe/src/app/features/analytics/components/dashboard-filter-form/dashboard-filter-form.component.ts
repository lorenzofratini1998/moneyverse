import {Component, effect, inject, OnInit} from '@angular/core';
import {AbstractFormComponent} from '../../../../shared/components/forms/abstract-form.component';
import {AnalyticsFilterFormData, DashboardFilter, Period, PeriodFormat} from '../../analytics.models';
import {AnalyticsFilterFormHandler} from '../../services/analytics-filter-form.handler';
import {DashboardStore} from '../../services/dashboard.store';
import {ReactiveFormsModule} from '@angular/forms';
import {
  AccountMultiSelectComponent
} from '../../../../shared/components/forms/account-multi-select/account-multi-select.component';
import {
  CategoryMultiSelectComponent
} from '../../../../shared/components/forms/category-multi-select/category-multi-select.component';
import {CurrencySelectComponent} from '../../../../shared/components/forms/currency-select/currency-select.component';
import {PeriodSelectorComponent} from '../period-selector/period-selector.component';
import {TagMultiSelectComponent} from '../../../../shared/components/forms/tag-multi-select/tag-multi-select.component';
import {getUTCDate} from '../../../../shared/utils/date.utils';

@Component({
  selector: 'app-dashboard-filter-form',
  imports: [
    AccountMultiSelectComponent,
    CategoryMultiSelectComponent,
    CurrencySelectComponent,
    PeriodSelectorComponent,
    ReactiveFormsModule,
    TagMultiSelectComponent
  ],
  templateUrl: './dashboard-filter-form.component.html'
})
export class DashboardFilterFormComponent extends AbstractFormComponent<DashboardFilter, AnalyticsFilterFormData> implements OnInit {
  protected override formHandler = inject(AnalyticsFilterFormHandler);
  protected dashboardStore = inject(DashboardStore);

  constructor() {
    super();
    effect(() => {
      const filter = this.dashboardStore.filter();
      this.patch(filter);
    });
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.initPeriodListener();
    this.initComparePeriodListener();
  }

  private initPeriodListener() {
    this.formGroup.get('periodFormat')?.valueChanges.subscribe((format: PeriodFormat) => {
      if (format && format !== 'none') {
        const defaultRange = this.formHandler.defaultPeriodRange(format);
        this.formGroup.patchValue({period: defaultRange}, {emitEvent: false});
      }
    })
  }

  private initComparePeriodListener() {
    this.formGroup.get('comparePeriodFormat')?.valueChanges.subscribe((format: PeriodFormat) => {
      if (format && format !== 'none') {
        const defaultRange = this.formHandler.defaultPeriodRange(format, true);
        this.formGroup.patchValue({comparePeriod: defaultRange}, {emitEvent: false});
      }
    })
  }

  override submit() {
    const formData = this.prepareData();
    this.dashboardStore.updateFilter({
      periodFormat: formData.period.format,
      period: formData.period.value ? this.formatPeriod(formData.period.value) : undefined,
      comparePeriodFormat: formData.comparePeriod?.format,
      comparePeriod: formData.comparePeriod?.value ? this.formatPeriod(formData.comparePeriod.value) : undefined,
      accounts: formData.accounts,
      categories: formData.categories,
      currency: formData.currency,
      tags: formData.tags
    });
  }

  private formatPeriod(period: { startDate: Date, endDate: Date }): Period {
    const startDate = period.startDate;
    const endDate = period.endDate;
    return {
      startDate: getUTCDate(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()),
      endDate: getUTCDate(endDate.getFullYear(), endDate.getMonth(), endDate.getDate())
    }

  }
}
