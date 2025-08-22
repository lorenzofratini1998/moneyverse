import {Component} from '@angular/core';
import {Category} from '../../../../category.model';
import {
  TransactionTableComponent
} from '../../../../../transaction/pages/transaction-management/components/transaction-table/transaction-table.component';
import {
  AnalyticsTransactionsTableComponent
} from '../../../../../analytics/components/analytics-transactions-table/analytics-transactions-table.component';
import {BoundCriteria} from '../../../../../../shared/models/criteria.model';

@Component({
  selector: 'app-category-analytics-transactions-table',
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
export class CategoryAnalyticsTransactionsTableComponent extends AnalyticsTransactionsTableComponent<Category> {

  protected getFilteredAccounts(): string[] | undefined {
    const dashboardFilters = this.dashboardStore.filter();
    return (dashboardFilters.accounts ?? []).length > 0 ? dashboardFilters.accounts : undefined
  }

  protected getFilteredCategories(): string[] | undefined {
    return this.data().map(cat => cat.categoryId)
  }

  protected override getAmountBoundCriteria(): BoundCriteria | undefined {
    return undefined;
  }
}
