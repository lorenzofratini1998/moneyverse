import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AccountManagementComponent} from './pages/account-management/account-management.component';
import {AccountRoutingModule} from './account-routing.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    AccountManagementComponent,
    AccountRoutingModule
  ]
})
export class AccountModule {
}
