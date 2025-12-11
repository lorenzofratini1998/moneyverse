import {Component, computed, inject} from '@angular/core';
import {
  AbstractBarChartComponent
} from '../../../../../../shared/components/charts/bar-chart/abstract-bar-chart-component.directive';
import {BarChartComponent} from "../../../../../../shared/components/charts/bar-chart/bar-chart.component";
import {TransactionDistributionChartService} from '../../services/transaction-distribution-chart.service';
import {BoundCriteria} from '../../../../../../shared/models/criteria.model';
import {BarLineChartOptions} from '../../../../../../shared/models/chart.model';
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Component({
  selector: 'app-transaction-distribution-chart',
  imports: [
    BarChartComponent
  ],
  template: `
    <app-bar-chart
      [options]="options()"
      [disableAutoCurrencyFormat]="true"
      [disableAutoTooltipFormat]="true"
      (onChartClick)="clickChart($event)"
    />
  `
})
export class TransactionDistributionChartComponent extends AbstractBarChartComponent<BoundCriteria> {

  private readonly chartService = inject(TransactionDistributionChartService);
  private readonly translateService = inject(TranslationService);

  override options = computed<BarLineChartOptions>(() => {
    this.translateService.lang();
    const transactionDistribution = this.chartService.data();
    if (!transactionDistribution || transactionDistribution.data.length === 0) return {
      labels: [],
      series: [{
        name: this.translateService.translate('app.chart.empty'),
        data: []
      }]
    } as BarLineChartOptions;
    const data = transactionDistribution.data;
    return {
      labels: this.chartService.getLabels(data),
      series: [{
        name: this.translateService.translate('app.chart.current'),
        data: data.map(t => t.count.count)
      }]
    }
  })

  override clickChart(event: any) {
    const transactionDistribution = this.chartService.data();
    if (!transactionDistribution || transactionDistribution.data.length === 0) return;
    const data = this.chartService.distributionRangeMap.get(event.name);
    if (!data) return;
    this.onChartClick.emit([{
      lower: data.range.upper ? -data.range.upper : undefined,
      upper: data.range.lower ? -data.range.lower : undefined
    }]);
  }
}
