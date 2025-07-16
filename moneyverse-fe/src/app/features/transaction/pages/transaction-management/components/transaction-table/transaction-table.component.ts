import {Component, computed, inject, output, signal, ViewChild} from '@angular/core';
import {TableModule, TablePageEvent} from 'primeng/table';
import {AccountStore} from '../../../../../account/account.store';
import {CategoryStore} from '../../../../../category/category.store';
import {
  isTransferFormData,
  Transaction,
  TransactionCriteria,
  TransactionForm,
  TransactionFormData,
  TransactionSortAttributeEnum,
  Transfer,
  TransferForm,
  TransferFormData
} from '../../../../transaction.model';
import {TransactionService} from '../../../../transaction.service';
import {AuthService} from '../../../../../../core/auth/auth.service';
import {ConfirmationService, MessageService} from 'primeng/api';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {DatePipe} from '@angular/common';
import {CurrencyPipe} from '../../../../../../shared/pipes/currency.pipe';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {ColorService} from '../../../../../../shared/services/color.service';
import {Category} from '../../../../../category/category.model';
import {Account} from '../../../../../account/account.model';
import {ButtonDirective} from 'primeng/button';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {Toast} from 'primeng/toast';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {PageResponse} from '../../../../../../shared/models/common.model';
import {Direction, PageCriteria} from '../../../../../../shared/models/criteria.model';
import {
  TransactionFormDialogComponent,
  TransactionFormDialogOptions
} from '../transaction-form-dialog/transaction-form-dialog.component';
import {TransferTableDialogComponent} from '../transfer-table-dialog/transfer-table-dialog.component';
import {CustomChipComponent} from '../../../../../../shared/components/custom-chip/custom-chip.component';

