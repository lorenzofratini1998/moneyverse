import {RouterModule, Routes} from '@angular/router';
import {CategoryContainerComponent} from './pages/category-container/category-container.component';
import {CategoryManagementComponent} from './pages/category-management/category-management.component';
import {NgModule} from '@angular/core';
import {CategoryDashboardComponent} from './pages/category-dashboard/category-dashboard.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'manage',
    pathMatch: 'full'
  },
  {
    path: '',
    component: CategoryContainerComponent,
    children: [
      {
        path: '',
        redirectTo: 'manage',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: CategoryDashboardComponent,
      },
      {
        path: 'manage',
        component: CategoryManagementComponent
      }
    ]
  }
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CategoryRoutingModule {
}
