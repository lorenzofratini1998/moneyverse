import {Component, viewChild} from '@angular/core';
import {
  AnalyticsTransactionDialogComponent
} from '../../../../../analytics/components/analytics-transaction-dialog/analytics-transaction-dialog.component';
import {BoundCriteria} from '../../../../../../shared/models/criteria.model';
import {
  TransactionAnalyticsTransactionsTableComponent
} from '../transaction-analytics-transactions-table/transaction-analytics-transactions-table.component';

@Component({
  selector: 'app-transaction-analytics-transaction-table-dialog',
  imports: [
    AnalyticsTransactionDialogComponent,
    TransactionAnalyticsTransactionsTableComponent
  ],
  template: `
    <app-analytics-transaction-dialog
      [tableTemplate]="transactionTableTemplate">

      <ng-template #transactionTableTemplate let-item>
        <app-transaction-analytics-transactions-table [data]="item"/>
      </ng-template>
    </app-analytics-transaction-dialog>
  `
})
export class TransactionAnalyticsTransactionTableDialogComponent {
  dialog = viewChild.required<AnalyticsTransactionDialogComponent<BoundCriteria>>(AnalyticsTransactionDialogComponent<BoundCriteria>);

  open(item?: BoundCriteria[]) {
    this.dialog().open(item);
  }
}
