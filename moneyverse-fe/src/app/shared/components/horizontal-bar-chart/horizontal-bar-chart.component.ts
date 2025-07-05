import {Component, computed, inject, input} from '@angular/core';
import {ThemeService} from '../../services/theme.service';
import {NgxEchartsDirective} from 'ngx-echarts';
import {PreferenceStore} from '../../stores/preference.store';
import {CurrencyPipe} from '../../pipes/currency.pipe';
import {PreferenceKey} from '../../models/preference.model';

export interface BarChartOptions {
  labels: string[];
  series: [{
    name: string;
    data: number[];
  }]
}

@Component({
  selector: 'app-horizontal-bar-chart',
  imports: [
    NgxEchartsDirective
  ],
  templateUrl: './horizontal-bar-chart.component.html',
  styleUrl: './horizontal-bar-chart.component.scss',
  providers: [CurrencyPipe]
})
export class HorizontalBarChartComponent {
  protected readonly themeService = inject(ThemeService);
  private readonly currencyPipe = inject(CurrencyPipe);
  private readonly preferenceStore = inject(PreferenceStore);
  options = input.required<BarChartOptions>();

  series = computed(() => {
    return this.options().series.map(serie => ({
      name: serie.name,
      type: 'bar',
      data: serie.data
    }))
  })

  chartOptions = computed(() => ({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
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
      type: 'value',
      boundaryGap: [0, 0.01],
      axisLabel: {
        formatter: (value: number) => {
          return this.currencyPipe.transform(value, this.preferenceStore
            .preferences()[PreferenceKey.CURRENCY]!.value);
        }
      }
    },
    yAxis: {
      type: 'category',
      data: this.options().labels
    },
    series: this.series()
  }))
}
