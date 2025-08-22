import {AbstractFormControl} from './AbstractFormControl.components';
import {AbstractControl, ValidationErrors} from '@angular/forms';
import {Directive, input} from '@angular/core';

@Directive()
export abstract class AbstractSelectComponent extends AbstractFormControl<string> {

  filter = input<boolean>(false);
  showClear = input<boolean>(false);

  validate(control: AbstractControl): ValidationErrors | null {
    this.control = control;
    if (this.required() && (!this.value || this.value.trim() === '')) {
      return {required: true};
    }
    return null;
  }

  onSelect(event: any) {
    this.value = event.value;
    this.onChange(this.value);
  }

  onBlur(event: any) {
    this.onTouched();
  }
}
