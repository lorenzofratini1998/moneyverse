import {CategoryAnalyticsComponent} from './pages/category-dashboard/category-analytics.component';
import {Routes} from '@angular/router';
import {CategoryManagementComponent} from './pages/category-management/category-management.component';
import {BudgetManagementComponent} from './pages/budget-management/budget-management.component';

export default [
  {path: '', redirectTo: 'dashboard', pathMatch: 'full'},
  {path: 'dashboard', component: CategoryAnalyticsComponent},
  {path: 'manage', component: CategoryManagementComponent},
  {path: 'budgeting', component: BudgetManagementComponent}
] as Routes;
