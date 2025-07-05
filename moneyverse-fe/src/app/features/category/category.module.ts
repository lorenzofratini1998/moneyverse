import {NgModule} from "@angular/core";
import {CommonModule} from '@angular/common';
import {CategoryManagementComponent} from './pages/category-management/category-management.component';
import {CategoryRoutingModule} from './category-routing.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    CategoryManagementComponent,
    CategoryRoutingModule
  ]
})
export class CategoryModule {
}
