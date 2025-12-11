import {PageResponse} from '../../../../../shared/models/common.model';
import {
  Transaction,
  TransactionCriteria, TransactionCriteriaTypeEnum,
  TransactionRequest,
  TransactionRequestItem,
  TransactionSortAttributeEnum,
  TransferRequest
} from '../../../transaction.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {TransactionService} from '../../../services/transaction.service';
import {computed, effect, inject} from '@angular/core';
import {AuthService} from '../../../../../core/auth/auth.service';
import {ToastService} from '../../../../../shared/services/toast.service';
import {BoundCriteria, Direction} from '../../../../../shared/models/criteria.model';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {debounceTime, filter, merge, Subscription, switchMap, tap} from 'rxjs';
import {TransactionEventService} from './transaction-event.service';
import {SubscriptionEventService} from '../../subscription-management/services/subscription-event.service';
import {TranslationService} from '../../../../../shared/services/translation.service';
import {SystemService} from '../../../../../core/services/system.service';

interface TransactionStoreState {
  criteria: TransactionCriteria,
  transactionPage: PageResponse<Transaction>
}

const initialState: TransactionStoreState = {
  criteria: {
    page: {
      offset: 0,
      limit: 25
    },
    sort: {
      attribute: TransactionSortAttributeEnum.DATE,
      direction: Direction.DESC
    }
  },
  transactionPage: {
    content: [],
    metadata: {
      number: 0,
      size: 0,
      totalElements: 0,
      totalPages: 0
    }
  }
}

function normalizeAmountForBackend(
  amount: BoundCriteria | undefined,
  type: TransactionCriteriaTypeEnum | undefined
): BoundCriteria | undefined {
  if (!amount || !type) return amount;

  if (type === TransactionCriteriaTypeEnum.EXPENSE) {
    return {
      lower: amount.upper ? -Math.abs(amount.upper) : undefined,
      upper: amount.lower ? -Math.abs(amount.lower) : undefined
    };
  }

  if (type === TransactionCriteriaTypeEnum.INCOME) {
    return {
      lower: amount.lower ? Math.abs(amount.lower) : 0,
      upper: amount.upper ? Math.abs(amount.upper) : undefined
    };
  }

  return amount;
}

export const TransactionStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withMethods((store) => {
    const transactionService = inject(TransactionService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const translateService = inject(TranslationService);

    const loadTransactions = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.transactionPage().content.length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.user().userId;
          const criteria = {
            ...store.criteria(),
            amount: normalizeAmountForBackend(store.criteria().amount, store.criteria().type)
          };
          return transactionService.getTransactionsByUser(userId, criteria).pipe(
            tap({
              next: (transactionPage) => patchState(store, {transactionPage: transactionPage}),
              error: () => toastService.error(translateService.translate("app.message.transaction.load.error"))
            })
          )
        })
      )
    )

    return {
      loadTransactions,

      createTransaction: rxMethod<TransactionRequest>((request$) =>
        request$.pipe(
          switchMap((request => transactionService.createTransaction(request))),
          tap({
            next: () => toastService.success(translateService.translate("app.message.transaction.create.success")),
            error: () => toastService.error(translateService.translate("app.message.transaction.create.error"))
          })
        )
      ),

      updateTransaction: rxMethod<{ transactionId: string, request: TransactionRequestItem }>((request$) =>
        request$.pipe(
          switchMap(({transactionId, request}) => transactionService.updateTransaction(transactionId, request)),
          tap({
            next: () => toastService.success(translateService.translate("app.message.transaction.update.success")),
            error: () => toastService.error(translateService.translate("app.message.transaction.update.error"))
          })
        )
      ),

      deleteTransaction: rxMethod<string>((transactionId$) =>
        transactionId$.pipe(
          switchMap(transactionId => transactionService.deleteTransaction(transactionId)),
          tap({
            next: () => toastService.success(translateService.translate("app.message.transaction.delete.success")),
            error: () => toastService.error(translateService.translate("app.message.transaction.delete.error"))
          })
        )
      ),

      createTransfer: rxMethod<TransferRequest>((request$) =>
        request$.pipe(
          switchMap((request => transactionService.createTransfer(request))),
          tap({
            next: () => toastService.success(translateService.translate("app.message.transfer.create.success")),
            error: () => toastService.error(translateService.translate("app.message.transfer.create.error"))
          })
        )
      ),

      updateTransfer: rxMethod<{ transferId: string, request: TransferRequest }>((request$) =>
        request$.pipe(
          switchMap(({transferId, request}) => transactionService.updateTransfer(transferId, request)),
          tap({
            next: () => toastService.success(translateService.translate("app.message.transfer.update.success")),
            error: () => toastService.error(translateService.translate("app.message.transfer.update.error"))
          })
        )
      ),

      deleteTransfer: rxMethod<string>((transferId$) =>
        transferId$.pipe(
          switchMap(transferId => transactionService.deleteTransfer(transferId)),
          tap({
            next: () => toastService.success(translateService.translate("app.message.transfer.delete.success")),
            error: () => toastService.error(translateService.translate("app.message.transfer.delete.error"))
          })
        )
      ),

      updateFilters(criteria: TransactionCriteria) {
        patchState(store, {
          criteria: {
            ...store.criteria(),
            ...criteria
          }
        })
      },

      resetFilters() {
        patchState(store, {
          criteria: {
            page: {
              offset: 0,
              limit: 25
            },
            sort: {
              attribute: TransactionSortAttributeEnum.DATE,
              direction: Direction.DESC
            }
          }
        })
      }
    }
  }),

  withComputed((store) => ({
    transactionPage: computed(() => store.transactionPage()),
    criteria: computed(() => store.criteria()),
    activeFiltersCount: computed(() => {
      const criteria = store.criteria();
      let count = 0;
      if ((criteria.accounts ?? []).length > 0) count++;
      if ((criteria.tags ?? []).length > 0) count++;
      if ((criteria.categories ?? []).length > 0) count++;
      if (criteria.amount && (criteria.amount.lower || criteria.amount.upper)) count++;
      if (criteria.date && (criteria.date.start || criteria.date.end)) count++;
      if (criteria.budget) count++;
      if (criteria.subscription) count++;
      if (criteria.transfer) count++;
      return count;
    })
  })),

  withHooks((store) => {
    const systemService = inject(SystemService);
    const transactionEventService = inject(TransactionEventService);
    const subscriptionEventService = inject(SubscriptionEventService);
    const subscriptions = new Subscription();

    return {
      onInit() {
        effect(() => {
          const translationsReady = systemService.translationsReady();
          if (translationsReady) {
            store.loadTransactions(true);
          }
        });

        effect(() => {
          store.criteria();
          store.loadTransactions(true);
        });

        const reloadEvents$ = merge(
          transactionEventService.onTransactionCreated(),
          transactionEventService.onTransactionUpdated(),
          transactionEventService.onTransactionDeleted(),
          transactionEventService.onTransferCreated(),
          transactionEventService.onTransferUpdated(),
          transactionEventService.onTransferDeleted(),
          subscriptionEventService.onSubscriptionCreated(),
          subscriptionEventService.onSubscriptionUpdated(),
          subscriptionEventService.onSubscriptionDeleted()
        )

        subscriptions.add(
          reloadEvents$.subscribe(() => store.loadTransactions(true))
        );
      },
      onDestroy() {
        subscriptions.unsubscribe();
      }
    }
  })
)

