import {Component, inject} from '@angular/core';
import {BadgeFilterComponent} from "../badge-filter/badge-filter.component";
import {DashboardStore} from '../../stores/dashboard.store';
import {DashboardFilter} from '../../models/dashboard.model';
import {BoundCriteria} from '../../models/criteria.model';

type TopKey = keyof DashboardFilter;
type SubKey = keyof BoundCriteria;

@Component({
  selector: 'app-dashboard-filter-panel',
  imports: [
    BadgeFilterComponent
  ],
  templateUrl: 'dashboard-filter-panel.component.html'
})
export class DashboardFilterPanelComponent {
  protected dashboardStore = inject(DashboardStore);

  onRemoveFilter(key: TopKey, item?: string | SubKey): void {
    const filterHandlers = {
      accounts: () => this.removeFromStringArray('accounts', item as string),
      categories: () => this.removeFromStringArray('categories', item as string),
      period: () => this.removePeriodFilter('period'),
      comparePeriod: () => this.removePeriodFilter('comparePeriod'),
      amount: () => this.removeAmountFilter(item as SubKey)
    };

    const handler = filterHandlers[key];
    if (handler) {
      handler();
    }
  }

  private removeFromStringArray(filterKey: 'accounts' | 'categories', itemToRemove: string): void {
    const current = this.dashboardStore.filter();
    const currentArray = current[filterKey] ?? [];
    const updatedArray = currentArray.filter(item => item !== itemToRemove);

    this.dashboardStore.updateFilter({
      [filterKey]: this.getArrayOrUndefined(updatedArray)
    } as Partial<DashboardFilter>);
  }

  private removePeriodFilter(filterKey: 'period' | 'comparePeriod'): void {
    this.dashboardStore.updateFilter({
      [filterKey]: undefined
    } as Partial<DashboardFilter>);
  }

  private removeAmountFilter(subKey: SubKey): void {
    const currentAmount = this.dashboardStore.filter().amount;

    if (!this.isValidAmountSubKey(currentAmount, subKey)) {
      return;
    }

    const updatedAmount = this.createUpdatedAmountCriteria(currentAmount, subKey);
    const finalAmount = this.isAmountCriteriaEmpty(updatedAmount) ? undefined : updatedAmount;

    this.dashboardStore.updateFilter({amount: finalAmount});
  }

  private isValidAmountSubKey(amount: BoundCriteria | undefined, subKey: SubKey): amount is BoundCriteria {
    return !!(amount && (subKey === 'lower' || subKey === 'upper'));
  }

  private createUpdatedAmountCriteria(current: BoundCriteria, subKey: SubKey): BoundCriteria {
    return {
      ...current,
      [subKey]: null
    };
  }

  private isAmountCriteriaEmpty(criteria: BoundCriteria): boolean {
    return criteria.lower == null && criteria.upper == null;
  }

  private getArrayOrUndefined<T>(array: T[]): T[] | undefined {
    return array.length > 0 ? array : undefined;
  }
}
