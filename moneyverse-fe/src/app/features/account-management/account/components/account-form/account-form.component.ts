import {Component, effect, inject, input, output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Account, AccountCategory} from '../../../account.model';
import {CurrencyDto} from '../../../../../shared/models/currencyDto';
import {SvgComponent} from '../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../shared/models/icons.model';

@Component({
  selector: 'app-account-form',
  imports: [
    ReactiveFormsModule,
    SvgComponent,
  ],
  templateUrl: './account-form.component.html',
  styleUrl: './account-form.component.scss'
})
export class AccountFormComponent {
  protected readonly Icons = IconsEnum;
  private readonly fb = inject(FormBuilder);
  categories = input.required<AccountCategory[]>();
  currencies = input.required<CurrencyDto[]>();
  account = input<Account | null>(null);
  currency = input.required<string>();
  save = output<any>();
  cancel = output<any>();

  accountForm: FormGroup = this.createForm();

  constructor() {
    effect(() => {
      const currentAccount = this.account();
      if (currentAccount) {
        this.patchForm(currentAccount);
      }
    })

    effect(() => {
      const currency = this.currency();
      if (!this.account()) {
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
    this.accountForm.patchValue({
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
    } else {
      Object.keys(form.controls).forEach(key => {
        form.get(key)?.markAsTouched();
        form.get(key)?.markAsDirty();
      })
    }
  }

  reset() {
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
    this.reset();
  }
}
