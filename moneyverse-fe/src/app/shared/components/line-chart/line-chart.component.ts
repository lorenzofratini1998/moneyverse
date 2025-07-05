import {Component, computed, inject, input} from '@angular/core';
import {ThemeService} from '../../services/theme.service';
import {CurrencyPipe} from '../../pipes/currency.pipe';
import {PreferenceStore} from '../../stores/preference.store';
import {PreferenceKey} from '../../models/preference.model';
import {NgxEchartsDirective} from 'ngx-echarts';

export interface LineChartOptions {
  labels: string[];
  series: [{
    name: string;
    data: number[];
  }]
}

@Component({
  selector: 'app-line-chart',
  imports: [
    NgxEchartsDirective
  ],
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.scss',
  providers: [CurrencyPipe]
})
export class LineChartComponent {
  protected readonly themeService = inject(ThemeService);
  private readonly currencyPipe = inject(CurrencyPipe);
  private readonly preferenceStore = inject(PreferenceStore);
  options = input.required<LineChartOptions>();

  series = computed(() => {
    return this.options().series.map(serie => ({
      name: serie.name,
      type: 'line',
      data: serie.data
    }))
  })

  chartOptions = computed(() => ({
    tooltip: {
      trigger: 'axis',
      formatter: (params: any[]) => {
        const currency = this.preferenceStore
          .preferences()[PreferenceKey.CURRENCY]!.value;
        return params
          .map(p =>
            `${p.name} (${p.seriesName}): ${this.currencyPipe.transform(
              p.value,
              currency
            )}`
          )
          .join('<br/>');
      }
    },
    legend: {},
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: this.options().labels
    },
    yAxis: {
      type: 'value',
      boundaryGap: [0, 0.01],
      axisLabel: {
        formatter: (value: number) => {
          return this.currencyPipe.transform(value, this.preferenceStore
            .preferences()[PreferenceKey.CURRENCY]!.value);
        }
      }
    },
    series: this.series()
  }))
}
