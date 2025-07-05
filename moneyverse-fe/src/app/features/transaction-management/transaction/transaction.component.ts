import {Component, computed, inject, ViewChild} from '@angular/core';
import {AuthService} from '../../../core/auth/auth.service';
import {MessageService} from '../../../shared/services/message.service';
import {combineLatest, shareReplay, startWith, Subject, switchMap} from 'rxjs';
import {IconsEnum} from '../../../shared/models/icons.model';
import {toSignal} from '@angular/core/rxjs-interop';
import {TransactionService} from '../transaction.service';
import {Transaction, TransactionRequest} from '../transaction.model';
import {TransactionTableComponent} from './components/transaction-table/transaction-table.component';
import {SvgComponent} from '../../../shared/components/svg/svg.component';
import {TransactionStore} from '../transaction.store';
import {CurrencyService} from '../../../shared/services/currency.service';
import {CategoryService} from '../../category/category.service';
import {CurrencyDto} from '../../../shared/models/currencyDto';
import {Category} from '../../category/category.model';
import {DialogComponent} from '../../../shared/components/dialog/dialog.component';
import {TransactionFormComponent} from './components/transaction-form/transaction-form.component';
import {ToastEnum} from '../../../shared/components/toast/toast.component';
import {AccountService} from '../../account/account.service';
import {Account} from '../../account/account.model';

@Component({
  selector: 'app-transaction',
  imports: [
    TransactionTableComponent,
    SvgComponent,
    DialogComponent,
    TransactionFormComponent
  ],
  templateUrl: './transaction.component.html',
  styleUrl: './transaction.component.scss'
})
export class TransactionComponent {
  protected readonly Icons = IconsEnum;
  protected readonly transactionStore = inject(TransactionStore);
  private readonly transactionService = inject(TransactionService);
  private readonly accountService = inject(AccountService);
  private readonly categoryService = inject(CategoryService);
  private readonly currencyService = inject(CurrencyService);
  private readonly authService = inject(AuthService);
  private readonly messageService = inject(MessageService);
  private readonly refreshTransactions$ = new Subject<void>();
  private readonly userId$ = this.authService.getUserId().pipe(shareReplay(1));
  @ViewChild(TransactionFormComponent) transactionForm!: TransactionFormComponent

  transactions$ = toSignal(
    combineLatest([
      this.userId$,
      this.refreshTransactions$.pipe(startWith(null))
    ]).pipe(
      switchMap(([userId]) => this.transactionService.getTransactionsByUser(userId))
    ),
    {initialValue: [] as Transaction[]}
  )

  accounts$ = toSignal(
    this.userId$.pipe(
      switchMap(userId => this.accountService.getAccounts(userId))
    ),
    {initialValue: [] as Account[]}
  )

  categories$ = toSignal(
    this.userId$.pipe(
      switchMap(userId => this.categoryService.getCategoriesByUser(userId))
    ),
    {initialValue: [] as Category[]}
  )

  readonly enrichedTransactions = computed(() => {
    const txs = this.transactions$();
    const accounts = this.accounts$();
    const categories = this.categories$();
    return txs.map(tx => this.transactionService.enrichTransaction(tx, accounts, categories));
  });

  currencies$ = toSignal(this.currencyService.getCurrencies(), {initialValue: [] as CurrencyDto[]});

  saveTransaction(formData: any) {
    this.userId$.pipe(
      switchMap(userId => {
        return this.transactionService.createTransaction(this.createTransactionRequest(userId, formData))
      })
    ).subscribe({
      next: () => {
        this.transactionStore.closeForm();
        this.messageService.showMessage({
          type: ToastEnum.SUCCESS,
          message: 'Transaction created successfully.'
        });
        this.transactionForm.reset();
        this.refreshTransactions$.next();
      },
      error: () => {
        this.messageService.showMessage({
          type: ToastEnum.ERROR,
          message: 'Error during the transaction creation.'
        });
      }
    })
  }

  createTransactionRequest(userId: string, formData: any): TransactionRequest {
    return {
      userId: userId,
      transactions: [
        {
          accountId: formData.account,
          categoryId: formData.category,
          amount: formData.amount,
          date: formData.date,
          description: formData.description,
          currency: formData.currency,
          tags: formData.tags
        }
      ]
    };
  }
}
