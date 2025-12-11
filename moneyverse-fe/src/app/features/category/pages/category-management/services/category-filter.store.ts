import {CategoryCriteria} from '../../../category.model';
import {patchState, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {computed, inject} from '@angular/core';
import {CategoryStore} from '../../../services/category.store';

interface CategoryFilterState {
  criteria: CategoryCriteria;
}

const initialState: CategoryFilterState = {
  criteria: {},
};

export const CategoryFilterStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withMethods((store) => ({
      updateFilters(criteria: CategoryCriteria) {
        patchState(store, {criteria})
      },

      resetFilters() {
        patchState(store, {criteria: {}})
      }

    })
  ),

  withComputed((store) => {
    const categoryStore = inject(CategoryStore);

    return {
      criteria: computed(() => store.criteria()),
      categories: computed(() => {
        const {name, parentCategories} = store.criteria();
        return categoryStore.categories()
          .filter(c =>
            !name || c.categoryName.toLowerCase().includes(name.toLowerCase())
          )
          .filter(c =>
              !parentCategories?.length || (
                c.parentCategory != null && parentCategories.includes(c.parentCategory)
              )
          );
      }),
      activeFiltersCount: computed(() => {
        const criteria = store.criteria();
        let count = 0;

        if (criteria.name) count++;
        if (criteria.parentCategories?.length) count++;

        return count;
      }),
    }

  })
);
