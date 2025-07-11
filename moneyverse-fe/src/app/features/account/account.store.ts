import {computed, inject} from '@angular/core';
import {Account, AccountCategory, AccountCriteria} from './account.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {AccountService} from './account.service';
import {AuthService} from '../../core/auth/auth.service';

export interface AccountStoreState {
  accountCriteria: AccountCriteria;
  accountCategories: AccountCategory[];
  filteredAccounts: Account[];
  accounts: Account[];
  error: string | null;
}

const initialState: AccountStoreState = {
  accountCriteria: {},
  accountCategories: [],
  filteredAccounts: [],
  accounts: [],
  error: null
};

export const AccountStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  withMethods((store) => {
    const accountService = inject(AccountService);
    const authService = inject(AuthService);

    return {
      updateCriteria(criteria: Partial<AccountCriteria>) {
        patchState(store, (state) => ({
          accountCriteria: {...state.accountCriteria, ...criteria}
        }));
        this.loadAccounts();
      },

      loadAccountCategories() {
        if (store.accountCategories().length > 0) {
          return;
        }
        accountService
          .getAccountCategories()
          .subscribe({
            next: (data) => {
              patchState(store, {
                accountCategories: data
              })
            },
            error: (_) => patchState(store, {
              error: 'Failed to load categories'
            })
          })
      },

      loadAccounts() {
        const userId = authService.getAuthenticatedUser().userId;
        const criteria = store.accountCriteria();

        accountService.getAccounts(
          userId,
          criteria
        ).subscribe({
          next: (accounts) => {
            const isEmptyCriteria = Object.keys(criteria).length === 0;
            patchState(store, (state) => ({
              filteredAccounts: accounts,
              accounts: isEmptyCriteria
                ? accounts
                : state.accounts
            }));
          },
          error: (_) => patchState(store, {
            error: 'Failed to load accounts',
          })
        });
      },

      resetFilters() {
        patchState(store, {
          accountCriteria: {}
        });
        this.loadAccounts();
      },

      refreshAccounts() {
        this.loadAccounts();
      }
    }
  }),

  withComputed((store) => ({
    categories: computed(() => store.accountCategories()),
    filteredAccounts: computed(() => store.filteredAccounts()),
    accounts: computed(() => store.accounts()),
    activeFiltersCount: computed(() => {
      const criteria = store.accountCriteria();
      let count = 0;

      if ((criteria.accountCategories ?? []).length > 0) count++;
      if ((criteria.currencies ?? []).length > 0) count++;
      if (criteria.balance &&
        (criteria.balance.lower !== null ||
          criteria.balance.upper !== null)) count++;
      if (criteria.balanceTarget &&
        (criteria.balanceTarget.lower !== null ||
          criteria.balanceTarget.upper !== null)) count++;
      if (criteria.isDefault !== undefined) count++;

      return count;
    }),
  })),

  withHooks({
    onInit: (store) => {
      store.loadAccountCategories();
      store.loadAccounts();
    }
  })
)
