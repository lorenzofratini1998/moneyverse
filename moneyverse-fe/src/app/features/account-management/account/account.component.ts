import {Component, inject, signal, ViewChild} from '@angular/core';
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
import {SvgComponent} from '../../../shared/components/svg/svg.component';
import {IconsEnum} from "../../../shared/models/icons.model";
import {AccountStore} from '../account.store';

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
    SvgComponent,

  ],
  templateUrl: './account.component.html'
})
export class AccountComponent {
  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
  private readonly accountService = inject(AccountService);
  private readonly currencyService = inject(CurrencyService);
  private readonly preferenceService = inject(PreferenceService);
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);

  private readonly refreshAccounts$ = new Subject<void>();
  private readonly userId$ = this.authService.getUserId().pipe(shareReplay(1));

  @ViewChild(AccountFormComponent) accountForm!: AccountFormComponent;

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
        this.messageService.showMessage({
          type: ToastEnum.SUCCESS,
          message: this.accountStore.selectedAccount() !== null ? 'Account updated successfully.' : 'Account created successfully.'
        });
        this.accountForm.reset();
        this.refreshAccounts$.next();
      },
      error: () => {
        this.messageService.showMessage({
          type: ToastEnum.ERROR,
          message: this.accountStore.selectedAccount() !== null ? 'Error during the account update.' : 'Error during the account creation.'
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
