import {Component, forwardRef, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {AbstractSelectComponent} from '../AbstractSelectComponent.component';
import {FloatLabel} from 'primeng/floatlabel';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';
import {Select} from 'primeng/select';

@Component({
  selector: 'app-select',
  imports: [
    FloatLabel,
    LabelComponent,
    Message,
    Select,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => SelectComponent),
      multi: true
    }
  ],
  templateUrl: './select.component.html'
})
export class SelectComponent extends AbstractSelectComponent {
  options = input<any>([])
  optionLabel = input<string | undefined>()
  optionValue = input<string | undefined>()
}
