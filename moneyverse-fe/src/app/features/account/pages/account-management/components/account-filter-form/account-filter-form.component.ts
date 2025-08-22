import {Component, effect, inject, input} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {AccountCriteria} from "../../../../account.model";
import {AccountFilterFormHandler} from '../../services/account-filter-form.handler';
import {Checkbox} from 'primeng/checkbox';
import {
  CurrencyMultiSelectComponent
} from '../../../../../../shared/components/forms/currency-multi-select/currency-multi-select.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {InputNumberComponent} from '../../../../../../shared/components/forms/input-number/input-number.component';
import {MultiSelectComponent} from '../../../../../../shared/components/forms/multi-select/multi-select.component';
import {AccountFilterStore} from '../../services/account-filter.store';
import {AccountStore} from '../../../../services/account.store';
import {AccountFilterFormData} from '../../models/form.model';

@Component({
  selector: 'app-account-filter-form',
  imports: [
    Checkbox,
    CurrencyMultiSelectComponent,
    FormsModule,
    InputNumberComponent,
    MultiSelectComponent,
    ReactiveFormsModule
  ],
  templateUrl: './account-filter-form.component.html'
})
export class AccountFilterFormComponent extends AbstractFormComponent<AccountCriteria, AccountFilterFormData> {
  showTargetBalanceSlider = input.required<boolean>();

  protected override formHandler = inject(AccountFilterFormHandler);
  protected readonly accountStore = inject(AccountStore);
  protected readonly accountFilterStore = inject(AccountFilterStore);

  constructor() {
    super();
    effect(() => {
      const criteria = this.accountFilterStore.criteria();
      this.patch(criteria);
    })
  }

  override submit(): void {
    const formData: AccountFilterFormData = this.prepareData();
    this.accountFilterStore.updateFilters({
      accountCategories: formData.accountCategories,
      currencies: formData.currencies,
      balance: formData.balance ? {
        lower: formData.balance.lower,
        upper: formData.balance.upper
      } : undefined,
      balanceTarget: formData.balanceTarget ? {
        lower: formData.balanceTarget?.lower,
        upper: formData.balanceTarget?.upper
      } : undefined,
      isDefault: formData.isDefault ?? undefined
    })
  }
}
