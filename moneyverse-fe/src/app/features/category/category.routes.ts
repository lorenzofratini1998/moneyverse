import {CategoryAnalyticsComponent} from './pages/category-dashboard/category-analytics.component';
import {Routes} from '@angular/router';
import {CategoryManagementComponent} from './pages/category-management/category-management.component';
import {BudgetManagementComponent} from './pages/budget-management/budget-management.component';

export default [
  {path: '', redirectTo: 'analytics', pathMatch: 'full'},
  {path: 'analytics', component: CategoryAnalyticsComponent},
  {path: 'manage', component: CategoryManagementComponent},
  {path: 'budgeting', component: BudgetManagementComponent}
] as Routes;
