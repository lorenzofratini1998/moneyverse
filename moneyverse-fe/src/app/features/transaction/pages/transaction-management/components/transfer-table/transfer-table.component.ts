import {Component, computed, inject, input, signal} from '@angular/core';
import {TableColumn, TableConfig} from '../../../../../../shared/models/table.model';
import {Transaction, Transfer} from '../../../../transaction.model';
import {TableComponent} from '../../../../../../shared/components/table/table.component';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {AccountStore} from '../../../../../account/services/account.store';
import {CellTemplateDirective} from '../../../../../../shared/directives/cell-template.directive';

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

  config = computed<TableConfig<Transaction>>(() => ({
    lazy: true
  } as TableConfig<Transaction>))

  columns = signal<TableColumn<Transaction>[]>([
    {field: 'date', header: 'Date'},
    {field: 'description', header: 'Description'},
    {field: 'amount', header: 'Amount'},
    {field: 'normalizedAmount', header: 'Normalized Amount'},
    {field: 'accountId', header: 'Account'},
  ])
}
