import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {TransactionContainerComponent} from './pages/transaction-container/transaction-container.component';
import {TagManagementComponent} from './pages/tag-management/tag-management.component';
import {TransactionManagementComponent} from './pages/transaction-management/transaction-management.component';
import {SubscriptionManagementComponent} from './pages/subscription-management/subscription-management.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'tags',
    pathMatch: 'full'
  },
  {
    path: '',
    component: TransactionContainerComponent,
    children: [
      {
        path: '',
        redirectTo: 'manage',
        pathMatch: 'full'
      },
      {
        path: 'manage',
        component: TransactionManagementComponent
      },
      {
        path: 'tags',
        component: TagManagementComponent
      },
      {
        path: 'subscriptions',
        component: SubscriptionManagementComponent
      }
    ]
  }
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TransactionRoutingModule {
}
