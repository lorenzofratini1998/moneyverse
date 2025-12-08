import {inject, Injectable} from '@angular/core';
import {FormHandler} from '../../../../../shared/models/form.model';
import {recurrenceRuleOptions, SubscriptionTransaction} from '../../../transaction.model';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {getUTCDate, today} from '../../../../../shared/utils/date.utils';
import {PreferenceStore} from '../../../../../shared/stores/preference.store';
import {AccountStore} from '../../../../account/services/account.store';
import {SubscriptionFormData} from "../models/form.model";

@Injectable({
  providedIn: 'root'
})
export class SubscriptionFormHandler implements FormHandler<SubscriptionTransaction, SubscriptionFormData> {
  private readonly fb = inject(FormBuilder);
  private readonly preferenceStore = inject(PreferenceStore);
  private readonly accountStore = inject(AccountStore);

  create(): FormGroup {
    return this.fb.group({
      subscriptionId: [null],
      subscriptionName: [null, Validators.required],
      amount: [null, Validators.required],
      currency: [this.preferenceStore.userCurrency(), Validators.required],
      recurrence: this.fb.group({
        recurrenceRule: [recurrenceRuleOptions.find(option => option.default)?.value, Validators.required],
        startDate: [today(), Validators.required],
        endDate: [null]
      }),
      account: [null, Validators.required],
      category: [null],
      active: [null],
      nextExecutionDate: [null],
    });
  }

  patch(form: FormGroup, subscription: SubscriptionTransaction): void {
    form.patchValue({
      subscriptionId: subscription.subscriptionId,
      subscriptionName: subscription.subscriptionName,
      amount: subscription.amount,
      currency: subscription.currency,
      recurrence: {
        recurrenceRule: subscription.recurrenceRule,
        startDate: new Date(subscription.startDate),
        endDate: subscription.endDate ? new Date(subscription.endDate) : null
      },
      account: subscription.accountId,
      category: subscription.categoryId,
      active: subscription.active,
      nextExecutionDate: new Date(subscription.nextExecutionDate)
    });
  }

  reset(form: FormGroup): void {
    form.reset({
      subscriptionId: null,
      subscriptionName: null,
      amount: null,
      currency: this.preferenceStore.userCurrency(),
      recurrence: {
        recurrenceRule: recurrenceRuleOptions.find(o => o.default)?.value,
        startDate: today(),
        endDate: null
      },
      account: this.accountStore.defaultAccount()?.accountId,
      category: null,
      active: null
    });
    form.markAsPristine();
    form.markAsUntouched();
  }

  prepareData(form: FormGroup): SubscriptionFormData {
    const value = form.value;
    const startDate = value.recurrence.startDate;
    const endDate = value.recurrence.endDate;
    const nextExecutionDate = value.nextExecutionDate;
    return {
      subscriptionId: value.subscriptionId,
      subscriptionName: value.subscriptionName,
      amount: value.amount,
      currency: value.currency,
      recurrence: {
        recurrenceRule: value.recurrence.recurrenceRule,
        startDate: getUTCDate(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()),
        endDate: endDate ? getUTCDate(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()) : undefined
      },
      accountId: value.account,
      categoryId: value.category,
      isActive: value.active,
      nextExecutionDate: nextExecutionDate ? getUTCDate(nextExecutionDate.getFullYear(), nextExecutionDate.getMonth(), nextExecutionDate.getDate()) : undefined
    };
  }

}
