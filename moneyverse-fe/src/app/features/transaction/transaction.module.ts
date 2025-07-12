import {NgModule} from '@angular/core';
import {TagManagementComponent} from './tag/pages/tag-management/tag-management.component';
import {TransactionRoutingModule} from './transaction-rounting.module';
import {CommonModule} from '@angular/common';

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
