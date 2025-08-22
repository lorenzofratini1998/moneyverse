import {computed, inject} from '@angular/core';
import {Category, CategoryCriteria, CategoryRequest} from '../category.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {CategoryService} from './category.service';
import {AuthService} from '../../../core/auth/auth.service';
import {ToastService} from '../../../shared/services/toast.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {debounceTime, distinctUntilChanged, filter, skip, switchMap, tap} from 'rxjs';
import {CategoryEventService} from '../pages/category-management/services/category-event.service';
import {toObservable} from '@angular/core/rxjs-interop';

interface CategoryStoreState {
  defaultCategories: Category[];
  categories: Category[];
  categoryCriteria: CategoryCriteria;
}

const initialState: CategoryStoreState = {
  defaultCategories: [],
  categories: [],
  categoryCriteria: {},
};

export const CategoryStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  withMethods((store) => {
    const categoryService = inject(CategoryService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);

    const loadCategories = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.categories().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.authenticatedUser.userId;
          return categoryService.getCategoriesByUser(userId).pipe(
            tap({
              next: (categories) => patchState(store, {categories: categories}),
              error: () => toastService.error('Failed to load categories')
            })
          )
        })
      )
    );

    return {
      loadDefaultCategories: rxMethod<void>((trigger) =>
        trigger.pipe(
          switchMap(() => categoryService.getDefaultCategories()),
          tap({
            next: (categories) => patchState(store, {defaultCategories: categories}),
            error: () => toastService.error('Failed to load default categories')
          })
        )
      ),

      createDefaultCategories: rxMethod<string>((userId$) =>
        userId$.pipe(
          switchMap(userId => categoryService.createDefaultCategories(userId)),
          tap({
            next: () => toastService.success('Default categories created successfully'),
            error: () => toastService.error('Failed to create default categories')
          })
        )
      ),

      loadCategories,

      createCategory: rxMethod<CategoryRequest>((request$) =>
        request$.pipe(
          switchMap((request) => categoryService.createCategory(request)),
          tap({
            next: () => toastService.success('Category created successfully'),
            error: () => toastService.error('Failed to create category')
          })
        )
      ),

      updateCategory: rxMethod<{ categoryId: string, request: CategoryRequest }>((request$) =>
        request$.pipe(
          switchMap(({categoryId, request}) => categoryService.updateCategory(categoryId, request)),
          tap({
            next: () => toastService.success('Category updated successfully'),
            error: () => toastService.error('Failed to update category')
          })
        )
      ),

      deleteCategory: rxMethod<string>((categoryId$) =>
        categoryId$.pipe(
          switchMap(categoryId => categoryService.deleteCategory(categoryId)),
          tap({
            next: () => toastService.success('Category deleted successfully'),
            error: () => toastService.error('Failed to delete category')
          })
        )
      ),

    }
  }),

  withComputed((store) => ({
    categories: computed(() => store.categories()),
    categoriesMap: computed(() => new Map(store.categories().map(category => [category.categoryId, category]))),
  })),

  withHooks((store) => {
    const eventService = inject(CategoryEventService);

    return {
      onInit() {
        store.loadCategories(true);

        toObservable(store.categories).pipe(
          skip(1),
          debounceTime(300),
          distinctUntilChanged((prev, curr) => prev.length === curr.length),
          filter((categories) => categories.length === 0)
        ).subscribe(() => {
          store.loadDefaultCategories();
        });

        eventService.connect().subscribe({
          error: (error) => console.log('SSE connection error: ', error)
        })
        eventService.onCategoryCreated().subscribe(() => store.loadCategories(true));
        eventService.onCategoryUpdated().subscribe(() => store.loadCategories(true));
        eventService.onCategoryDeleted().subscribe(() => store.loadCategories(true));
      },
      onDestroy() {
        eventService.disconnect();
      },
    }
  })
)
