import {Component, computed, inject, output, signal, ViewChild} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {Dialog} from 'primeng/dialog';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {SelectButton} from 'primeng/selectbutton';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {Button} from 'primeng/button';
import {ExpenseIncomeFormComponent} from '../expense-income-form/expense-income-form.component';
import {Transaction, TransactionFormData, Transfer, TransferFormData} from '../../../../transaction.model';
import {TransferFormComponent} from '../transfer-form/transfer-form.component';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';

export enum TransactionFormDialogOptions {
  EXPENSE = "Expense",
  INCOME = "Income",
  TRANSFER = "Transfer",
  SUBSCRIPTION = "Subscription",
}

@Component({
  selector: 'app-transaction-form-dialog',
  imports: [
    Dialog,
    FormsModule,
    SelectButton,
    SvgComponent,
    ReactiveFormsModule,
    Button,
    ExpenseIncomeFormComponent,
    TransferFormComponent
  ],
  templateUrl: './transaction-form-dialog.component.html',
  styleUrl: './transaction-form-dialog.component.scss'
})
export class TransactionFormDialogComponent {

  saved = output<TransactionFormData | TransferFormData>();

  protected readonly preferenceStore = inject(PreferenceStore);
  protected readonly TransactionFormDialogOptions = TransactionFormDialogOptions;
  protected readonly Icons = IconsEnum;
  protected options = signal<Array<{
    label: string,
    value: TransactionFormDialogOptions,
    icon: IconsEnum,
    disabled: boolean,
    selected: boolean
  }>>([
    {
      label: 'Expense',
      value: TransactionFormDialogOptions.EXPENSE,
      icon: IconsEnum.CIRCLE_MINUS,
      disabled: false,
      selected: true
    },
    {
      label: 'Income',
      value: TransactionFormDialogOptions.INCOME,
      icon: IconsEnum.CIRCLE_PLUS,
      disabled: false,
      selected: false
    },
    {
      label: 'Transfer',
      value: TransactionFormDialogOptions.TRANSFER,
      icon: IconsEnum.ARROW_LEFT_RIGHT,
      disabled: false,
      selected: false
    },
    {
      label: 'Subscription',
      value: TransactionFormDialogOptions.SUBSCRIPTION,
      icon: IconsEnum.CALENDAR_SYNC,
      disabled: true,
      selected: false
    },
  ]);
  private readonly fb = inject(FormBuilder);
  private selectedItem$ = signal<any>(null);
  selectedItem = this.selectedItem$.asReadonly();

  protected formGroup: FormGroup = this.fb.group({
    transaction: this.fb.group({
      date: [null, Validators.required],
      amount: [null, Validators.required],
      description: [null, Validators.required],
      account: [null, Validators.required],
      category: [null],
      currency: [null, Validators.required],
      tags: [null]
    }),
    transfer: this.fb.group({
      date: [null, Validators.required],
      amount: [null, Validators.required],
      currency: [this.preferenceStore.userCurrency(), Validators.required],
      fromAccount: [null, Validators.required],
      toAccount: [null, Validators.required],
    })
  });

  protected _isOpen = false;
  optionValue = signal<TransactionFormDialogOptions>(TransactionFormDialogOptions.EXPENSE);

  @ViewChild(ExpenseIncomeFormComponent) expenseIncomeFormComponent!: ExpenseIncomeFormComponent;
  @ViewChild(TransferFormComponent) transferFormComponent!: TransferFormComponent;

  open(selectedItem?: Transaction | Transfer | null, option?: TransactionFormDialogOptions) {
    this._isOpen = true;
    this.selectedItem$.set(selectedItem ?? null);
    const isEditMode = !!selectedItem;

    const options: TransactionFormDialogOptions[] = isEditMode
      ? Object.values(TransactionFormDialogOptions)
      : [
        TransactionFormDialogOptions.EXPENSE,
        TransactionFormDialogOptions.INCOME,
        TransactionFormDialogOptions.TRANSFER,
      ];

    const enabledOptions = (() => {
      switch (option) {
        case TransactionFormDialogOptions.EXPENSE:
        case TransactionFormDialogOptions.INCOME:
          return [TransactionFormDialogOptions.EXPENSE, TransactionFormDialogOptions.INCOME];
        case TransactionFormDialogOptions.TRANSFER:
          return [TransactionFormDialogOptions.TRANSFER];
        default:
          return options;
      }
    })();

    this.options.set(
      this.options()
        .filter(opt => options.includes(opt.value))
        .map(opt => ({
          ...opt,
          disabled: !enabledOptions.includes(opt.value)
        }))
    );

    if (option) {
      this.optionValue.set(option);
    }
  }

  close() {
    this._isOpen = false;
    this.selectedItem$.set(null);
    this.expenseIncomeFormComponent.reset();
    this.transferFormComponent.reset();
  }

  protected resetForms() {
    switch (this.optionValue()) {
      case TransactionFormDialogOptions.EXPENSE:
      case TransactionFormDialogOptions.INCOME:
        this.transferFormComponent.reset();
        break;
      case TransactionFormDialogOptions.TRANSFER:
        this.expenseIncomeFormComponent.reset();
        break;
    }
  }

  get transactionForm(): FormGroup {
    return this.formGroup.get('transaction') as FormGroup;
  }

  get transferForm(): FormGroup {
    return this.formGroup.get('transfer') as FormGroup;
  }

  onSave(): void {
    switch (this.optionValue()) {
      case TransactionFormDialogOptions.EXPENSE:
      case TransactionFormDialogOptions.INCOME:
        this.onSaveTransaction();
        break;
      case TransactionFormDialogOptions.TRANSFER:
        this.onSaveTransfer();
        break;
    }
  }

  private onSaveTransaction() {
    if (this.transactionForm.valid) {
      const transactionFormData = this.transactionForm.value as TransactionFormData;
      this.saved.emit({
        ...transactionFormData,
        amount: this.optionValue() === TransactionFormDialogOptions.EXPENSE
          ? -Math.abs(transactionFormData.amount)
          : Math.abs(transactionFormData.amount)
      });
      this.close();
    } else {
      Object.values(this.transactionForm.controls).forEach(control => {
        control.markAsTouched();
        control.markAsDirty();
      });
    }
  }

  private onSaveTransfer() {
    if (this.transferForm.valid) {
      const transferFormData = this.transferForm.value as TransferFormData;
      this.saved.emit(transferFormData);
      this.close();
    } else {
      Object.values(this.transferForm.controls).forEach(control => {
        control.markAsTouched();
        control.markAsDirty();
      });
    }
  }

  get selectedOption() {
    return this.optionValue();
  }

  set selectedOption(value: TransactionFormDialogOptions) {
    this.optionValue.set(value);
  }
}
