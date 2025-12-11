import {Component} from '@angular/core';
import {TransactionKpiComponent} from './components/transaction-kpi/transaction-kpi.component';
import {
  TransactionDistributionChartComponent
} from './components/transaction-distribution-chart/transaction-distribution-chart.component';
import {TransactionTrendChartComponent} from './components/transaction-trend-chart/transaction-trend-chart.component';
import {
  TransactionAnalyticsTransactionTableDialogComponent
} from './components/transaction-analytics-transaction-table-dialog/transaction-analytics-transaction-table-dialog.component';
import {Card} from 'primeng/card';

@Component({
  selector: 'app-transaction-analytics',
  imports: [
    TransactionKpiComponent,
    TransactionDistributionChartComponent,
    TransactionTrendChartComponent,
    TransactionAnalyticsTransactionTableDialogComponent,
    Card
  ],
  template: `
    <div class="flex flex-col gap-4">
      <app-transaction-kpi/>
      <p-card>
        <app-transaction-distribution-chart (onChartClick)="transactionTableDialog.open($event)"/>
      </p-card>
      <p-card>
        <app-transaction-trend-chart (onChartClick)="transactionTableDialog.open($event)"/>
      </p-card>
    </div>
    <app-transaction-analytics-transaction-table-dialog #transactionTableDialog/>
  `
})
export class TransactionAnalyticsComponent {

}
