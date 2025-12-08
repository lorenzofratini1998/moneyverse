import {patchState, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {computed, inject} from '@angular/core';
import {AccountStore} from '../../account/services/account.store';
import {CategoryStore} from '../../category/services/category.store';
import {AuthService} from '../../../core/auth/auth.service';
import {DashboardFilter} from '../analytics.models';

interface AnalyticsState {
  filter: DashboardFilter
}

function createInitialState(userId: string): AnalyticsState {
  return {
    filter: {
      periodFormat: 'year',
      period: {
        startDate: new Date(new Date().getFullYear(), 0, 1),
        endDate: new Date(new Date().getFullYear(), 11, 31)
      },
      comparePeriodFormat: 'none',
      userId: userId
    }
  };
}

export const DashboardStore = signalStore(
  {providedIn: 'root'},

  withState(() => {
    const authService = inject(AuthService);
    const userId = authService.user().userId;
    return createInitialState(userId);
  }),

  withMethods((store) => {
    const authService = inject(AuthService);
    const user = authService.user().userId;

    return {
      updateFilter(filter: Partial<DashboardFilter>) {
        patchState(store, (state) => ({
          filter: {...state.filter, ...filter}
        }))
      },

      resetFilter() {
        patchState(store, createInitialState(user));
      }
    }
  }),

  withComputed((store) => {
    const accountStore = inject(AccountStore);
    const categoryStore = inject(CategoryStore);
    return {
      filter: computed(() => store.filter()),
      selectedAccounts: computed(() => (store.filter().accounts ?? []).map(acc => accountStore.accountsMap().get(acc)!)),
      selectedCategories: computed(() => (store.filter().categories ?? []).map(cat => categoryStore.categoriesMap().get(cat)!)),
      activeFiltersCount: computed(() => {
        const filter = store.filter();
        let count = 0;

        if (filter.accounts && filter.accounts.length > 0) count++;
        if (filter.categories && filter.categories.length > 0) count++;
        if (filter.period) count++;
        if (filter.comparePeriod) count++;
        if (filter.currency) count++;

        return count;
      })
    }
  })
)
