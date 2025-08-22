import {inject, Injectable} from '@angular/core';
import {AccountCriteria} from '../../../account.model';
import {FormHandler} from '../../../../../shared/models/form.model';
import {FormBuilder, FormGroup} from "@angular/forms";
import {AccountFilterStore} from './account-filter.store';
import {AccountFilterFormData} from '../models/form.model';

@Injectable({
  providedIn: 'root'
})
export class AccountFilterFormHandler implements FormHandler<AccountCriteria, AccountFilterFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly accountFilterStore = inject(AccountFilterStore);

  create(): FormGroup {
    return this.fb.group({
      accountCategories: null,
      currencies: null,
      balance: this.fb.group({
        lower: null,
        upper: null
      }),
      balanceTarget: this.fb.group({
        lower: null,
        upper: null
      }),
      isDefault: null
    });
  }

  patch(form: FormGroup, criteria: AccountCriteria): void {
    form.patchValue({
      accountCategories: criteria.accountCategories ?? null,
      currencies: criteria.currencies ?? null,
      balance: {
        lower: criteria.balance?.lower ?? null,
        upper: criteria.balance?.upper ?? null
      },
      balanceTarget: {
        lower: criteria.balanceTarget?.lower ?? null,
        upper: criteria.balanceTarget?.upper ?? null
      },
      isDefault: criteria.isDefault ?? null
    }, {emitEvent: false});
  }

  reset(form: FormGroup): void {
    this.accountFilterStore.resetFilters();
  }

  prepareData(form: FormGroup): AccountFilterFormData {
    return form.value as AccountFilterFormData;
  }

}
