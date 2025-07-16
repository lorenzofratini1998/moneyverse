import {Component, inject, ViewChild} from '@angular/core';
import {Button} from "primeng/button";
import {SvgComponent} from "../../../../shared/components/svg/svg.component";
import {IconsEnum} from '../../../../shared/models/icons.model';
import {
  isTransferForm,
  isTransferFormData,
  Transaction,
  TransactionForm,
  TransactionFormData,
  Transfer,
  TransferForm,
  TransferFormData
} from '../../transaction.model';
import {Toast} from 'primeng/toast';
import {ConfirmationService, MessageService} from 'primeng/api';
import {AuthService} from '../../../../core/auth/auth.service';
import {TransactionService} from '../../transaction.service';
import {switchMap, take} from 'rxjs';
import {TransactionTableComponent} from './components/transaction-table/transaction-table.component';
import {TransactionFormDialogComponent} from './components/transaction-form-dialog/transaction-form-dialog.component';
import {TransactionFactory} from './models/transaction.factory';

@Component({
  selector: 'app-transaction-management',
  imports: [
    Button,
    SvgComponent,
    Toast,
    TransactionTableComponent,
    TransactionFormDialogComponent
  ],
  templateUrl: './transaction-management.component.html',
  styleUrl: './transaction-management.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class TransactionManagementComponent {

  protected readonly IconsEnum = IconsEnum;
  protected readonly authService = inject(AuthService);
  protected readonly transactionService = inject(TransactionService);
  protected readonly messageService = inject(MessageService);

  @ViewChild(TransactionFormDialogComponent) transactionFormDialog!: TransactionFormDialogComponent;
  @ViewChild(TransactionTableComponent) transactionTable!: TransactionTableComponent;

  onCreate(formData: TransactionFormData | TransferFormData) {
    if (isTransferFormData(formData)) {
      this.onCreateTransfer(formData);
    } else {
      this.onCreateTransaction(formData);
    }
  }

  private onCreateTransaction(formData: TransactionFormData) {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => this.transactionService.createTransaction(TransactionFactory.createTransactionRequest(userId, formData)))
    ).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Transaction created successfully.'
        });
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the transaction creation.'
        });
      }
    })
  }

  private onCreateTransfer(formData: TransferFormData) {
    this.authService.getUserId().pipe(
      take(1),
      switchMap(userId => this.transactionService.createTransfer(TransactionFactory.createTransferRequest(userId, formData)))
    ).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Transfer created successfully.'
        });
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the transfer creation.'
        });
      }
    })
  }

  onEdit(formData: TransactionForm | TransferForm) {
    if (isTransferForm(formData)) {
      this.onEditTransfer(formData);
    } else {
      this.onEditTransaction(formData);
    }
  }

  private onEditTransaction(formData: TransactionForm) {
    this.transactionService.updateTransaction(formData.transactionId!, TransactionFactory.createTransactionUpdateRequest(formData.formData)).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Transaction updated successfully.'
        });
        this.transactionTable.loadTransactions();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the transaction update.'
        });
      }
    })
  }

  private onEditTransfer(formData: TransferForm) {
    this.transactionService.updateTransfer(formData.transferId!, TransactionFactory.createTransferUpdateRequest(formData.formData)).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Transfer updated successfully.'
        });
        this.transactionTable.loadTransactions();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the transfer update.'
        });
      }
    })
  }

  onDeleteTransaction(transaction: Transaction) {
    this.transactionService.deleteTransaction(transaction.transactionId).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Transaction deleted successfully.'
        });
        this.transactionTable.loadTransactions();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the transaction deletion.'
        });
      }
    })
  }

  onDeleteTransfer(transfer: Transfer) {
    this.transactionService.deleteTransfer(transfer.transferId).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          detail: 'Transfer deleted successfully.'
        });
        this.transactionTable.loadTransactions();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Error during the transfer deletion.'
        });
      }
    })
  }
}
