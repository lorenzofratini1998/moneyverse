import {inject, Injectable} from '@angular/core';
import {FormHandler} from '../../../../../shared/models/form.model';
import {Transfer} from '../../../transaction.model';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {getUTCDate, today} from '../../../../../shared/utils/date.utils';
import {PreferenceStore} from '../../../../../shared/stores/preference.store';
import {TransferFormData} from "../models/form.model";

@Injectable({
  providedIn: 'root'
})
export class TransferFormHandler implements FormHandler<Transfer, TransferFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly preferenceStore = inject(PreferenceStore);

  create(): FormGroup {
    return this.fb.group({
      transferId: [null],
      date: [null, Validators.required],
      amount: [null, Validators.required],
      currency: [null, Validators.required],
      fromAccount: [null, Validators.required],
      toAccount: [null, Validators.required],
    })
  }

  patch(form: FormGroup, transfer: Transfer): void {
    form.patchValue({
      transferId: transfer.transferId,
      date: new Date(transfer.date),
      amount: transfer.amount,
      currency: transfer.currency,
      fromAccount: transfer.transactionFrom.accountId,
      toAccount: transfer.transactionTo.accountId
    });
  }

  reset(form: FormGroup): void {
    form.reset({
      transferId: null,
      date: today(),
      amount: null,
      currency: this.preferenceStore.userCurrency(),
      fromAccount: null,
      toAccount: null
    });
    form.markAsPristine();
    form.markAsUntouched();
  }

  prepareData(form: FormGroup): TransferFormData {
    const value = form.value;
    return {
      transferId: value.transferId,
      date: getUTCDate(value.date.getFullYear(), value.date.getMonth(), value.date.getDate()),
      amount: value.amount,
      currency: value.currency,
      fromAccount: value.fromAccount,
      toAccount: value.toAccount
    };
  }

}
