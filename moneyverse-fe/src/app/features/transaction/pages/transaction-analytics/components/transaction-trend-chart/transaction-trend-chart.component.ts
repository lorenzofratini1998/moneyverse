import {Component, computed, inject} from '@angular/core';
import {TransactionTrendService} from '../../services/transaction-trend.service';
import {
  AbstractLineChartComponent
} from '../../../../../../shared/components/charts/line-chart/abstract-line-chart.component';
import {LineChartComponent} from '../../../../../../shared/components/charts/line-chart/line-chart.component';
import {LineChartOptions} from '../../../../../../shared/models/chart.model';
import {BoundCriteria} from '../../../../../../shared/models/criteria.model';
import {TranslationService} from '../../../../../../shared/services/translation.service';

@Component({
  selector: 'app-transaction-trend-chart',
  imports: [
    LineChartComponent
  ],
  templateUrl: './transaction-trend-chart.component.html'
})
export class TransactionTrendChartComponent extends AbstractLineChartComponent<BoundCriteria> {
  private readonly chartService = inject(TransactionTrendService);
  private readonly translateService = inject(TranslationService);

  override options = computed(() => {
    this.translateService.lang();
    const trend = this.chartService.data();
    if (!trend || trend.data.length === 0) {
      return {
        series: [{
          name: this.translateService.translate('app.chart.empty'),
          data: []
        }]
      } as LineChartOptions;
    }
    const data = trend.data;
    return {
      labels: this.chartService.getLabels(data),
      series: this.chartService.getSeries(data),
    } as LineChartOptions
  })

  override clickChart(event: any) {
    if (!event) return;
    const seriesIndex: number = event.seriesIndex;
    const boundCriteria: BoundCriteria = seriesIndex === 0 ? {
      upper: 0
    } : {
      lower: 0
    }
    this.onChartClick.emit([boundCriteria]);
  }

}
