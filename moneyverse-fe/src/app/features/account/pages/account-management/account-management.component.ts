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
import {TranslationService} from '../../../../shared/services/translation.service';

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
  private readonly translateService = inject(TranslationService);

  accountFormDialog = viewChild.required(AccountFormDialogComponent);

  managementConfig = computed<ManagementConfig>(() => {
    this.translateService.lang();
    return {
      title: this.translateService.translate('app.manageAccounts'),
      actions: [
        {
          icon: IconsEnum.REFRESH,
          variant: 'text',
          severity: 'secondary',
          action: () => this.accountStore.loadAccounts(true)
        },
        {
          icon: IconsEnum.PLUS,
          label: this.translateService.translate('app.actions.newAccount'),
          action: () => this.accountFormDialog().open()
        }
      ]
    }
  });

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
        userId: this.authService.user().userId
      });
    }
  }

  deleteAccount(account: Account) {
    this.accountStore.deleteAccount(account.accountId);
  }
}
