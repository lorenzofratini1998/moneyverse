import {Component, inject} from '@angular/core';
import {AccountStore} from '../../../../services/account.store';
import {Account} from '../../../../account.model';
import {ReactiveFormsModule} from '@angular/forms';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {ToggleSwitch} from 'primeng/toggleswitch';
import {InputTextComponent} from '../../../../../../shared/components/forms/input-text/input-text.component';
import {TextAreaComponent} from '../../../../../../shared/components/forms/text-area/text-area.component';
import {
  AccountCategorySelectComponent
} from '../../../../../../shared/components/forms/account-category-select/account-category-select.component';
import {
  CurrencySelectComponent
} from '../../../../../../shared/components/forms/currency-select/currency-select.component';
import {
  AmountInputNumberComponent
} from '../../../../../../shared/components/forms/amount-input-number/amount-input-number.component';
import {AccountFormHandler} from '../../services/account-form.handler';
import {AccountFormData} from '../../models/form.model';
import {ColorPickerComponent} from '../../../../../../shared/components/forms/color-picker/color-picker.component';
import {FormPreviewComponent} from '../../../../../../shared/components/forms/form-preview/form-preview.component';
import {IconPickerComponent} from '../../../../../../shared/components/forms/icon-picker/icon-picker.component';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-account-form',
  imports: [
    ReactiveFormsModule,
    ToggleSwitch,
    InputTextComponent,
    TextAreaComponent,
    AccountCategorySelectComponent,
    CurrencySelectComponent,
    AmountInputNumberComponent,
    ColorPickerComponent,
    FormPreviewComponent,
    IconPickerComponent,
    TranslatePipe
  ],
  templateUrl: './account-form.component.html',
})
export class AccountFormComponent extends AbstractFormComponent<Account, AccountFormData> {

  protected override readonly formHandler = inject(AccountFormHandler);
  private readonly accountStore = inject(AccountStore);

  override patch(item: Account) {
    super.patch(item);
    if (this.accountStore.accounts().length <= 1) {
      const currentDefault = this.getControlValue<boolean>('isDefault');
      this.disableControl('isDefault', {onlySelf: true});
      this.setControlValue('isDefault', currentDefault, {emitEvent: false});
    }
  }
}
