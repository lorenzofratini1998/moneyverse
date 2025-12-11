import {Component, forwardRef, input} from '@angular/core';
import {AbstractControl, FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors} from '@angular/forms';
import {AbstractFormControl} from '../AbstractFormControl.components';
import {Checkbox} from 'primeng/checkbox';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-checkbox',
  imports: [
    Checkbox,
    FormsModule,
    Message
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CheckboxComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => CheckboxComponent),
      multi: true
    }
  ],
  templateUrl: './checkbox.component.html'
})
export class CheckboxComponent extends AbstractFormControl<boolean> {
  binary = input<boolean>(false);

  validate(control: AbstractControl): ValidationErrors | null {
    this.control = control;
    if (this.required() && (this.value === null || !this.value)) {
      return {required: true};
    }
    return null;
  }

  onCheckboxChange(event: any) {
    this.value = event.checked;
    this.onChange(this.value);
    this.onTouched();
  }
}
