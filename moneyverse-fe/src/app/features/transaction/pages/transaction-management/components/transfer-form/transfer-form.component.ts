import {Component, effect, inject, input, Input, output} from '@angular/core';
import {DatePicker} from "primeng/datepicker";
import {FloatLabel} from "primeng/floatlabel";
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputNumber} from "primeng/inputnumber";
import {Message} from "primeng/message";
import {Select} from "primeng/select";
import {InputGroup} from 'primeng/inputgroup';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {AccountStore} from '../../../../../account/account.store';
import {CurrencyStore} from '../../../../../../shared/stores/currency.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {LanguageService} from '../../../../../../shared/services/language.service';
import {isInvalid} from '../../../../../../shared/utils/form-utils';
import {Transfer, TransferFormData} from '../../../../transaction.model';
import {today} from '../../../../../../shared/utils/date-utils';

@Component({
  selector: 'app-transfer-form',
  imports: [
    DatePicker,
    FloatLabel,
    FormsModule,
    InputNumber,
    Message,
    ReactiveFormsModule,
    Select,
    InputGroup
  ],
  templateUrl: './transfer-form.component.html',
  styleUrl: './transfer-form.component.scss'
})
export class TransferFormComponent {
  @Input({required: true}) formGroup!: FormGroup;
  transferToEdit = input<Transfer | null>(null);

  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
  protected readonly currencyStore = inject(CurrencyStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly languageService = inject(LanguageService);
  protected readonly isInvalid = isInvalid;

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
  }
}
