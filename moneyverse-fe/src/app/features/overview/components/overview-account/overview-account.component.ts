import {Component, computed, inject} from '@angular/core';
import {AccountStore} from '../../../account/services/account.store';
import {TableColumn, TableConfig} from '../../../../shared/models/table.model';
import {AccountAnalyticsDistribution} from '../../../account/pages/account-dashboard/models/account-analytics.model';
import {AnalyticsService} from '../../../../shared/services/analytics.service';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs';
import {OverviewService} from '../../services/overview.service';
import {CellTemplateDirective} from '../../../../shared/directives/cell-template.directive';
import {CurrencyPipe} from '../../../../shared/pipes/currency.pipe';
import {TableComponent} from '../../../../shared/components/table/table.component';
import {PreferenceStore} from '../../../../shared/stores/preference.store';
import {ChipComponent} from '../../../../shared/components/chip/chip.component';
import {Card} from 'primeng/card';
import {TranslatePipe} from '@ngx-translate/core';
import {TranslationService} from '../../../../shared/services/translation.service';

@Component({
  selector: 'app-overview-account',
  imports: [
    CellTemplateDirective,
    CurrencyPipe,
    TableComponent,
    ChipComponent,
    Card,
    TranslatePipe
  ],
  templateUrl: './overview-account.component.html'
})
export class OverviewAccountComponent {
  protected readonly accountStore = inject(AccountStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly overviewService = inject(OverviewService);
  private readonly translateService = inject(TranslationService);

  data = toSignal(
    toObservable(this.overviewService.filter).pipe(
      switchMap(filter => this.analyticsService.calculateAccountAnalyticsDistribution(filter))
    ),
    {initialValue: []}
  )

  config = computed<TableConfig<AccountAnalyticsDistribution>>(() => ({
    dataKey: 'accountId',
    paginator: true,
    rows: 3,
    stripedRows: true,
    styleClass: 'mt-4'
  }));

  columns = computed<TableColumn<AccountAnalyticsDistribution>[]>(() => {
    this.translateService.lang();

    return [
      {field: 'accountId', header: this.translateService.translate('app.account'), sortable: true},
      {field: 'totalIncome', header: this.translateService.translate('app.income')},
      {field: 'totalExpense', header: this.translateService.translate('app.expense')},
    ]
  });
}
