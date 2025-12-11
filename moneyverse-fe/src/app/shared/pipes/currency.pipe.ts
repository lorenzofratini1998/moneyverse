import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'currency'
})
export class CurrencyPipe implements PipeTransform {

  transform(amount: number, currency: string, locale?: string): string {
    return new Intl.NumberFormat(locale,
      {
        style: 'currency',
        currency: currency
      }).format(amount);
  }

}
