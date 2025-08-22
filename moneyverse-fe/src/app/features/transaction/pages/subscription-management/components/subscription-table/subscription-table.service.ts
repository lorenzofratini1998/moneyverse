import {inject, Injectable, signal} from '@angular/core';
import {AppConfirmationService} from '../../../../../../shared/services/confirmation.service';
import {Subscription, Transaction} from "../../../../transaction.model";
import {TableRowExpandEvent} from 'primeng/table';
import {TransactionService} from '../../../../services/transaction.service';
import {map, Observable, tap} from 'rxjs';
import {ToastService} from '../../../../../../shared/services/toast.service';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionTableService {

  private readonly confirmationService = inject(AppConfirmationService);
  private readonly transactionService = inject(TransactionService);
  private readonly toastService = inject(ToastService);

  private readonly _subscriptionTransactionsMap = signal<Map<string, Transaction[]>>(new Map());
  readonly subscriptionTransactionsMap = this._subscriptionTransactionsMap.asReadonly();

  confirmDelete(subscription: Subscription, onDeleteSubscription: () => void) {
    this.confirmationService.confirmDelete({
      message: `Are you sure you want to delete the subscription "${subscription.subscriptionName}"? All associated transactions will be deleted.`,
      header: 'Delete subscription',
      accept: () => onDeleteSubscription(),
    })
  }

  onRowExpand(event: TableRowExpandEvent) {
    const subscriptionId = event.data.subscriptionId;

    this.getSubscription(subscriptionId).subscribe({
      next: (subscription) => {
        this._subscriptionTransactionsMap.update(map => {
          const newMap = new Map(map);
          newMap.set(subscriptionId, subscription.transactions);
          return newMap;
        });
      },
      error: () => {
        this._subscriptionTransactionsMap.update(map => {
          const newMap = new Map(map);
          newMap.set(subscriptionId, []);
          return newMap;
        });
      }
    })
  }

  private getSubscription(subscriptionId: string): Observable<Subscription> {
    return this.transactionService.getSubscription(subscriptionId).pipe(
      map(subscription => ({
        ...subscription,
        transactions: subscription.transactions.sort(
          (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
        )
      })),
      tap({
        error: () => this.toastService.error('Failed to load subscription')
      })
    );
  }

  onRowCollapse(event: TableRowExpandEvent) {
    const subscriptionId = event.data.subscriptionId;

    this._subscriptionTransactionsMap.update(map => {
      const newMap = new Map(map);
      newMap.delete(subscriptionId);
      return newMap;
    });
  }
}
