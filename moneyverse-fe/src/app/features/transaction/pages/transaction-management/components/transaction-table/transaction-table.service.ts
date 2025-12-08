import {inject, Injectable} from '@angular/core';
import {SubscriptionTransaction, Transaction, TransactionSortAttributeEnum, Transfer} from '../../../../transaction.model';
import {TransactionService} from '../../../../services/transaction.service';
import {ToastService} from '../../../../../../shared/services/toast.service';
import {AppConfirmationService} from '../../../../../../shared/services/confirmation.service';
import {AccountStore} from '../../../../../account/services/account.store';
import {TransactionFormDialogOptionsEnum} from '../transaction-form-dialog/transaction-form-dialog.component';
import {TablePageEvent} from 'primeng/table';
import {Direction, PageCriteria} from '../../../../../../shared/models/criteria.model';
import {TransactionStore} from '../../services/transaction.store';
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Injectable({
  providedIn: 'root'
})
export class TransactionTableService {

  protected readonly transactionService = inject(TransactionService);
  protected readonly toastService = inject(ToastService);
  private readonly confirmationService = inject(AppConfirmationService);
  private readonly accountStore = inject(AccountStore);
  private readonly transactionStore = inject(TransactionStore);
  private readonly translateService = inject(TranslationService);

  confirmDelete(
    transaction: Transaction,
    onDeleteTransaction: (tx: Transaction) => void,
    onDeleteTransfer: (transfer: Transfer) => void
  ) {
    if (transaction.transferId) {
      this.transactionService.getTransactionsByTransferId(transaction.transferId).subscribe({
        next: (transfer: Transfer) => {
          this.confirmationService.confirmDelete({
            message: this.translateService.translate('app.dialog.transfer.confirmDelete'),
            header: this.translateService.translate('app.dialog.transfer.delete', {from: this.accountStore.accountsMap().get(transfer.transactionFrom.accountId)!.accountName, to: this.accountStore.accountsMap().get(transfer.transactionTo.accountId)!.accountName}),
            accept: () => onDeleteTransfer(transfer)
          });
        },
        error: () => this.toastService.error(this.translateService.translate("app.message.transfer.load.error"))
      });
    } else {
      this.confirmationService.confirmDelete({
        message: this.translateService.translate('app.dialog.transaction.confirmDelete'),
        header: this.translateService.translate('app.dialog.transaction.delete'),
        accept: () => onDeleteTransaction(transaction)
      });
    }
  }

  loadForEdit(
    transaction: Transaction,
    onEdit: (payload: { data: Transaction | Transfer, option: TransactionFormDialogOptionsEnum }) => void
  ) {
    if (transaction.transferId != null) {
      this.transactionService.getTransactionsByTransferId(transaction.transferId).subscribe({
        next: (transfer: Transfer) => onEdit({
          data: transfer,
          option: TransactionFormDialogOptionsEnum.TRANSFER
        }),
        error: () => this.toastService.error(this.translateService.translate("app.message.transfer.load.error"))
      });
    } else if (transaction.subscriptionId != null) {
      onEdit({
        data: transaction,
        option: TransactionFormDialogOptionsEnum.SUBSCRIPTION
      });
    } else {
      onEdit({
        data: transaction,
        option: transaction.amount < 0
          ? TransactionFormDialogOptionsEnum.EXPENSE
          : TransactionFormDialogOptionsEnum.INCOME
      });
    }
  }

  openTransferDetails(transferId: string, openDialog: (transfer: Transfer) => void) {
    this.transactionService.getTransactionsByTransferId(transferId).subscribe({
      next: (transfer: Transfer) => openDialog(transfer),
      error: () => this.toastService.error(this.translateService.translate("app.message.transfer.load.error"))
    });
  }

  openSubscriptionDetails(subscriptionId: string, openDialog: (subscription: SubscriptionTransaction) => void) {
    this.transactionService.getSubscription(subscriptionId).subscribe({
      next: (subscription) => openDialog(subscription),
      error: () => this.toastService.error(this.translateService.translate("app.message.subscription.load.error"))
    });
  }

  onPage(event: TablePageEvent) {
    const pageCriteria: PageCriteria = {
      offset: event.first,
      limit: event.rows
    };
    this.transactionStore.updateFilters({
      page: pageCriteria
    });
  }

  onSort(event: any) {
    this.transactionStore.updateFilters({
      sort: {
        attribute: event.field as TransactionSortAttributeEnum,
        direction: event.order === 1 ? Direction.ASC : Direction.DESC
      }
    })
  }

}
