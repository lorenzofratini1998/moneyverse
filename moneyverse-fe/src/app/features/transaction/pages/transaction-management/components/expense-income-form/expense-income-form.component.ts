import {Component, effect, inject, input, Input, output} from '@angular/core';
import {DatePicker} from "primeng/datepicker";
import {FloatLabel} from "primeng/floatlabel";
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputNumber} from "primeng/inputnumber";
import {InputText} from "primeng/inputtext";
import {Message} from "primeng/message";
import {MultiSelect} from "primeng/multiselect";
import {Select} from "primeng/select";
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {AccountStore} from '../../../../../account/account.store';
import {CategoryStore} from '../../../../../category/category.store';
import {CurrencyStore} from '../../../../../../shared/stores/currency.store';
import {TransactionStore} from '../../../../transaction.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {LanguageService} from '../../../../../../shared/services/language.service';
import {Transaction, TransactionFormData} from '../../../../transaction.model';
import {today} from '../../../../../../shared/utils/date-utils';
import {isInvalid} from '../../../../../../shared/utils/form-utils';
import {InputGroup} from 'primeng/inputgroup';
import {TransactionFormDialogOptions} from '../transaction-form-dialog/transaction-form-dialog.component';

@Component({
  selector: 'app-expense-income-form',
  imports: [
    DatePicker,
    FloatLabel,
    FormsModule,
    InputNumber,
    InputText,
    Message,
    MultiSelect,
    ReactiveFormsModule,
    Select,
    InputGroup
  ],
  templateUrl: './expense-income-form.component.html',
  styleUrl: './expense-income-form.component.scss'
})
export class ExpenseIncomeFormComponent {

  @Input({required: true}) formGroup!: FormGroup;
  transactionFormOption = input<TransactionFormDialogOptions | null>(null);
  transactionToEdit = input<Transaction | null>(null);

  protected readonly Icons = IconsEnum;
  protected readonly isInvalid = isInvalid;

  protected readonly accountStore = inject(AccountStore);
  protected readonly categoryStore = inject(CategoryStore);
  protected readonly currencyStore = inject(CurrencyStore);
  protected readonly transactionStore = inject(TransactionStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly languageService = inject(LanguageService);

  save = output<TransactionFormData>();

  constructor() {
    effect(() => {
      const _transactionToEdit = this.transactionToEdit();
      if (_transactionToEdit) {
        this.patchForm(_transactionToEdit);
      } else {
        this.reset();
      }
    })
  }

  private patchForm(transaction: Transaction) {
    this.formGroup.patchValue({
      date: new Date(transaction.date),
      amount: Math.abs(transaction.amount),
      description: transaction.description,
      account: transaction.accountId,
      category: transaction.categoryId,
      currency: transaction.currency,
      tags: transaction.tags
    });

    if (this.isSubscription) {
      this.formGroup.get('date')?.disable();
      this.formGroup.get('description')?.disable();
      this.formGroup.get('currency')?.disable();
      this.formGroup.get('category')?.disable();
    } else {
      this.formGroup.get('date')?.enable();
      this.formGroup.get('description')?.enable();
      this.formGroup.get('currency')?.enable();
      this.formGroup.get('category')?.enable();
    }
  }

  reset() {
    this.formGroup.reset({
      date: today(),
      amount: null,
      description: null,
      account: this.accountStore.defaultAccount()?.accountId,
      category: null,
      currency: this.preferenceStore.userCurrency(),
      tags: null
    });
    this.formGroup.markAsPristine();
    this.formGroup.markAsUntouched();
  }

  get isSubscription(): boolean {
    return this.transactionFormOption() === TransactionFormDialogOptions.SUBSCRIPTION;
  }
}
