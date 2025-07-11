import {Component, inject, input, output, signal} from '@angular/core';
import {Dialog} from 'primeng/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DatePicker} from 'primeng/datepicker';
import {FloatLabel} from 'primeng/floatlabel';
import {InputNumber} from 'primeng/inputnumber';
import {LanguageService} from '../../../../../../shared/services/language.service';
import {Select} from 'primeng/select';
import {CategoryStore} from '../../../../category.store';
import {CurrencyStore} from '../../../../../../shared/stores/currency.store';
import {Message} from 'primeng/message';
import {Button} from 'primeng/button';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {Budget, BudgetForm} from '../../../../category.model';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';

@Component({
  selector: 'app-budget-form-dialog',
  imports: [
    Dialog,
    ReactiveFormsModule,
    DatePicker,
    FloatLabel,
    InputNumber,
    Select,
    Message,
    Button,
    SvgComponent,
  ],
  templateUrl: './budget-form-dialog.component.html',
  styleUrl: './budget-form-dialog.component.scss'
})
export class BudgetFormDialogComponent {
  protected readonly languageService = inject(LanguageService);
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly currencyStore = inject(CurrencyStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly Icons = IconsEnum;
  private readonly fb = inject(FormBuilder);
  isOpen = input<boolean>(false);
  protected _isOpen = false;
  protected _budgetToEdit = signal<Budget | null>(null);

  submitted = output<BudgetForm>();

  budgetForm: FormGroup;

  constructor() {
    this.budgetForm = this.createForm();
  }

  protected set currency(currency: string) {
    this.budgetForm.get('currency')?.setValue(currency);
  }

  open(budget?: Budget) {
    this._isOpen = true;
    if (budget) {
      this._budgetToEdit.set(budget);
      this.patchForm(budget);
    } else {
      this._budgetToEdit.set(null);
      this.reset();
    }
  }


  close() {
    this._isOpen = false;
    this._budgetToEdit.set(null);
    this.reset();
  }

  private createForm() {
    return this.fb.group({
      rangeDates: [[this.startDate, this.endDate], Validators.required],
      budgetLimit: [null, Validators.required],
      amount: [null],
      category: [null, Validators.required],
      currency: [null, Validators.required]
    });
  }

  private patchForm(budget: Budget) {
    this.budgetForm.patchValue({
      rangeDates: [new Date(budget.startDate), new Date(budget.endDate)],
      budgetLimit: budget.budgetLimit,
      amount: budget.amount,
      category: budget.category.categoryId,
      currency: budget.currency
    });
  }

  protected saveBudget() {
    if (this.budgetForm.valid) {
      const formValue = this.budgetForm.value;
      this.submitted.emit({
        budgetId: this._budgetToEdit()?.budgetId,
        formData: {
          startDate: this.clearDate(formValue.rangeDates[0]),
          endDate: this.clearDate(formValue.rangeDates[1]),
          budgetLimit: formValue.budgetLimit,
          amount: formValue.amount,
          category: formValue.category,
          currency: formValue.currency
        }
      });
      this.close();
    } else {
      Object.keys(this.budgetForm.controls).forEach(key => {
        const control = this.budgetForm.get(key);
        control?.markAsTouched();
        control?.markAsDirty();
      });
    }
  }

  private clearDate(date: Date): Date {
    return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()))
  }

  reset() {
    this.budgetForm.reset({
      rangeDates: [this.startDate, this.endDate],
      budgetLimit: null,
      amount: null,
      category: null,
      currency: this.preferenceStore.userCurrency()
    });
    this.budgetForm.markAsPristine();
    this.budgetForm.markAsUntouched();
  }

  private get startDate() {
    const today = new Date();
    return new Date(Date.UTC(today.getFullYear(), today.getMonth(), 1));
  }

  private get endDate() {
    const today = new Date();
    return new Date(Date.UTC(today.getFullYear(), today.getMonth() + 1, 0));
  }

  protected cancel() {
    this.reset();
    this.close();
  }

  protected isInvalid(controlName: string) {
    const control = this.budgetForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }
}
