import {Component, computed, inject, signal} from '@angular/core';
import {AnalyticsService} from '../../../../shared/services/analytics.service';
import {OverviewService} from '../../services/overview.service';
import {CategoryStore} from '../../../category/services/category.store';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs';
import {TableColumn, TableConfig} from '../../../../shared/models/table.model';
import {
  CategoryAnalyticsDistribution
} from '../../../category/pages/category-dashboard/models/category-analytics.model';
import {CellTemplateDirective} from '../../../../shared/directives/cell-template.directive';
import {CurrencyPipe} from '../../../../shared/pipes/currency.pipe';
import {TableComponent} from '../../../../shared/components/table/table.component';
import {ChipComponent} from '../../../../shared/components/chip/chip.component';
import {PreferenceStore} from '../../../../shared/stores/preference.store';
import {Card} from 'primeng/card';
import {TranslatePipe} from '@ngx-translate/core';
import {TranslationService} from '../../../../shared/services/translation.service';

@Component({
  selector: 'app-overview-category',
  imports: [
    CellTemplateDirective,
    CurrencyPipe,
    TableComponent,
    ChipComponent,
    Card,
    TranslatePipe
  ],
  templateUrl: './overview-category.component.html'
})
export class OverviewCategoryComponent {
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly overviewService = inject(OverviewService);
  private readonly translateService = inject(TranslationService);

  data = toSignal(
    toObservable(this.overviewService.filter).pipe(
      switchMap(filter => this.analyticsService.calculateCategoryDistribution(filter))
    ),
    {initialValue: []}
  )

  config = computed<TableConfig<CategoryAnalyticsDistribution>>(() => ({
    dataKey: 'categoryId',
    paginator: true,
    rows: 5,
    stripedRows: true,
    styleClass: 'mt-4'
  }));

  columns = computed<TableColumn<CategoryAnalyticsDistribution>[]>(() => {
    this.translateService.lang();
    return [
      {field: 'categoryId', header: this.translateService.translate('app.category'), sortable: true},
      {field: 'totalIncome', header: this.translateService.translate('app.income')},
      {field: 'totalExpense', header: this.translateService.translate('app.expense')},
    ]
  })
}
