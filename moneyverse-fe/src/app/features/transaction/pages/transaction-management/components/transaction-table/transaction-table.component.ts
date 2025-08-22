import {Component, computed, inject, input, output, signal, viewChild} from '@angular/core';
import {TableModule} from 'primeng/table';
import {AccountStore} from '../../../../../account/services/account.store';
import {CategoryStore} from '../../../../../category/services/category.store';
import {Transaction, Transfer} from '../../../../transaction.model';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {DatePipe} from '@angular/common';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {TransactionFormDialogOptionsEnum} from '../transaction-form-dialog/transaction-form-dialog.component';
import {TransferTableDialogComponent} from '../transfer-table-dialog/transfer-table-dialog.component';
import {ChipComponent} from '../../../../../../shared/components/chip/chip.component';
import {TableAction, TableColumn, TableConfig} from '../../../../../../shared/models/table.model';
import {TableComponent} from '../../../../../../shared/components/table/table.component';
import {TableActionsComponent} from '../../../../../../shared/components/table-actions/table-actions.component';
import {CellTemplateDirective} from '../../../../../../shared/directives/cell-template.directive';
import {SubscriptionTableDialogComponent} from '../subscription-table-dialog/subscription-table-dialog.component';
import {TransactionTableService} from './transaction-table.service';
import {PageResponse} from '../../../../../../shared/models/common.model';

@Component({
  selector: 'app-transaction-table',
  imports: [
    TableModule,
    DatePipe,
    CurrencyPipe,
    TransferTableDialogComponent,
    ChipComponent,
    TableComponent,
    CellTemplateDirective,
    TableActionsComponent,
    SubscriptionTableDialogComponent
  ],
  templateUrl: './transaction-table.component.html'
})
export class TransactionTableComponent {

  transactions = input.required<PageResponse<Transaction>>();
  readonly = input<boolean>(false);
  config = input<Partial<TableConfig<Transaction>>>({})

  onDeleteTransaction = output<Transaction>();
  onDeleteTransfer = output<Transfer>();
  onEdit = output<{
    data: Transaction | Transfer,
    option: TransactionFormDialogOptionsEnum
  }>();
  onPage = output<any>();
  onSort = output<any>();

  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly accountStore = inject(AccountStore);
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly transactionTableService = inject(TransactionTableService);

  protected readonly Icons = IconsEnum;

  transferTableDialog = viewChild.required(TransferTableDialogComponent);
  subscriptionTableDialog = viewChild.required<SubscriptionTableDialogComponent>(SubscriptionTableDialogComponent);

  tableConfig = computed<TableConfig<Transaction>>(() => {
    const baseConfig: TableConfig<Transaction> = {
      currentPageReportTemplate: 'Showing {first} to {last} of {totalRecords} entries',
      customSort: true,
      dataKey: 'transactionId',
      lazy: true,
      paginator: true,
      rows: 25,
      rowsPerPageOptions: [10, 25, 50, 100],
      showCurrentPageReport: true,
      sortField: "date",
      sortOrder: -1,
      stripedRows: true,
      styleClass: 'mt-4',
      scrollable: true,
      totalRecords: this.transactions().metadata.totalElements
    }
    return {
      ...baseConfig,
      ...this.config()
    } as TableConfig<Transaction>;
  })

  columns = signal<TableColumn<Transaction>[]>([
    {field: 'date', header: 'Date', sortable: true},
    {field: 'description', header: 'Description'},
    {field: 'amount', header: 'Amount', sortable: true},
    {field: 'normalizedAmount', header: 'Normalized Amount'},
    {field: 'categoryId', header: 'Category'},
    {field: 'accountId', header: 'Account'},
    {field: 'tags', header: 'Tags'}
  ])

  actions = computed<TableAction<Transaction>[]>(() => [
    {
      icon: IconsEnum.PENCIL,
      severity: "secondary",
      click: (tx) => this.onClickEdit(tx)
    },
    {
      icon: IconsEnum.TRASH,
      severity: "danger",
      click: (tx) => this.confirmDelete(tx)
    },
    {
      icon: IconsEnum.ARROW_LEFT_RIGHT,
      severity: "info",
      visible: (tx) => tx.transferId != null,
      click: (tx) => this.transferDetails(tx.transferId)
    },
    {
      icon: IconsEnum.CALENDAR_SYNC,
      severity: "info",
      visible: (tx) => tx.subscriptionId != null,
      click: (tx) => this.subscriptionDetails(tx.subscriptionId)
    }
  ])

  confirmDelete(transaction: Transaction) {
    this.transactionTableService.confirmDelete(
      transaction,
      (tx) => this.onDeleteTransaction.emit(tx),
      (transfer) => this.onDeleteTransfer.emit(transfer)
    );
  }

  onClickEdit(transaction: Transaction) {
    this.transactionTableService.loadForEdit(transaction, (payload) => this.onEdit.emit(payload));
  }

  transferDetails(transferId: string) {
    this.transactionTableService.openTransferDetails(
      transferId,
      (transfer) => this.transferTableDialog().open(transfer)
    );
  }

  subscriptionDetails(subscriptionId: string) {
    this.transactionTableService.openSubscriptionDetails(
      subscriptionId,
      (subscription) => this.subscriptionTableDialog().open(subscription)
    );
  }
}
