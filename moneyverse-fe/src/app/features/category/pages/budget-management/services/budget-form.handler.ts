import {inject, Injectable} from '@angular/core';
import {FormHandler} from '../../../../../shared/models/form.model';
import {Budget} from '../../../category.model';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {firstDayOfCurrentMonth, getUTCDate, lastDayOfCurrentMonth} from '../../../../../shared/utils/date.utils';
import {PreferenceStore} from '../../../../../shared/stores/preference.store';
import {BudgetFormData} from '../models/form.models';

@Injectable({
  providedIn: 'root'
})
export class BudgetFormHandler implements FormHandler<Budget, BudgetFormData> {
  private readonly fb = inject(FormBuilder);
  private preferenceStore = inject(PreferenceStore);

  create(): FormGroup {
    return this.fb.group({
      budgetId: [null],
      rangeDates: [[firstDayOfCurrentMonth(), lastDayOfCurrentMonth()], Validators.required],
      budgetLimit: [null, Validators.required],
      amount: [null],
      category: [null, Validators.required],
      currency: [this.preferenceStore.userCurrency(), Validators.required]
    });
  }

  patch(form: FormGroup, budget: Budget): void {
    form.patchValue({
      budgetId: budget.budgetId,
      rangeDates: [new Date(budget.startDate), new Date(budget.endDate)],
      budgetLimit: budget.budgetLimit,
      amount: budget.amount,
      category: budget.category.categoryId,
      currency: budget.currency
    });
  }

  prepareData(form: FormGroup): BudgetFormData {
    const value = form.value;
    const startDate: Date = value.rangeDates[0];
    const endDate: Date = value.rangeDates[1];
    return {
      budgetId: value.budgetId,
      startDate: getUTCDate(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()),
      endDate: getUTCDate(endDate.getFullYear(), endDate.getMonth(), endDate.getDate()),
      budgetLimit: value.budgetLimit,
      amount: value.amount,
      categoryId: value.category,
      currency: value.currency
    }
  }

  reset(form: FormGroup): void {
    form.reset({
      budgetId: null,
      rangeDates: [firstDayOfCurrentMonth(), lastDayOfCurrentMonth()],
      budgetLimit: null,
      amount: null,
      category: null,
      currency: this.preferenceStore.userCurrency()
    });
    form.markAsPristine();
    form.markAsUntouched();
  }

}
