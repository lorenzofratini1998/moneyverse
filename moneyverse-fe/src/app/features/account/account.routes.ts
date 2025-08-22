import {AccountAnalyticsComponent} from './pages/account-dashboard/account-analytics.component';
import {AccountManagementComponent} from './pages/account-management/account-management.component';
import {Routes} from '@angular/router';

export default [
  {path: '', redirectTo: 'dashboard', pathMatch: 'full'},
  {path: 'dashboard', component: AccountAnalyticsComponent},
  {path: 'manage', component: AccountManagementComponent}
] as Routes;
