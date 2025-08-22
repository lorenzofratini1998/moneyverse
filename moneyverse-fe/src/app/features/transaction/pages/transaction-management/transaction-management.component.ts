import {Component, computed, inject, viewChild} from '@angular/core';
import {IconsEnum} from '../../../../shared/models/icons.model';
import {Transaction, Transfer} from '../../transaction.model';
import {AuthService} from '../../../../core/auth/auth.service';
import {TransactionTableComponent} from './components/transaction-table/transaction-table.component';
import {TransactionFormDialogComponent} from './components/transaction-form-dialog/transaction-form-dialog.component';
import {TransactionStore} from './services/transaction.store';
import {isTransferFormData, TransactionFormData, TransferFormData} from './models/form.model';
import {ManagementComponent, ManagementConfig} from '../../../../shared/components/management/management.component';
import {
  TransactionFilterPanelComponent
} from './components/transaction-filter-panel/transaction-filter-panel.component';
import {TransactionTableService} from './components/transaction-table/transaction-table.service';

@Component({
  selector: 'app-transaction-management',
  imports: [
    TransactionTableComponent,
    TransactionFormDialogComponent,
    ManagementComponent,
    TransactionFilterPanelComponent
  ],
  templateUrl: './transaction-management.component.html'
})
export class TransactionManagementComponent {

  protected readonly transactionStore = inject(TransactionStore);
  protected readonly transactionTableService = inject(TransactionTableService);
  private readonly authService = inject(AuthService);

  transactionFormDialog = viewChild.required(TransactionFormDialogComponent);

  managementConfig = computed<ManagementConfig>(() => ({
    title: 'Transaction Management',
    actions: [
      {
        icon: IconsEnum.REFRESH,
        variant: 'text',
        severity: 'secondary',
        action: () => this.transactionStore.loadTransactions(true)
      },
      {
        icon: IconsEnum.PLUS,
        label: 'New Transaction',
        action: () => this.transactionFormDialog().open()
      }
    ]
  }))

  submit(formData: TransactionFormData | TransferFormData) {
    if (isTransferFormData(formData)) {
      this.submitTransfer(formData);
    } else {
      this.submitTransaction(formData);
    }
  }

  private submitTransfer(formData: TransferFormData) {
    const transferId = formData.transferId;
    if (transferId) {
      this.transactionStore.updateTransfer({
        transferId,
        request: {...formData}
      })
    } else {
      this.transactionStore.createTransfer({
          userId: this.authService.authenticatedUser.userId,
          ...formData
        }
      )
    }
  }

  private submitTransaction(formData: TransactionFormData) {
    const transactionId = formData.transactionId;
    if (transactionId) {
      this.transactionStore.updateTransaction({
        transactionId: transactionId,
        request: {...formData}
      })
    } else {
      this.transactionStore.createTransaction({
        userId: this.authService.authenticatedUser.userId,
        transactions: [{...formData}]
      })
    }
  }

  deleteTransaction(transaction: Transaction) {
    this.transactionStore.deleteTransaction(transaction.transactionId);
  }

  deleteTransfer(transfer: Transfer) {
    this.transactionStore.deleteTransfer(transfer.transferId);
  }
}
