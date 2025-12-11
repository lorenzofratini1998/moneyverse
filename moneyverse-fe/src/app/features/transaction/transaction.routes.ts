import {TransactionManagementComponent} from './pages/transaction-management/transaction-management.component';
import {TagManagementComponent} from './pages/tag-management/tag-management.component';
import {SubscriptionManagementComponent} from './pages/subscription-management/subscription-management.component';
import {Routes} from '@angular/router';
import {TransactionAnalyticsComponent} from './pages/transaction-analytics/transaction-analytics.component';

export default [
  {path: '', redirectTo: 'analytics', pathMatch: 'full'},
  {path: 'analytics', component: TransactionAnalyticsComponent},
  {path: 'manage', component: TransactionManagementComponent},
  {path: 'tags', component: TagManagementComponent},
  {path: 'subscriptions', component: SubscriptionManagementComponent}
] as Routes;
