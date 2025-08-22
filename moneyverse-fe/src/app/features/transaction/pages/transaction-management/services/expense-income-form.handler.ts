import {inject, Injectable} from '@angular/core';
import {FormHandler} from '../../../../../shared/models/form.model';
import {Transaction} from '../../../transaction.model';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {today} from '../../../../../shared/utils/date.utils';
import {PreferenceStore} from '../../../../../shared/stores/preference.store';
import {AccountStore} from '../../../../account/services/account.store';
import {TransactionFormData} from '../models/form.model';

@Injectable({
  providedIn: 'root'
})
export class ExpenseIncomeFormHandler implements FormHandler<Transaction, TransactionFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly preferenceStore = inject(PreferenceStore);
  private readonly accountStore = inject(AccountStore);

  create(): FormGroup {
    return this.fb.group({
      transactionId: [null],
      date: [null, Validators.required],
      amount: [null, Validators.required],
      description: [null, Validators.required],
      account: [null, Validators.required],
      category: [null],
      currency: [null, Validators.required],
      tags: [null]
    })
  }

  patch(form: FormGroup, transaction: Transaction): void {
    form.patchValue({
      transactionId: transaction.transactionId,
      date: new Date(transaction.date),
      amount: Math.abs(transaction.amount),
      description: transaction.description,
      account: transaction.accountId,
      category: transaction.categoryId,
      currency: transaction.currency,
      tags: transaction.tags
    });
  }

  reset(form: FormGroup): void {
    form.reset({
      transactionId: null,
      date: today(),
      amount: null,
      description: null,
      account: this.accountStore.defaultAccount()?.accountId,
      category: null,
      currency: this.preferenceStore.userCurrency(),
      tags: null
    });
    form.markAsPristine();
    form.markAsUntouched();
  }

  prepareData(form: FormGroup): TransactionFormData {
    const value = form.value;
    return {
      transactionId: value.transactionId,
      date: value.date,
      amount: value.amount,
      description: value.description,
      accountId: value.account,
      categoryId: value.category,
      currency: value.currency,
      tags: value.tags
    };
  }

}
