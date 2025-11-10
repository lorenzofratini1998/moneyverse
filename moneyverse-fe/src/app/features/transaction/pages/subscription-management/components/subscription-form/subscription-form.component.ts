import {Component, inject} from '@angular/core';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {RecurrenceRuleEnum, RecurrenceRuleOption, SubscriptionTransaction} from '../../../../transaction.model';
import {SubscriptionFormHandler} from '../../services/subscription-form.handler';
import {
  AccountSelectComponent
} from '../../../../../../shared/components/forms/account-select/account-select.component';
import {
  AmountInputNumberComponent
} from '../../../../../../shared/components/forms/amount-input-number/amount-input-number.component';
import {
  CategorySelectComponent
} from '../../../../../../shared/components/forms/category-select/category-select.component';
import {
  CurrencySelectComponent
} from '../../../../../../shared/components/forms/currency-select/currency-select.component';
import {DatePickerComponent} from '../../../../../../shared/components/forms/date-picker/date-picker.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {InputTextComponent} from '../../../../../../shared/components/forms/input-text/input-text.component';
import {SelectComponent} from '../../../../../../shared/components/forms/select/select.component';
import {ToggleSwitch} from 'primeng/toggleswitch';
import {SubscriptionFormData} from "../../models/form.model";

export const recurrenceRuleOptions: RecurrenceRuleOption[] = [
  {label: RecurrenceRuleEnum.WEEKLY, value: 'FREQ=WEEKLY', default: false},
  {label: RecurrenceRuleEnum.MONTHLY, value: 'FREQ=MONTHLY', default: true},
  {label: RecurrenceRuleEnum.YEARLY, value: 'FREQ=YEARLY', default: false}
];

@Component({
  selector: 'app-subscription-form',
  imports: [
    AccountSelectComponent,
    AmountInputNumberComponent,
    CategorySelectComponent,
    CurrencySelectComponent,
    DatePickerComponent,
    FormsModule,
    InputTextComponent,
    ReactiveFormsModule,
    SelectComponent,
    ToggleSwitch
  ],
  templateUrl: './subscription-form.component.html'
})
export class SubscriptionFormComponent extends AbstractFormComponent<SubscriptionTransaction, SubscriptionFormData> {
  protected override readonly formHandler = inject(SubscriptionFormHandler);
  protected readonly recurrenceRuleOptions = recurrenceRuleOptions;
}
