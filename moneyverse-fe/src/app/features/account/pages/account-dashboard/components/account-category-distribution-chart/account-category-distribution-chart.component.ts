import {Component, computed, inject, viewChild} from '@angular/core';
import {PieChartComponent} from '../../../../../../shared/components/charts/pie-chart/pie-chart.component';
import {FormsModule} from '@angular/forms';
import {
  PieChartCardComponent
} from '../../../../../../shared/components/charts/pie-chart-card/pie-chart-card.component';
import {AccountStore} from '../../../../services/account.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {
  AbstractPieChartComponent
} from '../../../../../../shared/components/charts/pie-chart/abstract-pie-chart.component';
import {Account, AccountCategory} from '../../../../account.model';
import {AccountDistributionChartService} from '../../services/account-distribution-chart.service';

@Component({
  selector: 'app-account-category-pie-chart',
  imports: [
    PieChartComponent,
    FormsModule,
    PieChartCardComponent
  ],
  template: `
    <app-pie-chart-card>
      <div chart-content>
        <app-pie-chart
          [options]="pieChartOptions()"
          [currency]="preferenceStore.userCurrency()"
          (onChartClick)="clickChart($event)">
        </app-pie-chart>
      </div>
    </app-pie-chart-card>
  `,
})
export class AccountCategoryDistributionChartComponent extends AbstractPieChartComponent<Account> {

  pieChartCard = viewChild.required(PieChartCardComponent);
  protected readonly accountStore = inject(AccountStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  private readonly accountDistributionService = inject(AccountDistributionChartService);

  override pieChartOptions = computed(() => {
    const categoryTotals = new Map<string, number>();

    for (const accData of this.accountDistributionService.data()) {
      const account = this.accountStore.accountsMap().get(accData.accountId);
      if (!account) continue;

      const category = account.accountCategory;
      const value = this.accountDistributionService.getPieChartValue(accData, this.pieChartCard().pieChartFilter());

      categoryTotals.set(category, (categoryTotals.get(category) ?? 0) + value);
    }

    return {
      data: Array.from(categoryTotals.entries()).map(([accountCategoryId, value]) => ({
        name: this.accountStore.accountsCategoryMap().get(+accountCategoryId)!.name,
        value
      }))
    };
  })

  clickChart(event: any) {
    const accountCategory: AccountCategory = this.accountStore.categories().find(cat => cat.name === event.name)!;
    this.onChartClick.emit(this.accountStore.accounts().filter(acc => +acc.accountCategory === +accountCategory.accountCategoryId));
  }
}
