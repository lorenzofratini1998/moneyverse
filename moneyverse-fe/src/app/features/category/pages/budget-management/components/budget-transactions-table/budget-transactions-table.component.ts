import {Component, computed, effect, inject, input} from '@angular/core';
import {Budget} from '../../../../category.model';
import {TransactionCriteria} from '../../../../../transaction/transaction.model';
import {
  TransactionTableComponent
} from '../../../../../transaction/pages/transaction-management/components/transaction-table/transaction-table.component';
import {TransactionsTableStore} from '../../../../../../shared/stores/transactions-table.store';

@Component({
  selector: 'app-budget-transactions-table',
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
export class BudgetTransactionsTableComponent {
  budget = input.required<Budget>();
  protected readonly tableStore = inject(TransactionsTableStore);

  tableConfig = computed(() => ({rows: 10}));

  private criteria = computed<TransactionCriteria>(() => {
    const budget = this.budget();
    return {
      budget: budget.budgetId,
      categories: [budget.category.categoryId],
      page: this.tableStore.page(),
      sort: this.tableStore.sort()
    };
  });

  constructor() {
    effect(() => {
      this.tableStore.load(this.criteria())
    });
  }
}

