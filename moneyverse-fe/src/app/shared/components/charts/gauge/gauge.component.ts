import {Component, computed, input} from '@angular/core';
import {ChartComponent} from '../chart.component';
import {NgxEchartsDirective} from 'ngx-echarts';
import {CurrencyPipe} from '../../../pipes/currency.pipe';
import {GaugeData} from '../../../models/chart.model';

@Component({
  selector: 'app-gauge',
  imports: [
    NgxEchartsDirective
  ],
  template: `
    <div
      echarts
      [options]="gaugeOptions()"
      class="h-128"
      [theme]="layoutService.theme()">
    </div>
  `,
  providers: [CurrencyPipe]
})
export class GaugeComponent extends ChartComponent {
  data = input.required<GaugeData>();
  currency = input.required<string>();
  min = input<number>(0);
  max = input<number>(100);

  constructor() {
    super();
  }

  gaugeColor = computed<{ color: string, borderColor: string }>(() => {
    if (this.data().value / this.max() < 0.7) {
      return {
        color: this.getCSSVariable('--style-green-text'),
        borderColor: this.getCSSVariable('--style-green-bg')
      }
    } else if (this.data().value / this.max() < 0.9) {
      return {
        color: this.getCSSVariable('--style-yellow-text'),
        borderColor: this.getCSSVariable('--style-yellow-bg')
      }
    } else return {
      color: this.getCSSVariable('--style-red-text'),
      borderColor: this.getCSSVariable('--style-red-bg')
    }
  })

  series = computed(() => {
    this.translateService.lang();
    return {
      type: 'gauge',
      startAngle: 90,
      endAngle: -270,
      min: this.min(),
      max: this.max(),
      pointer: {
        show: false
      },
      progress: {
        show: true,
        overlap: false,
        roundCap: true,
        clip: false,
        itemStyle: {
          color: this.gaugeColor().color,
          borderWidth: 1,
          borderColor: this.gaugeColor().borderColor,
        }
      },
      axisLine: {
        lineStyle: {
          width: 20
        }
      },
      splitLine: {
        show: false,
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        show: false,
      },
      data: [{
        ...this.data(),
        title: {
          show: false
        },
        itemStyle: {
          color: this.textColor()
        }
      }],
      detail: {
        width: 70,
        height: 14,
        fontSize: 40,
        offsetCenter: [0, 0],
        color: 'inherit',
        borderColor: 'inherit',
        borderWidth: 0,
        formatter: (value: number) => {
          return [
            `{first|${this.currencyPipe.transform(value, this.currency())}}`,
            `{second| ${this.translateService.translate('app.of').toLowerCase()} ${this.currencyPipe.transform(this.max(), this.currency())}}`
          ].join('\n');
        },
        rich: {
          second: {
            fontSize: 17,
          }
        }
      }
    }
  })

  gaugeOptions = computed(() => ({
    backgroundColor: this.surfaceCard(),
    series: this.series()
  }))

}
