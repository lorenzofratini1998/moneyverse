import {Component, forwardRef, inject, input} from '@angular/core';
import {AbstractMultiSelectComponent} from '../abstract-multi-select.component';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {CurrencyStore} from '../../../stores/currency.store';
import {FloatLabel} from 'primeng/floatlabel';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';
import {MultiSelect} from 'primeng/multiselect';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-currency-multi-select',
  imports: [
    FloatLabel,
    LabelComponent,
    Message,
    MultiSelect,
    FormsModule,
    TranslatePipe
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CurrencyMultiSelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CurrencyMultiSelectComponent),
      multi: true
    }
  ],
  templateUrl: './currency-multi-select.component.html'
})
export class CurrencyMultiSelectComponent extends AbstractMultiSelectComponent {
  override id = input<string>('currency-multi-select')
  override label = input<string>('app.currencies')

  protected readonly currencyStore = inject(CurrencyStore);

}
