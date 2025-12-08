import {Component, computed, effect, inject} from '@angular/core';
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
import {TranslationService} from '../../../../../../shared/services/translation.service';
import {TranslatePipe} from '@ngx-translate/core';

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
    SelectButtonComponent,
    TranslatePipe
  ],
  templateUrl: './transaction-filter-form.component.html'
})
export class TransactionFilterFormComponent extends AbstractFormComponent<TransactionCriteria, TransactionFilterFormData> {

  protected override formHandler = inject(TransactionFilterFormHandler);
  protected readonly transactionStore = inject(TransactionStore);
  private readonly translateService = inject(TranslationService);

  expenseIncomeOptions = computed(() => {
    this.translateService.lang();
    return [
      {label: this.translateService.translate('app.expense'), value: TransactionCriteriaTypeEnum.EXPENSE},
      {label: this.translateService.translate('app.income'), value: TransactionCriteriaTypeEnum.INCOME}
    ];
  })

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
      type: formData.type,
      accounts: formData.accounts,
      categories: formData.categories,
      date: formData.date ? {
        start: formData.date.start,
        end: formData.date.end,
      } : undefined,
      amount: formData.amount ?? undefined,
      tags: formData.tags,
      budget: formData.budget,
      subscription: formData.subscription ?? undefined,
      transfer: formData.transfer ?? undefined
    });
  }

}
