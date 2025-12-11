import {Component, forwardRef, inject, input} from '@angular/core';
import {
  AbstractControl,
  FormsModule,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ReactiveFormsModule,
  ValidationErrors
} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {InputNumber} from 'primeng/inputnumber';
import {LabelComponent} from "../label/label.component";
import {Message} from "primeng/message";
import {AbstractFormControl} from '../AbstractFormControl.components';
import {SystemService} from '../../../../core/services/system.service';

@Component({
  selector: 'app-amount-input-number',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AmountInputNumberComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => AmountInputNumberComponent),
      multi: true
    }
  ],
  imports: [
    FloatLabel,
    InputNumber,
    ReactiveFormsModule,
    LabelComponent,
    Message,
    FormsModule
  ],
  templateUrl: './amount-input-number.component.html'
})
export class AmountInputNumberComponent extends AbstractFormControl<number> {

  currency = input.required<string>();

  protected readonly systemService = inject(SystemService);

  validate(control: AbstractControl): ValidationErrors | null {
    this.control = control;
    if (this.required() && (this.value === null || this.value === undefined)) {
      return {required: true};
    }
    return null;
  }

  onInput(event: any) {
    if (event.value !== undefined) {
      this.value = event.value;
      this.onChange(this.value);
    }
  }

  onBlur(event: any) {
    this.onTouched();
  }
}
