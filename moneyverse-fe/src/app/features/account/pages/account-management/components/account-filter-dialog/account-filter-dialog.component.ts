import {Component, effect, inject, input, output} from '@angular/core';
import {IconsEnum} from '../../../../../../shared/models/icons.model';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AccountStore} from '../../../../account.store';
import {CurrencyStore} from '../../../../../../shared/stores/currency.store';

import {AccountCriteria} from '../../../../account.model';
import {MultiSelect} from 'primeng/multiselect';
import {FloatLabel} from 'primeng/floatlabel';
import {RangeSliderComponent} from '../../../../../../shared/components/range-slider/range-slider.component';
import {BoundCriteria} from '../../../../../../shared/models/criteria.model';
import {Dialog} from 'primeng/dialog';
import {ToggleSwitch} from 'primeng/toggleswitch';
import {Checkbox} from 'primeng/checkbox';

@Component({
  selector: 'app-account-filter-dialog',
  imports: [
    ReactiveFormsModule,
    MultiSelect,
    FloatLabel,
    RangeSliderComponent,
    Dialog,
    ToggleSwitch,
    FormsModule,
    Checkbox
  ],
  templateUrl: './account-filter-dialog.component.html',
  styleUrl: './account-filter-dialog.component.scss'
})
export class AccountFilterDialogComponent {

  protected readonly Icons = IconsEnum;
  protected readonly currencyStore = inject(CurrencyStore);
  protected readonly accountStore = inject(AccountStore);
  private readonly fb = inject(FormBuilder);
  isOpen = input<boolean>(false);
  submitted = output<any>();
  closed = output<void>();
  protected _isOpen = false;
  isBalanceTargetEnabled = false;
  filterForm: FormGroup;

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

  constructor() {
    effect(() => {
      if (this.isOpen() !== this._isOpen) {
        this._isOpen = this.isOpen();
      }
    });
    this.filterForm = this.createForm();
    effect(() => {
      const criteria = this.accountStore.accountCriteria();
      this.filterForm.patchValue({
        accountCategories: criteria.accountCategories ?? null,
        currencies: criteria.currencies ?? null,
        balanceMin: criteria.balance?.lower ?? null,
        balanceMax: criteria.balance?.upper ?? null,
        balanceTargetMin: criteria.balanceTarget?.lower ?? null,
        balanceTargetMax: criteria.balanceTarget?.upper ?? null,
        isDefault: criteria.isDefault ?? null
      }, {emitEvent: false});
    });
  }

  private createForm(): FormGroup {
    const criteria = this.accountStore.accountCriteria();
    return this.fb.group({
      accountCategories: [criteria.accountCategories ?? null],
      currencies: [criteria.currencies ?? null],
      balanceMin: [criteria.balance?.lower ?? null],
      balanceMax: [criteria.balance?.upper ?? null],
      balanceTargetMin: [criteria.balanceTarget?.lower ?? null],
      balanceTargetMax: [criteria.balanceTarget?.upper ?? null],
      isDefault: [criteria.isDefault ?? null]
    });
  }

  open() {
    this._isOpen = true;
  }

  close() {
    this._isOpen = false;
    this.closed.emit();
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

  get isDefault() {
    return this.filterForm.value.isDefault;
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
    this.isBalanceTargetEnabled = false;
    this.close();
  }

  apply() {
    const {
      accountCategories,
      currencies,
      balanceMin,
      balanceMax,
      balanceTargetMin,
      balanceTargetMax,
      isDefault
    } = this.filterForm.value;

    const criteria: AccountCriteria = {
      accountCategories: accountCategories,
      currencies: currencies,
      balance: {
        lower: balanceMin,
        upper: balanceMax
      },
      isDefault: isDefault
    };

    if (this.isBalanceTargetEnabled && balanceTargetMin !== null && balanceTargetMax !== null) {
      criteria.balanceTarget = {
        lower: balanceTargetMin,
        upper: balanceTargetMax
      };
    }

    this.accountStore.updateCriteria(criteria);
    this.submitted.emit({});
    this.close();
  }

}

