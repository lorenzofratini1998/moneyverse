import {effect, inject} from '@angular/core';
import {Tag, TagRequest} from '../../../transaction.model';
import {patchState, signalStore, withHooks, withMethods, withState} from '@ngrx/signals';
import {TransactionService} from '../../../services/transaction.service';
import {AuthService} from '../../../../../core/auth/auth.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {debounceTime, filter, merge, Subscription, switchMap, tap} from 'rxjs';
import {ToastService} from '../../../../../shared/services/toast.service';
import {TagEventService} from './tag-event.service';
import {TranslationService} from '../../../../../shared/services/translation.service';
import {SystemService} from '../../../../../core/services/system.service';

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
    const translateService = inject(TranslationService);

    const loadTags = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.tags().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.user().userId;
          return transactionService.getTagsByUser(userId).pipe(
            tap({
              next: (tags) => patchState(store, {tags: tags}),
              error: () => toastService.error(translateService.translate('app.message.tag.load.error'))
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
            next: () => toastService.success(translateService.translate('app.message.tag.create.success')),
            error: () => toastService.error(translateService.translate('app.message.tag.create.error'))
          })
        )
      ),

      updateTag: rxMethod<{ tagId: string, request: TagRequest }>((request$) =>
        request$.pipe(
          switchMap(({tagId, request}) => transactionService.updateTag(tagId, request)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.tag.update.success')),
            error: () => toastService.error(translateService.translate('app.message.tag.update.error'))
          })
        )
      ),

      deleteTag: rxMethod<string>((tagId$) =>
        tagId$.pipe(
          switchMap(tagId => transactionService.deleteTag(tagId)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.tag.delete.success')),
            error: () => toastService.error(translateService.translate('app.message.tag.delete.error'))
          })
        )
      )
    }
  }),
  withHooks((store) => {
    const systemService = inject(SystemService);
    const eventService = inject(TagEventService);
    const subscriptions = new Subscription();

    return {
      onInit() {
        effect(() => {
          const translationsReady = systemService.translationsReady();
          if (translationsReady) {
            store.loadTags(true);
          }
        });

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
