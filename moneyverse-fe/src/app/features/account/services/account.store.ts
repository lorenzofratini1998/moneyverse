import {computed, inject} from '@angular/core';
import {Account, AccountCategory, AccountRequest} from '../account.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {AccountService} from './account.service';
import {AuthService} from '../../../core/auth/auth.service';
import {ToastService} from '../../../shared/services/toast.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {debounceTime, filter, switchMap, tap} from 'rxjs';
import {AccountEventService} from '../pages/account-management/services/account-event.service';

interface AccountStoreState {
  accountCategories: AccountCategory[];
  accounts: Account[];
}

const initialState: AccountStoreState = {
  accountCategories: [],
  accounts: []
};

export const AccountStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  withMethods((store) => {
    const accountService = inject(AccountService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);

    const loadAccounts = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.accounts().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.authenticatedUser.userId;
          return accountService.getAccounts(userId, {}).pipe(
            tap({
              next: (accounts) => patchState(store, {accounts: accounts}),
              error: () => toastService.error('Failed to load accounts')
            })
          )
        })
      )
    );

    return {

      loadAccountCategories: rxMethod<void>((trigger) =>
        trigger.pipe(
          filter(() => store.accountCategories().length === 0),
          switchMap(() => accountService.getAccountCategories()),
          tap({
            next: (data) => patchState(store, {accountCategories: data}),
            error: () => toastService.error('Failed to load categories')
          })
        )
      ),

      loadAccounts,

      createAccount: rxMethod<AccountRequest>((request$) =>
        request$.pipe(
          switchMap((request) => accountService.createAccount(request)),
          tap({
            next: () => {
              toastService.success('Account created successfully');
              loadAccounts(true);
            },
            error: () => toastService.error('Failed to create account')
          })
        )
      ),

      updateAccount: rxMethod<{ accountId: string, request: AccountRequest }>((request$) =>
        request$.pipe(
          switchMap(({accountId, request}) => accountService.updateAccount(accountId, request)),
          tap({
            next: () => {
              toastService.success('Account updated successfully');
              loadAccounts(true);
            },
            error: () => toastService.error('Failed to update account')
          })
        )
      ),

      deleteAccount: rxMethod<string>((accountId$) =>
        accountId$.pipe(
          switchMap(accountId => accountService.deleteAccount(accountId)),
          tap({
              next: () => {
                toastService.success('Account deleted successfully');
                loadAccounts(true);
              },
              error: () => toastService.error('Failed to delete account')
            }
          )
        )
      ),
    }
  }),

  withComputed((store) => ({
    categories: computed(() => store.accountCategories()),
    accounts: computed(() => store.accounts()),
    defaultAccount: computed(() => store.accounts().find(acc => acc.default)),
    accountsMap: computed(() => new Map(store.accounts().map(account => [account.accountId, account])))
  })),

  withHooks((store) => {
    const eventService = inject(AccountEventService);

    return {
      onInit() {
        store.loadAccountCategories();
        store.loadAccounts(true);
        eventService.connect().subscribe({
          error: (error) => console.log('SSE connection error: ', error)
        })
        eventService.onAccountCreated().subscribe(() => store.loadAccounts(true));
        eventService.onAccountUpdated().subscribe(() => store.loadAccounts(true));
        eventService.onAccountDeleted().subscribe(() => store.loadAccounts(true));
      },
      onDestroy() {
        eventService.disconnect();
      },
    };
  })
)
