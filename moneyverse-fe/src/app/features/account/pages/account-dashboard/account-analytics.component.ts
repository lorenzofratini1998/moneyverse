import {Component} from '@angular/core';
import {
  AccountDistributionChartComponent
} from './components/account-distribution-chart/account-distribution-chart.component';
import {AccountKpiComponent} from './components/account-kpi/account-kpi.component';
import {
  AccountCategoryDistributionChartComponent
} from './components/account-category-distribution-chart/account-category-distribution-chart.component';
import {AccountTrendChartComponent} from './components/account-trend-chart/account-trend-chart.component';
import {
  AccountTransactionsTableDialogComponent
} from './components/account-transactions-table-dialog/account-transactions-table-dialog.component';

@Component({
  selector: 'app-account-analytics',
  imports: [
    AccountDistributionChartComponent,
    AccountKpiComponent,
    AccountCategoryDistributionChartComponent,
    AccountTrendChartComponent,
    AccountTransactionsTableDialogComponent
  ],
  template: `
    <div class="flex flex-col justify-center items-center gap-4">
      <app-account-kpi/>
      <div class="flex flex-col md:flex-row justify-center items-stretch gap-4 w-full">
        <div class="flex-1 min-h-[450px]">
          <app-account-pie-chart
            class="w-full h-full"
            (onChartClick)="accountTransactionsTableDialog.open($event)"
          />
        </div>
        <div class="flex-1 min-h-[450px]">
          <app-account-category-pie-chart
            class="w-full h-full"
            (onChartClick)="accountTransactionsTableDialog.open($event)"
          />
        </div>
      </div>
      <app-account-trend class="w-full h-full" (onChartClick)="accountTransactionsTableDialog.open($event)"/>
      <app-account-transactions-table-dialog #accountTransactionsTableDialog/>
    </div>
  `
})
export class AccountAnalyticsComponent {

}
