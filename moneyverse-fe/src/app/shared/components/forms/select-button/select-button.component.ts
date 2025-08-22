import {Component, forwardRef, input} from '@angular/core';
import {AbstractFormControl} from '../AbstractFormControl.components';
import {AbstractControl, FormsModule, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors} from "@angular/forms";
import {SelectButton} from 'primeng/selectbutton';

@Component({
  selector: 'app-select-button',
  imports: [
    SelectButton,
    FormsModule
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectButtonComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => SelectButtonComponent),
      multi: true
    }
  ],
  templateUrl: './select-button.component.html'
})
export class SelectButtonComponent extends AbstractFormControl<string> {
  options = input<any>([]);
  optionLabel = input<string>('label');
  optionValue = input<string>('value');
  multiple = input<boolean>(false);
  unselectable = input<boolean>(false);

  override validate(control: AbstractControl): ValidationErrors | null {
    this.control = control;
    if (this.required() && (this.value == null || (Array.isArray(this.value) && this.value.length === 0))) {
      return {required: true};
    }
    return null;
  }

  onSelectChange(value: any) {
    this.value = value;
    this.onChange(this.value);
    this.onTouched();
  }

}
