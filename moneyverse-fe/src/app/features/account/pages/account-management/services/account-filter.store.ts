import {Account, AccountCriteria} from '../../../account.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {AccountService} from "../../../services/account.service";
import {computed, effect, inject} from '@angular/core';
import {AuthService} from '../../../../../core/auth/auth.service';
import {ToastService} from '../../../../../shared/services/toast.service';
import {AccountStore} from '../../../services/account.store';
import {rxMethod} from "@ngrx/signals/rxjs-interop";
import {switchMap, tap} from 'rxjs';
import {TranslationService} from '../../../../../shared/services/translation.service';

interface AccountFilterState {
  criteria: AccountCriteria;
  accounts: Account[];
}

const initialState: AccountFilterState = {
  criteria: {},
  accounts: []
};

export const AccountFilterStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withMethods((store) => {
    const accountService = inject(AccountService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const translateService = inject(TranslationService);

    const loadFilteredAccounts = rxMethod<AccountCriteria | void>((criteria$) =>
      criteria$.pipe(
        switchMap((criteriaArg) => {
          const criteria = criteriaArg ?? store.criteria();
          return accountService.getAccounts(authService.user().userId, criteria).pipe(
            tap({
              next: (accounts) => patchState(store, {accounts}),
              error: () => toastService.error(translateService.translate('app.message.account.load.error'))
            })
          )
        })
      )
    );

    return {
      updateFilters(criteria: AccountCriteria) {
        patchState(store, {criteria});
        loadFilteredAccounts(criteria);
      },

      resetFilters() {
        patchState(store, {criteria: {}})
        loadFilteredAccounts({})
      },

      loadFilteredAccounts,

    };

  }),

  withComputed((store) => ({
    criteria: computed(() => store.criteria()),
    accounts: computed(() => store.accounts()),
    activeFiltersCount: computed(() => {
      const criteria = store.criteria();
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
    })
  })),

  withHooks((store) => {
    const accountStore = inject(AccountStore);

    return {
      onInit() {
        effect(() => {
          accountStore.accounts();
          if (store.criteria()) {
            store.loadFilteredAccounts(store.criteria());
          }
        });
      }
    }
  })
)
