import {Component, inject} from '@angular/core';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {TransactionKpiService} from '../../services/transaction-kpi.service';
import {KpiComponent} from '../../../../../../shared/components/charts/kpi/kpi.component';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';

@Component({
  selector: 'app-transaction-kpi',
  imports: [
    KpiComponent,
    CurrencyPipe
  ],
  template: `
    @if (kpiService.data(); as kpi) {
      <div class="grid grid-cols-2 md:grid-cols-5 gap-4 max-w-6xl mx-auto">
        <app-kpi label="Number of transactions"
                 [value]="kpi.numberOfTransactions.count"
                 [variation]="kpi.numberOfTransactions.variation"/>
        <app-kpi label="Total income"
                 [value]="kpi.totalIncome.amount | currency: preferenceStore.userCurrency()"
                 [variation]="kpi.totalIncome.variation"/>
        <app-kpi label="Total expense"
                 [value]="kpi.totalExpense.amount | currency: preferenceStore.userCurrency()"
                 [variation]="kpi.totalExpense.variation"/>
        <app-kpi label="Average amount"
                 [value]="kpi.averageAmount.amount | currency: preferenceStore.userCurrency()"
                 [variation]="kpi.averageAmount.variation"/>
        <app-kpi label="90% quantile"
                 [value]="kpi.quantile90.amount | currency: preferenceStore.userCurrency()"
                 [variation]="kpi.quantile90.variation"/>
      </div>
    }
  `
})
export class TransactionKpiComponent {
  protected readonly preferenceStore = inject(PreferenceStore)
  protected readonly kpiService = inject(TransactionKpiService);
}
