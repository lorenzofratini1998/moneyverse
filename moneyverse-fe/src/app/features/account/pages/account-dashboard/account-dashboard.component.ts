import {Component} from '@angular/core';
import {AccountPieChartComponent} from './components/account-pie-chart/account-pie-chart.component';
import {AccountFilterComponent} from '../account-management/components/account-filter/account-filter.component';
import {AccountKpiComponent} from './components/account-kpi/account-kpi.component';

@Component({
  selector: 'app-account-dashboard',
  imports: [
    AccountPieChartComponent,
    AccountFilterComponent,
    AccountKpiComponent
  ],
  templateUrl: './account-dashboard.component.html',
  styleUrl: './account-dashboard.component.scss'
})
export class AccountDashboardComponent {

}
