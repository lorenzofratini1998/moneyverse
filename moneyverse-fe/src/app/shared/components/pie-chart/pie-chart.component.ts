import {Component, computed, inject, input, output, Signal} from '@angular/core';
import {ThemeService} from '../../services/theme.service';
import {CurrencyPipe} from '../../pipes/currency.pipe';
import {PercentagePipe} from '../../pipes/percentage.pipe';
import {NgxEchartsDirective} from 'ngx-echarts';
import {EChartsCoreOption} from 'echarts';

export interface PieChartOptions {
  data: {
    name: string;
    value: number;
  }[];
}

@Component({
  selector: 'app-pie-chart',
  imports: [
    NgxEchartsDirective
  ],
  templateUrl: './pie-chart.component.html',
  providers: [CurrencyPipe, PercentagePipe]
})
export class PieChartComponent {
  protected readonly themeService = inject(ThemeService);
  private readonly currencyPipe = inject(CurrencyPipe);
  private readonly percentagePipe = inject(PercentagePipe);
  currency = input.required<string>();
  options = input.required<PieChartOptions>();
  sectionClick = output<any>();

  private readonly colorPalette = [
    '#A8D5BA', // Verde Menta
    '#F9D5E5', // Rosa Pallido
    '#F7CAC9', // Rosa Pesca
    '#92A8D1', // Azzurro Soft
    '#F3E5AB', // Giallo Crema
    '#FFCCBC', // Arancione Soft
    '#B5EAD7', // Verde Acqua Pastello
    '#C6A9A3', // Rosa Antico
    '#E0BBE4', // Lavanda Soft
    '#F5D6BA', // Beige Soft
    '#D6E9C6', // Verde Lime Chiaro
    '#FFB7B2', // Corallo Soft
    '#F5E6CC', // Giallo Sabbia
    '#C1E1C1', // Verde Chiaro
    '#AED9E0', // Blu Acqua
    '#F9CB9C', // Arancio Chiaro
    '#C9C0BB', // Grigio Chiaro
    '#D1B3C4', // Rosa Malva
    '#E8A87C', // Arancio Miele
    '#F7C6C7', // Rosa Confetto
  ];


  total$ = computed(() => this.options().data.reduce((total, item) => total + item.value, 0));
  chartOptions$ = computed(() => ({
    tooltip: {
      trigger: 'item',
      formatter: (params: { value: number; name: string }) =>
        `${params.name}: ${this.currencyPipe.transform(params.value, this.currency())}`
    },
    series: [
      {
        type: 'pie',
        radius: '50%',
        center: ['50%', '50%'],
        data: this.options().data.map((item, index) => ({
          value: item.value,
          name: item.name,
          itemStyle: {color: this.colorPalette[index % this.colorPalette.length]},
          percent: this.percentagePipe.transform(item.value, this.total$())
        })),
        label: {
          show: true,
          formatter: (params: { name: string; percent: number }) =>
            `${params.name} (${params.percent}%)`
        },
        roseType: 'radius',
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

  onChartClick(event: any): void {
    this.sectionClick.emit(event);
  }
}
