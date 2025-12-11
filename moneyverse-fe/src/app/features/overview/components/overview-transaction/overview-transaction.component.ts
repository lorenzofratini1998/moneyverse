import {Component, computed, effect, inject} from '@angular/core';
import {TransactionsTableStore} from '../../../../shared/stores/transactions-table.store';
import {TableConfig} from '../../../../shared/models/table.model';
import {Transaction, TransactionCriteria, TransactionSortAttributeEnum} from '../../../transaction/transaction.model';
import {Direction} from '../../../../shared/models/criteria.model';
import {
  TransactionTableComponent
} from '../../../transaction/pages/transaction-management/components/transaction-table/transaction-table.component';
import {Card} from 'primeng/card';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-overview-transaction',
  imports: [
    TransactionTableComponent,
    Card,
    TranslatePipe
  ],
  template: `
    <p-card>
      <ng-template #title>
        <h3>{{ 'app.recentTransactions' | translate }}</h3>
      </ng-template>
      <app-transaction-table [transactions]="tableStore.transactions()"
                             [config]="tableConfig()"
                             [readonly]="true"/>
    </p-card>
  `
})
export class OverviewTransactionComponent {
  protected readonly tableStore = inject(TransactionsTableStore);

  tableConfig = computed<TableConfig<Transaction>>(() => ({
    customSort: false,
    lazy: true,
    paginator: false,
    rows: 10,
    sortField: "date",
    sortOrder: -1,
    stripedRows: true,
    styleClass: 'mt-4',
    scrollable: true,
    tableStyle: {'min-width': '70rem'}
  }))

  criteria = computed<TransactionCriteria>(() => {
    return {
      page: {offset: 0, limit: 10},
      sort: {
        attribute: TransactionSortAttributeEnum.DATE,
        direction: Direction.DESC
      }
    };
  });

  constructor() {
    effect(() => {
      this.tableStore.load(this.criteria())
    });
  }

}
