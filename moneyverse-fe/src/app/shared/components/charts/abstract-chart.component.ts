import {Directive, output} from '@angular/core';

@Directive()
export abstract class AbstractChartComponent<T> {
  onChartClick = output<T[]>();

  abstract clickChart(event: any): void;
}
