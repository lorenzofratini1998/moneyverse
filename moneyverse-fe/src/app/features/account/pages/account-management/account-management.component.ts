import {Component, computed, inject, viewChild} from '@angular/core';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {AccountStore} from '../../services/account.store';
import {AccountFormDialogComponent} from './components/account-form-dialog/account-form-dialog.component';
import {Account} from '../../account.model';
import {AccountFilterStore} from './services/account-filter.store';
import {AccountFormData} from './models/form.model';
import {AuthService} from '../../../../core/auth/auth.service';
import {ManagementComponent, ManagementConfig} from '../../../../shared/components/management/management.component';
import {AccountTableComponent} from './components/account-table/account-table.component';
import {AccountFilterPanelComponent} from './components/account-filter-panel/account-filter-panel.component';

@Component({
  selector: 'app-account-management',
  imports: [
    AccountFormDialogComponent,
    AccountFormDialogComponent,
    ManagementComponent,
    AccountTableComponent,
    AccountFilterPanelComponent
  ],
  templateUrl: './account-management.component.html'
})
export class AccountManagementComponent {
  protected readonly accountStore = inject(AccountStore);
  protected readonly accountFilterStore = inject(AccountFilterStore);
  private readonly authService = inject(AuthService);

  accountFormDialog = viewChild.required(AccountFormDialogComponent);

  managementConfig = computed<ManagementConfig>(() => (
    {
      title: 'Account Management',
      actions: [
        {
          icon: IconsEnum.REFRESH,
          variant: 'text',
          severity: 'secondary',
          action: () => this.accountStore.loadAccounts(true)
        },
        {
          icon: IconsEnum.PLUS,
          label: 'New Account',
          action: () => this.accountFormDialog().open()
        }
      ]
    }
  ));

  submit(formData: AccountFormData) {
    const accountId = formData.accountId;
    if (accountId) {
      this.accountStore.updateAccount({
        accountId,
        request: {...formData}
      });
    } else {
      this.accountStore.createAccount({
        ...formData,
        userId: this.authService.authenticatedUser.userId
      });
    }
  }

  deleteAccount(account: Account) {
    this.accountStore.deleteAccount(account.accountId);
  }
}
