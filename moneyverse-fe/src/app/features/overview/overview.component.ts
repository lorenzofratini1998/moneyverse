import {Component} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {OverviewChartComponent} from './components/overview-chart/overview-chart.component';
import {OverviewAccountComponent} from './components/overview-account/overview-account.component';
import {OverviewCategoryComponent} from './components/overview-category/overview-category.component';
import {OverviewTransactionComponent} from './components/overview-transaction/overview-transaction.component';

@Component({
  selector: 'app-overview',
  imports: [
    FormsModule,
    OverviewChartComponent,
    OverviewAccountComponent,
    OverviewCategoryComponent,
    OverviewTransactionComponent
  ],
  template: `
    <div class="flex flex-col gap-4">
      <app-overview-chart/>
      <app-overview-transaction/>
      <div class="flex flex-col lg:flex-row gap-4">
        <app-overview-account class="w-full"/>
        <app-overview-category class="w-full"/>
      </div>
    </div>
  `,
})
export class OverviewComponent {

}
