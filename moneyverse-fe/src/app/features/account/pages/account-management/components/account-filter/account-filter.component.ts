import {Component, effect, inject, input, output} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {AccountStore} from '../../../../account.store';
import {CurrencyStore} from '../../../../../../shared/stores/currency.store';

import {AccountCriteria} from '../../../../account.model';
import {MultiSelect} from 'primeng/multiselect';
import {FloatLabel} from 'primeng/floatlabel';
import {RangeSliderComponent} from '../../../../../../shared/components/range-slider/range-slider.component';
import {BoundCriteria} from '../../../../../../shared/models/criteria.model';
import {Dialog} from 'primeng/dialog';

@Component({
  selector: 'app-account-filter',
  imports: [
    ReactiveFormsModule,
    MultiSelect,
    FloatLabel,
    RangeSliderComponent,
    Dialog
  ],
  templateUrl: './account-filter.component.html',
  styleUrl: './account-filter.component.scss'
})
export class AccountFilterComponent {

  protected readonly Icons = IconsEnum;
  protected readonly currencyStore = inject(CurrencyStore);
  protected readonly accountStore = inject(AccountStore);
  private readonly fb = inject(FormBuilder);
  isVisible = input.required<boolean>();
  submit = output<any>();
  close = output<void>();
  filterForm: FormGroup = this.createForm();

  private getBalanceRange(): { min: number; max: number } {
    const accounts = this.accountStore.accounts();
    if (!accounts || accounts.length === 0) {
      return {min: -10000, max: 100000};
    }

    const balances = accounts.map(acc => acc.balance).filter(b => b !== null);
    return {
      min: Math.min(...balances, 0),
      max: Math.max(...balances, 1000)
    };
  }

  private getBalanceTargetRange(): { min: number; max: number } {
    const accounts = this.accountStore.accounts();
    if (!accounts || accounts.length === 0) {
      return {min: -10000, max: 100000};
    }

    const balances = accounts.map(acc => acc.balanceTarget).filter(b => b !== null && b !== undefined);
    return {
      min: Math.min(...balances, 0),
      max: Math.max(...balances, 1000)
    };
  }

  private createForm(): FormGroup {
    const criteria = this.accountStore.accountCriteria();

    return this.fb.group({
      accountCategories: [criteria.accountCategories ?? null],
      currencies: [criteria.currencies ?? null],
      balanceMin: [criteria.balance?.lower ?? null],
      balanceMax: [criteria.balance?.upper ?? null],
      balanceTargetMin: [criteria.balanceTarget?.lower ?? null],
      balanceTargetMax: [criteria.balanceTarget?.upper ?? null]
    });
  }

  constructor() {
    effect(() => {
      const criteria = this.accountStore.accountCriteria();
      this.filterForm.patchValue({
        accountCategory: criteria.accountCategories ?? null,
        currency: criteria.currencies ?? null,
        balanceMin: criteria.balance?.lower ?? null,
        balanceMax: criteria.balance?.upper ?? null,
        balanceTargetMin: criteria.balanceTarget?.lower ?? null,
        balanceTargetMax: criteria.balanceTarget?.upper ?? null
      }, {emitEvent: false});
    });
  }

  getBalanceMin(): number {
    return this.getBalanceRange().min;
  }

  getBalanceMax(): number {
    return this.getBalanceRange().max;
  }

  getBalanceTargetMin(): number {
    return this.getBalanceTargetRange().min;
  }

  getBalanceTargetMax(): number {
    return this.getBalanceTargetRange().max;
  }

  getBalanceInitialValues(): [number, number] {
    const formValue = this.filterForm.value;
    const range = this.getBalanceRange();
    return [
      formValue.balanceMin ?? range.min,
      formValue.balanceMax ?? range.max
    ];
  }

  onBalanceRangeChange(range: BoundCriteria): void {
    this.filterForm.patchValue({
      balanceMin: range.lower,
      balanceMax: range.upper
    }, {emitEvent: false});
  }

  onBalanceTargetRangeChange(range: BoundCriteria): void {
    this.filterForm.patchValue({
      balanceTargetMin: range.lower,
      balanceTargetMax: range.upper
    }, {emitEvent: false});
  }


  reset() {
    this.accountStore.resetFilters();
  }

  apply() {
    const {
      accountCategories,
      currencies,
      balanceMin,
      balanceMax,
      balanceTargetMin,
      balanceTargetMax
    } = this.filterForm.value;

    const criteria: AccountCriteria = {
      accountCategories: accountCategories,
      currencies: currencies,
      balance: {
        lower: balanceMin,
        upper: balanceMax
      },
      balanceTarget: {
        lower: balanceTargetMin,
        upper: balanceTargetMax
      }
    };

    this.accountStore.updateCriteria(criteria);
    this.submit.emit({});
  }

  onDialogHide() {
    this.close.emit();
  }

}

