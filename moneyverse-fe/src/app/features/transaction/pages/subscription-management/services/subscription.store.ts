import {debounceTime, filter, merge, Subscription, switchMap, tap} from 'rxjs';
import {SubscriptionTransaction, SubscriptionRequest} from '../../../transaction.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {TransactionService} from '../../../services/transaction.service';
import {computed, effect, inject} from '@angular/core';
import {AuthService} from '../../../../../core/auth/auth.service';
import {ToastService} from '../../../../../shared/services/toast.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {SubscriptionEventService} from './subscription-event.service';
import {TranslationService} from '../../../../../shared/services/translation.service';
import {SystemService} from '../../../../../core/services/system.service';

interface SubscriptionState {
  subscriptions: SubscriptionTransaction[],
}

const initialState: SubscriptionState = {
  subscriptions: [],
}

export const SubscriptionStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withMethods((store) => {
    const transactionService = inject(TransactionService);
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const translateService = inject(TranslationService);

    const loadSubscriptions = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.subscriptions().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.user().userId;
          return transactionService.getSubscriptionsByUser(userId).pipe(
            tap({
              next: (subscriptions) => patchState(store, {subscriptions: subscriptions}),
              error: () => toastService.error(translateService.translate('app.message.subscription.load.error'))
            })
          )
        })
      ));

    return {
      loadSubscriptions,

      createSubscription: rxMethod<SubscriptionRequest>((request$) =>
        request$.pipe(
          switchMap((request => transactionService.createSubscription(request))),
          tap({
            next: () => toastService.success(translateService.translate('app.message.subscription.create.success')),
            error: () => toastService.error(translateService.translate('app.message.subscription.create.error'))
          })
        )
      ),

      updateSubscription: rxMethod<{ subscriptionId: string, request: SubscriptionRequest }>((request$) =>
        request$.pipe(
          switchMap(({subscriptionId, request}) => transactionService.updateSubscription(subscriptionId, request)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.subscription.update.success')),
            error: () => toastService.error(translateService.translate('app.message.subscription.update.error'))
          })
        )),

      deleteSubscription: rxMethod<string>((subscriptionId$) =>
        subscriptionId$.pipe(
          switchMap(subscriptionId => transactionService.deleteSubscription(subscriptionId)),
          tap({
            next: () => toastService.success(translateService.translate('app.message.subscription.delete.success')),
            error: () => toastService.error(translateService.translate('app.message.subscription.delete.error'))
          })
        )
      ),

    };
  }),

  withComputed((store) => ({
    subscriptions: computed(() => store.subscriptions()),
  })),

  withHooks((store) => {
    const systemService = inject(SystemService);
    const eventService = inject(SubscriptionEventService);
    const subscriptions = new Subscription();

    return {
      onInit() {
        effect(() => {
          const translationsReady = systemService.translationsReady();
          if (translationsReady) {
            store.loadSubscriptions(true);
          }
        });

        const reloadEvents$ = merge(
          eventService.onSubscriptionCreated(),
          eventService.onSubscriptionUpdated(),
          eventService.onSubscriptionDeleted()
        )

        subscriptions.add(
          reloadEvents$.subscribe(() => store.loadSubscriptions(true))
        );
      },
      onDestroy() {
        subscriptions.unsubscribe();
      }
    }
  })
)
