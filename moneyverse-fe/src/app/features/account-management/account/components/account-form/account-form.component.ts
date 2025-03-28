import {Component, computed, effect, inject, input, output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Account, AccountCategory} from '../../../account.model';
import {CurrencyDto} from '../../../../../shared/models/currencyDto';
import {SvgIconComponent} from 'angular-svg-icon';

@Component({
  selector: 'app-account-form',
  imports: [
    ReactiveFormsModule,
    SvgIconComponent,
  ],
  templateUrl: './account-form.component.html',
  styleUrl: './account-form.component.scss'
})
export class AccountFormComponent {
  private readonly fb = inject(FormBuilder);
  categories = input.required<AccountCategory[]>();
  currencies = input.required<CurrencyDto[]>();
  account = input.required<Account | null>();
  currency = input.required<string>();
  save = output<any>();
  cancel = output<any>();

  accountForm: FormGroup = this.createForm();
  isEditMode = computed(() => !!this.account());

  constructor() {
    effect(() => {
      const currentAccount = this.account();
      if (currentAccount) {
        this.patchForm(currentAccount);
      } else {
        this.resetForm();
      }
    })

    effect(() => {
      const currency = this.currency();
      if (!this.isEditMode) {
        this.accountForm.get('currency')?.setValue(currency);
      }
    })
  }

  private createForm(): FormGroup {
    return this.fb.group({
      accountName: ['', Validators.required],
      accountDescription: [null],
      accountCategory: ['', Validators.required],
      balance: [0.00],
      target: [null],
      currency: ['', Validators.required],
      isDefault: [null]
    });
  }

  private patchForm(account: Account): void {
    const form = this.accountForm;
    form.patchValue({
        accountName: account.accountName,
        accountDescription: account.accountDescription,
        accountCategory: account.accountCategory,
        balance: account.balance.toFixed(2),
        target: account.balanceTarget ? account.balanceTarget.toFixed(2) : null,
        currency: account.currency,
        isDefault: account.default
      }
    )
  }

  onBlurBalance(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = parseFloat(input.value);
    if (!isNaN(value)) {
      const formattedValue = value.toFixed(2);
      input.value = formattedValue;
      this.accountForm.get('balance')?.setValue(formattedValue);
    }
  }

  onBlurTarget(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = parseFloat(input.value);
    if (!isNaN(value)) {
      const formattedValue = value.toFixed(2);
      input.value = formattedValue;
      this.accountForm.get('target')?.setValue(formattedValue);
    }
  }

  saveAccount(): void {
    const form = this.accountForm;
    if (form.valid) {
      this.save.emit(form.value);
      this.resetForm();
    } else {
      Object.keys(form.controls).forEach(key => {
        form.get(key)?.markAsTouched();
        form.get(key)?.markAsDirty();
      })
    }
  }

  resetForm() {
    this.accountForm.reset({
      accountName: '',
      accountDescription: null,
      accountCategory: '',
      balance: 0.0,
      target: null,
      currency: this.currency(),
      isDefault: null
    });
  }

  cancelForm(): void {
    this.cancel.emit({});
  }
}
