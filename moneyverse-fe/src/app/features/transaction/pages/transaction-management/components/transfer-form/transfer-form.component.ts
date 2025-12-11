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
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-transfer-form',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    AmountInputNumberComponent,
    CurrencySelectComponent,
    DatePickerComponent,
    AccountSelectComponent,
    TranslatePipe
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
}
