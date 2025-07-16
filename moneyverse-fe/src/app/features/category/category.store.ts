import {computed, inject} from '@angular/core';
import {Category} from './category.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {CategoryService} from './category.service';
import {AuthService} from '../../core/auth/auth.service';

export interface CategoryStoreState {
  categories: Category[];
  error: string | null;
}

const initialState: CategoryStoreState = {
  categories: [],
  error: null
};

export const CategoryStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  withMethods((store) => {
    const categoryService = inject(CategoryService);
    const authService = inject(AuthService);

    return {
      loadCategories() {
        categoryService.getCategoriesByUser(authService.getAuthenticatedUser().userId)
          .subscribe({
            next: (categories) => patchState(store, {
              categories: categories
            }),
            error: (_) => patchState(store, {
              error: 'Failed to load categories'
            })
          })
      },

      refreshCategories() {
        this.loadCategories();
      }
    }
  }),

  withComputed((store) => ({
    categories: computed(() => store.categories()),
    categoriesMap: computed(() => new Map(store.categories().map(category => [category.categoryId, category]))),
  })),

  withHooks({
    onInit: (store) => {
      store.loadCategories();
    }
  })
)
