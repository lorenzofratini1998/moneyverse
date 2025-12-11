import {inject, Injectable} from '@angular/core';
import {TransactionFilterFormData} from '../models/form.model';
import {TransactionCriteria, TransactionCriteriaTypeEnum} from '../../../transaction.model';
import {FormHandler} from '../../../../../shared/models/form.model';
import {FormBuilder, FormGroup} from "@angular/forms";
import {TransactionStore} from './transaction.store';

@Injectable({
  providedIn: 'root'
})
export class TransactionFilterFormHandler implements FormHandler<TransactionCriteria, TransactionFilterFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly transactionStore = inject(TransactionStore);

  create(): FormGroup {
    return this.fb.group({
      type: [TransactionCriteriaTypeEnum.EXPENSE],
      accounts: [null],
      categories: [null],
      date: this.fb.group({
        start: [null],
        end: [null],
      }),
      amount: this.fb.group({
        lower: [null],
        upper: [null],
      }),
      tags: [null],
      budget: [null],
      subscription: [null],
      transfer: [null],
    })
  }

  patch(form: FormGroup, data: TransactionCriteria): void {
    form.patchValue({
      //type: this.inferTypeFromAmount(data),
      type: data.type,
      accounts: data.accounts ?? null,
      categories: data.categories ?? null,
      date: {
        start: data.date?.start ?? null,
        end: data.date?.end ?? null,
      },
      amount: {
        lower: data.amount ? (data.amount.lower ? Math.abs(data.amount.lower) : null) : null,
        upper: data.amount ? (data.amount.upper ? Math.abs(data.amount.upper) : null) : null,
      },
      tags: data.tags ?? null,
      budget: data.budget ?? null,
      subscription: data.subscription ?? null,
      transfer: data.transfer ?? null,
    }, {emitEvent: false});
  }

  private inferTypeFromAmount(data: TransactionCriteria): TransactionCriteriaTypeEnum | null {
    if (!data.amount) {
      return null;
    }
    const {lower, upper} = data.amount;
    if ((lower == null || lower <= 0) && (upper == null || upper <= 0)) {
      return TransactionCriteriaTypeEnum.EXPENSE;
    }
    if ((lower == null || lower >= 0) && (upper == null || upper >= 0)) {
      return TransactionCriteriaTypeEnum.INCOME;
    }
    return null;
  }

  reset(form: FormGroup): void {
    this.transactionStore.resetFilters();
  }

  prepareData(form: FormGroup): TransactionFilterFormData {
    return form.value as TransactionFilterFormData;
  }

}
