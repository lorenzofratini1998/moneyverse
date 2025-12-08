import {Component, computed, effect, inject, output, signal, viewChild} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {SelectButton} from 'primeng/selectbutton';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {Transaction, Transfer} from '../../../../transaction.model';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {FormDialogComponent} from '../../../../../../shared/components/dialogs/form-dialog/form-dialog.component';
import {DynamicDialogConfig} from 'primeng/dynamicdialog';
import {ExpenseIncomeFormComponent} from '../expense-income-form/expense-income-form.component';
import {TransferFormComponent} from '../transfer-form/transfer-form.component';
import {AbstractFormComponent} from '../../../../../../shared/components/forms/abstract-form.component';
import {TransactionFormData, TransferFormData} from '../../models/form.model';
import {TranslationService} from '../../../../../../shared/services/translation.service';

export enum TransactionFormDialogOptionsEnum {
  EXPENSE = "Expense",
  INCOME = "Income",
  TRANSFER = "Transfer",
  SUBSCRIPTION = "Subscription",
}

type TransactionFormDialogOption = {
  label: string,
  value: TransactionFormDialogOptionsEnum,
  icon: IconsEnum,
  disabled: boolean,
  selected: boolean
}

@Component({
  selector: 'app-transaction-form-dialog',
  imports: [
    FormsModule,
    SelectButton,
    SvgComponent,
    ReactiveFormsModule,
    FormDialogComponent,
    ExpenseIncomeFormComponent,
    TransferFormComponent
  ],
  templateUrl: './transaction-form-dialog.component.html'
})
export class TransactionFormDialogComponent {

  onSubmit = output<TransactionFormData | TransferFormData>();

  protected expenseIncomeForm = viewChild(ExpenseIncomeFormComponent);
  protected transferForm = viewChild(TransferFormComponent);

  private readonly translateService = inject(TranslationService);

  isExpenseOrIncome = computed(() => this.dialogOption() === TransactionFormDialogOptionsEnum.EXPENSE || this.dialogOption() === TransactionFormDialogOptionsEnum.INCOME);
  isTransfer = computed(() => this.dialogOption() === TransactionFormDialogOptionsEnum.TRANSFER);
  isSubscription = computed(() => this.dialogOption() === TransactionFormDialogOptionsEnum.SUBSCRIPTION);

  transactionToEdit = computed(() => this.formDialog().selectedItem() as Transaction | null);
  transferToEdit = computed(() => this.formDialog().selectedItem() as Transfer | null);

  protected formDialog = viewChild.required(FormDialogComponent<Transaction | Transfer, TransactionFormData | TransferFormData>);

  protected currentForm = computed(() => {
    const form = this.isExpenseOrIncome() || this.isSubscription() ? this.expenseIncomeForm() : this.transferForm();
    return form as AbstractFormComponent<Transaction | Transfer, TransactionFormData | TransferFormData>;
  });

  private dialogOptions = computed<TransactionFormDialogOption[]>(() => {
    this.translateService.lang();
    return [
      {
        label: this.translateService.translate('app.expense'),
        value: TransactionFormDialogOptionsEnum.EXPENSE,
        icon: IconsEnum.CIRCLE_MINUS,
        disabled: false,
        selected: true
      },
      {
        label: this.translateService.translate('app.income'),
        value: TransactionFormDialogOptionsEnum.INCOME,
        icon: IconsEnum.CIRCLE_PLUS,
        disabled: false,
        selected: false
      },
      {
        label: this.translateService.translate('app.transfer'),
        value: TransactionFormDialogOptionsEnum.TRANSFER,
        icon: IconsEnum.ARROW_LEFT_RIGHT,
        disabled: false,
        selected: false
      },
      {
        label: this.translateService.translate('app.subscription'),
        value: TransactionFormDialogOptionsEnum.SUBSCRIPTION,
        icon: IconsEnum.CIRCLE_PLUS,
        disabled: false,
        selected: false
      }
    ]
  });

  private readonly header = computed(() => {
    this.translateService.lang();
    const prefix = this.formDialog().selectedItem() ? this.translateService.translate('app.edit') : this.translateService.translate('app.add');
    const option = this.dialogOption();

    const headerMap: Record<TransactionFormDialogOptionsEnum, string> = {
      [TransactionFormDialogOptionsEnum.EXPENSE]: `${prefix} ${this.translateService.translate('app.transaction')}`,
      [TransactionFormDialogOptionsEnum.INCOME]: `${prefix} ${this.translateService.translate('app.transaction')}`,
      [TransactionFormDialogOptionsEnum.TRANSFER]: `${prefix} ${this.translateService.translate('app.transfer')}`,
      [TransactionFormDialogOptionsEnum.SUBSCRIPTION]: `${prefix} ${this.translateService.translate('app.subscription')}`
    };

    return headerMap[option] || `${prefix} Item`;
  });

  config = computed<DynamicDialogConfig>(() => ({
    header: this.header(),
    styleClass: 'w-[90vw] lg:w-[75vw] xl:w-[60vw] lg:max-w-[700px]'
  }))

  constructor() {
    effect(() => {
      const expenseIncomeForm = this.expenseIncomeForm();
      if (expenseIncomeForm) {
        expenseIncomeForm.onSubmit
          .subscribe(data => this.onSubmit.emit(data));
      }
    });
    effect(() => {
      const transferForm = this.transferForm();
      if (transferForm) {
        transferForm.onSubmit
          .subscribe(data => this.onSubmit.emit(data));
      }
    });
  }

  open(selectedItem?: Transaction | Transfer, option?: TransactionFormDialogOptionsEnum) {
    if (option) {
      this.dialogOption.set(option);
    } else if (!selectedItem) {
      this.dialogOption.set(TransactionFormDialogOptionsEnum.EXPENSE);
    }
    this.formDialog().open(selectedItem);
  }

  protected readonly preferenceStore = inject(PreferenceStore);

  dialogOption = signal<TransactionFormDialogOptionsEnum>(TransactionFormDialogOptionsEnum.EXPENSE);

  protected options = computed(() => {
    const isEditMode = !!this.formDialog().selectedItem();
    const currentOption = this.dialogOption();

    const availableOptions = isEditMode
      ? Object.values(TransactionFormDialogOptionsEnum)
      : [
        TransactionFormDialogOptionsEnum.EXPENSE,
        TransactionFormDialogOptionsEnum.INCOME,
        TransactionFormDialogOptionsEnum.TRANSFER
      ];

    const enabledOptions = this.getEnabledOptions(isEditMode, currentOption);

    return this.dialogOptions()
      .filter(opt => availableOptions.includes(opt.value))
      .map(opt => ({
        ...opt,
        disabled: !enabledOptions.includes(opt.value),
        selected: opt.value === currentOption
      }));
  });

  private getEnabledOptions(
    isEditMode: boolean,
    currentOption?: TransactionFormDialogOptionsEnum
  ): TransactionFormDialogOptionsEnum[] {
    const {EXPENSE, INCOME, TRANSFER, SUBSCRIPTION} = TransactionFormDialogOptionsEnum;

    if (!isEditMode) {
      return [EXPENSE, INCOME, TRANSFER];
    }

    switch (currentOption) {
      case EXPENSE:
      case INCOME:
        return [EXPENSE, INCOME];
      case TRANSFER:
        return [TRANSFER];
      case SUBSCRIPTION:
        return [SUBSCRIPTION];
      default:
        return [EXPENSE, INCOME, TRANSFER];
    }
  }

}
