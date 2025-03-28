import {Component, inject, signal} from '@angular/core';
import {Account, AccountCategory, AccountRequest} from '../account.model';
import {AccountService} from '../account.service';
import {AuthService} from '../../../core/auth/auth.service';
import {combineLatest, map, shareReplay, startWith, Subject, switchMap, take} from 'rxjs';
import {AccountSummaryComponent} from './components/account-summary/account-summary.component';
import {CurrencyService} from '../../../shared/services/currency.service';
import {PreferenceService} from '../../../shared/services/preference.service';
import {AccountFormComponent} from './components/account-form/account-form.component';
import {MessageService} from '../../../shared/services/message.service';
import {ToastComponent, ToastEnum} from '../../../shared/components/toast/toast.component';
import {AccountCardComponent} from './components/account-card/account-card.component';
import {AccountDetailComponent} from './components/account-detail/account-detail.component';
import {NgIf} from '@angular/common';
import {DialogComponent} from '../../../shared/components/dialog/dialog.component';
import {toSignal} from '@angular/core/rxjs-interop';
import {CurrencyDto} from '../../../shared/models/currencyDto';
import {PreferenceKey} from '../../../shared/models/preference.model';
import {SvgIconComponent} from 'angular-svg-icon';

@Component({
  selector: 'app-account',
  imports: [
    AccountSummaryComponent,
    AccountFormComponent,
    ToastComponent,
    AccountCardComponent,
    AccountDetailComponent,
    NgIf,
    DialogComponent,
    SvgIconComponent,

  ],
  templateUrl: './account.component.html'
})
export class AccountComponent {
  private readonly accountService = inject(AccountService);
  private readonly currencyService = inject(CurrencyService);
  private readonly preferenceService = inject(PreferenceService);
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);

  private readonly refreshAccounts$ = new Subject<void>();
  private readonly userId$ = this.authService.getUserId().pipe(shareReplay(1));

  accounts$ = toSignal(
    combineLatest([
      this.userId$,
      this.refreshAccounts$.pipe(startWith(null))
    ]).pipe(
      switchMap(([userId]) => this.accountService.getAccounts(userId).pipe(
        map((accounts: Account[]) => [...accounts].sort((a, b) => b.balance - a.balance))
      ))
    ),
    {initialValue: [] as Account[]}
  )
  accountCategories$ = toSignal(this.accountService.getAccountCategories(), {initialValue: [] as AccountCategory[]});
  currencies$ = toSignal(this.currencyService.getCurrencies(), {initialValue: [] as CurrencyDto[]});
  userCurrency$ = toSignal(this.userId$.pipe(
    switchMap(userId => this.preferenceService.getUserPreference(userId, PreferenceKey.CURRENCY))));

  selectedAccount = signal<Account | undefined>(undefined);
  showAccountFormDialog = signal<boolean>(false);
  accountToEdit = signal<Account | null>(null);

  showAccountDetails(account: Account): void {
    this.selectedAccount.set(account);
  }

  clearSelectedAccount(): void {
    this.selectedAccount.set(undefined);
  }

  getCategory(categoryName: string): AccountCategory {
    const category = this.accountCategories$().find(c => c.name === categoryName);
    if (!category) throw new Error(`Category not found: ${categoryName}`);
    return category;
  }

  openAddAccountDialog(): void {
    this.accountToEdit.set(null);
    this.showAccountFormDialog.set(true);
  }

  openEditAccountDialog(account: Account): void {
    this.accountToEdit.set(account);
    this.showAccountFormDialog.set(true);
  }

  saveAccount(accountData: any): void {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => {
          if (this.accountToEdit() !== null) {
            return this.accountService.updateAccount(this.accountToEdit()!.accountId, this.createUpdateAccountRequest(accountData))
          } else {
            return this.accountService.createAccount(this.createAccountRequest(accountData, userId))
          }
        }
      )
    ).subscribe({
      next: () => {
        this.refreshAccounts$.next();
        this.messageService.showMessage({
          type: ToastEnum.SUCCESS,
          message: this.accountToEdit() !== null ? 'Account updated successfully.' : 'Account created successfully.'
        });
        this.showAccountFormDialog.set(false);
      },
      error: () => {
        this.messageService.showMessage({
          type: ToastEnum.ERROR,
          message: this.accountToEdit() !== null ? 'Error during the account update.' : 'Error during the account creation.'
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

  closeAccountDialog(): void {
    this.showAccountFormDialog.set(false);
    this.accountToEdit.set(null);
  }

  deleteAccount(account: Account) {
    this.accountService.deleteAccount(account.accountId).subscribe({
      next: () => {
        this.refreshAccounts$.next();
        this.messageService.showMessage({
          type: ToastEnum.SUCCESS,
          message: 'Account deleted successfully.'
        });
        this.refreshAccounts$.next();
      },
      error: () => {
        this.messageService.showMessage({
          type: ToastEnum.ERROR,
          message: 'Error during the account deletion.'
        });
      }
    })
  }
}
