import {Component, inject} from '@angular/core';
import {CurrencyPipe} from "@angular/common";
import {AccountStore} from '../../../../services/account.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {KpiComponent} from '../../../../../../shared/components/charts/kpi/kpi.component';
import {AccountKpiChartService} from '../../services/account-kpi-chart.service';

@Component({
  selector: 'app-account-kpi',
  imports: [
    CurrencyPipe,
    KpiComponent
  ],
  template: `
    @if (accountKpiService.data(); as kpi) {
      <div class="grid grid-cols-3 gap-4 max-w-6xl mx-auto">
        <app-kpi label="Total amount"
                 [value]="(kpi.totalAmount.amount | currency: preferenceStore.userCurrency()) ?? 'N/A'"
                 [variation]="kpi.totalAmount.variation"/>
        <app-kpi label="Active accounts"
                 [value]="kpi.numberOfActiveAccounts.count"/>
        <app-kpi label="Most used account"
                 [value]="accountStore.accountsMap().get(kpi.mostUsedAccount)?.accountName ?? 'N/A'"/>
      </div>
    }
  `
})
export class AccountKpiComponent {

  protected readonly accountStore = inject(AccountStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly accountKpiService = inject(AccountKpiChartService);

}
