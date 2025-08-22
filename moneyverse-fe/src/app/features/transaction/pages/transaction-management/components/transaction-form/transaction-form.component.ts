import {Component, computed, effect, inject, input, viewChild} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {Transaction, Transfer} from '../../../../transaction.model';
import {TransactionFormDialogOptionsEnum} from '../transaction-form-dialog/transaction-form-dialog.component';
import {TransferFormHandler} from '../../services/transfer-form.handler';
import {ExpenseIncomeFormHandler} from '../../services/expense-income-form.handler';
import {ExpenseIncomeFormComponent} from '../expense-income-form/expense-income-form.component';
import {TransferFormComponent} from '../transfer-form/transfer-form.component';
import {TransactionFormData, TransferFormData} from "../../models/form.model";

@Component({
  selector: 'app-transaction-form',
  imports: [
    ExpenseIncomeFormComponent,
    TransferFormComponent
  ],
  templateUrl: './transaction-form.component.html'
})
export class TransactionFormComponent extends AbstractFormComponent<Transaction | Transfer, TransactionFormData | TransferFormData> {

  formType = input<TransactionFormDialogOptionsEnum>(TransactionFormDialogOptionsEnum.EXPENSE);

  isExpenseOrIncome = computed(() => this.formType() === TransactionFormDialogOptionsEnum.EXPENSE || this.formType() === TransactionFormDialogOptionsEnum.INCOME);
  isTransfer = computed(() => this.formType() === TransactionFormDialogOptionsEnum.TRANSFER);
  isSubscription = computed(() => this.formType() === TransactionFormDialogOptionsEnum.SUBSCRIPTION);

  transactionToEdit = computed(() => this.selectedItem() as Transaction | null);
  transferToEdit = computed(() => this.selectedItem() as Transfer | null);

  protected expenseIncomeForm = viewChild(ExpenseIncomeFormComponent);
  protected transferForm = viewChild(TransferFormComponent);

  private readonly expenseIncomeFormHandler = inject(ExpenseIncomeFormHandler);
  private readonly transferFormHandler = inject(TransferFormHandler);

  constructor() {
    super();
    effect(() => {
      const expenseForm = this.expenseIncomeForm();
      if (expenseForm) {
        expenseForm.onSubmit
          .subscribe(data => this.onSubmit.emit(data));
      }
    });

    effect(() => {
      const transferForm = this.transferForm();
      if (transferForm) {
        transferForm.onSubmit
          .subscribe(data => this.onSubmit.emit(data));
      }
    });
  }

  protected readonly _formHandler = computed(() => {
    return this.isExpenseOrIncome() || this.isSubscription() ? this.expenseIncomeFormHandler : this.transferFormHandler;
  });

  protected override readonly formHandler = this._formHandler();

}
