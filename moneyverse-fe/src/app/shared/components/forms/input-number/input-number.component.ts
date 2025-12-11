import {Component, forwardRef, inject, input} from '@angular/core';
import {AbstractFormControl} from '../AbstractFormControl.components';
import {AbstractControl, FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {InputNumber} from 'primeng/inputnumber';
import {LabelComponent} from '../label/label.component';
import {Message} from 'primeng/message';
import {SystemService} from '../../../../core/services/system.service';

@Component({
  selector: 'app-input-number',
  imports: [
    FloatLabel,
    InputNumber,
    LabelComponent,
    Message,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputNumberComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => InputNumberComponent),
      multi: true
    }
  ],
  templateUrl: './input-number.component.html'
})
export class InputNumberComponent extends AbstractFormControl<number> {

  min = input<number | undefined>(undefined);
  max = input<number | undefined>(undefined);
  minFractionDigits = input<number>(2);
  maxFractionDigits = input<number>(2);

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
