import {Component, inject} from '@angular/core';
import {CategoryStore} from '../../../../services/category.store';
import {KpiComponent} from '../../../../../../shared/components/charts/kpi/kpi.component';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {CategoryKpiService} from '../../services/category-kpi.service';

@Component({
  selector: 'app-category-kpi',
  imports: [
    KpiComponent,
    CurrencyPipe
  ],
  template: `
    @if (kpiService.data(); as kpi) {
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 max-w-6xl mx-auto">
        <app-kpi label="Top Category"
                 [value]="categoryStore.categoriesMap().get(kpi.topCategory)?.categoryName ?? 'N/A'"/>
        <app-kpi label="Most Used Category"
                 [value]="categoryStore.categoriesMap().get(kpi.mostUsedCategory)?.categoryName ?? 'N/A'"/>
        <app-kpi label="Active Categories"
                 [value]="kpi.activeCategories.count"
                 [variation]="kpi.activeCategories.variation"/>
        <app-kpi label="Uncategorized Amount"
                 [value]="kpi.uncategorizedAmount.amount | currency: preferenceStore.userCurrency()"
                 [variation]="kpi.uncategorizedAmount.variation"/>
      </div>
    }
  `
})
export class CategoryKpiComponent {
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly kpiService = inject(CategoryKpiService);

}
