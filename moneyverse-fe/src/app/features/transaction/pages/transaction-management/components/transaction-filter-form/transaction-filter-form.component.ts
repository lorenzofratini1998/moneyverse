import {Component, effect, inject} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {TransactionFilterFormData} from '../../models/form.model';
import {TransactionCriteria, TransactionCriteriaTypeEnum} from '../../../../transaction.model';
import {TransactionFilterFormHandler} from '../../services/transaction-filter-form.handler';
import {TransactionStore} from '../../services/transaction.store';
import {ReactiveFormsModule} from '@angular/forms';
import {
  AccountMultiSelectComponent
} from '../../../../../../shared/components/forms/account-multi-select/account-multi-select.component';
import {
  CategoryMultiSelectComponent
} from '../../../../../../shared/components/forms/category-multi-select/category-multi-select.component';
import {DatePickerComponent} from '../../../../../../shared/components/forms/date-picker/date-picker.component';
import {InputNumberComponent} from '../../../../../../shared/components/forms/input-number/input-number.component';
import {
  TagMultiSelectComponent
} from '../../../../../../shared/components/forms/tag-multi-select/tag-multi-select.component';
import {CheckboxComponent} from '../../../../../../shared/components/forms/checkbox/checkbox.component';
import {SelectButtonComponent} from '../../../../../../shared/components/forms/select-button/select-button.component';
import {BoundCriteria} from '../../../../../../shared/models/criteria.model';

@Component({
  selector: 'app-transaction-filter-form',
  imports: [
    ReactiveFormsModule,
    AccountMultiSelectComponent,
    CategoryMultiSelectComponent,
    DatePickerComponent,
    InputNumberComponent,
    TagMultiSelectComponent,
    CheckboxComponent,
    SelectButtonComponent
  ],
  templateUrl: './transaction-filter-form.component.html'
})
export class TransactionFilterFormComponent extends AbstractFormComponent<TransactionCriteria, TransactionFilterFormData> {

  protected override formHandler = inject(TransactionFilterFormHandler);
  protected readonly transactionStore = inject(TransactionStore);

  expenseIncomeOptions = [
    {label: 'Expense', value: TransactionCriteriaTypeEnum.EXPENSE},
    {label: 'Income', value: TransactionCriteriaTypeEnum.INCOME}
  ];

  constructor() {
    super();
    effect(() => {
      const criteria = this.transactionStore.criteria();
      this.patch(criteria);
    });
  }

  override submit(): void {
    const formData: TransactionFilterFormData = this.prepareData();
    this.transactionStore.updateFilters({
      accounts: formData.accounts,
      categories: formData.categories,
      date: formData.date ? {
        start: formData.date.start,
        end: formData.date.end,
      } : undefined,
      amount: this.prepareAmountFilter(formData),
      tags: formData.tags,
      budget: formData.budget,
      subscription: formData.subscription ?? undefined,
      transfer: formData.transfer ?? undefined
    });
  }

  private prepareAmountFilter(formData: TransactionFilterFormData): BoundCriteria | undefined {
    if (!formData.amount) {
      return undefined;
    }
    const amount = formData.amount;
    const type = formData.type
    if (!type) {
      return amount;
    }
    if (type === TransactionCriteriaTypeEnum.EXPENSE) {
      return {
        lower: amount?.upper ? -Math.abs(amount.upper) : undefined,
        upper: amount?.lower ? -Math.abs(amount.lower) : undefined
      }
    }
    if (type === TransactionCriteriaTypeEnum.INCOME) {
      return {
        lower: amount?.lower ? Math.abs(amount.lower) : 0,
        upper: amount?.upper ? Math.abs(amount.upper) : undefined
      }
    }
    return amount;
  }

}
