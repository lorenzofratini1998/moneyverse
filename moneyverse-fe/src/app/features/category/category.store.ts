import {computed, inject} from '@angular/core';
import {Category} from './category.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {CategoryService} from './category.service';
import {AuthService} from '../../core/auth/auth.service';

export interface CategoryStoreState {
  selectedCategory: Category | null;
  isFormOpen: boolean;
  isTreeOpen: boolean;
  categories: Category[];
  error: string | null;
}

const initialState: CategoryStoreState = {
  selectedCategory: null,
  isFormOpen: false,
  isTreeOpen: false,
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
      openForm(category?: Category) {
        patchState(store, {
          selectedCategory: category ?? null,
          isFormOpen: true
        });
      },

      closeForm() {
        patchState(store, {
          selectedCategory: null,
          isFormOpen: false
        })
      },

      showCategoryTree(category?: Category) {
        patchState(store, {
          selectedCategory: category ?? null,
          isTreeOpen: true
        })
      },

      closeCategoryTree() {
        patchState(store, {
          selectedCategory: null,
          isTreeOpen: false
        })
      },

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
    hasSelection: computed(() => store.selectedCategory() !== null),
    canEdit: computed(() => store.isFormOpen()),
    categories: computed(() => store.categories()),
  })),

  withHooks({
    onInit: (store) => {
      store.loadCategories();
    }
  })
)
