import {Budget, BudgetRequest} from '../../../category.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {computed, effect, inject} from '@angular/core';
import {CategoryService} from '../../../services/category.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {ToastService} from '../../../../../shared/services/toast.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {debounceTime, filter, merge, Subscription, switchMap, tap} from 'rxjs';
import {BudgetEventService} from './budget-event.service';
import {TranslationService} from '../../../../../shared/services/translation.service';
import {SystemService} from '../../../../../core/services/system.service';

interface BudgetState {
  budgets: Budget[];
}

const initialState: BudgetState = {
  budgets: []
};

export const BudgetStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withMethods((store) => {
    const categoryService = inject(CategoryService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const translateService = inject(TranslationService);

    const loadBudgets = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.budgets().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.user().userId;
          return categoryService.getBudgetsByUser(userId).pipe(
            tap({
              next: (budgets) => patchState(store, {budgets: budgets}),
              error: () => toastService.error(translateService.translate('app.message.budget.load.error'))
            })
          );
        })
      )
    )

    return {
      loadBudgets,

      createBudget: rxMethod<BudgetRequest>((request$) =>
        request$.pipe(
          switchMap((request) => categoryService.createBudget(request)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.budget.create.success')),
            error: () => toastService.error(translateService.translate('app.message.budget.create.error'))
          })
        )
      ),

      updateBudget: rxMethod<{ budgetId: string, request: BudgetRequest }>((request$) =>
        request$.pipe(
          switchMap(({budgetId, request}) => categoryService.updateBudget(budgetId, request)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.budget.update.success')),
            error: () => toastService.error(translateService.translate('app.message.budget.update.error'))
          })
        )
      ),

      deleteBudget: rxMethod<string>((budgetId$) =>
        budgetId$.pipe(
          switchMap((budgetId) => categoryService.deleteBudget(budgetId)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.budget.delete.success')),
            error: () => toastService.error(translateService.translate('app.message.budget.delete.error'))
          })
        )
      )
    }
  }),

  withComputed((store) => ({
    budgets: computed(() => store.budgets()),
    budgetCategories: computed(() => store.budgets().map(budget => budget.category).filter((value, index, self) => self.indexOf(value) === index))
  })),

  withHooks((store) => {
    const systemService = inject(SystemService);
    const eventService = inject(BudgetEventService);
    const subscriptions = new Subscription();

    return {
      onInit() {
        effect(() => {
          const translationsReady = systemService.translationsReady();
          if (translationsReady) {
            store.loadBudgets(true);
          }
        });

        const reloadEvents$ = merge(
          eventService.onBudgetCreated(),
          eventService.onBudgetUpdated(),
          eventService.onBudgetDeleted()
        );

        subscriptions.add(
          reloadEvents$.subscribe(() => store.loadBudgets(true))
        );
      },
      onDestroy() {
        subscriptions.unsubscribe();
      },
    }
  })
)
