import {computed, effect, inject} from '@angular/core';
import {Category, CategoryCriteria, CategoryRequest} from '../category.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {CategoryService} from './category.service';
import {AuthService} from '../../../core/auth/auth.service';
import {ToastService} from '../../../shared/services/toast.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {debounceTime, distinctUntilChanged, EMPTY, filter, merge, skip, Subscription, switchMap, tap} from 'rxjs';
import {CategoryEventService} from '../pages/category-management/services/category-event.service';
import {toObservable} from '@angular/core/rxjs-interop';
import {SystemService} from '../../../core/services/system.service';
import {TranslationService} from '../../../shared/services/translation.service';

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
    const translateService = inject(TranslationService);

    const loadCategories = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.categories().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.user().userId;
          return categoryService.getCategoriesByUser(userId).pipe(
            tap({
              next: (categories) => patchState(store, {categories: categories}),
              error: () => toastService.error(translateService.translate('app.message.category.load.error'))
            })
          )
        })
      )
    );

    return {
      loadDefaultCategories: rxMethod<boolean | void>((trigger) =>
        trigger.pipe(
          switchMap((force = false) => {
            if (!force && store.defaultCategories().length > 0) {
              return EMPTY;
            }
            return categoryService.getDefaultCategories().pipe(
              tap({
                next: (categories) => patchState(store, {defaultCategories: categories}),
                error: () => toastService.error(translateService.translate('app.message.defaultCategories.load.error'))
              })
            );
          })
        )
      ),

      createDefaultCategories: rxMethod<string>((userId$) =>
        userId$.pipe(
          switchMap(userId => categoryService.createDefaultCategories(userId)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.defaultCategories.create.success')),
            error: () => toastService.error(translateService.translate('app.message.defaultCategories.create.error'))
          })
        )
      ),

      loadCategories,

      createCategory: rxMethod<CategoryRequest>((request$) =>
        request$.pipe(
          switchMap((request) => categoryService.createCategory(request)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.category.create.success')),
            error: () => toastService.error(translateService.translate('app.message.category.create.error'))
          })
        )
      ),

      updateCategory: rxMethod<{ categoryId: string, request: CategoryRequest }>((request$) =>
        request$.pipe(
          switchMap(({categoryId, request}) => categoryService.updateCategory(categoryId, request)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.category.update.success')),
            error: () => toastService.error(translateService.translate('app.message.category.update.error'))
          })
        )
      ),

      deleteCategory: rxMethod<string>((categoryId$) =>
        categoryId$.pipe(
          switchMap(categoryId => categoryService.deleteCategory(categoryId)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.category.delete.success')),
            error: () => toastService.error(translateService.translate('app.message.category.delete.error'))
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
    const systemService = inject(SystemService);
    const eventService = inject(CategoryEventService);
    const subscriptions = new Subscription();

    return {
      onInit() {
        effect(() => {
          const translationsReady = systemService.translationsReady();
          if (translationsReady) {
            store.loadCategories(true);
          }
        });

        effect(() => {
          const lang = systemService.languageChanged();
          if (lang) {
            store.loadDefaultCategories(true);
          }
        })

        subscriptions.add(
          toObservable(store.categories).pipe(
            skip(1),
            debounceTime(300),
            distinctUntilChanged((prev, curr) => prev.length === curr.length),
            filter((categories) => categories.length === 0)
          ).subscribe(() => {
            store.loadDefaultCategories(true);
          })
        );

        const reloadEvents$ = merge(
          eventService.onCategoryCreated(),
          eventService.onCategoryUpdated(),
          eventService.onCategoryDeleted()
        );

        subscriptions.add(
          reloadEvents$.subscribe(() => store.loadCategories(true))
        );
      },
      onDestroy() {
        subscriptions.unsubscribe();
      },
    }
  })
)
