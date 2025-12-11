import {Component, inject} from '@angular/core';
import {CurrencyPipe} from "@angular/common";
import {AccountStore} from '../../../../services/account.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {KpiComponent} from '../../../../../../shared/components/charts/kpi/kpi.component';
import {AccountKpiChartService} from '../../services/account-kpi-chart.service';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-account-kpi',
  imports: [
    CurrencyPipe,
    KpiComponent,
    TranslatePipe
  ],
  template: `
    @if (accountKpiService.data(); as kpi) {
      <div class="grid grid-cols-3 gap-4 max-w-6xl mx-auto">
        <app-kpi [label]="'app.chart.total' | translate"
                 [value]="(kpi.totalAmount.amount | currency: preferenceStore.userCurrency()) ?? 'N/A'"
                 [variation]="kpi.totalAmount.variation"/>
        <app-kpi [label]="'app.chart.activeAccounts' | translate"
                 [value]="kpi.numberOfActiveAccounts.count"/>
        <app-kpi [label]="'app.chart.mostUsedAccount' | translate"
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
