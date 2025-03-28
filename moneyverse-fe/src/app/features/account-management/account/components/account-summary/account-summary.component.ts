import {Component, computed, input, signal} from '@angular/core';
import {Account, AccountCategory} from '../../../account.model';
import {CurrencyPipe} from '@angular/common';
import {PieChartComponent} from '../../../../../shared/components/pie-chart/pie-chart.component';
import {SvgIconComponent} from 'angular-svg-icon';

@Component({
  selector: 'app-account-summary',
  templateUrl: './account-summary.component.html',
  imports: [
    CurrencyPipe,
    PieChartComponent,
    SvgIconComponent
  ]
})
export class AccountSummaryComponent {

  accounts = input.required<Account[]>();
  categories = input.required<AccountCategory[]>();
  userCurrency = input.required<string>();
  selectedCategory = signal<string | null>(null);

  accountSummary$ = computed(() => {
    return {
      totalBalance: this.accounts().reduce((total, account) => total + account.balance, 0),
      categories: this.categories().map(category => ({
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
      const categoryAccounts = this.accounts().filter(account => account.accountCategory === this.selectedCategory());
      return {
        data: categoryAccounts.map(account => ({
          name: account.accountName,
          value: account.balance
        }))
      }
    }
  })

  getTotalBalance(): number {
    return this.pieChartOptions$().data.reduce((total, item) => total + item.value, 0);
  }

  getCategoryTotalBalance(category: AccountCategory): number {
    return this.accounts().reduce((total, account) => {
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
