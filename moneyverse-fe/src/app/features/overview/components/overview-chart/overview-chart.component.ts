import {Component, computed, effect, inject, signal} from '@angular/core';
import {OverviewChartService} from '../../services/overview-chart.service';
import {
  AbstractBarChartComponent
} from '../../../../shared/components/charts/bar-chart/abstract-bar-chart-component.directive';
import {BarLineChartOptions} from '../../../../shared/models/chart.model';
import {BarChartComponent} from '../../../../shared/components/charts/bar-chart/bar-chart.component';
import {PeriodFormat} from '../../../analytics/analytics.models';
import {Card} from 'primeng/card';
import {SelectButton} from 'primeng/selectbutton';
import {FormsModule} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {TranslationService} from '../../../../shared/services/translation.service';

@Component({
  selector: 'app-overview-chart',
  imports: [
    BarChartComponent,
    Card,
    SelectButton,
    FormsModule,
    TranslatePipe
  ],
  templateUrl: './overview-chart.component.html'
})
export class OverviewChartComponent extends AbstractBarChartComponent<any> {
  protected readonly overviewChartService = inject(OverviewChartService);
  protected viewOptions = signal<PeriodFormat>('month');
  private readonly translateService = inject(TranslationService);

  constructor() {
    super();
    effect(() => {
      this.overviewChartService.setViewOption(this.viewOptions());
    });
  }

  override options = computed<BarLineChartOptions>(() => {
    this.translateService.lang();
    const data = this.overviewChartService.data();
    if (!data || data.length === 0) return {
      labels: [],
      series: [{
        name: this.translateService.translate('app.chart.empty'),
        data: []
      }]
    } as BarLineChartOptions;
    return {
      labels: this.overviewChartService.getLabels(data, this.viewOptions()),
      series: this.overviewChartService.series()
    } as BarLineChartOptions
  })

  override clickChart(event: any): void {}
}
