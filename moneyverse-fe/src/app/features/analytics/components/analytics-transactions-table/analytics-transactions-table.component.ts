import {computed, Directive, effect, inject, input} from '@angular/core';
import {TransactionsTableStore} from '../../../../shared/stores/transactions-table.store';
import {DashboardStore} from '../../services/dashboard.store';
import {TableConfig} from '../../../../shared/models/table.model';
import {Transaction, TransactionCriteria} from '../../../transaction/transaction.model';
import {getUTCDate} from '../../../../shared/utils/date.utils';
import {BoundCriteria} from '../../../../shared/models/criteria.model';

@Directive()
export abstract class AnalyticsTransactionsTableComponent<T> {
  data = input.required<T[]>();

  protected readonly tableStore = inject(TransactionsTableStore);
  protected readonly dashboardStore = inject(DashboardStore);

  tableConfig = computed<TableConfig<Transaction>>(() => ({
    rows: 10,
    scrollable: true,
    tableStyle: {'min-width': '70rem'}
  }))

  criteria = computed<TransactionCriteria>(() => {
    const dashboardFilters = this.dashboardStore.filter();
    const startDate = dashboardFilters.period.startDate;
    const endDate = dashboardFilters.period.endDate;

    return {
      accounts: this.getFilteredAccounts(),
      categories: this.getFilteredCategories(),
      tags: (dashboardFilters.tags ?? []).length > 0 ? dashboardFilters.tags : undefined,
      date: {
        start: getUTCDate(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()),
        end: getUTCDate(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()),
      },
      amount: this.getAmountBoundCriteria(),
      page: this.tableStore.page(),
      sort: this.tableStore.sort()
    };
  });

  protected abstract getFilteredAccounts(): string[] | undefined;

  protected abstract getFilteredCategories(): string[] | undefined;

  protected abstract getAmountBoundCriteria(): BoundCriteria | undefined;

  constructor() {
    effect(() => {
      this.tableStore.load(this.criteria())
    });
  }
}
