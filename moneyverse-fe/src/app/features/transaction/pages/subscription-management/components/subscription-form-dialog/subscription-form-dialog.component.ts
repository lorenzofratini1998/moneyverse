import {Component, inject, output, signal} from '@angular/core';
import {Dialog} from 'primeng/dialog';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Message} from 'primeng/message';
import {isInvalid} from '../../../../../../shared/utils/form-utils';
import {recurrenceRuleOptions, Subscription, SubscriptionFormData} from '../../../../transaction.model';
import {InputGroup} from 'primeng/inputgroup';
import {InputNumber} from 'primeng/inputnumber';
import {Select} from 'primeng/select';
import {AccountStore} from '../../../../../account/account.store';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {CategoryStore} from '../../../../../category/category.store';
import {CurrencyStore} from '../../../../../../shared/stores/currency.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {LanguageService} from '../../../../../../shared/services/language.service';
import {DatePicker} from 'primeng/datepicker';
import {today} from '../../../../../../shared/utils/date-utils';
import {Button} from 'primeng/button';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {ToggleSwitch} from 'primeng/toggleswitch';

@Component({
  selector: 'app-subscription-form-dialog',
  imports: [
    Dialog,
    ReactiveFormsModule,
    FloatLabel,
    InputText,
    Message,
    InputGroup,
    InputNumber,
    Select,
    DatePicker,
    Button,
    SvgComponent,
    ToggleSwitch
  ],
  templateUrl: './subscription-form-dialog.component.html',
  styleUrl: './subscription-form-dialog.component.scss'
})
export class SubscriptionFormDialogComponent {

  saved = output<SubscriptionFormData>();

  protected readonly Icons = IconsEnum;
  protected readonly accountStore = inject(AccountStore);
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly currencyStore = inject(CurrencyStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly languageService = inject(LanguageService);
  private readonly fb = inject(FormBuilder);

  protected readonly isInvalid = isInvalid;
  protected _isOpen = false;
  protected _subscriptionToEdit = signal<Subscription | null>(null);
  subscriptionToEdit = this._subscriptionToEdit.asReadonly();

  protected readonly recurrenceRuleOptions = recurrenceRuleOptions;

  protected formGroup: FormGroup = this.fb.group({
    subscriptionName: [null, Validators.required],
    amount: [null, Validators.required],
    currency: [this.preferenceStore.userCurrency(), Validators.required],
    recurrence: this.fb.group({
      recurrenceRule: [recurrenceRuleOptions.find(option => option.default)?.value, Validators.required],
      startDate: [today(), Validators.required],
      endDate: [null]
    }),
    account: [this.accountStore.defaultAccount()?.accountId, Validators.required],
    category: [null],
    active: [null],
    nextExecutionDate: [today(), Validators.required],
  });

  open(subscription?: Subscription) {
    this._isOpen = true;
    if (subscription) {
      this._subscriptionToEdit.set(subscription);
      this.pathForm(subscription);
    } else {
      this._subscriptionToEdit.set(null);
      this.reset();
    }
  }

  close() {
    this._isOpen = false;
    this._subscriptionToEdit.set(null);
    this.reset();
  }

  private pathForm(subscription: Subscription) {
    this.formGroup.patchValue({
      subscriptionName: subscription.subscriptionName,
      amount: subscription.amount,
      currency: subscription.currency,
      recurrence: {
        recurrenceRule: subscription.recurrenceRule,
        startDate: new Date(subscription.startDate),
        endDate: subscription.endDate ? new Date(subscription.endDate) : null
      },
      account: subscription.accountId,
      category: subscription.categoryId,
      active: subscription.active,
      nextExecutionDate: new Date(subscription.nextExecutionDate)
    });
  }

  reset() {
    this.formGroup.reset({
      subscriptionName: null,
      amount: null,
      currency: this.preferenceStore.userCurrency(),
      recurrence: {
        recurrenceRule: recurrenceRuleOptions.find(o => o.default)?.value,
        startDate: today(),
        endDate: null
      },
      account: this.accountStore.defaultAccount()?.accountId,
      category: null,
      active: null
    });
    this.formGroup.markAsPristine();
    this.formGroup.markAsUntouched();
  }

  onSave() {
    const form = this.formGroup;
    if (form.valid) {
      this.saved.emit({
        subscriptionName: form.value.subscriptionName,
        amount: -Math.abs(form.value.amount),
        currency: form.value.currency,
        recurrence: {
          recurrenceRule: form.value.recurrence.recurrenceRule,
          startDate: form.value.recurrence.startDate,
          endDate: form.value.recurrence.endDate
        },
        accountId: form.value.account,
        categoryId: form.value.category,
        isActive: form.value.active,
        nextExecutionDate: form.value.nextExecutionDate
      });
      this.close();
    } else {
      Object.keys(form.controls).forEach(key => {
        form.get(key)?.markAsTouched();
        form.get(key)?.markAsDirty();
      });
    }
  }
}
