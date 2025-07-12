import {inject} from '@angular/core';
import {Tag} from './transaction.model';
import {patchState, signalStore, withHooks, withMethods, withState} from '@ngrx/signals';
import {TransactionService} from './transaction.service';
import {AuthService} from '../../core/auth/auth.service';

export interface TransactionStoreState {
  tags: Tag[];
  error: string | null;
}

const initialState: TransactionStoreState = {
  tags: [],
  error: null
};

export const TransactionStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  withMethods((store) => {
    const transactionService = inject(TransactionService);
    const authService = inject(AuthService);

    return {
      loadTags() {
        transactionService.getTagsByUser(authService.getAuthenticatedUser().userId)
          .subscribe({
            next: (tags) => patchState(store, {
              tags: tags
            }),
            error: (_) => patchState(store, {
              error: 'Failed to load tags'
            })
          })
      },

      refreshTags() {
        this.loadTags();
      }
    }
  }),
  withHooks({
    onInit: (store) => {
      store.loadTags();
    }
  })
)
