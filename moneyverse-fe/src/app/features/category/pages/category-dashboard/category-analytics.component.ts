import {Component} from '@angular/core';
import {CategoryKpiComponent} from './components/category-kpi/category-kpi.component';
import {
  CategoryDistributionChartComponent
} from './components/category-distribution-chart/category-distribution-chart.component';
import {CategoryTrendChartComponent} from './components/category-trend-chart/category-trend-chart.component';
import {
  CategoryAnalyticsTransactionTableDialogComponent
} from './components/category-analytics-transaction-table-dialog/category-analytics-transaction-table-dialog.component';

@Component({
  selector: 'app-category-dashboard',
  imports: [
    CategoryKpiComponent,
    CategoryDistributionChartComponent,
    CategoryTrendChartComponent,
    CategoryAnalyticsTransactionTableDialogComponent
  ],
  template: `
    <div class="flex flex-col justify-center items-center gap-4">
      <app-category-kpi/>
      <app-category-distribution-chart class="w-full" (onChartClick)="analyticsTransactionTableDialog.open($event)"/>
      <app-category-trend-chart class="w-full h-full" (onChartClick)="analyticsTransactionTableDialog.open($event)"/>
      <app-category-analytics-transaction-table-dialog #analyticsTransactionTableDialog/>
    </div>
  `
})
export class CategoryAnalyticsComponent {

}