@Component({
  selector: 'app-transaction-table',
  imports: [
    TableModule,
    DatePipe,
    CurrencyPipe,
    SvgComponent,
    ButtonDirective,
    ConfirmDialog,
    Toast,
    TransactionFormDialogComponent,
    TransferTableDialogComponent,
    CustomChipComponent
  ],
  templateUrl: './transaction-table.component.html',
  styleUrl: './transaction-table.component.scss',
  providers: [ConfirmationService, MessageService]
})
export class TransactionTableComponent {

  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly colorService = inject(ColorService);
  private readonly accountStore = inject(AccountStore);
  private readonly categoryStore = inject(CategoryStore);
  private readonly transactionService = inject(TransactionService);
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);
  private readonly confirmationService = inject(ConfirmationService);

  protected readonly Icons = IconsEnum;

  @ViewChild(TransactionFormDialogComponent) transactionFormDialog!: TransactionFormDialogComponent;
  @ViewChild(TransferTableDialogComponent) transferTableDialog!: TransferTableDialogComponent;

  protected pageResponse = signal<PageResponse<Transaction>>({
    content: [],
    metadata: {
      number: 0,
      size: 0,
      totalElements: 0,
      totalPages: 0
    }
  });

  protected rowPerPage = signal(25);
  protected sortAttribute = signal<TransactionSortAttributeEnum>(TransactionSortAttributeEnum.DATE);
  protected sortDirection = signal<Direction>(Direction.DESC);

  protected criteria = signal<TransactionCriteria>({
    page: {
      offset: 0,
      limit: this.rowPerPage()
    },
    sort: {
      attribute: this.sortAttribute(),
      direction: this.sortDirection()
    }
  });

  deletedTransaction = output<Transaction>();
  deletedTransfer = output<Transfer>();
  edited = output<TransactionForm | TransferForm>();

  accountsMap = computed<Map<string, Account>>(() => {
    return new Map(this.accountStore.accounts().map(account => [account.accountId, account]));
  })

  categoriesMap = computed<Map<string, Category>>(() => {
    return new Map(this.categoryStore.categories().map(category => [category.categoryId, category]));
  })

  constructor() {
    this.loadTransactions();
  }

  loadTransactions() {
    const userId = this.authService.getAuthenticatedUser().userId;
    this.transactionService.getTransactionsByUser(userId, this.criteria()).subscribe({
      next: (pageResponse: PageResponse<Transaction>) => this.pageResponse.set(pageResponse),
      error: () => {
        this.messageService.add({
          severity: 'error',
          detail: 'Failed to load transactions.'
        });
      }
    });
  }

  onEdit(formData: TransactionFormData | TransferFormData) {
    if (isTransferFormData(formData)) {
      this.edited.emit({
        transferId: (this.transactionFormDialog.selectedItem() as Transfer).transferId,
        formData: formData
      });
    } else {
      this.edited.emit({
        transactionId: (this.transactionFormDialog.selectedItem() as Transaction).transactionId,
        formData: formData
      });
    }
  }

  onDelete(event: Event, transaction: Transaction) {
    if (transaction.transferId) {
      this.transactionService.getTransactionsByTransferId(transaction.transferId).subscribe({
        next: (transfer: Transfer) => this.onDeleteTransfer(event, transfer),
        error: () => {
          this.messageService.add({
            severity: 'error',
            detail: 'Failed to load transfer.'
          });
        }
      })
    } else {
      this.onDeleteTransaction(event, transaction);
    }
  }

  private onDeleteTransfer(event: Event, transfer: Transfer) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Are you sure that you want to proceed? All transactions associated with this transfer will be deleted.`,
      header: 'Delete transfer',
      rejectLabel: 'Cancel',
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true,
        rounded: true
      },
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger',
        rounded: true
      },
      accept: () => {
        this.deletedTransfer.emit(transfer);
      },
      reject: () => {
      }
    });
  }

  private onDeleteTransaction(event: Event, transaction: Transaction) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: `Are you sure that you want to proceed?`,
      header: 'Delete transaction',
      rejectLabel: 'Cancel',
      rejectButtonProps: {
        label: 'Cancel',
        severity: 'secondary',
        outlined: true,
        rounded: true
      },
      acceptButtonProps: {
        label: 'Delete',
        severity: 'danger',
        rounded: true
      },
      accept: () => {
        this.deletedTransaction.emit(transaction);
      },
      reject: () => {
      }
    });
  }

  onPage(event: TablePageEvent) {
    const pageCriteria: PageCriteria = {
      offset: event.first,
      limit: event.rows
    };
    this.criteria.set({
      ...this.criteria(),
      page: pageCriteria
    });
    this.loadTransactions();
  }

  onSort(event: any) {
    this.criteria().sort = {
      attribute: event.field as TransactionSortAttributeEnum,
      direction: event.order === 1 ? Direction.ASC : Direction.DESC
    };

    this.loadTransactions();
  }

  onClickEdit(transaction: Transaction) {
    if (transaction.transferId != null) {
      this.transactionService.getTransactionsByTransferId(transaction.transferId).subscribe({
        next: (transfer: Transfer) => this.transactionFormDialog.open(transfer, TransactionFormDialogOptions.TRANSFER),
        error: () => {
          this.messageService.add({
            severity: 'error',
            detail: 'Failed to load transfer.'
          });
        }
      })
    } else if (transaction.subscriptionId != null) {
      this.transactionFormDialog.open(transaction, TransactionFormDialogOptions.SUBSCRIPTION);
    } else {
      this.transactionFormDialog.open(transaction, transaction.amount < 0 ? TransactionFormDialogOptions.EXPENSE : TransactionFormDialogOptions.INCOME);
    }
  }

  onClickDetails(transaction
                 :
                 Transaction
  ) {
    if (transaction.transferId != null) {
      this.transactionService.getTransactionsByTransferId(transaction.transferId).subscribe({
        next: (transfer: Transfer) => this.transferTableDialog.open(transfer),
        error: () => {
          this.messageService.add({
            severity: 'error',
            detail: 'Failed to load transfer.'
          });
        }
      })
    }
  }
}
