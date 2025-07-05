import {RouterModule, Routes} from '@angular/router';
import {AccountManagementComponent} from './pages/account-management/account-management.component';
import {NgModule} from '@angular/core';
import {AccountContainerComponent} from './pages/account-container/account-container.component';
import {AccountDashboardComponent} from './pages/account-dashboard/account-dashboard.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: '',
    component: AccountContainerComponent,
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: AccountDashboardComponent,
      },
      {
        path: 'manage',
        component: AccountManagementComponent
      }
    ]
  },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AccountRoutingModule {
}
