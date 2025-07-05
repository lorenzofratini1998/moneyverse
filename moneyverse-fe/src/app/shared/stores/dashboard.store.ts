import {DashboardFilter} from '../models/dashboard.model';
import {patchState, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {computed, inject} from '@angular/core';
import {PeriodDashboardEnum} from '../../features/category/pages/category-dashboard/category-dashboard.model';
import {AccountStore} from '../../features/account/account.store';
import {CategoryStore} from '../../features/category/category.store';

interface DashboardState {
  filter: DashboardFilter
}

const initialState: DashboardState = {
  filter: {
    period: {
      period: PeriodDashboardEnum.MONTH,
      year: new Date().getFullYear(),
      month: new Date().getMonth()
    }
  }
}

export const DashboardStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  withMethods((store) => {
    return {
      updateFilter(filter: Partial<DashboardFilter>) {
        patchState(store, (state) => ({
          filter: {...state.filter, ...filter}
        }))
      },

      resetFilter() {
        patchState(store, {
          filter: {}
        })
      }
    }
  }),

  withComputed((store) => {
    const accountStore = inject(AccountStore);
    const categoryStore = inject(CategoryStore);
    return {
      filter: computed(() => store.filter()),
      selectedAccounts: computed(() => {
        const accountNames = store.filter().accounts || [];
        return accountStore.accounts().filter(acc => accountNames.includes(acc.accountName));
      }),
      selectedCategories: computed(() => {
        const categoryNames = store.filter().categories || [];
        return categoryStore.categories().filter(cat => categoryNames.includes(cat.categoryName));
      }),
      activeFiltersCount: computed(() => {
        const filter = store.filter();
        let count = 0;

        if (filter.accounts && filter.accounts.length > 0) count++;
        if (filter.categories && filter.categories.length > 0) count++;
        if (filter.period) count++;
        if (filter.comparePeriod) count++;
        if (filter.amount &&
          (filter.amount.lower !== null ||
            filter.amount.upper !== null)) count++;

        return count;
      })
    }
  })
)
