import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'percentage'
})
export class PercentagePipe implements PipeTransform {

  transform(value: number, total: number, decimalPlaces: number = 2): number {
    if (!total || total === 0) return 0;
    const percentage = (value / total) * 100;
    return Number(percentage.toFixed(decimalPlaces));
  }

}
