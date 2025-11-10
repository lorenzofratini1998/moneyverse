import {inject} from '@angular/core';
import {Tag, TagRequest} from '../../../transaction.model';
import {patchState, signalStore, withHooks, withMethods, withState} from '@ngrx/signals';
import {TransactionService} from '../../../services/transaction.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {debounceTime, filter, merge, Subscription, switchMap, tap} from 'rxjs';
import {ToastService} from '../../../../../shared/services/toast.service';
import {TagEventService} from './tag-event.service';

interface TagStoreState {
  tags: Tag[];
}

const initialState: TagStoreState = {
  tags: []
};

export const TagStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  withMethods((store) => {
    const transactionService = inject(TransactionService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);

    const loadTags = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.tags().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.authenticatedUser.userId;
          return transactionService.getTagsByUser(userId).pipe(
            tap({
              next: (tags) => patchState(store, {tags: tags}),
              error: () => toastService.error('Failed to load tags')
            })
          )
        })
      )
    )

    return {
      loadTags,

      createTag: rxMethod<TagRequest>((request$) =>
        request$.pipe(
          switchMap((request => transactionService.createTag(request))),
          tap({
            next: () => toastService.success('Tag created successfully'),
            error: () => toastService.error('Failed to create tag')
          })
        )
      ),

      updateTag: rxMethod<{ tagId: string, request: TagRequest }>((request$) =>
        request$.pipe(
          switchMap(({tagId, request}) => transactionService.updateTag(tagId, request)),
          tap({
            next: () => toastService.success('Tag updated successfully'),
            error: () => toastService.error('Failed to update tag')
          })
        )
      ),

      deleteTag: rxMethod<string>((tagId$) =>
        tagId$.pipe(
          switchMap(tagId => transactionService.deleteTag(tagId)),
          tap({
            next: () => toastService.success('Tag deleted successfully'),
            error: () => toastService.error('Failed to delete tag')
          })
        )
      )
    }
  }),
  withHooks((store) => {
    const eventService = inject(TagEventService);
    const subscriptions = new Subscription();

    return {
      onInit() {
        store.loadTags(true);
        const reloadEvents$ = merge(
          eventService.onTagCreated(),
          eventService.onTagUpdated(),
          eventService.onTagDeleted()
        )

        subscriptions.add(
          reloadEvents$.subscribe(() => store.loadTags(true))
        )
      },
      onDestroy() {
        subscriptions.unsubscribe();
      }
    }
  })
)
