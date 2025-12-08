import {computed, effect, inject} from '@angular/core';
import {Account, AccountCategory, AccountRequest} from '../account.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {AccountService} from './account.service';
import {AuthService} from '../../../core/auth/auth.service';
import {ToastService} from '../../../shared/services/toast.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {debounceTime, EMPTY, filter, merge, Subscription, switchMap, tap} from 'rxjs';
import {AccountEventService} from '../pages/account-management/services/account-event.service';
import {SystemService} from '../../../core/services/system.service';
import {TranslationService} from '../../../shared/services/translation.service';

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
    const translateService = inject(TranslationService);

    const loadAccounts = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.accounts().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.user().userId;
          return accountService.getAccounts(userId, {}).pipe(
            tap({
              next: (accounts) => patchState(store, {accounts: accounts}),
              error: () => toastService.error(translateService.translate('app.message.account.load.error'))
            })
          )
        })
      )
    );

    return {

      loadAccountCategories: rxMethod<boolean | void>((trigger) =>
        trigger.pipe(
          switchMap((force = false) => {
            if (!force && store.accountCategories().length > 0) {
              return EMPTY;
            }
            return accountService.getAccountCategories().pipe(
              tap({
                next: (data) => patchState(store, { accountCategories: data }),
                error: () => toastService.error(translateService.translate('app.message.accountCategories.load.error'))
              })
            );
          })
        )
      ),

      loadAccounts,

      createAccount: rxMethod<AccountRequest>((request$) =>
        request$.pipe(
          switchMap((request) => accountService.createAccount(request)),
          tap({
            next: () => {
              toastService.success(translateService.translate('app.message.account.create.success'));
              loadAccounts(true);
            },
            error: () => toastService.error(translateService.translate('app.message.account.create.error'))
          })
        )
      ),

      updateAccount: rxMethod<{ accountId: string, request: AccountRequest }>((request$) =>
        request$.pipe(
          switchMap(({accountId, request}) => accountService.updateAccount(accountId, request)),
          tap({
            next: () => {
              toastService.success(translateService.translate('app.message.account.update.success'));
              loadAccounts(true);
            },
            error: () => toastService.error(translateService.translate('app.message.account.update.error'))
          })
        )
      ),

      deleteAccount: rxMethod<string>((accountId$) =>
        accountId$.pipe(
          switchMap(accountId => accountService.deleteAccount(accountId)),
          tap({
              next: () => {
                toastService.success(translateService.translate('app.message.account.delete.success'));
                loadAccounts(true);
              },
              error: () => toastService.error(translateService.translate('app.message.account.delete.error'))
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
    accountsMap: computed(() => new Map(store.accounts().map(account => [account.accountId, account]))),
    accountsCategoryMap: computed(() => new Map(
      store.accountCategories().map(category => [
        Number(category.accountCategoryId),
        category
      ])
    )),
  })),

  withHooks((store) => {
    const systemService = inject(SystemService);
    const eventService = inject(AccountEventService);
    const subscriptions = new Subscription();

    return {
      onInit() {
        effect(() => {
          const translationsReady = systemService.translationsReady();
          if (translationsReady) {
            store.loadAccountCategories();
            store.loadAccounts(true);
          }
        });

        effect(() => {
          const lang = systemService.languageChanged();
          if (lang) {
            store.loadAccountCategories(true);
            store.loadAccounts(true);
          }
        })

        const reloadEvents$ = merge(
          eventService.onAccountCreated(),
          eventService.onAccountUpdated(),
          eventService.onAccountDeleted()
        );
        subscriptions.add(
          reloadEvents$.subscribe(() => store.loadAccounts(true))
        )
      },
      onDestroy() {
        subscriptions.unsubscribe();
      },
    };
  })
)
