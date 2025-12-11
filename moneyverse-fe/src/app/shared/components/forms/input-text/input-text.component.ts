import {Component, forwardRef, input} from '@angular/core';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {InputText} from 'primeng/inputtext';
import {Message} from 'primeng/message';
import {LabelComponent} from '../label/label.component';
import {AbstractTextComponent} from '../AbstractTextFormControl.component';

@Component({
  selector: 'app-input-text',
  imports: [
    FloatLabel,
    InputText,
    Message,
    ReactiveFormsModule,
    FormsModule,
    LabelComponent
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputTextComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => InputTextComponent),
      multi: true
    }
  ],
  templateUrl: './input-text.component.html'
})
export class InputTextComponent extends AbstractTextComponent {

  override id = input<string>('input-text')

}
