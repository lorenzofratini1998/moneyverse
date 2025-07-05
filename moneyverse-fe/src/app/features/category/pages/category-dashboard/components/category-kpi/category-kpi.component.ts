import {Component, computed, inject, input} from '@angular/core';
import {EnrichedCategoryDashboard} from '../../category-dashboard.model';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {PreferenceKey} from '../../../../../../shared/models/preference.model';
import {KpiComponent} from '../../../../../../shared/components/kpi/kpi.component';

@Component({
  selector: 'app-category-kpi',
  imports: [
    CurrencyPipe,
    KpiComponent
  ],
  templateUrl: './category-kpi.component.html',
  styleUrl: './category-kpi.component.scss'
})
export class CategoryKpiComponent {
  protected readonly preferenceStore = inject(PreferenceStore);
  readonly categoryDashboard = input.required<EnrichedCategoryDashboard>();

  readonly kpiData = computed(() => this.categoryDashboard().kpi ?? {
    totalAmount: 0,
    averageAmount: 0,
    numberOfActiveCategories: 0,
    previous: null
  });

  readonly topCategoryData = computed(() => this.categoryDashboard().topCategory ?? {
    category: {categoryName: 'N/A'},
    amount: 0
  });

}
