import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'decimal'
})
export class DecimalPipe implements PipeTransform {

  transform(value: number | undefined | null, decimalDigits: number = 2): string {
    if (value == null || !isFinite(value)) return '';
    return new Intl.NumberFormat('it-IT', {
      minimumFractionDigits: decimalDigits,
      maximumFractionDigits: decimalDigits
    }).format(value);
  }

}
