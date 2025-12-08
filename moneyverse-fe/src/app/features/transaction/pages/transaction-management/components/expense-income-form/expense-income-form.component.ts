import {Component, computed, effect, inject, input} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TagStore} from '../../../tag-management/services/tag.store';
import {Transaction} from '../../../../transaction.model';
import {TransactionFormDialogOptionsEnum} from '../transaction-form-dialog/transaction-form-dialog.component';
import {DatePickerComponent} from '../../../../../../shared/components/forms/date-picker/date-picker.component';
import {
  AmountInputNumberComponent
} from '../../../../../../shared/components/forms/amount-input-number/amount-input-number.component';
import {
  CurrencySelectComponent
} from '../../../../../../shared/components/forms/currency-select/currency-select.component';
import {TextAreaComponent} from '../../../../../../shared/components/forms/text-area/text-area.component';
import {
  AccountSelectComponent
} from '../../../../../../shared/components/forms/account-select/account-select.component';
import {
  CategorySelectComponent
} from '../../../../../../shared/components/forms/category-select/category-select.component';
import {
  TagMultiSelectComponent
} from '../../../../../../shared/components/forms/tag-multi-select/tag-multi-select.component';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {ExpenseIncomeFormHandler} from '../../services/expense-income-form.handler';
import {TransactionFormData} from "../../models/form.model";
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-expense-income-form',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    DatePickerComponent,
    AmountInputNumberComponent,
    CurrencySelectComponent,
    TextAreaComponent,
    AccountSelectComponent,
    CategorySelectComponent,
    TagMultiSelectComponent,
    TranslatePipe
  ],
  templateUrl: './expense-income-form.component.html'
})
export class ExpenseIncomeFormComponent extends AbstractFormComponent<Transaction, TransactionFormData> {

  transactionToEdit = input<Transaction | null>(null);
  transactionFormOption = input<TransactionFormDialogOptionsEnum | null>(null);

  protected override readonly formHandler = inject(ExpenseIncomeFormHandler)
  protected readonly tagStore = inject(TagStore);

  isSubscription = computed(() => this.transactionFormOption() === TransactionFormDialogOptionsEnum.SUBSCRIPTION);
  isExpense = computed(() => this.transactionFormOption() === TransactionFormDialogOptionsEnum.EXPENSE);

  constructor() {
    super();
    effect(() => {
      const _transactionToEdit = this.transactionToEdit();
      if (_transactionToEdit) {
        this.patch(_transactionToEdit);
      } else {
        this.reset();
      }
    })
  }

  override patch(transaction: Transaction): void {
    super.patch(transaction);
    const controls = ['data', 'description', 'currency', 'category']
    if (this.isSubscription()) {
      this.disableControls(controls);
    } else {
      this.enableControls(controls);
    }
  }

  protected override prepareData(): TransactionFormData {
    const data = super.prepareData();
    return {
      ...data,
      amount: this.isExpense() || this.isSubscription() ? -Math.abs(data.amount) : Math.abs(data.amount)
    }
  }
}
