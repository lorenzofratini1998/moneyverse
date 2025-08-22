import {Component, forwardRef, input} from '@angular/core';
import {AbstractTextComponent} from '../AbstractTextFormControl.component';
import {FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {Message} from 'primeng/message';
import {Textarea} from 'primeng/textarea';
import {LabelComponent} from '../label/label.component';

@Component({
  selector: 'app-text-area',
  imports: [
    FloatLabel,
    Message,
    ReactiveFormsModule,
    Textarea,
    FormsModule,
    LabelComponent
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TextAreaComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => TextAreaComponent),
      multi: true
    }
  ],
  templateUrl: './text-area.component.html',
  styles: `
    textarea {
      resize: none;
    }
  `
})
export class TextAreaComponent extends AbstractTextComponent {

  override id = input<string>('text-area')
  cols = input<number>(30)
  rows = input<number>(5)

}
