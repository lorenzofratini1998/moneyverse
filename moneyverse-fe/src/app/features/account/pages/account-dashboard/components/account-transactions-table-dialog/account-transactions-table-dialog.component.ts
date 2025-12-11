import {Component, viewChild} from '@angular/core';
import {Account} from '../../../../account.model';
import {AccountTransactionsTableComponent} from '../account-transactions-table/account-transactions-table.component';
import {
  AnalyticsTransactionDialogComponent
} from '../../../../../analytics/components/analytics-transaction-dialog/analytics-transaction-dialog.component';

@Component({
  selector: 'app-account-transactions-table-dialog',
  imports: [
    AccountTransactionsTableComponent,
    AnalyticsTransactionDialogComponent
  ],
  template: `
    <app-analytics-transaction-dialog
      [tableTemplate]="accountTableTemplate">

      <ng-template #accountTableTemplate let-item>
        <app-account-transactions-table [data]="item"/>
      </ng-template>
    </app-analytics-transaction-dialog>
  `
})
export class AccountTransactionsTableDialogComponent {
  protected dialog = viewChild.required<AnalyticsTransactionDialogComponent<Account>>(AnalyticsTransactionDialogComponent<Account>);

  open(item?: Account[]) {
    this.dialog().open(item);
  }
}
