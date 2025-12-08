import {PageResponse} from '../models/common.model';
import {
  Transaction,
  TransactionCriteria,
  TransactionSortAttributeEnum
} from '../../features/transaction/transaction.model';
import {Direction, PageCriteria, SortCriteria} from '../models/criteria.model';
import {patchState, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {computed, inject} from '@angular/core';
import {TransactionService} from '../../features/transaction/services/transaction.service';
import {AuthService} from '../../core/auth/auth.service';
import {ToastService} from '../services/toast.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {switchMap, tap} from 'rxjs';
import {TranslationService} from '../services/translation.service';

type TransactionsTableState = {
  transactions: PageResponse<Transaction>,
  page: PageCriteria,
  sort: SortCriteria
}

const initialState: TransactionsTableState = {
  transactions: {
    content: [],
    metadata: {number: 0, size: 0, totalElements: 0, totalPages: 0}
  },
  page: {offset: 0, limit: 10},
  sort: {
    attribute: TransactionSortAttributeEnum.DATE,
    direction: Direction.DESC
  }
};

export const TransactionsTableStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withMethods((store) => {
    const transactionService = inject(TransactionService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const translateService = inject(TranslationService);

    return {
      load: rxMethod<TransactionCriteria>((criteria$) =>
        criteria$.pipe(
          switchMap(criteria => transactionService.getTransactionsByUser(authService.user().userId, criteria)),
          tap({
            next: (response) => patchState(store, {transactions: response}),
            error: () => toastService.error(translateService.translate('app.message.transaction.load.error'))
          })
        )
      ),

      onPage(event: { first: number; rows: number }) {
        patchState(store, {page: {offset: event.first, limit: event.rows}});
      },

      onSort(event: { field: string; order: number }) {
        patchState(store, {
          sort: {
            attribute: event.field as any,
            direction: event.order === 1 ? Direction.ASC : Direction.DESC
          }
        });
      }
    };
  }),

  withComputed((store) => ({
    totalRecords: computed(() => store.transactions().metadata.totalElements),
    rows: computed(() => store.transactions.content.length)
  }))
);
