import {inject, Injectable, signal} from '@angular/core';
import {AppConfirmationService} from '../../../../../../shared/services/confirmation.service';
import {SubscriptionTransaction, Transaction} from "../../../../transaction.model";
import {TableRowExpandEvent} from 'primeng/table';
import {TransactionService} from '../../../../services/transaction.service';
import {map, Observable, tap} from 'rxjs';
import {ToastService} from '../../../../../../shared/services/toast.service';
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionTableService {

  private readonly confirmationService = inject(AppConfirmationService);
  private readonly transactionService = inject(TransactionService);
  private readonly toastService = inject(ToastService);
  private readonly translateService = inject(TranslationService);

  private readonly _subscriptionTransactionsMap = signal<Map<string, Transaction[]>>(new Map());
  readonly subscriptionTransactionsMap = this._subscriptionTransactionsMap.asReadonly();

  confirmDelete(subscription: SubscriptionTransaction, onDeleteSubscription: () => void) {
    this.confirmationService.confirmDelete({
      message: this.translateService.translate("app.dialog.subscription.confirmDelete", {field: subscription.subscriptionName}),
      header: this.translateService.translate("app.dialog.subscription.delete"),
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

  private getSubscription(subscriptionId: string): Observable<SubscriptionTransaction> {
    return this.transactionService.getSubscription(subscriptionId).pipe(
      map(subscription => ({
        ...subscription,
        transactions: subscription.transactions.sort(
          (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
        )
      })),
      tap({
        error: () => this.toastService.error(this.translateService.translate("app.message.subscription.load.error"))
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
