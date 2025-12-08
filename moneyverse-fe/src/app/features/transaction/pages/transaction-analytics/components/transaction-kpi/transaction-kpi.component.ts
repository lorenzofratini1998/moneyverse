import {Component, inject} from '@angular/core';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {TransactionKpiService} from '../../services/transaction-kpi.service';
import {KpiComponent} from '../../../../../../shared/components/charts/kpi/kpi.component';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-transaction-kpi',
  imports: [
    KpiComponent,
    CurrencyPipe,
    TranslatePipe
  ],
  template: `
    @if (kpiService.data(); as kpi) {
      <div class="grid grid-cols-2 md:grid-cols-5 gap-4 max-w-6xl mx-auto">
        <app-kpi [label]="'app.chart.numberOfTransactions' | translate"
                 [value]="kpi.numberOfTransactions.count"
                 [variation]="kpi.numberOfTransactions.variation"/>
        <app-kpi [label]="'app.chart.totalIncome' | translate"
                 [value]="kpi.totalIncome.amount | currency: preferenceStore.userCurrency()"
                 [variation]="kpi.totalIncome.variation"/>
        <app-kpi [label]="'app.chart.totalExpense' | translate"
                 [value]="kpi.totalExpense.amount | currency: preferenceStore.userCurrency()"
                 [variation]="kpi.totalExpense.variation"/>
        <app-kpi [label]="'app.chart.averageAmount' | translate"
                 [value]="kpi.averageAmount.amount | currency: preferenceStore.userCurrency()"
                 [variation]="kpi.averageAmount.variation"/>
        <app-kpi [label]="'app.chart.quantile90' | translate"
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
