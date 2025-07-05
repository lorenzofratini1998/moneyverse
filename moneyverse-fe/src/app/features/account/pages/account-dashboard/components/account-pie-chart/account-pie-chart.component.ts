import {Component, computed, inject, signal} from '@angular/core';
import {PieChartComponent} from "../../../../../../shared/components/pie-chart/pie-chart.component";
import {AccountStore} from '../../../../account.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {AccountCategory} from '../../../../account.model';
import {PreferenceKey} from '../../../../../../shared/models/preference.model';
import {SvgComponent} from '../../../../../../shared/components/svg/svg.component';
import {IconsEnum} from '../../../../../../shared/models/icons.model';

@Component({
  selector: 'app-account-pie-chart',
  imports: [
    PieChartComponent,
    SvgComponent
  ],
  templateUrl: './account-pie-chart.component.html',
  styleUrl: './account-pie-chart.component.scss'
})
export class AccountPieChartComponent {
  protected readonly Icons = IconsEnum;
  protected readonly PreferenceKey = PreferenceKey;
  protected readonly accountStore = inject(AccountStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  selectedCategory = signal<string | null>(null);

  accountSummary$ = computed(() => {
    return {
      totalBalance: this.accountStore.accounts().reduce((total, account) => total + account.balance, 0),
      categories: this.accountStore.categories().map(category => ({
        categoryName: category.name,
        categoryBalance: this.getCategoryTotalBalance(category)
      }))
        .filter(summary => summary.categoryBalance !== 0)
    }
  })

  pieChartOptions$ = computed(() => {
    if (this.selectedCategory() === null) {
      return {
        data: this.accountSummary$().categories.map(category => ({
          name: category.categoryName,
          value: category.categoryBalance
        }))
      }
    } else {
      const categoryAccounts = this.accountStore.accounts().filter(account => account.accountCategory === this.selectedCategory());
      return {
        data: categoryAccounts.map(account => ({
          name: account.accountName,
          value: account.balance
        }))
      }
    }
  })

  private getCategoryTotalBalance(category: AccountCategory): number {
    return this.accountStore.accounts().reduce((total, account) => {
      if (account.accountCategory === category.name) {
        return total + account.balance;
      }
      return total;
    }, 0);
  }

  handleDrillDown(event: any): void {
    if (!this.selectedCategory()) {
      this.selectedCategory.set(event.name)
    }
  }

  resetDrillDown(): void {
    this.selectedCategory.set(null);
  }
}
