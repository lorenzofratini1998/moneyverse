import {Component, inject, ViewChild} from '@angular/core';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {AccountStore} from '../../account.store';
import {AccountService} from '../../account.service';
import {AuthService} from '../../../../core/auth/auth.service';

import {switchMap, take} from 'rxjs';
import {AccountFormDialogComponent} from './components/account-form-dialog/account-form-dialog.component';
import {Account, AccountForm, AccountFormData, AccountRequest} from '../../account.model';
import {ToastEnum} from '../../../../shared/components/toast/toast.component';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {AccountTableComponent} from './components/account-table/account-table.component';
import {AccountFilterDialogComponent} from './components/account-filter-dialog/account-filter-dialog.component';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {ConfirmationService, MessageService} from 'primeng/api';
import {Toast} from 'primeng/toast';
import {AccountFilterPanelComponent} from './components/account-filter-panel/account-filter-panel.component';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-account-management',
  imports: [
    AccountFormDialogComponent,
    SvgComponent,
    AccountTableComponent,
    AccountFilterDialogComponent,
    Toast,
    AccountFilterPanelComponent,
    Button
  ],
  templateUrl: './account-management.component.html',
  styleUrl: './account-management.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class AccountManagementComponent {
  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
  private readonly accountService = inject(AccountService);
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);

  @ViewChild(AccountFormDialogComponent) accountForm!: AccountFormDialogComponent;
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;
  @ViewChild(AccountFilterDialogComponent) accountFilterComponent!: AccountFilterDialogComponent;

  createAccount(formData: AccountFormData): void {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => this.accountService.createAccount(this.createAccountRequest(formData, userId)))
    ).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Account created successfully.'
        });
        this.accountStore.refreshAccounts();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the account creation.'
        });
      }
    })
  }

  private createAccountRequest(formData: AccountFormData, userId: string): AccountRequest {
    return {
      userId: userId,
      accountName: formData.accountName,
      accountDescription: formData.accountDescription,
      accountCategory: formData.accountCategory,
      balance: formData.balance,
      balanceTarget: formData.balanceTarget,
      currency: formData.currency
    }
  }

  editAccount(accountForm: AccountForm) {
    this.accountService.updateAccount(accountForm.accountId!, this.createUpdateAccountRequest(accountForm.formData)).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Account updated successfully.'
        });
        this.accountStore.refreshAccounts();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the account update.'
        });
      }
    })
  }

  private createUpdateAccountRequest(formData: AccountFormData): Partial<AccountRequest> {
    const updateRequest: Partial<AccountRequest> = {};
    updateRequest.accountName = formData.accountName;
    updateRequest.balance = Number(formData.balance);
    updateRequest.balanceTarget = Number(formData.balanceTarget);
    updateRequest.accountCategory = formData.accountCategory;
    updateRequest.accountDescription = formData.accountDescription;
    updateRequest.currency = formData.currency;
    updateRequest.isDefault = formData.isDefault;
    return updateRequest;
  }

  deleteAccount(account: Account) {
    this.accountService.deleteAccount(account.accountId).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: 'Account deleted successfully.'
        });
        this.accountStore.refreshAccounts();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: 'Error during the account deletion.'
        });
      }
    })
  }
}
