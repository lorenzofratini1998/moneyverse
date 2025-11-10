import {debounceTime, filter, merge, Subscription, switchMap, tap} from 'rxjs';
import {SubscriptionTransaction, SubscriptionRequest} from '../../../transaction.model';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {TransactionService} from '../../../services/transaction.service';
import {computed, inject} from '@angular/core';
import {AuthService} from '../../../../../core/auth/auth.service';
import {ToastService} from '../../../../../shared/services/toast.service';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {SubscriptionEventService} from './subscription-event.service';

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

    const loadSubscriptions = rxMethod<boolean | void>((trigger) =>
      trigger.pipe(
        debounceTime(300),
        filter((forceRefresh = false) => {
          const refresh = forceRefresh ?? false;
          return store.subscriptions().length === 0 || refresh;
        }),
        switchMap(() => {
          const userId = authService.authenticatedUser.userId;
          return transactionService.getSubscriptionsByUser(userId).pipe(
            tap({
              next: (subscriptions) => patchState(store, {subscriptions: subscriptions}),
              error: () => toastService.error('Failed to load subscriptions')
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
            next: () => toastService.success('Subscription created successfully'),
            error: () => toastService.error('Failed to create subscription')
          })
        )
      ),

      updateSubscription: rxMethod<{ subscriptionId: string, request: SubscriptionRequest }>((request$) =>
        request$.pipe(
          switchMap(({subscriptionId, request}) => transactionService.updateSubscription(subscriptionId, request)),
          tap({
            next: () => toastService.success('Subscription updated successfully'),
            error: () => toastService.error('Failed to update subscription')
          })
        )),

      deleteSubscription: rxMethod<string>((subscriptionId$) =>
        subscriptionId$.pipe(
          switchMap(subscriptionId => transactionService.deleteSubscription(subscriptionId)),
          tap({
            next: () => toastService.success('Subscription deleted successfully'),
            error: () => toastService.error('Failed to delete subscription')
          })
        )
      ),

    };
  }),

  withComputed((store) => ({
    subscriptions: computed(() => store.subscriptions()),
  })),

  withHooks((store) => {
    const eventService = inject(SubscriptionEventService);
    const subscriptions = new Subscription();

    return {
      onInit() {
        store.loadSubscriptions(true);
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
