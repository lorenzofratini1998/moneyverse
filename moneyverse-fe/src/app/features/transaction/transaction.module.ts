import {NgModule} from '@angular/core';
import {TransactionRoutingModule} from './transaction-rounting.module';
import {CommonModule} from '@angular/common';
import {TagManagementComponent} from './pages/tag-management/tag-management.component';

@NgModule(({
  declarations: [],
  imports: [
    CommonModule,
    TagManagementComponent,
    TransactionRoutingModule
  ]
}))
export class TransactionModule {
}
