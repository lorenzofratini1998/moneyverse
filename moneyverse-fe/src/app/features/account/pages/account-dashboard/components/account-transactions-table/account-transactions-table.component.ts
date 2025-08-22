import {Component} from '@angular/core';
import {Account} from '../../../../account.model';
import {
  TransactionTableComponent
} from '../../../../../transaction/pages/transaction-management/components/transaction-table/transaction-table.component';
import {
  AnalyticsTransactionsTableComponent
} from '../../../../../analytics/components/analytics-transactions-table/analytics-transactions-table.component';
import {BoundCriteria} from '../../../../../../shared/models/criteria.model';

@Component({
  selector: 'app-account-transactions-table',
  imports: [
    TransactionTableComponent
  ],
  template: `
    <app-transaction-table [transactions]="tableStore.transactions()"
                           [config]="tableConfig()"
                           [readonly]="true"
                           (onPage)="tableStore.onPage($event)"
                           (onSort)="tableStore.onSort($event)"/>
  `
})
export class AccountTransactionsTableComponent extends AnalyticsTransactionsTableComponent<Account> {

  protected override getFilteredAccounts(): string[] | undefined {
    return this.data().map(acc => acc.accountId) ?? null;
  }

  protected override getFilteredCategories(): string[] | undefined {
    const dashboardFilters = this.dashboardStore.filter();
    return (dashboardFilters.categories ?? []).length > 0 ? dashboardFilters.categories : undefined
  }

  protected override getAmountBoundCriteria(): BoundCriteria | undefined {
    return undefined;
  }

}
