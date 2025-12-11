import {Component, forwardRef, input} from '@angular/core';
import {AbstractMultiSelectComponent} from '../abstract-multi-select.component';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';
import {MultiSelect} from 'primeng/multiselect';

@Component({
  selector: 'app-multi-select',
  imports: [
    FloatLabel,
    LabelComponent,
    Message,
    MultiSelect,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => MultiSelectComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => MultiSelectComponent),
      multi: true
    }
  ],
  templateUrl: './multi-select.component.html'
})
export class MultiSelectComponent extends AbstractMultiSelectComponent {
  options = input<any>([]);
  optionLabel = input<string | undefined>();
  optionValue = input<string | undefined>();
}
