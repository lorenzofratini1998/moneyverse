import {Component, forwardRef, inject, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {CurrencyStore} from '../../../stores/currency.store';
import {FloatLabel} from 'primeng/floatlabel';
import {Message} from 'primeng/message';
import {Select} from 'primeng/select';
import {LabelComponent} from '../label/label.component';
import {AbstractSelectComponent} from '../AbstractSelectComponent.component';

@Component({
  selector: 'app-currency-select',
  imports: [
    FloatLabel,
    Message,
    ReactiveFormsModule,
    Select,
    FormsModule,
    LabelComponent
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CurrencySelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CurrencySelectComponent),
      multi: true
    }
  ],
  templateUrl: './currency-select.component.html'
})
export class CurrencySelectComponent extends AbstractSelectComponent {

  override id = input<string>('currency-select')
  override label = input<string>('Currency')

  protected readonly currencyStore = inject(CurrencyStore);

}
