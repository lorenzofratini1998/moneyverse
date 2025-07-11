import {Component, effect, inject, input, output, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Account, AccountCategory, AccountFormData} from '../../../../account.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {CurrencyStore} from '../../../../../../shared/stores/currency.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Message} from 'primeng/message';
import {Select} from 'primeng/select';
import {InputNumber} from 'primeng/inputnumber';
import {LanguageService} from '../../../../../../shared/services/language.service';
import {ToggleSwitch} from 'primeng/toggleswitch';
import {Dialog} from 'primeng/dialog';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-account-form-dialog',
  imports: [
    ReactiveFormsModule,
    SvgComponent,
    FloatLabel,
    InputText,
    Message,
    Select,
    InputNumber,
    ToggleSwitch,
    Dialog,
    Button,
  ],
  templateUrl: './account-form-dialog.component.html',
  styleUrl: './account-form-dialog.component.scss'
})
export class AccountFormDialogComponent {
  protected readonly Icons = IconsEnum;
  protected readonly currencyStore = inject(CurrencyStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly languageService = inject(LanguageService);
  private readonly fb = inject(FormBuilder);
  accounts = input.required<Account[]>();
  categories = input.required<AccountCategory[]>();
  isOpen = input<boolean>(false);
  protected _accountToEdit = signal<Account | null>(null);
  protected _isOpen = false;

  accountToEdit = this._accountToEdit.asReadonly();
  save = output<AccountFormData>();

  accountForm: FormGroup;

  constructor() {
    effect(() => {
      if (this.isOpen() !== this._isOpen) {
        this._isOpen = this.isOpen();
      }
    });
    this.accountForm = this.createForm();
    effect(() => {
      const _accountToEdit = this._accountToEdit();
      if (_accountToEdit !== null) {
        this.patchForm(_accountToEdit);
        const currentCurrency = this.accountForm.get('currency')?.value;
        this.accountForm.get('currency')?.disable({onlySelf: true});
        this.accountForm.get('currency')?.setValue(currentCurrency, {emitEvent: false});
      }
    })

    effect(() => {
      const currency = this.preferenceStore.userCurrency();
      if (!this._accountToEdit()) {
        this.currency = currency;
      }
    })
  }

  protected set currency(currency: string) {
    this.accountForm.get('currency')?.setValue(currency);
  }

  open(account?: Account) {
    this._isOpen = true;
    if (account) {
      this._accountToEdit.set(account);
    }
  }

  close() {
    this._isOpen = false;
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
    if (this.accounts().length <= 1) {
      const currentDefault = this.accountForm.get('isDefault')?.value;
      this.accountForm.get('isDefault')?.disable({onlySelf: true});
      this.accountForm.get('isDefault')?.setValue(currentDefault, {emitEvent: false});
    }
  }

  protected saveAccount(): void {
    if (this.accountForm.valid) {
      const formValue = this.accountForm.getRawValue();
      this.save.emit({
        accountName: formValue.accountName,
        accountDescription: formValue.accountDescription,
        accountCategory: formValue.accountCategory,
        balance: formValue.balance ?? 0.0,
        balanceTarget: formValue.target,
        currency: formValue.currency,
        isDefault: formValue.isDefault
      });
      this.close();
    } else {
      Object.keys(this.accountForm.controls).forEach(key => {
        const control = this.accountForm.get(key);
        control?.markAsTouched();
        control?.markAsDirty();
      });
    }
  }

  reset() {
    this.accountForm.reset({
      accountName: '',
      accountDescription: null,
      accountCategory: '',
      balance: 0.0,
      target: null,
      currency: this.preferenceStore.userCurrency(),
      isDefault: null
    });
  }

  protected cancel(): void {
    this.reset();
    this.close();
  }

  protected isInvalid(controlName: string) {
    const control = this.accountForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }
}
