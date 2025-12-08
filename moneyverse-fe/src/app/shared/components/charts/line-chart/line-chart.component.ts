import {Component, computed, inject, input, linkedSignal, output} from '@angular/core';
import {CurrencyPipe} from '../../../pipes/currency.pipe';
import {PreferenceStore} from '../../../stores/preference.store';
import {PreferenceKey} from '../../../models/preference.model';
import {NgxEchartsDirective} from 'ngx-echarts';
import {ChartComponent} from '../chart.component';
import {LineChartConfig, LineChartOptions} from '../../../models/chart.model';

@Component({
  selector: 'app-line-chart',
  imports: [
    NgxEchartsDirective
  ],
  template: `
    <div
      echarts
      [options]="chartOptions()"
      class="h-128"
      [theme]="layoutService.theme()"
      (chartClick)="onChartClick.emit($event)">
    </div>
  `,
  providers: [CurrencyPipe]
})
export class LineChartComponent extends ChartComponent {
  private readonly preferenceStore = inject(PreferenceStore);
  options = input.required<LineChartOptions>();
  config = input<LineChartConfig>({});

  baseConfig = computed<LineChartConfig>(() => ({
    showAverage: true,
    showArea: false,
    showLegend: false,
    smooth: true,
    tooltipFormatter: (params: any[]) => {
      const currency = this.preferenceStore.userCurrency();
      const label = params[0].axisValueLabel || params[0].axisValue;
      const lines = params.map(p => {
        const rawValue = typeof p.value === 'object' ? p.value.value : p.value;
        const formatted = this.currencyPipe.transform(rawValue, currency);

        return `
      ${p.marker}
      ${p.seriesName}:&nbsp;&nbsp;<b>${formatted}</b>
    `;
      });

      return `<strong>${label}</strong><br/>` + lines.join('<br/>');
    },
    yAxisFormatter: (value: number) => {
      return this.currencyPipe.transform(value, this.preferenceStore.userCurrency());
    }
  }));

  chartConfig = linkedSignal<LineChartConfig>(() => ({
    ...this.baseConfig(),
    ...this.config()
  }));

  onChartClick = output<any>();

  constructor() {
    super();
  }

  series = computed(() => {
    return this.options().series.map((s, index) => {
      const baseSeries: any = {
        name: s.name,
        type: 'line',
        data: s.data,
        triggerLineEvent: true,
        smooth: this.chartConfig().smooth,
        itemStyle: {
          color: this.colorPalette()[index % this.colorPalette().length],
        },
        areaStyle: this.chartConfig().showArea ? {} : undefined
      };

      if (this.chartConfig().showAverage) {
        baseSeries.markLine = {
          data: [{
            type: 'average',
            name: 'Avg',
            lineStyle: {
              color: this.textColor(),
            }
          }],
          label: {
            position: 'insideEndTop',
            color: this.textColor(),
            formatter: (params: any) => {
              return this.currencyPipe.transform(
                params.value,
                this.preferenceStore.preferences()[PreferenceKey.CURRENCY]!.value
              );
            }
          },
        };
      }
      return baseSeries;
    });
  });

  chartOptions = computed(() => ({
    backgroundColor: this.surfaceCard(),
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: this.chartConfig().tooltipFormatter
    },
    legend: this.chartConfig().showLegend ? {
      textStyle: {
        color: this.textColor(),
      }
    } : undefined,
    toolbox: {
      show: true,
      itemSize: 18,
      feature: {
        dataZoom: {
          yAxisIndex: 'none'
        },
        magicType: {type: ['line', 'bar']},
        saveAsImage: {}
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: this.options().labels,
      axisLabel: {
        color: this.textColor(),
        rotate: 45,
        margin: 10
      },
    },
    yAxis: {
      type: 'value',
      boundaryGap: [0, 0.01],
      axisLabel: {
        color: this.textColor(),
        formatter: this.chartConfig().yAxisFormatter
      }
    },
    series: this.series()
  }))
}
