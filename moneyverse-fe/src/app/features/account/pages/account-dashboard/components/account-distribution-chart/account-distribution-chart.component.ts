import {Component, computed, inject, viewChild} from '@angular/core';
import {PieChartComponent} from "../../../../../../shared/components/charts/pie-chart/pie-chart.component";
import {AccountStore} from '../../../../services/account.store';
import {PreferenceStore} from '../../../../../../shared/stores/preference.store';
import {FormsModule} from '@angular/forms';
import {
  PieChartCardComponent
} from '../../../../../../shared/components/charts/pie-chart-card/pie-chart-card.component';
import {Account} from '../../../../account.model';
import {
  AbstractPieChartComponent
} from '../../../../../../shared/components/charts/pie-chart/abstract-pie-chart.component';
import {AccountDistributionChartService} from '../../services/account-distribution-chart.service';

@Component({
  selector: 'app-account-pie-chart',
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
  `
})
export class AccountDistributionChartComponent extends AbstractPieChartComponent<Account> {

  pieChartCard = viewChild.required(PieChartCardComponent);
  protected readonly accountStore = inject(AccountStore);
  protected readonly preferenceStore = inject(PreferenceStore);
  private readonly accountDistributionService = inject(AccountDistributionChartService);

  override pieChartOptions = computed(() => {
    const _data = this.accountDistributionService.data();
    return {
      data: _data.map(account => ({
        name: this.accountStore.accountsMap().get(account.accountId)!.accountName,
        value: this.accountDistributionService.getPieChartValue(account, this.pieChartCard().pieChartFilter())
      }))
    }
  })

  override clickChart(event: any) {
    this.onChartClick.emit([this.accountStore.accounts().find(acc => acc.accountName === event.name)! as Account]);
  }
}
