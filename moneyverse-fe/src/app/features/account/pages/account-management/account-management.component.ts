import {Component, inject, ViewChild} from '@angular/core';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {AccountStore} from '../../account.store';
import {AccountService} from '../../account.service';
import {AuthService} from '../../../../core/auth/auth.service';
//import {MessageService} from '../../../../shared/services/message.service';
import {switchMap, take} from 'rxjs';
import {AccountFormComponent} from './components/account-form/account-form.component';
import {Account, AccountRequest} from '../../account.model';
import {ToastEnum} from '../../../../shared/components/toast/toast.component';
import {SvgComponent} from '../../../../shared/components/svg/svg.component';
import {AccountTableComponent} from './components/account-table/account-table.component';
import {AccountFilterComponent} from './components/account-filter/account-filter.component';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {Dialog} from 'primeng/dialog';
import {ConfirmationService, MessageService} from 'primeng/api';
import {Toast} from 'primeng/toast';
import {Panel} from 'primeng/panel';
import {Drawer} from 'primeng/drawer';
import {ButtonDirective} from 'primeng/button';

@Component({
  selector: 'app-account-management',
  imports: [
    AccountFormComponent,
    SvgComponent,
    AccountTableComponent,
    AccountFilterComponent,
    Dialog,
    Toast,
    Panel,
    Drawer,
    ButtonDirective
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
  private readonly confirmationService = inject(ConfirmationService);
  isFilterOpen = false;

  @ViewChild(AccountFormComponent) accountForm!: AccountFormComponent;
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;
  console: any;

  saveAccount(accountData: any): void {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => {
          if (this.accountStore.selectedAccount() !== null) {
            return this.accountService.updateAccount(this.accountStore.selectedAccount()!.accountId, this.createUpdateAccountRequest(accountData))
          } else {
            return this.accountService.createAccount(this.createAccountRequest(accountData, userId))
          }
        }
      )
    ).subscribe({
      next: () => {
        this.messageService.add({
          severity: ToastEnum.SUCCESS,
          detail: this.accountStore.selectedAccount() !== null ? 'Account updated successfully.' : 'Account created successfully.'
        });
        this.accountForm.reset();
        this.accountStore.refreshAccounts();
      },
      error: () => {
        this.messageService.add({
          severity: ToastEnum.ERROR,
          detail: this.accountStore.selectedAccount() !== null ? 'Error during the account update.' : 'Error during the account creation.'
        });
      }
    })
  }

  createAccountRequest(accountData: any, userId: string): AccountRequest {
    return {
      userId: userId,
      accountName: accountData.accountName,
      accountDescription: accountData.accountDescription,
      accountCategory: accountData.accountCategory,
      balance: accountData.balance,
      balanceTarget: accountData.target,
      currency: accountData.currency
    }
  }

  createUpdateAccountRequest(accountData: any): Partial<AccountRequest> {
    const updateRequest: Partial<AccountRequest> = {};
    updateRequest.accountName = accountData.accountName;
    updateRequest.balance = Number(accountData.balance);
    updateRequest.balanceTarget = Number(accountData.target);
    updateRequest.accountCategory = accountData.accountCategory;
    updateRequest.accountDescription = accountData.accountDescription;
    updateRequest.currency = accountData.currency;
    updateRequest.isDefault = accountData.isDefault;
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

  toggleFilter() {
    this.isFilterOpen = !this.isFilterOpen;
  }

  get isDialogVisible(): boolean {
    return this.accountStore.isFormOpen();
  }

  set isDialogVisible(value: boolean) {
    if (!value) {
      this.accountStore.closeForm();
    }
  }
}
