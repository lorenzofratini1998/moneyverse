import {AbstractFormControl} from './AbstractFormControl.components';
import {Directive, input} from '@angular/core';
import {AbstractControl, ValidationErrors} from '@angular/forms';

@Directive()
export abstract class AbstractMultiSelectComponent extends AbstractFormControl<string[]> {
  display = input<string>('chip')
  filter = input<boolean>(true)

  validate(control: AbstractControl): ValidationErrors | null {
    this.control = control;
    if (this.required() && (!this.value || this.value.length === 0)) {
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
