import {Component} from '@angular/core';
import {
  AnalyticsTransactionsTableComponent
} from '../../../../../analytics/components/analytics-transactions-table/analytics-transactions-table.component';
import {BoundCriteria} from "../../../../../../shared/models/criteria.model";
import {
  TransactionTableComponent
} from '../../../transaction-management/components/transaction-table/transaction-table.component';

@Component({
  selector: 'app-transaction-analytics-transactions-table',
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
export class TransactionAnalyticsTransactionsTableComponent extends AnalyticsTransactionsTableComponent<BoundCriteria> {
  protected override getFilteredAccounts(): string[] | undefined {
    const dashboardFilters = this.dashboardStore.filter();
    return (dashboardFilters.accounts ?? []).length > 0 ? dashboardFilters.accounts : undefined
  }

  protected override getFilteredCategories(): string[] | undefined {
    const dashboardFilters = this.dashboardStore.filter();
    return (dashboardFilters.categories ?? []).length > 0 ? dashboardFilters.categories : undefined
  }

  protected override getAmountBoundCriteria(): BoundCriteria | undefined {
    return this.data()[0] ?? undefined;
  }

}
