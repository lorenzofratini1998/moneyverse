import {Component, computed, effect, inject, signal} from '@angular/core';
import {AnalyticsService} from '../../../../../../shared/services/analytics.service';
import {LineChartComponent} from '../../../../../../shared/components/charts/line-chart/line-chart.component';
import {AccountStore} from '../../../../services/account.store';
import {Card} from 'primeng/card';
import {Select} from 'primeng/select';
import {Account} from '../../../../account.model';
import {FormsModule} from '@angular/forms';
import {SelectButton} from 'primeng/selectbutton';
import {
  AbstractLineChartComponent
} from '../../../../../../shared/components/charts/line-chart/abstract-line-chart.component';
import {AccountTrendChartService} from '../../services/account-trend-chart.service';
import {ChartFilter} from '../../../../../analytics/analytics.models';
import {LineChartOptions} from "../../../../../../shared/models/chart.model";
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Component({
  selector: 'app-account-trend',
  imports: [
    LineChartComponent,
    Card,
    Select,
    FormsModule,
    SelectButton
  ],
  templateUrl: './account-trend-chart.component.html'
})
export class AccountTrendChartComponent extends AbstractLineChartComponent<Account> {

  protected readonly accountStore = inject(AccountStore);
  protected readonly analyticsService = inject(AnalyticsService);
  private readonly accountTrendChartService = inject(AccountTrendChartService);
  private readonly translateService = inject(TranslationService);

  protected chartFilter = signal<ChartFilter>('totalAmount')
  protected selectedAccount = signal<Account | null>(null);

  override options = computed(() => {
    this.translateService.lang();
    const trend = this.accountTrendChartService.data();
    if (!trend || trend.length === 0 || !this.selectedAccount()) {
      return {
        series: [{
          name: this.translateService.translate('app.chart.empty'),
          data: []
        }]
      } as LineChartOptions;
    }
    return {
      labels: this.accountTrendChartService.getLabels(trend, this.selectedAccount()!),
      series: this.accountTrendChartService.getSeries(trend, this.selectedAccount()!, this.chartFilter()),
    } as LineChartOptions
  })

  constructor() {
    super()
    effect(() => {
      this.selectedAccount.set(this.accountStore.defaultAccount()!);
    });
  }

  override clickChart(event: any) {
    if (this.selectedAccount() && this.selectedAccount()?.accountName == event.seriesName) {
      this.onChartClick.emit([this.selectedAccount()!]);
    }
  }
}
