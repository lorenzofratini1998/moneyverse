import {Component, computed, input, output} from '@angular/core';
import {CurrencyPipe} from '../../../pipes/currency.pipe';
import {PercentagePipe} from '../../../pipes/percentage.pipe';
import {NgxEchartsDirective} from 'ngx-echarts';
import {ChartComponent} from '../chart.component';
import {PieChartOptions} from '../../../models/chart.model';

@Component({
  selector: 'app-pie-chart',
  imports: [
    NgxEchartsDirective
  ],
  template: `
    <div
      echarts
      [options]="chartOptions$()"
      class="min-h-[450px]"
      [theme]="layoutService.theme()"
      (chartClick)="onChartClick.emit($event)">
    </div>
  `,
  providers: [CurrencyPipe, PercentagePipe]
})
export class PieChartComponent extends ChartComponent {
  currency = input.required<string>();
  options = input.required<PieChartOptions>();
  onChartClick = output<any>();

  constructor() {
    super();
  }

  total$ = computed(() => this.options().data.reduce((total, item) => total + item.value, 0));
  chartOptions$ = computed(() => ({
    backgroundColor: this.surfaceCard(),
    tooltip: {
      trigger: 'item',
      backgroundColor: this.surfaceCard(),
      textStyle: {
        color: this.textColor()
      },
      formatter: (params: { value: number; name: string }) =>
        `${params.name}: ${this.currencyPipe.transform(params.value, this.currency())}`
    },
    grid: {
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      containLabel: false
    },
    toolbox: {
      show: true,
      itemSize: 18,
      feature: {
        saveAsImage: {show: true}
      },
      right: '2%',
    },
    legend: {
      show: true,
      orient: 'vertical',
      align: 'right',
      left: '5%',
      bottom: '5%',
      itemGap: 10
    },
    series: [
      {
        type: 'pie',
        radius: ['0%', '80%'],
        center: ['50%', '50%'],
        padAngle: 2,
        data: this.options().data.map((item, index) => ({
          value: item.value,
          name: item.name,
          itemStyle: {
            borderRadius: 5,
            color: this.colorPalette()[index % this.colorPalette().length],
          },
          percent: this.percentagePipe.transform(item.value, this.total$())
        })),
        label: {
          show: true,
          color: this.textColor(),
          formatter: (params: { name: string; percent: number }) =>
            `${params.name} (${params.percent}%)`
        },
        labelLine: {
          show: false
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }));
}
