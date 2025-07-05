import {Component, computed, effect, inject, input, output} from '@angular/core';
import {Category} from '../../../../category/category.model';
import {CurrencyDto} from '../../../../../shared/models/currencyDto';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {SvgComponent} from '../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../shared/models/icons.model';
import {Account} from '../../../../account/account.model';

@Component({
  selector: 'app-transaction-form',
  imports: [
    ReactiveFormsModule,
    SvgComponent
  ],
  templateUrl: './transaction-form.component.html',
  styleUrl: './transaction-form.component.scss'
})
export class TransactionFormComponent {
  protected readonly Icons = IconsEnum;
  categories = input.required<Category[]>();
  accounts = input.required<Account[]>();
  currencies = input.required<CurrencyDto[]>();

  defaultAccount = computed(() => this.accounts().find(a => a.default)!);
  private readonly fb = inject(FormBuilder);
  cancel = output<any>();
  save = output<any>();
  transactionForm: FormGroup = this.createForm();

  private createForm(): FormGroup {
    const today = new Date().toISOString().substring(0, 10);
    return this.fb.group({
      description: ['', Validators.required],
      amount: ['', Validators.required],
      category: ['', Validators.required],
      account: [0.00, Validators.required],
      currency: ['', Validators.required],
      date: [today, Validators.required]
    })
  }

  constructor() {
    effect(() => {
      const {accountId, currency} = this.defaultAccount();
      this.transactionForm.patchValue({
        account: accountId,
        currency: currency
      }, {emitEvent: false});
    });
  }

  protected onBlurAmount(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = parseFloat(input.value);
    if (!isNaN(value)) {
      const formattedValue = value.toFixed(2);
      input.value = formattedValue;
      this.transactionForm.get('amount')?.setValue(formattedValue);
    }
  }

  saveTransaction() {
    const form = this.transactionForm;
    if (form.valid) {
      this.save.emit(form.value)
    } else {
      Object.keys(form.controls).forEach(key => {
        form.get(key)?.markAsTouched();
        form.get(key)?.markAsDirty();
      })
    }
  }

  cancelForm(): void {
    this.cancel.emit({});
    this.reset();
  }

  reset() {
    const {accountId, currency} = this.defaultAccount();
    const today = new Date().toISOString().substring(0, 10);
    this.transactionForm.reset({
      description: '',
      amount: 0.00,
      category: '',
      account: accountId,
      currency: currency,
      date: today
    })
  }

  get date() {
    return this.transactionForm.get('date');
  }

  get description() {
    return this.transactionForm.get('description');
  }

  get account() {
    return this.transactionForm.get('account');
  }

  get category() {
    return this.transactionForm.get('category');
  }
}
