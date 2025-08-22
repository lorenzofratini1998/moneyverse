import {Component, effect, inject, input} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Transfer} from '../../../../transaction.model';
import {
  AmountInputNumberComponent
} from '../../../../../../shared/components/forms/amount-input-number/amount-input-number.component';
import {
  CurrencySelectComponent
} from '../../../../../../shared/components/forms/currency-select/currency-select.component';
import {DatePickerComponent} from '../../../../../../shared/components/forms/date-picker/date-picker.component';
import {
  AccountSelectComponent
} from '../../../../../../shared/components/forms/account-select/account-select.component';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {TransferFormHandler} from '../../services/transfer-form.handler';
import {TransferFormData} from "../../models/form.model";

@Component({
  selector: 'app-transfer-form',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AmountInputNumberComponent,
    CurrencySelectComponent,
    DatePickerComponent,
    AccountSelectComponent
  ],
  templateUrl: './transfer-form.component.html'
})
export class TransferFormComponent extends AbstractFormComponent<Transfer, TransferFormData> {

  transferToEdit = input<Transfer | null>(null);

  protected override readonly formHandler = inject(TransferFormHandler);

  protected readonly preferenceStore = inject(PreferenceStore);

  constructor() {
    super();
    effect(() => {
      const _transferToEdit = this.transferToEdit();
      if (_transferToEdit) {
        this.patch(_transferToEdit);
      } else {
        this.reset();
      }
    })
  }

  /*override patchForm(transfer: Transfer): void {
    this.formGroup.patchValue({
      transferId: transfer.transferId,
      date: new Date(transfer.date),
      amount: transfer.amount,
      currency: transfer.currency,
      fromAccount: transfer.transactionFrom.accountId,
      toAccount: transfer.transactionTo.accountId
    });
  }

  override reset(): void {
    this.formGroup.reset({
      transferId: null,
      date: today(),
      amount: null,
      currency: this.preferenceStore.userCurrency(),
      fromAccount: null,
      toAccount: null
    });
    this.formGroup.markAsPristine();
    this.formGroup.markAsUntouched();
  }

  protected override createForm(transfer?: Transfer | undefined): FormGroup {
    return this.fb.group({
      transferId: [transfer?.transferId ?? null],
      date: [transfer ? new Date(transfer.date) : null, Validators.required],
      amount: [transfer?.amount ?? null, Validators.required],
      currency: [transfer?.currency ?? '', Validators.required],
      fromAccount: [transfer?.transactionFrom.accountId ?? null, Validators.required],
      toAccount: [transfer?.transactionTo.accountId ?? null, Validators.required],
    })
  }

  protected override prepareFormData(): TransferFormData {
    const formValue = this.formGroup.value;
    return {
      transferId: formValue.transferId,
      date: formValue.date,
      amount: formValue.amount,
      currency: formValue.currency,
      fromAccount: formValue.fromAccount,
      toAccount: formValue.toAccount
    };
  }*/

  /*@Input({required: true}) formGroup!: FormGroup;
  transferToEdit = input<Transfer | null>(null);

  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
  protected readonly currencyStore = inject(CurrencyStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly languageService = inject(LanguageService);
  protected readonly isInvalid = isInvalidOld;

  save = output<TransferFormData>();

  constructor() {
    effect(() => {
      const _transferToEdit = this.transferToEdit();
      if (_transferToEdit) {
        this.patchForm(_transferToEdit);
      } else {
        this.reset();
      }
    })
  }

  private patchForm(transfer: Transfer) {
    this.formGroup.patchValue({
      date: new Date(transfer.date),
      amount: transfer.amount,
      currency: transfer.currency,
      fromAccount: transfer.transactionFrom.accountId,
      toAccount: transfer.transactionTo.accountId
    });
  }

  reset() {
    this.formGroup.reset({
      date: today(),
      amount: null,
      currency: this.preferenceStore.userCurrency(),
      fromAccount: null,
      toAccount: null
    });
    this.formGroup.markAsPristine();
    this.formGroup.markAsUntouched();
  }*/
}
