import {Component, computed, inject, input, output} from '@angular/core';
import {NgxEchartsDirective} from 'ngx-echarts';
import {PreferenceStore} from '../../../stores/preference.store';
import {CurrencyPipe} from '../../../pipes/currency.pipe';
import {ChartComponent} from '../chart.component';
import {BarLineChartOptions, Orientation} from '../../../models/chart.model';

@Component({
  selector: 'app-bar-chart',
  imports: [
    NgxEchartsDirective
  ],
  template: `
    <div
      echarts
      [options]="chartOptions()"
      class="h-128"
      [theme]="layoutService.theme"
      (chartClick)="onChartClick.emit($event)">
    </div>
  `,
  providers: [CurrencyPipe]
})
export class BarChartComponent extends ChartComponent {
  private readonly preferenceStore = inject(PreferenceStore);
  options = input.required<BarLineChartOptions>();
  orientation = input<Orientation>('vertical');
  xAxisFormatter = input<((value: any) => string) | undefined>();
  yAxisFormatter = input<((value: any) => string) | undefined>();
  tooltipFormatter = input<((params: any[]) => string) | undefined>();
  disableAutoCurrencyFormat = input<boolean>(false);
  disableAutoTooltipFormat = input<boolean>(false);

  onChartClick = output<any>();

  constructor() {
    super();
  }

  private getXAxisFormatter = computed(() => {
    const customFormatter = this.xAxisFormatter();
    if (customFormatter) {
      return customFormatter;
    }

    if (this.disableAutoCurrencyFormat()) {
      return undefined;
    }

    const isHorizontal = this.orientation() === 'horizontal';
    return isHorizontal
      ? (value: number) => this.currencyPipe.transform(value, this.preferenceStore.userCurrency())
      : undefined;
  });

  private getYAxisFormatter = computed(() => {
    const customFormatter = this.yAxisFormatter();
    if (customFormatter) {
      return customFormatter;
    }

    if (this.disableAutoCurrencyFormat()) {
      return undefined;
    }

    const isHorizontal = this.orientation() === 'horizontal';
    return !isHorizontal
      ? (value: number) => this.currencyPipe.transform(value, this.preferenceStore.userCurrency())
      : undefined;
  });

  private getTooltipFormatter = computed(() => {
    const customFormatter = this.tooltipFormatter();
    if (customFormatter) {
      return customFormatter;
    }

    if (this.disableAutoTooltipFormat()) {
      return undefined;
    }

    return (params: any[]) => {
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
    };
  });

  series = computed(() => {
    return this.options().series.map((s, index) => ({
      name: s.name,
      type: s.type ?? 'bar',
      data: s.data,
      itemStyle: {
        color: this.colorPalette()[index % this.colorPalette().length],
      },
      emphasis: {
        focus: 'series'
      }
    }))
  })

  chartOptions = computed(() => {
    const isHorizontal = this.orientation() === 'horizontal'
    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: this.getTooltipFormatter()
      },
      toolbox: {
        show: true,
        itemSize: 18,
        feature: {
          saveAsImage: {show: true}
        },
        right: '2%',
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      legend: {
        textStyle: {
          color: this.textColor(),
        }
      },
      xAxis: {
        type: isHorizontal ? 'value' : 'category',
        data: isHorizontal ? undefined : this.options().labels,
        boundaryGap: isHorizontal ? [0, 0.01] : true,
        axisLabel: {
          color: this.textColor(),
          formatter: this.getXAxisFormatter()
        }
      },
      yAxis: {
        type: isHorizontal ? 'category' : 'value',
        data: isHorizontal ? this.options().labels : undefined,
        boundaryGap: isHorizontal ? true : [0, 0.01],
        axisLabel: {
          color: this.textColor(),
          formatter: this.getYAxisFormatter()
        }
      },
      series: this.series()
    }
  })
}
