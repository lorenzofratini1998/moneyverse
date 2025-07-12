import {RouterModule, Routes} from '@angular/router';
import {TagManagementComponent} from './tag/pages/tag-management/tag-management.component';
import {NgModule} from '@angular/core';
import {TransactionContainerComponent} from './tag/pages/transaction-container/transaction-container.component';

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
        redirectTo: 'tags',
        pathMatch: 'full'
      },
      {
        path: 'tags',
        component: TagManagementComponent
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
