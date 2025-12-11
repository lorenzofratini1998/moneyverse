import {Component, computed, inject, input, signal} from '@angular/core';
import {TableColumn, TableConfig} from '../../../../../../shared/models/table.model';
import {Transaction, Transfer} from '../../../../transaction.model';
import {TableComponent} from '../../../../../../shared/components/table/table.component';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {AccountStore} from '../../../../../account/services/account.store';
import {CellTemplateDirective} from '../../../../../../shared/directives/cell-template.directive';
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Component({
  selector: 'app-transfer-table',
  imports: [
    TableComponent,
    CellTemplateDirective,
    CurrencyPipe
  ],
  templateUrl: './transfer-table.component.html'
})
export class TransferTableComponent {

  transfer = input.required<Transfer>();

  transferTransactions = computed(() => [this.transfer().transactionFrom, this.transfer().transactionTo]);

  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly accountStore = inject(AccountStore);
  private readonly translateService = inject(TranslationService);

  config = computed<TableConfig<Transaction>>(() => ({
    lazy: true
  } as TableConfig<Transaction>))

  columns = computed<TableColumn<Transaction>[]>(() => {
    this.translateService.lang();
    return [
      {field: 'date', header: this.translateService.translate('app.date')},
      {field: 'description', header: this.translateService.translate('app.description')},
      {field: 'amount', header: this.translateService.translate('app.amount')},
      {field: 'normalizedAmount', header: this.translateService.translate('app.normalizedAmount')},
      {field: 'accountId', header: this.translateService.translate('app.account')},
    ]
  })
}
