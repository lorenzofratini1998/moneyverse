import {inject, Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Account} from '../../../account.model';
import {FormHandler} from '../../../../../shared/models/form.model';
import {PreferenceStore} from '../../../../../shared/stores/preference.store';
import {AccountFormData} from '../models/form.model';
import {IconsEnum} from '../../../../../shared/models/icons.model';

@Injectable({
  providedIn: 'root'
})
export class AccountFormHandler implements FormHandler<Account, AccountFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly preferenceStore = inject(PreferenceStore);

  create(): FormGroup {
    return this.fb.group({
      accountId: [null],
      accountName: [null, Validators.required],
      accountDescription: [null],
      accountCategory: [null, Validators.required],
      balance: [0.00],
      target: [null],
      currency: [this.preferenceStore.userCurrency(), Validators.required],
      isDefault: [null],
      color: ['yellow'],
      icon: [IconsEnum.CREDIT_CARD]
    });
  }

  patch(form: FormGroup, account: Account): void {
    form.patchValue({
        accountId: account.accountId,
        accountName: account.accountName,
        accountDescription: account.accountDescription,
        accountCategory: parseInt(account.accountCategory),
        balance: account.balance.toFixed(2),
        target: account.balanceTarget ? account.balanceTarget.toFixed(2) : null,
        currency: account.currency,
        isDefault: account.default,
      color: account.style.color,
      icon: account.style.icon
      }
    )
  }

  reset(form: FormGroup): void {
    form.reset({
      accountId: null,
      accountName: null,
      accountDescription: null,
      accountCategory: null,
      balance: 0.0,
      target: null,
      currency: this.preferenceStore.userCurrency(),
      isDefault: null,
      color: 'yellow',
      icon: IconsEnum.CREDIT_CARD
    });
    form.markAsPristine();
    form.markAsUntouched();
  }

  prepareData(form: FormGroup): AccountFormData {
    const value = form.value;
    return {
      accountId: value.accountId,
      accountName: value.accountName,
      accountDescription: value.accountDescription,
      accountCategory: value.accountCategory,
      balance: value.balance ?? 0.0,
      balanceTarget: value.target,
      currency: value.currency,
      isDefault: value.isDefault,
      style: {
        color: value.color,
        icon: value.icon
      }
    };
  }

}
